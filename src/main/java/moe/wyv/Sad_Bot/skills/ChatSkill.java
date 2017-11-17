package moe.wyv.Sad_Bot.skills;

import java.util.regex.Pattern;

import moe.wyv.Sad_Bot.BotInstance;
import moe.wyv.Sad_Bot.Message;
import moe.wyv.Sad_Bot.ai.MegaDon;

public class ChatSkill implements BotSkill {
	
	private static final Pattern PATTERN = Pattern.compile(".*?(\\w*bot)\\b.*", Pattern.CASE_INSENSITIVE);

	@Override
	public Pattern getPattern() {
		return PATTERN;
	}

	@Override
	public String getName() {
		return "ChatSkill";
	}

	@Override
	public void action(BotInstance bot, Message message) {
		if (message.getMessage().split(" ").length < 4) {
			bot.send(message.getChannel(), 
					mimic(message.getMessage(), bot.getName(), message.getSender()));
			return;
		}
		
		MegaDon ai = bot.getAi();
		
		String response = ai.getResponse(message.getMessage(), message.getSender());
		
		bot.send(message.getChannel(), response);
		
	}
	
	private static String mimic(String message, String botname, String sender) {
		int start = message.toLowerCase().indexOf(botname.toLowerCase());
		int end = botname.length()+start;
		String aa  = message.substring(0, start);
		String bb = message.substring(end);
		
		return aa+sender+bb;
	}

}
