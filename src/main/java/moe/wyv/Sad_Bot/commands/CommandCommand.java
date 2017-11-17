package moe.wyv.Sad_Bot.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import moe.wyv.Sad_Bot.BotInstance;
import moe.wyv.Sad_Bot.Message;

public class CommandCommand implements BotCommand {

	private Map<String, String> replies = new HashMap<String, String>();
	
	public CommandCommand() {
		loadCommands();
	}
	
	@Override
	public void activate(BotInstance bot, Message message) {
		String[] args = message.getMessage().split(" ");
		if (args[0].equals( bot.getSettings().getCommandChar() + getKeyword() )) {
			//normal behavior
			if (args.length > 2 && bot.getUserForced(message.getSender()).isMod()) {
				switch (args[1]) {
				case "add":
					putCommand(bot, message, args);
					break;
					
				case "remove":
					if (replies.remove(args[2]) != null) {
						bot.send(message.getChannel(), "Command removed.");
						saveCommands();
					} else {
						bot.send(message.getChannel(), "Command '"+args[2]+"' not found.");
					}
					break;
					
				case "set":
					putCommand(bot, message, args);
					break;

				default:
					break;
				}
			} else {
				//$command
				listCommands(bot, message.getChannel());
			}
		} else {
			//custom command
			String reply = replies.get(args[0].substring(1));
			if (reply != null) {
				bot.send(message.getChannel(), reply);
			} else {
				return;
			}
		}
	}
	
	/**
	 * Adds command to database. The command has one word trigger
	 * and the rest is added as the reply.
	 * 
	 * @param bot this
	 * @param message message data
	 * @param args we already split the message once
	 */
	private void putCommand(BotInstance bot, Message message, String[] args) {
		String keyword = args[2];
		String reply = message.getMessage().replace(args[0]+" "+args[1]+" "+args[2]+" ", "");
		replies.put(keyword, reply);
		bot.send(message.getChannel(), "Command '"+keyword+"' added: "+reply);
		saveCommands();
	}

	/**
	 * Lists all known commands to chat. If there are none, gives a hint.
	 * 
	 * @param bot this
	 * @param channel target channel
	 */
	private void listCommands(BotInstance bot, String channel) {
		if (replies.isEmpty()) {
			bot.send(channel, "No commands found! Try making one with 'command add <keyword> <reply text...>'");
		} else {
			StringBuffer reply = new StringBuffer("Commands: ");
			for (String word : replies.keySet()) {
				reply.append(word).append(", ");
			}
			bot.send(channel, reply.substring(0, reply.length()-2));
		}
	}
	
	private void saveCommands() {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(new File("ccommands.dat")));
			out.writeObject(replies);
			out.flush();
			out.close();
		} catch (IOException e) {
			//It's okay
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException l) {
					l.printStackTrace();
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadCommands() {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(new File("ccommands.dat")));
			replies = (Map<String, String>) in.readObject();
			in.close();
		} catch (IOException | ClassNotFoundException e) {
			//Its okay
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException l){
					l.printStackTrace();
				}
			}
		}
	}

	@Override
	public String getKeyword() {
		return "command";
	}

	@Override
	public String getName() {
		return "CommandCommand";
	}

	@Override
	public String getHelpReply() {
		return "Allows broadcaster to setup simple replys. Use 'command <add,set,remove> <keyword> <reply text...> ";
	}

}
