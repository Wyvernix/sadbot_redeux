package moe.wyv.Sad_Bot.commands;

import moe.wyv.Sad_Bot.BotInstance;
import moe.wyv.Sad_Bot.Message;

/**
 * Forces the bot to disconnect from the IRC server. User data is
 * saved automatically.
 * 
 * @author fettuccine
 *
 */
public class QuitCommand implements BotCommand {

	public String getKeyword() {
		return "quit";
	}

	public void activate(BotInstance bot, Message message) {
		if (bot.getUserForced(message.getSender()).isMod()) { 
			bot.quit();
		}
	}

	@Override
	public String getName() {
		return "QuitCommand";
	}

	@Override
	public String getHelpReply() {
		return "Disconnects the bot";
	}

}
