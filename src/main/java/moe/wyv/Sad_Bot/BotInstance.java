package moe.wyv.Sad_Bot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import moe.wyv.Sad_Bot.ai.MegaDon;
import moe.wyv.Sad_Bot.commands.BotCommand;
import moe.wyv.Sad_Bot.skills.BotSkill;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;


/**
 * All of the infrastructure for the chatbot is contained in this
 * class. While this may make it a God class, there isn't a whole
 * lot to be done because PircBot is a God class. The bot parses
 * commands and skills. The commands and skills are enabled
 * according to a settings file located in the same directory. This
 * file will be created if it does not exist. To add more commands
 * or skills, create an appropriate class implementing the interface,
 * then add it to the list in {@link Settings}. Chat skills are the
 * unique parts of this bot. They are triggered by a regular
 * expression that matches a pattern in the chat. Please read the
 * source files for more information.
 * 
 * @author fettuccine
 *
 */
public class BotInstance extends PircBot {
	
	/**
	 * YAML settings file with same name as bot
	 */
	private Settings settings;
	
	/**
	 * List of enabled commands
	 */
	private List<BotCommand> commands;
	
	/**
	 * List of enabled skills
	 */
	private List<BotSkill> skills;
	
	/**
	 * The channel data for the bot's primary channel. User
	 * statistics are stored here.
	 */
	private Channel mainChannel;
	
	/**
	 * The user cache is a cache of users that join the channel.
	 * Every five minutes, if the user didn't leave, the user is
	 * added to the current viewer list and statistics are taken.
	 * This is to allow statistics on users that don't chat
	 * (lurkers), but also keeps the size of the statistics database
	 * small.
	 */
	private List<String> userCache = new ArrayList<String>();
	
	/**
	 * Pseudo-artificial intelligence. Not done.
	 */
	private MegaDon ai;
	
	/**
	 * Creates a new chatbot with data loaded from a YAML file
	 * 
	 * @param botName name of bot in CamelCase (filename)
	 */
	public BotInstance(String botName) {
		super();
		this.setMessageDelay(1535); // 20 messages in 30 seconds, plus a little to compensate for latency
		
		loadData(botName);
		mainChannel = new Channel(settings.getPrimaryChannel());
		ai = new MegaDon();
	}

	/**
	 * This method loads settings from a file, and then populates
	 * the bot's parameters with those values. If the file does not
	 * exist, the values are populated with default values. Then
	 * the statistics data is loaded.
	 * 
	 * @param botName username of bot to use in chat
	 */
	private void loadData(String botName) {
		settings = new Settings(botName+".yaml");
		this.setName(settings.getBotName().toLowerCase());
		commands = settings.getEnabledCommands();
		skills = settings.getEnabledSkills();
	}
	
	/**
	 * This method will try to reconnect to chat with an exponential
	 * delay.
	 */
	public void connect() {
		this.disconnect();
		try {
			connect("irc.chat.twitch.tv", 6667, settings.getOauth());
			sendRawLineViaQueue("CAP REQ :twitch.tv/membership");
			joinChannel(settings.getPrimaryChannel());
			joinChannel("#"+settings.getBotName().toLowerCase());
		} catch (NickAlreadyInUseException e) {
			quit();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IrcException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to disconnect the bot and cleanly close the program
	 */
	public void quit() {
		mainChannel.updateChatters();
		
		if (this.isConnected()) {
			this.quitServer();
		}
		this.dispose();
	}
	
	/* (non-Javadoc)
	 * @see org.jibble.pircbot.PircBot#onDisconnect()
	 * 
	 * If the bot gets disconnected, try to reconnect
	 */
	@Override
	public void onDisconnect() {
		long delay = 500;
		
		while (!isConnected()) {
			try {
				reconnect();
			} catch (NickAlreadyInUseException e) {
				this.dispose();
			} catch (IOException | IrcException e) {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException ee) {	}
				
				//increments delay by 1.5 times, up until 60 seconds
				if (delay < 60*1000) {
					delay = (long) (delay * 1.5);
				}
			}
		}
		
		this.sendRawLineViaQueue("CAP REQ :twitch.tv/membership");
		joinChannel(settings.getPrimaryChannel());
		joinChannel("#"+settings.getBotName().toLowerCase());
	}
	
	/* (non-Javadoc)
	 * @see org.jibble.pircbot.PircBot#onMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 * 
	 * The main logic
	 */
	@Override
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		if (sender.equalsIgnoreCase(settings.getBotName())) {
			return;
		}
		
		Message mess = new Message(channel, sender, message);
		String split[] = message.split(" ");
		
		//adds +1 to message count
		mainChannel.userMessage(sender);
		//user talked, so remove them from the filter cache
		userCache.remove(sender);
		
		//Message is a command for the bot?
		if (message.charAt(0) == settings.getCommandChar()) {
			//ccomand is the custom command that allows for many $commands
			BotCommand ccommand = null;
			for (BotCommand command : commands) {
				if (split[0].equalsIgnoreCase(settings.getCommandChar()+command.getKeyword())) {
					command.activate(this, mess);
//					System.out.println(System.currentTimeMillis()+" speed test [command complete]");
					return;
				}
				//used to find CommandCommand for next part
				if (command.getKeyword().equals("command")) {
					ccommand = command;
				}
			}
			
			//Did not find matching command, maybe custom command?
			if (ccommand != null) {
				ccommand.activate(this, mess);
				return;
			}
			return;
		}
		
		//Learn from message
		ai.add(message);
		
		//Message is a party skill?
		for (BotSkill skill : skills) {
			if (skill.getPattern().matcher(message).matches()) {
				skill.action(this, mess);
//				System.out.println(System.currentTimeMillis()+" speed test [skill complete]");
				return;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jibble.pircbot.PircBot#onJoin(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 * 
	 * Adds user to chatter cache. After 5 minutes, user is
	 * processed in statistics database. Prevents the database from
	 * being overloaded with random empty users.
	 */
	@Override
	public void onJoin(String channel, String sender, String login, String hostname) {
		//This statement is a way to perform initialization code after bot connects to channel 
		if (sender.equalsIgnoreCase(settings.getBotName())) {
			this.setVerbose(true);
			Logger.getInstance().registerLogger(this);
			return;
		}
		userCache.add(sender);
	}
	
	/* (non-Javadoc)
	 * @see org.jibble.pircbot.PircBot#onPart(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 * 
	 * Removes user from all tracking. 
	 */
	@Override
	public void onPart(String channel, String sender, String login, String hostname) {
		if (sender.equalsIgnoreCase(settings.getBotName())) {
			return;
		}
		//Only update the user's data if we care about them
		if (userCache.contains(sender)) {
			userCache.remove(sender);
		} else {
			mainChannel.userPart(sender);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jibble.pircbot.PircBot#onOp(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 * 
	 * Sets user as moderator
	 */
	@Override
	public void onOp(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
		log("MODDED "+recipient);
		if (recipient.equalsIgnoreCase(settings.getBotName())) {
			return;
		}
		if (channel.equals(settings.getPrimaryChannel())) {
			mainChannel.addMod(recipient);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jibble.pircbot.PircBot#onUserMode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 * 
	 * For some reason onOp() doesn't work in TwitchIRC. So this is a work around
	 */
	@Override
	public void onUserMode(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String mode) { // NOPMD by fettuccine on 6/13/17 10:28 AM
		//twitch broke something
		//	targetNick > "MODE"
		//	sourceNick > "jtv"
		//	sourceLogin > ""
		//	sourceHostname > ""
		//	mode > "#channel +o target_username"
		if (targetNick.equals("MODE")) {
			String[] operation = mode.split(" ");
			String channel = operation[0];
			mode = operation[1];
			targetNick = operation[2];
			
			if (targetNick.equalsIgnoreCase(settings.getBotName())) {
				return;
			}
			if (mode.equals("+o") && channel.equals(settings.getPrimaryChannel())) {
				mainChannel.addMod(targetNick);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jibble.pircbot.PircBot#onPing(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 * 
	 * This is a makeshift 5 minute timer because TwitchIRC sends ping every 5 minutes or so.
	 */
	@Override
	public void onServerPing(String response) {
		super.onServerPing(response);
		
		//Lurker detector:
		//If there are any users that have been watching for a while
		//but haven't talked, add them to the viewer list. You could
		//also put code here to track lurker%?
		mainChannel.updateChatters();
		for (String name : userCache) {
			mainChannel.userJoin(name);
		}
		userCache = new ArrayList<String>();
	}
	
	@Override
	public void onUserList(String channel, org.jibble.pircbot.User[] users) {
		if (channel.equals(settings.getPrimaryChannel())) {
			for (org.jibble.pircbot.User user : users) {
				log("Userlist: "+user.getNick());
				this.onJoin(channel, user.getNick(), "", "");
			}
		}
	}
	
	/**
	 * Prevents abuse from user trying to get bot to say '.commands',
	 * which are TwitchIRC's way of sending '/commands' to chat. I
	 * don't know what happens if you send /something but I don't think
	 * it would do anything.
	 * 
	 * @param channel Target channel
	 * @param message message to send
	 */
	public void send(String channel, String message) {
//		System.out.println(System.currentTimeMillis()+" speed test [queue]");
		if (message.charAt(0) == '.') {
			super.sendMessage(channel, settings.getCommandChar()+" "+message);
		} else {
			super.sendMessage(channel, message);
		}
	}
	
	/**
	 * Use to send a command to the channel. This method should not
	 * be directly called by plugins unless absolutely necessary.
	 * 
	 * @param channel Target channel
	 * @param command omit preceding '.'
	 */
	public void sendCommand(String channel, String command) {
		super.sendMessage(channel, "." + command);
	}
	
	/**
	 * Retrieves the user from the channel database. Note: If the
	 * user doesn't exist, they are created. Please use
	 * {@link BotInstance#hasUser(String)} to check if db instead.
	 * 
	 * @param username name in lowercase
	 * @return User if they exist, {@code null} otherwise
	 */
	public User getUserForced(String username) {
		return mainChannel.getUserForced(username);
	}
	
	/**
	 * If the channel database has information about a user.
	 * 
	 * @param username chatter name in lowercase
	 * @return if database contains user
	 */
	public boolean hasUser(String username) {
		return mainChannel.hasUser(username);
	}

	/**
	 * Returns the primary channel's Twitch id
	 * 
	 * @return the primary channel's Twitch id
	 */
	public String getChannelID() {
		return mainChannel.getChannelID();
	}
	
	/**
	 * Returns the bot settings
	 * 
	 * @return the bot settings
	 */
	public Settings getSettings() {
		return settings;
	}

	public MegaDon getAi() {
		return ai;
	}
	
}
