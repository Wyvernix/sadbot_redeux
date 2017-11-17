package moe.wyv.Sad_Bot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moe.wyv.Sad_Bot.commands.*;
import moe.wyv.Sad_Bot.skills.*;

import org.yaml.snakeyaml.Yaml;

/**
 * This class contains all of the user-configurable settings to use
 * in computations. It also allows for multiple bots to be written
 * without having to rewrite code.
 * 
 * @author fettuccine
 *
 */
public class Settings {

	/**
	 * Master list of all possible commands
	 */
	private static final List<BotCommand> ALL_COMMANDS = Arrays.asList(
			new QuitCommand(), new HelpCommand(), new TimeCommand() , new RandomCommand(), new CSGOCommand(),
			new UserCommand(), new PubgCommand(), new QueueCommand(), new CommandCommand(), new SteamCommand()); 
	/**
	 * Master list of all possible skills
	 */
	private static final List<BotSkill> ALL_SKILLS = Arrays.asList( new YoutubeSkill(), new ChatSkill(), new TwitchSkill() );
	
	/**
	 * Used because I cannot spell.
	 */
	private static final String[] SPELLCHECK = {"botname", "oauth", "primaryChannel", "commandChar", "disabledCommands", "disabledSkills"};

	//Instance variables
	private String filename;
	private List<BotCommand> enabledCommands = new ArrayList<BotCommand>();
	private List<BotSkill> enabledSkills = new ArrayList<BotSkill>();
	
	//These are the default values if the file cannot be loaded
	private String botname = "testbot";
	private String oauth = "oauth:numbershere12345";
	private String primaryChannel = "#sad_bot";
	private char commandChar = '$';
	
	
	/**
	 * Loads a settings file from disk. If one is not found, one is
	 * generated.
	 * 
	 * @param file filename
	 */
	public Settings(String file) {
		this.filename = file;
		loadFromFile();
	}
	
	/**
	 * Adds variables to data set and writes to file.
	 */
	private void saveToFile() {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		//Encode data
		data.put(SPELLCHECK[0], botname);
		data.put(SPELLCHECK[1], oauth);
		data.put(SPELLCHECK[2], primaryChannel);
		data.put(SPELLCHECK[3], Character.toString(commandChar));
		data.put(SPELLCHECK[4], Arrays.asList( "RandomCommand", "TimeCommand" ));
		data.put(SPELLCHECK[5], Arrays.asList( "YoutubeSkill", "ChatSkill" ));
		
		try {
			new Yaml().dump(data, new PrintWriter(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Interprets data set from file and populates variables. If file
	 * is not found, one is created.
	 */
	@SuppressWarnings("unchecked")
	private void loadFromFile() {
		
		File datafile = new File(filename);
		
		try {
			Map<String, Object> data = (Map<String, Object>) (new Yaml()).load(new FileReader(datafile));
			
			//Decode data
			botname = (String) data.get(SPELLCHECK[0]);
			oauth = (String) data.get(SPELLCHECK[1]);
			primaryChannel = (String) data.get(SPELLCHECK[2]);
			commandChar = ((String) data.get(SPELLCHECK[3])).charAt(0);
			loadCommands(((ArrayList<String>) data.get(SPELLCHECK[4])));
			loadSkills((ArrayList<String>) data.get(SPELLCHECK[5]));
			
		} catch (FileNotFoundException e) {
			saveToFile();
			Logger.getInstance().log("****new file made");
			return;
		}
	}
	
	/**
	 * If the skill is listed in the file, deactivate it
	 * 
	 * @param skills list of skills to disable
	 */
	private void loadSkills(ArrayList<String> skills) {
		enabledSkills = new ArrayList<BotSkill>();
		for (BotSkill skill : ALL_SKILLS) {
			if (!skills.contains(skill.getName())) {
				enabledSkills.add(skill);
			}
		}
	}

	/**
	 * If the command is listed in the file, deactivate it
	 * 
	 * @param commands list of commands to disable
	 */
	private void loadCommands(ArrayList<String> commands) {
		enabledCommands = new ArrayList<BotCommand>();
		for (BotCommand botCommand : ALL_COMMANDS) {
			if (!commands.contains(botCommand.getName())) {
				enabledCommands.add(botCommand);
			}
		}
	}
	
	//Get data
	public String getBotName() { return botname; }
	public String getOauth() { return oauth; }
	public String getPrimaryChannel() { return primaryChannel; }
	public char getCommandChar() { return commandChar; }
	public List<BotCommand> getEnabledCommands() { return enabledCommands; }
	public List<BotSkill> getEnabledSkills() { return enabledSkills; }
	
}
