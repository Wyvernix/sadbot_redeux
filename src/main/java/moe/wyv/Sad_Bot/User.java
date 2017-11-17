package moe.wyv.Sad_Bot;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import moe.wyv.Sad_Bot.web.TwitchAPI;

/**
 * This class is a wrapper for a set of properties contained in a
 * map. In the future, this will be stored in an external database
 * like SQL. 
 * 
 * @author fettuccine
 *
 */
public class User implements Serializable {
	private static final long serialVersionUID = 7493310343555944330L;
	private static final String[] WEEK = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
	
	/**
	 * All user data is stored here
	 */
	private Map<String, Object> properties;
	//contains:
	//	private transient String displayName;
	//	private transient String userID;
	//	private transient Calendar birth;
	//	private transient int[] workWeek;
	//	private transient Calendar lastSeen;
	//	private transient boolean isMod;
	//	private transient int chatLines;
	//	private transient int watchedTime;

	
	/**
	 * Upgrade user data to current version
	 */
	public void checkUpgrade() {
		Integer version = (Integer) properties.get("VERSION");
		
		if (version < 2) {
			Logger.getInstance().log("Updating user: "+getDisplayName());
			switch (version) {
			case 1:
				setWatchedTime(0);
				//$FALL-THROUGH$
			default:
				properties.put("VERSION", 2);
				break;
			}
		}
	}
	
	/**
	 * Populates data with default user values for a new user at this local time.
	 * 
	 * @param username name of user
	 */
	public User(String username) {
		properties = new HashMap<String, Object>();
		
		String[] userMeta = TwitchAPI.getUserMeta(username);
		Calendar birth = Calendar.getInstance();
		int[] workWeek = new int[7]; 
		++workWeek[birth.get(Calendar.DAY_OF_WEEK)-1];
		
		setUserID(userMeta[0]);
		setDisplayName(userMeta[1]);
		setBirth(birth);
		setWorkWeek(workWeek);
		setLastSeen(birth);
		setMod(false);
		setChatLines(0);
		setWatchedTime(0);
		
		properties.put("VERSION", 2);
	}
	
	
	/**
	 * Overload to update a user's information with the current time.
	 * See {@link User#updateUser(int, Calendar, int, boolean)}
	 * 
	 * @param chatLines number of messages to add
	 * @param isLive if the broadcaster is streaming
	 */
	public void updateUser(int chatLines, boolean isLive) {
		Calendar now = Calendar.getInstance();
		updateUser(chatLines, now, now.get(Calendar.DAY_OF_WEEK), isLive);
	}
	
	/**
	 * Updates user information with number of chat lines and last
	 * seen time. Use this method if many users are being updated at
	 * the same time so only one Calendar is created.
	 * 
	 * @param chatLines number of messages to add
	 * @param now ({@code Calendar.getInstance()}
	 * @param dayOfWeek day of week represented as a number. Sunday = 0. Use {@code calendar.get(Calendar.DAY_OF_WEEK)}
	 * @param isLive if the broadcaster is streaming
	 */
	public void updateUser(int chatLines, Calendar now, int dayOfWeek, boolean isLive) {
		if (getLastSeen().get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
			int[] workWeek = getWorkWeek();
			++workWeek[dayOfWeek-1];
			setWorkWeek(workWeek);
		}
		if (chatLines > 0) {
			setChatLines(getChatLines() + chatLines);
		}
		if (isLive) {
			setWatchedTime(getWatchedTime()+1);
		}
		setLastSeen(now);
	}
	
	/**
	 * Does user follow channel? If there is follow data, yes.
	 * 
	 * @param channelID this channel
	 * @return {@code true} if user follows channel
	 */
	public boolean followsChannel(String channelID) {
		return getFollowDate(channelID) != null;
	}
	
	/**
	 * @param channelID user id of broadcaster
	 * @return Date user followed channel. {@code null} if user
	 * doesn't follow channel or other error occurred.
	 */
	public String getFollowDate(String channelID) {
		Object followDate = properties.get("followDate");
		if (followDate != null) {
			return (String) followDate;
		}
		String newDate = TwitchAPI.getUserFollowsChannelMeta(getUserID(), channelID);
		if (newDate != null) {
			properties.put("followDate", newDate);
			return newDate;
		}
		return null;
	}
	
	/**
	 * @return day of the week when the user is usually here
	 */
	public String getMostCommonDay() {
		int maxday = 0;
		int[] workWeek = getWorkWeek();
		for (int i = 1; i < WEEK.length; i++) {
			if (workWeek[i] > workWeek[maxday]){
				maxday = i;
			}
		}
		return WEEK[maxday];
	}
	
	public synchronized Object get(String property) {
		return properties.get(property);
	}
	
	public synchronized void set(String property, Object value) {
		properties.put(property, value);
	}
	
	public synchronized boolean has(String property) {
		return properties.containsKey(property);
	}
	
	public synchronized String getDisplayName() {
		return (String) properties.get("displayName");
	}

	private synchronized void setDisplayName(String displayName) {
		properties.put("displayName", displayName);
	}

	public synchronized String getUserID() {
		return (String) properties.get("userID");
	}

	private synchronized void setUserID(String userID) {
		properties.put("userID", userID);
	}

	public synchronized Calendar getBirth() {
		return (Calendar) properties.get("birth");
	}

	private synchronized void setBirth(Calendar birth) {
		properties.put("birth", birth);
	}

	private synchronized int[] getWorkWeek() {
		return (int[]) properties.get("workWeek");
	}

	private synchronized void setWorkWeek(int[] workWeek) {
		properties.put("workWeek", workWeek);
	}

	public synchronized Calendar getLastSeen() {
		return (Calendar) properties.get("lastSeen");
	}

	private synchronized void setLastSeen(Calendar lastSeen) {
		properties.put("lastSeen", lastSeen);
	}

	public synchronized boolean isMod() {
		return (boolean) properties.get("isMod");
	}

	synchronized void setMod(boolean isMod) {
		properties.put("isMod", isMod);
	}

	public synchronized int getChatLines() {
		return (int) properties.get("chatLines");
	}

	private synchronized void setChatLines(int chatLines) {
		properties.put("chatLines", chatLines);
	}

	/**
	 * @return Amount of time watched in minutes
	 */
	public synchronized int getWatchedTime() {
		return 5 * (int) properties.get("watchedTime");
	}

	private synchronized void setWatchedTime(int watchedTime) {
		properties.put("watchedTime", watchedTime);
	}

	/**
	 * Compares users by alphabetical username 
	 */
	public static Comparator<User> userNameComparator = new Comparator<User>() {
		public int compare(User user1, User user2) {
			String name1 = user1.getDisplayName().toLowerCase();
			String name2 = user2.getDisplayName().toLowerCase();
			
			return name1.compareTo(name2);
		}
	};
	
	/**
	 * Compares users from oldest to youngest
	 */
	public static Comparator<User> userAgeComparator = new Comparator<User>() {
		public int compare(User user1, User user2) {
			Calendar time1 = user1.getBirth();
			Calendar time2 = user2.getBirth();
			
			return time1.compareTo(time2);
		}
	};
	
}
