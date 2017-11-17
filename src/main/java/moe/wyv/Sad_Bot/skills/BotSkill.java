package moe.wyv.Sad_Bot.skills;

import java.util.regex.Pattern;

import moe.wyv.Sad_Bot.BotInstance;
import moe.wyv.Sad_Bot.Message;

public interface BotSkill {
	
	/**
	 * This method is used to enable or disable skills in the
	 * settings.
	 * 
	 * @return the name identifying the skill
	 */
	public String getName();
	
	/**
	 * Patterns should be a static final referenced to a compiled
	 * pattern that when matched will trigger the 
	 * {@link BotSkill#action(BotInstance, Message)}
	 * 
	 * @return a compiled regular expression
	 */
	public Pattern getPattern();
	
	/**
	 * The action to be performed after being triggered.
	 * 
	 * @param bot this
	 * @param message message that triggered the action
	 */
	public void action(BotInstance bot, Message message);
	
}
