package moe.wyv.Sad_Bot.skills;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import moe.wyv.Sad_Bot.BotInstance;
import moe.wyv.Sad_Bot.Message;
import moe.wyv.Sad_Bot.web.TwitchAPI;

/**
 * Skill will provide title and channel info about Twitch links.
 * 
 * @author fettuccine
 *
 */
public class TwitchSkill implements BotSkill {

	private static final Pattern PATTERN = Pattern.compile(".*?(clips)?.twitch.tv\\/(?:twitch\\/v\\/(\\d{9}))?.*");
	private static final Pattern CLIPS = Pattern.compile(".*clips.twitch.tv\\/(\\w*)(?:[^\\w]|$).*");
	private static final Pattern VODS = Pattern.compile(".*twitch.tv\\/(?:(?:twitch\\/v)|(?:videos))\\/(\\d{9}).*");
	
	@Override
	public String getName() {
		return "TwitchSkill";
	}

	@Override
	public Pattern getPattern() {
		return PATTERN;
	}

	@Override
	public void action(BotInstance bot, Message message) {
		if (message.getMessage().contains("clips")) {
			//is clip
			Matcher clips = CLIPS.matcher(message.getMessage());
			clips.find();
			
			String[] data = TwitchAPI.getClipMeta(clips.group(1));
			if (data[0] != null) {
				bot.send(message.getChannel(), "[TTV]["+data[1]+"] "+data[0]);
			}
		} else {
			//is video
			Matcher vods = VODS.matcher(message.getMessage());
			vods.find();
			
			String[] data = TwitchAPI.getVideoMeta(vods.group(1));
			if (data[0] != null) { 
				bot.send(message.getChannel(), "[TTV]["+data[2]+"] "+data[0]);
			}
		}
	}
	
}
