package moe.wyv.Sad_Bot.commands;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import moe.wyv.Sad_Bot.BotInstance;
import moe.wyv.Sad_Bot.Logger;
import moe.wyv.Sad_Bot.Message;
import moe.wyv.Sad_Bot.web.Web;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Endpoints:
 * master = .Stats[0].Stats[]
 * Map stat = {.label, .displayValue }
 * 
 * "K/D Ratio"
 * "Time Survived"
 * "Rounds Played"
 * "Kills"
 * "Headshot Kill Ratio"
 * "Move Distance" meters
 * "Longest Time Survived"
 * "Avg Survival Time"
 * https://pubgtracker.com/site-api
 * 
 * @author fettuccine
 *
 */
public class PubgCommand implements BotCommand {
	
//	private static final String STEAM_ID = "76561198078981297";
//	private static final String PROFILE_URL = "https://pubgtracker.com/api/search?steamId="+STEAM_ID;
	/**
	 * Found using https://pubgtracker.com/api/search?steamId=76561198078981297. 
	 * Steam64 ID found using http://www.steam64.com/.
	 */
	private static final String URL = "https://pubgtracker.com/api/profile/pc/ActiveEnergy";
	/**
	 * Private pubgtracker.com API key
	 */
	private static final String KEY = "c337207b-47d2-439d-bafb-00e898e6717b";
	
	/**
	 * HTTP Headers
	 */
	private static final String[][] PROPERTIES = new String[][] { 
		new String[] { "TRN-Api-Key", KEY }
	};
	
	/**
	 * There is a large amount of information recorded by the game.
	 * This data structure contains all of it.
	 */
	private Map<String, Double> gameData = new HashMap<String, Double>();
	
	/**
	 * Last time in milliseconds that the data was updated
	 */
	private long lastChecked = 0;
	
	public PubgCommand() {
//		updateStats();
	}

	@Override
	public void activate(BotInstance bot, Message message) {
		//Update stats every five minutes
		if (System.currentTimeMillis() - lastChecked > 8*60*1000) {
			updateStats();
		}
		
		double kills = gameData.get("Kills");
		double kd  = gameData.get("K/D Ratio");
		double timePlayed = gameData.get("Time Survived") / 3600.0;
		double rounds = gameData.get("Rounds Played");
		double survival = gameData.get("Avg Survival Time") / 60.0;
		double walked = gameData.get("Move Distance");
		
		String sending = String.format("Kills: %.0f, K/D: %.2f, Survived: %.1f hours, Rounds: %.0f, Avg survival: %.0f minutes, Distance walked: %.0f meters", 
				kills, kd, timePlayed, rounds, survival, walked);
		bot.send(message.getChannel(), sending);
	}
	
	private void updateStats() {
		Logger.getInstance().log("Updating PUBG stats");
		try {
			String json = Web.httpGet(URL, PROPERTIES);

			JsonArray blob = new JsonParser().parse(json).getAsJsonObject()
					.getAsJsonArray("Stats").get(0).getAsJsonObject()
					.getAsJsonArray("Stats");
			
			for (JsonElement stat : blob) {
				String name = stat.getAsJsonObject().get("label").getAsString();
				String value = stat.getAsJsonObject().get("displayValue").getAsString();
				gameData.put(name, Double.parseDouble(value.replaceAll(",", "")));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		lastChecked = System.currentTimeMillis();
	}

	@Override
	public String getKeyword() {
		return "pubg";
	}

	@Override
	public String getName() {
		return "PubgCommand";
	}

	@Override
	public String getHelpReply() {
		return "pubg will display some of our stats from PLAYERUNKNOWN'S BATTLEFIELD";
	}

}
