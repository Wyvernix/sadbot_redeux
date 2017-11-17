package moe.wyv.Sad_Bot.commands;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import moe.wyv.Sad_Bot.BotInstance;
import moe.wyv.Sad_Bot.Message;
import moe.wyv.Sad_Bot.User;

/**
 * Retrieves user information from database. This class also
 * demonstrates what data can be retrieved from the database
 * for use elsewhere.
 * 
 * @author fettuccine
 *
 */
public class UserCommand implements BotCommand {

	@Override
	public void activate(BotInstance bot, Message message) {
		if (!bot.getUserForced(message.getSender()).isMod()) return;
		
		String locate = null;
		
		//If a username isn't given, use the sender's
		if (message.getMessage().indexOf(' ') > -1) {
			locate = message.getMessage().split(" ")[1].toLowerCase();
		} else {
			locate = message.getSender();
		}
		
		//This is important so a new user is not created.
		if (!bot.hasUser(locate)) {
			bot.send(message.getChannel(), "User not found.");
			return;
		}
		
		User user = bot.getUserForced(locate);
		
		Calendar age = user.getBirth();
		int lines = user.getChatLines();
		String followDate = user.getFollowDate(bot.getChannelID());
		String day = user.getMostCommonDay();
		String userid = user.getUserID();
		Calendar last = user.getLastSeen();
		String username = user.getDisplayName();
		int watchedTime = user.getWatchedTime();
		
		SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
		
		String sending = String.format("User %s:%s was first seen %s, and last seen %s. They are usually seen on %ss and have sent %d chat messages during %d minutes.", 
				username, userid, format.format(age.getTime()), format.format(last.getTime()), day, lines, watchedTime);
		if (followDate != null) {
			sending += " Follower since "+followDate;
		}
		
		bot.send(message.getChannel(), sending);
	}

	@Override
	public String getKeyword() {
		return "user";
	}

	@Override
	public String getName() {
		return "UserCommand";
	}

	@Override
	public String getHelpReply() {
		return "Gives statistics on many users. Use 'user Kappa' to lookup specific users by name.";
	}

}
