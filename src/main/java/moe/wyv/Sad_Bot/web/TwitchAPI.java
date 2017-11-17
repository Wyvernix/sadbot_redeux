package moe.wyv.Sad_Bot.web;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * note: channelID == userID most of the time
 * 
 * @author fettuccine
 *
 */
public abstract class TwitchAPI {
	
//	private static final int[] VALID_ERRORS = { 400, 401, 403, 404, 422, 429, 500, 503 };
	/**
	 * Private Twitch API key
	 */
	private static final String CLIENT_ID = "df0s6sdx3ef3g1o20y1ovrqi5f6lc7o";
	/**
	 * HTTP Headers
	 */
	private static final String[][] PROPERTIES = new String[][] { 
		new String[] { "Accept", "application/vnd.twitchtv.v5+json" },
		new String[] { "Client-ID", CLIENT_ID }
	};
	private static final JsonParser PARSER = new JsonParser();
	
	
	/**
	 * @param slug strange blob of words
	 * @return array [ title, channel ] 
	 */
	public static String[] getClipMeta(String slug) {
		String title = null;
		String channel = null;
//		double duration = -1;
//		int views = -1;
		
		try {
			String json = Web.httpGet("https://api.twitch.tv/kraken/clips/"+slug, PROPERTIES);
			
			JsonObject blob = PARSER.parse(json).getAsJsonObject();
			title = blob.has("title") ? blob.get("title").getAsString() : null;
			channel = blob.getAsJsonObject("broadcaster").get("display_name").getAsString();
			
//			duration = blob.get("duration").getAsDouble();
//			views = blob.get("views").getAsInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new String[]{ title, channel };
	}
	
	/**
	 * Returns data about live stream. Array contains: [ viewers,
	 * average FPS, stream delay ]. If the channel is offline, an
	 * empty array is returned. 
	 * 
	 * @param channelID Twitch ID for broadcaster
	 * @return array [viewers, FPS, delay] or [] if offline
	 */
	public static int[] getStreamMeta(String channelID) {
		
		int viewers = -1;
		int averageFps = -1;
		int delay = -1;
		
		try {
			String json = Web.httpGet("https://api.twitch.tv/kraken/streams/"+channelID, PROPERTIES);
			
			JsonObject blob = PARSER.parse(json).getAsJsonObject();
			if (blob.get("stream").isJsonNull()) {
				return new int[0];
			}
			blob = blob.getAsJsonObject("stream");
			viewers = blob.get("viewers").getAsInt();
			averageFps = blob.get("average_fps").getAsInt();
			delay = blob.get("delay").getAsInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new int[]{ viewers, averageFps, delay };
	}
	
	/**
	 * @param id video id
	 * @return array of [ title, description, channel ]
	 */
	public static String[] getVideoMeta(String id) {
		
		String title = null;
		String description = null;
		String channel = null;
		
		try {
			String json = Web.httpGet("https://api.twitch.tv/kraken/videos/"+id, PROPERTIES);
			
			JsonObject blob = PARSER.parse(json).getAsJsonObject();
			title = blob.has("title") ? blob.get("title").getAsString() : null;
			description = blob.has("description") ? blob.get("description").getAsString() : null;
			channel = blob.getAsJsonObject("channel").get("display_name").getAsString();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		return new String[]{title, description, channel};
	}
	
	/**
	 * Format is 2017-05-24T02:26:43Z
	 * 
	 * @param id id of user
	 * @param channelID id of target channel
	 * @return Date user followed channel. {@code null} if user
	 * 		doesn't follow channel or other error occurred.
	 */
	public static String getUserFollowsChannelMeta(String id, String channelID) {
		String date = null;
		if (id.equals(channelID)) { //can't follow yourself :(
			return null;
		}
		
		try {
			String json = Web.httpGet("https://api.twitch.tv/kraken/users/"+id+"/follows/channels/"+channelID, PROPERTIES);
			
			JsonObject blob = PARSER.parse(json).getAsJsonObject();
			if (blob.has("error")) {
				return null;
			}
			date = blob.has("created_at") ? blob.get("created_at").getAsString() : null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
	/**
	 * @param username username of user
	 * @return array of [ userId, display_name ]
	 */
	public static String[] getUserMeta(String username) {
		String id = null;
		String displayName = null;
		
		try {
			String json = Web.httpGet("https://api.twitch.tv/kraken/users?login="+username, PROPERTIES);
			
			JsonObject blob = PARSER.parse(json).getAsJsonObject().getAsJsonArray("users").get(0).getAsJsonObject();
			
			id = Integer.toString(blob.get("_id").getAsInt());
			displayName = blob.get("display_name").getAsString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new String[]{id, displayName};
	}
	
	/**
	 * Old method don't use?
	 * 
	 * @param username name to search
	 * @return array [ displayname, game, steamid ]
	 */
	public static String[] getChannelMetaDeprecated(String username) {
		String displayName = null;
		String game = null;
		String steam = null;
		
		try {
			String json = Web.httpGet("https://api.twitch.tv/api/channels/"+username, PROPERTIES);
			
			JsonObject blob = PARSER.parse(json).getAsJsonObject();
			
			displayName = blob.get("display_name").getAsString();
			game = blob.get("game").getAsString();
			JsonElement steamblob = blob.get("steam_id");
			if (steamblob.isJsonPrimitive()) {
				steam = steamblob.getAsString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return new String[]{displayName, game, steam};
	}
	
	/**
	 * Retrieves channel chat users. Currently only retrieves
	 * viewers and mods. This is a problem because Global Mods and
	 * Admins are in their own groups. (An extremely rare problem) 
	 * 
	 * @param channelName username of broadcaster
	 * @return array of [ viewers[], moderators[] ]
	 */
	public static String[][] getChatMeta(String channelName) {
		
		String[] moderators = null;
		String[] viewers = null;
		
		try {
			String json = Web.httpGet("https://tmi.twitch.tv/group/user/"+channelName+"/chatters");
			
			JsonObject blob = PARSER.parse(json).getAsJsonObject().getAsJsonObject("chatters");
			
			JsonArray mods = blob.getAsJsonArray("moderators");
			moderators = new String[mods.size()];
			for (int i = 0; i < moderators.length; i++) {
				moderators[i] = mods.get(i).getAsString();
			}
			JsonArray views = blob.getAsJsonArray("viewers");
			viewers = new String[views.size()];
			for (int i = 0; i < viewers.length; i++) {
				viewers[i] = views.get(i).getAsString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return new String[][]{ viewers, moderators };
	}
}
