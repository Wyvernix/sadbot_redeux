package moe.wyv.Sad_Bot.commands;

import moe.wyv.Sad_Bot.BotInstance;
import moe.wyv.Sad_Bot.Message;

/**
 * Sample command
 * 
 * @author fettuccine
 *
 */
public class TimeCommand implements BotCommand {

	public void activate(BotInstance bot, Message message) {
		String time = new java.util.Date().toString();
        bot.send(message.getChannel(), message.getSender() + ": The time is now " + time);
	}

	public String getKeyword() {
		return "time";
	}

	@Override
	public String getName() {
		return "TimeCommand";
	}

	@Override
	public String getHelpReply() {
		return "Has the bot look at the clock on the wall and tell you what it says.";
	}

}
