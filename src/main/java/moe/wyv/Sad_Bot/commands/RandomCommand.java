package moe.wyv.Sad_Bot.commands;

import moe.wyv.Sad_Bot.BotInstance;
import moe.wyv.Sad_Bot.Message;

/**
 * Random will generate random positive integers. Future update will
 * pick users from chat. See {@link moe.wyv.Sad_Bot.web.TwitchAPI#getChatMeta(String)}
 * 
 * @author fettuccine
 *
 */
public class RandomCommand implements BotCommand {

	@Override
	public void activate(BotInstance bot, Message message) {
		String[] args = message.getMessage().split(" ");
		
		int lowerBound = 1;
		int upperBound = 6;
		
		//TODO pick random viewer
		//If 2 arguments, first is lowerBound and second is upperBound
		//If 1 arguments, argument is upperBound
		if (args.length > 1 && args[1].matches("\\d+")) {
			if (args.length > 2 && args[2].matches("\\d+")) {
				upperBound = Integer.parseInt(args[2]);
				lowerBound = Integer.parseInt(args[1]);
			} else {
				upperBound = Integer.parseInt(args[1]);
			}
		}
		
		//Rolls a number from lower to upper inclusive
		int rand = (int) (Math.random()*(upperBound-lowerBound+1)) + lowerBound;
		
		bot.send(message.getChannel(), "The die rolled a "+rand);
	}

	@Override
	public String getKeyword() {
		return "random";
	}

	@Override
	public String getName() {
		return "RandomCommand";
	}

	@Override
	public String getHelpReply() {
		return "Gets a random number from 1 to 10. Use 'random 100' or 'random 10 20' to change the range.";
	}

}
