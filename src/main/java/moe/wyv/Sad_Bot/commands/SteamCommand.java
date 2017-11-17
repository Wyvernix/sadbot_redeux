package moe.wyv.Sad_Bot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import moe.wyv.Sad_Bot.BotInstance;
import moe.wyv.Sad_Bot.Message;
import moe.wyv.Sad_Bot.User;
import moe.wyv.Sad_Bot.web.GamesAPI;
import moe.wyv.Sad_Bot.web.TwitchAPI;

public class SteamCommand implements BotCommand {
	
	private static final Pattern PATTERN = Pattern.compile(".*steamcommunity.com\\/(?:(?:id\\/(\\w+))|(?:profiles\\/(\\d{17}))).*");

	@Override
	public void activate(BotInstance bot, Message message) {
		// TODO change to formal command with less ambiguity
		
		User user = bot.getUserForced(message.getSender());
		//no args:
		//search twitchapi for steam
		//if they have a steam set, print it
		//else complain linkpls
		//
		//args
		//try to test if regex matches link
		//	if link works, use it
		//	else complain invalid
		//else complain invalid
		
		String[] args = message.getMessage().split(" ");
		if (args.length > 1) {
			//regex link?
			Matcher match = PATTERN.matcher(args[1]);
			if (match.find()) {
				if (match.group(1).isEmpty()) {
					//is pattern, use it
					bot.getUserForced(message.getSender()).set("SteamID", match.group(2));
					return;
				}
				String id = GamesAPI.getSteam64(match.group(1));
				if (id != null) {
					user.set("SteamID", id);
					return;
				} 
			}
			
			//link did not match regex or could not find id
			bot.sendMessage(message.getChannel(), "Invalid link. Sorry.");
		} else {
			boolean hasId = user.has("SteamID");
			if (!user.has("SteamID")) {
				String idTest = searchTwitch(message.getSender());
				if (idTest != null) {
					user.set("SteamID", idTest);
					hasId = true;
				}
			}
			
			if (hasId) {
				String id = (String) user.get("SteamID");
				bot.sendMessage(message.getChannel(), "Your steam id is: "+id);
			} else {
				bot.sendMessage(message.getChannel(), "You do not have a Steam account set. Please link to your Steam account");
			}
		}
	}

	private static String searchTwitch(String username) {
		String[] usermeta = TwitchAPI.getChannelMetaDeprecated(username);
		return usermeta[2];
	}

	@Override
	public String getKeyword() {
		return "steam";
	}

	@Override
	public String getName() {
		return "SteamCommand";
	}

	@Override
	public String getHelpReply() {
		return "Find your steam id by linking your steam community url. e.g. 'steam https://steamcommunity.com/id/xxtryhardxx'";
	}

}
