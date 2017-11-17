package moe.wyv.Sad_Bot.web;

import java.io.IOException;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class GamesAPI {
	/**
	 * Private Steam API key
	 */
	private static final String STEAM_KEY = "881F322C98FAC4594BB711E659F1AEF1";
	
	private static final JsonParser PARSER = new JsonParser();
	
	/**
	 * Retrieves current information from the Steam web API. Records
	 * all information even if it is not used. 
	 * 
	 * Note: hasn't been tested
	 */
	public static HashMap<String, Integer> getSteamGameStats(String appId, String user64) {
		
		final String URL = "http://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v0002/?appid="+appId+"&key="+STEAM_KEY+"&steamid="+user64;
		HashMap<String, Integer> gameData = new HashMap<String, Integer>();
		
		try {
			String json = Web.httpGet(URL);

			JsonArray blob = PARSER.parse(json).getAsJsonObject()
					.getAsJsonObject("playerstats")
					.getAsJsonArray("stats");
			
			for (JsonElement stat : blob) {
				String name = stat.getAsJsonObject().get("name").getAsString();
				Integer value = stat.getAsJsonObject().get("value").getAsInt();
				gameData.put(name, value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return gameData;
	}
	
	/**
	 * @param name
	 * @return Steam64 ID
	 */
	public static String getSteam64(String name) {
		final String URL = "http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?key="+STEAM_KEY+"&vanityurl="+name;
		//.response.success == 1
		//	.response.steamid
		
		String id = null;
		try {
			String json = Web.httpGet(URL);

			JsonObject blob = PARSER.parse(json).getAsJsonObject()
					.getAsJsonObject("response");
			
			if (blob.get("success").getAsInt() == 1) {
				id = blob.get("steamid").getAsString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return id;
	}
	
	
}
