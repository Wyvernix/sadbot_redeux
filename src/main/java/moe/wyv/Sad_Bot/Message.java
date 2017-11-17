package moe.wyv.Sad_Bot;

/**
 * Storage class to hold information about a message received from the server
 * 
 * @author fettuccine
 *
 */
public class Message {
	private String channel;
	private String sender;
	private String message;

	/**
	 * @return #channel message sent in
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * @return username of user who sent message
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * @return message contents
	 */
	public String getMessage() {
		return message;
	}
	
	public Message(String channel, String sender, String message) {
		this.channel = channel;
		this.sender = sender;
		this.message = message;
	}
}
