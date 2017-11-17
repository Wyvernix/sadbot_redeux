package moe.wyv.Sad_Bot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import moe.wyv.Sad_Bot.web.TwitchAPI;

/**
 * Contains all the statistics on viewers. This class currently uses
 * Java data structures, but will eventually be changed to use a SQL
 * database. The methods reflect this.
 * 
 * @author fettuccine
 *
 */
public class Channel {
	private String filename;
	private Map<String, User> users;
	private String channelID;
	
	/**
	 * Current users in chat
	 */
	private Set<String> chatters = new HashSet<String>();
	/**
	 * List of users who have talked since last checked. Users are
	 * mapped to number of chat lines.
	 */
	private Map<String, Integer> chatLines = new HashMap<String, Integer>();
	
	/**
	 * Tries to load database from file. Also retrieves the
	 * channel's Twitch ID from the API.
	 * 
	 * @param channel broadcaster username
	 */
	public Channel(String channel) {
		filename = channel+"Channel.dat";
		if (!loadUserDatabase()) {
			users = new HashMap<String, User>();
		}
		
		channelID = TwitchAPI.getUserMeta(channel.substring(1))[0]; 
		Logger.getInstance().log("Channel id: "+channelID);
	}
	
	/**
	 * Updates user statistics for all current chatters in channel.
	 * This includes view time if the broadcaster is online.
	 * Updates and resets the chat messages list.
	 */
	public void updateChatters() {
		//clear chat list
		Map<String, Integer> oldChatLines = chatLines;
		chatLines = new HashMap<String, Integer>();
		
		Calendar now = Calendar.getInstance();
		int day = now.get(Calendar.DAY_OF_WEEK);
		boolean isLive = TwitchAPI.getStreamMeta(channelID).length > 0;
		
		if (!oldChatLines.isEmpty()) {
			Logger.getInstance().log("Update: "+oldChatLines.keySet()+" from "+chatters);
		}
		
		
		User user;
		for (String username : chatters) {
			user = getUserForced(username);
			
			if (oldChatLines.containsKey(username)) {
				user.updateUser(oldChatLines.get(username), now, day, isLive);
			} else {
				user.updateUser(0, now, day, isLive);
			}
		}
		
		saveUserDatabase();
	}
	
	/**
	 * Called when a user sends a message to chat. If the user is
	 * not in the current list of chatters, they are added. Adds 1
	 * to the user's message statistic.
	 * 
	 * @param username username of user
	 */
	public void userMessage(String username) {
		Integer previous = chatLines.get(username);
		if (previous == null) {
			chatLines.put(username, 1);
		} else {
			chatLines.put(username, previous+1);
		}
		
		userJoin(username);
	}
	
	/**
	 * Adds user to list of current chatters
	 * 
	 * @param username name of user
	 */
	public void userJoin(String username) {
		chatters.add(username);
	}
	
	/**
	 * Updates user statistics and removes them from list of current chatters
	 * 
	 * @param username name of user
	 */
	public void userPart(String username) {
		User user = getUserForced(username);
		if (chatLines.containsKey(username)) {
			user.updateUser(chatLines.get(username), false);
			chatLines.remove(username);
		} else {
			user.updateUser(0, false);
		}
		chatters.remove(username);
	}
	
	/**
	 * Sets the moderator flag for a user
	 * 
	 * @param username name of user
	 */
	public void addMod(String username) {
		User user = getUserForced(username);
		user.setMod(true);
	}
	
	/**
	 * Retrieves a user from the database, even if they don't exist.
	 * A new user entry will be created. CAREFUL! Most of the time
	 * we need to get a user, we know the user is in the chat for
	 * one reason or another. The only time this shouldn't be used
	 * is if one user is looking for information about another user.
	 * 
	 * @param username name of user
	 * @return user if they exist, otherwise generates a new user
	 */
	public User getUserForced(String username) {
		User user = users.get(username);
		if (user == null) {
			user = addUser(username);
		}
		return user;
	}
	
	/**
	 * Simple boolean to check if the database has info on the user.
	 * 
	 * @param username username of user
	 * @return {@code true} if information is found, {@code false} otherwise
	 */
	public boolean hasUser(String username) {
		return users.containsKey(username);
	}
	
	/**
	 * @return the Twitch channel ID for the broadcaster
	 */
	public String getChannelID() {
		return channelID;
	}
	
	/**
	 * Creates a new user information set and adds them to the database
	 * 
	 * @param name username
	 * @return the new user
	 */
	private User addUser(String name) {
		Logger.getInstance().log("New user: "+name);
		User user = new User(name);
		users.put(name.toLowerCase(), user);
		return user;
	}
	
	/**
	 * Returns an array of users sorted by name.
	 * 
	 * @return AAA to ZZZ
	 */
	public User[] sortByName() {
		User[] usernames = users.values().toArray(new User[0]);
		Arrays.sort(usernames, User.userNameComparator);
		return usernames;
	}
	
	/**
	 * Returns an array of users sorted by oldest first.
	 * 
	 * @return Oldest to Newest
	 */
	public User[] sortByAge() {
		User[] ages = users.values().toArray(new User[0]);
		Arrays.sort(ages, User.userAgeComparator);
		return ages;
	}
	
	
	/**
	 * Loads data to file
	 * @return if database exists
	 */
	@SuppressWarnings("unchecked")
	private boolean loadUserDatabase() {
		Logger.getInstance().log("Loading user data...");
		ObjectInputStream ois = null;

		boolean loaded = false;
		try {
			ois = new ObjectInputStream(new FileInputStream(filename));
			users = (Map<String, User>) ois.readObject();
			
			//TODO upgrade database
			for (User user : users.values()) {
				user.checkUpgrade();
			}
			loaded = true;
		} catch (FileNotFoundException e) {
			//File DNE, let program handle it
			Logger.getInstance().log("New database.");
		} catch(IOException e) {
			System.err.println("Unable to read filesystem: "+e.getMessage());
		} catch (ClassNotFoundException e) {
			System.err.println("Database corrupted: "+e.getMessage());
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return loaded;
	}

	/**
	 * Saves data to file
	 */
	private void saveUserDatabase() {
//		System.out.println("Database saved.");
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(filename));
			oos.writeObject(users);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					// how i feel right now
					e.printStackTrace();
				}
			}
		}
	}
	
}
