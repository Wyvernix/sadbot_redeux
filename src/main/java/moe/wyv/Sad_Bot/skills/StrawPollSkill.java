package moe.wyv.Sad_Bot.skills;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import moe.wyv.Sad_Bot.BotInstance;
import moe.wyv.Sad_Bot.Message;
import moe.wyv.Sad_Bot.web.Web;

public class StrawPollSkill implements BotSkill {

	/**
	 * URL to get stats depends on the id of the poll. The pollID is cat onto the end of this url.
	 * getUrl+id
	 */
	private static final String URL = "https://strawpoll.me/api/v2/polls/";
	private static final Pattern PATTERN = Pattern.compile(".*www\\.strawpoll.me\\/(\\d{8}).*");
	private Timer timer;
	
	public StrawPollSkill() {
		timer = new Timer();
	}
	
	@Override
	public String getName() {
		return "StrawPollSkill";
	}

	@Override
	public Pattern getPattern() {
		return PATTERN;
	}

	@Override
	public void action(BotInstance bot, Message message) {
		if (bot.getUserForced(message.getSender()).isMod()){
			//only if a mod posts link. Otherwise chat could be abused
			Matcher match = PATTERN.matcher(message.getMessage());
			match.find();
			
			String id = match.group(1);
			
		}
	}
	
	void schedule() {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
		}, 3*60*1000);
	}
	
	/**
	 * Returns the statistics from the last poll posted. Polls don't
	 * end, so the values may change over time.
	 * 
	 * @return array [ title, winner, votes ]
	 */
	private String[] getPoll(String pollID) {
		
		
		String title = null;
		int max = 0;
		String winner = null;
		
		try {
			String json = Web.httpGet(URL+pollID);
			
			JsonObject blob = new JsonParser().parse(json).getAsJsonObject();
			title = blob.get("title").getAsString();
			
			JsonArray options = blob.getAsJsonArray("options");
			JsonArray votes = blob.getAsJsonArray("votes");
			
			int index = 0;
			for (int i = 0; i < votes.size(); i++) {
				int strawVotes = votes.get(i).getAsInt();
				if (strawVotes > max) {
					max = strawVotes;
					index = i;
				}
			}
			
			winner = options.get(index).getAsString();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) { //catch everything except the kitchen sink
			e.printStackTrace();
		}
		
		return new String[]{ title, winner, Integer.toString(max)};
	}

}
