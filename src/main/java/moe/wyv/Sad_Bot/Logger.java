package moe.wyv.Sad_Bot;

/**
 * Allows a single instance to log information to a screen.
 * Currently only outputs to sysout, but may change in the future.
 * 
 * @author fettuccine
 *
 */
public class Logger {
	
	private static Logger instance = null;
	private BotInstance logbot = null;
	
	/**
	 * Logger is a singleton
	 */
	private Logger() {
	}
	
	/**
	 * Singleton
	 * 
	 * @return the current instance of Logger
	 */
	public static synchronized Logger getInstance() {
		if (instance == null) {
			instance = new Logger();
		}
		return instance;
	}
	
	/**
	 * Used to output log information using the native bot's method
	 * 
	 * @param bot chatbot
	 */
	public void registerLogger(BotInstance bot) {
		logbot = bot;
	}
	
	/**
	 * Prints data to log
	 * 
	 * @param line data
	 */
	public void log(String line) {
		if (logbot != null) {
			logbot.log("$$$ "+line);
		} else {
			System.out.println(System.currentTimeMillis()+ " $$$ "+line);
		}
	}
	
	/**
	 * Overload to print empty line
	 */
	public void log() {
		log("");
	}
	
}
