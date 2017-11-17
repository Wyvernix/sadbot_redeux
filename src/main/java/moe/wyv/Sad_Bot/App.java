package moe.wyv.Sad_Bot;

public class App {
	
    /**
     * Will launch as many bots as are declared by -name=$name arguments
     * 
     * @param args Command line arguments
     */
    public static void main( String[] args ) {
        
    	if (args.length < 1) {
    		System.out.println("Please provide an argument -name=username_of_bot");
//    		launchBot("energybot", false);
    		return;
    	}
    	
    	for (String arg : args) {
			String[] pair = arg.substring(1).split("=");
			if ("name".equals(pair[0]) && !pair[1].isEmpty()) {
				launchBot(pair[1], true);
			}
		}
    }
    
    /**
     * Creates a new {@link BotInstance} and optionally starts it.
     * 
     * @param doConnect if {@code true}, connect to server, otherwise do dry run
     * @param name username of chat bot 
     */
    private static void launchBot(String name, boolean doConnect) {
    	
    	BotInstance bot = new BotInstance(name);
    	bot.setVerbose(true);
        
        if (doConnect){
			bot.connect();
        }
    }
}
