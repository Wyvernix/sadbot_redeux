package moe.wyv.Sad_Bot.ai;

import java.io.Serializable;
import java.util.ArrayList;

public class Language implements Serializable {
	
	private static final long serialVersionUID = -5871666979754917433L;
	public static final int SUBJECT = 1;
	public static final int TARGET = 2;
	public static final int IMPORTANT = 3;
	public static final int NOT_IMPORTANT = 4;
	
	
	public boolean matchWord(String word, int keyword) {
		boolean matches = false;
		//TODO
		switch (keyword) {
		case SUBJECT:

			break;
		case TARGET:

			break;
		case IMPORTANT:

			break;
		case NOT_IMPORTANT:

			break;
		default:
			break;
		}
		
		return matches;
	}


	public void add(ArrayList<String> words) {
		
		
	}
	
	

}
