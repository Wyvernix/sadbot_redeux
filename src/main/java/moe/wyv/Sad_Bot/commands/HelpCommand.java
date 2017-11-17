package moe.wyv.Sad_Bot.commands;

import java.util.List;

import moe.wyv.Sad_Bot.BotInstance;
import moe.wyv.Sad_Bot.Message;

public class HelpCommand implements BotCommand {

	@Override
	public void activate(BotInstance bot, Message message) {
		String[] args = message.getMessage().split(" ");
		
		if (args.length == 1) {
			//!help
			bot.send(message.getChannel(), "Need help? Use 'help <command>' to get help for a specific command or 'help list' to list all available commands.");
		} else {
			List<BotCommand> list = bot.getSettings().getEnabledCommands();
			if (args[1].equals("list")) {
				//!help list - list all enabled commands
				StringBuffer commandList = new StringBuffer();
				
				for (BotCommand command : list) {
					commandList.append(command.getKeyword()).append(", ");
				}
				bot.send(message.getChannel(), "Enabled commands: "+commandList.substring(0, commandList.length()-2));
				
			} else {
				//lookup command help
				
				for (BotCommand command : list) {
					if (command.getKeyword().equals(args[1])) {
						//found it!
						bot.send(message.getChannel(), command.getName()+": "+command.getHelpReply());
						return;
					}
				}
				
				//didn't find it
				bot.send(message.getChannel(), "That command does not have a help document");
			}
		}
	}

	@Override
	public String getKeyword() {
		return "help";
	}

	@Override
	public String getName() {
		return "HelpCommand";
	}

	@Override
	public String getHelpReply() {
		return "Gets help information on various parts of the bot.";
	}

}
