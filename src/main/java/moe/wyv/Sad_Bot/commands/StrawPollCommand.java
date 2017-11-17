package moe.wyv.Sad_Bot.commands;

import moe.wyv.Sad_Bot.BotInstance;
import moe.wyv.Sad_Bot.Message;
import moe.wyv.Sad_Bot.web.Web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Doesn't work and I don't know where I was going with this. I may
 * change it into a skill that will blurb poll results after a few
 * minutes.
 * 
 * @author fettuccine
 *
 */
@SuppressWarnings("unused")
@Deprecated
public class StrawPollCommand implements BotCommand {

	/**
	 * URL to get stats depends on the id of the poll. The pollID is cat onto the end of this url.
	 * getUrl+id
	 */
	private static final String GETURL = "https://strawpoll.me/api/v2/polls/";
	
	/**
	 * URL to post is a single endpoint
	 */
	private static final String PUTURL = "https://strawpoll.me/api/v2/polls";
	
	private String lastPollID = "";
	
	
	
	@Override
	public void activate(BotInstance bot, Message message) {
		String[] args = message.getMessage().split(" ");
		
		if (args.length > 1 && bot.getUserForced(message.getSender()).isMod()) {
			//new poll
			
//			String args = 
			
		} else {
			//poll stats and link
			String[] poll = getPoll();
			if (poll[0] == null) return;
			
			String response = "Winner of "+poll[0]+" poll: "+poll[1]+" ["+poll[2]+" votes]";
			
			bot.send(message.getChannel(), response);
			
		}
	}
	
	/**
	 * Requires title and at least two choices
	 * 
	 * @param vars title and questions
	 */
	@SuppressWarnings("static-method")
	private void newPoll(String[] vars) {
		if (vars.length < 3) return;
		
		String title = vars[0];
		
		//building json element
		JsonObject master = new JsonObject();
		master.addProperty("title", title);
		JsonArray options = new JsonArray();
		for (int i = 1; i < vars.length-1; i++) {
			options.add(vars[i]);
		}
		master.add("options", options);
		
		System.out.println(master.toString());
		//TODO put poll
	}
	
	/**
	 * Returns the statistics from the last poll posted. Polls don't
	 * end, so the values may change over time.
	 * 
	 * @return array [ title, winner, votes ]
	 */
	private String[] getPoll() {
		if (lastPollID.isEmpty()) return new String[] {"", "", ""};
		
		String title = null;
		int max = 0;
		String winner = null;
		
		try {
			String json = Web.httpGet(GETURL+lastPollID);
			
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
	
	@Override
	public String getKeyword() {
		return "strawpoll";
	}

	@Override
	public String getName() {
		return "StrawPollCommand";
	}

	@Override
	public String getHelpReply() {
		return "Helps the broadcaster manage Straw Polls. Use 'strawpoll stuff' to do something?";
	}

}
