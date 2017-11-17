package moe.wyv.Sad_Bot.commands;

import java.util.HashMap;

import moe.wyv.Sad_Bot.BotInstance;
import moe.wyv.Sad_Bot.Logger;
import moe.wyv.Sad_Bot.Message;
import moe.wyv.Sad_Bot.User;
import moe.wyv.Sad_Bot.web.GamesAPI;

/**
 * points:
 * 
 * kills = total_kills
 * k/d = kills / total_deaths
 * time played = total_time_played / 3600
 * win% = total_wins / total_rounds_played * 100
 * accuracy = total_shots_hit / total_shots_fired
 * headshot% = total_kills_headshot / %kills
 * mvp = total_mvps
 * https://developer.valvesoftware.com/wiki/Steam_Web_API#GetGlobalAchievementPercentagesForApp_.28v0001.29
 * 
 * @author fettuccine
 *
 */
public class CSGOCommand implements BotCommand {
	
	/**
	 * Default id found using http://www.steam64.com/
	 */
	private static final String DEFAULT_ID = "76561198078981297";
//	/**
//	 * Private Steam API key
//	 */
//	private static final String KEY = "881F322C98FAC4594BB711E659F1AEF1";
//	private static final String URL = "http://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v0002/?appid=730&key="+KEY+"&steamid="+STEAM_ID;
	
//	/**
//	 * Contains all of Steam's recorded statistics for a player.
//	 * Steam records a large amount of information.
//	 */
//	private Map<String, Integer> gameData = new HashMap<String, Integer>();
//	/**
//	 * Keeps track of last time data was updated. This is
//	 * represented as the time in milliseconds.
//	 */
//	private long lastChecked = 0;
	
	private long lastCall = 0;
	
	public CSGOCommand() {
//		updateStats();
	}
	
	@Override
	public void activate(BotInstance bot, Message message) {
		//Rate limit every 10 seconds
		if (System.currentTimeMillis() - lastCall < 10*1000) {
			return;
		}
		lastCall = System.currentTimeMillis();
		
//		//Update info every five minutes
//		if (System.currentTimeMillis() - lastChecked > 5*60*1000) {
//			updateStats();
//		}
		
		HashMap<String, Integer> gameData;
		String[] args = message.getMessage().split(" ");
		String username;
		String userId;
		
		if (args.length > 1) {
			username = args[1];
			if (!bot.hasUser(username)) {
				bot.sendMessage(message.getChannel(), "We do not have information on that user.");
				return;
			}
			User user = bot.getUserForced(username);
			if (user.has("SteamID")) {
				userId = (String) user.get("SteamID");
			} else {
				bot.sendMessage(message.getChannel(), "User has not set their steam account with the $steam command.");
				return;
			}
		} else {
			username = message.getSender();
			User user = bot.getUserForced(username);
			if (user.has("SteamID")) {
				userId = (String) user.get("SteamID");
			} else {
				username = message.getChannel().substring(1);
				userId = DEFAULT_ID;
			}
		}
		
		gameData = getData(userId);
		
		double kills = gameData.get("total_kills");
		double kd = kills / gameData.get("total_deaths");
		double playHours = gameData.get("total_time_played") / 3600.0;
		double wins = gameData.get("total_wins") / (double) gameData.get("total_rounds_played") * 100.0;
		double accuracy = gameData.get("total_shots_hit") / (double) gameData.get("total_shots_fired") * 100.0;
		double headshots = gameData.get("total_kills_headshot") / kills * 100.0;
		double mvp = gameData.get("total_mvps");
		
		String sending = String.format("[%s] Kills: %.0f, K/D: %.2f, Played: %.0f hours, Win percent: %.1f%%, Accuracy: %.1f%%, Headshot percent %.0f%%, MVPs: %.0f",
				username, kills, kd, playHours, wins, accuracy, headshots, mvp);
		
		bot.send(message.getChannel(), sending);
	}
	
	/**
	 * Retrieves current information from the Steam web API. Records
	 * all information even if it is not used.
	 */
	private static HashMap<String, Integer> getData(String userId) {
		Logger.getInstance().log("Updating CS:GO stats");
		return GamesAPI.getSteamGameStats("730", userId);
	}

	@Override
	public String getKeyword() {
		return "csgo";
	}

	@Override
	public String getName() {
		return "CSGOCommand";
	}

	@Override
	public String getHelpReply() {
		return "csgo will display some of our CS:GO stats";
	}

}
