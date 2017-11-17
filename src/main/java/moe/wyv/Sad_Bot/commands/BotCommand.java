package moe.wyv.Sad_Bot.commands;

import moe.wyv.Sad_Bot.BotInstance;
import moe.wyv.Sad_Bot.Message;

/**
 * Interface outlining the structure of a command
 * 
 * @author fettuccine
 *
 */
public interface BotCommand {

	/**
	 * The action that is taken after being triggered by the
	 * keyword.
	 * 
	 * @param bot this
	 * @param message the message to process
	 */
	public void activate(BotInstance bot, Message message);
	
	/**
	 * This method should return a constant String that
	 * is more or less the "name" of the command. The
	 * name should be formatted without the preceeding
	 * prefix "!" and should not have any spaces.
	 * 
	 * E.g. "quit"
	 * 
	 * @return the word required to activate this command
	 */
	public String getKeyword();
	
	/**
	 * Returns the name representation of the command to be used in
	 * the save file. This should be the same as the class name.
	 * 
	 * @return name of command
	 */
	public String getName();
	
	/**
	 * Used to explain how to use the command. Should be an English
	 * sentence.
	 * 
	 * @return a message explaining the use of the command
	 */
	public String getHelpReply();
	
}
