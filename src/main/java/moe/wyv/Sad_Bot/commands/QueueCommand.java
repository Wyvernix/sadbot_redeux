package moe.wyv.Sad_Bot.commands;

import java.util.ArrayList;
import java.util.List;

import moe.wyv.Sad_Bot.BotInstance;
import moe.wyv.Sad_Bot.Message;
import moe.wyv.Sad_Bot.User;

public class QueueCommand implements BotCommand {

	/**
	 * Queue data structure implemented using ArrayList
	 */
	private List<String> queue = new ArrayList<String>();
	
	@Override
	public void activate(BotInstance bot, Message message) {
		String args[] = message.getMessage().split(" ");
		
		User user = bot.getUserForced(message.getSender());
		
		if (user.isMod()) {
			if (args.length > 1 && args[1].matches("clear|reset")) {
				//!queue reset - clears queue
				queue = new ArrayList<String>();
				bot.send(message.getChannel(), "Queue has been cleared!");
			} else {
				//!queue - gets user from queue
				if (!queue.isEmpty()) {
					bot.send(message.getChannel(), queue.remove(0) + " is first in the queue!");
				} else {
					bot.send(message.getChannel(), "Queue is empty!!!");
				}
			}
		} else {
			//!queue - adds self to queue
			if (!queue.contains("user")) {
				queue.add("user");
			}
		}
	}

	@Override
	public String getKeyword() {
		return "queue";
	}

	@Override
	public String getName() {
		return "QueueCommand";
	}

	@Override
	public String getHelpReply() {
		return "queue will allow viewers to join a queue. Use 'queue clear' to reset the queue.";
	}

}
