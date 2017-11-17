package moe.wyv.Sad_Bot.ai;

import java.util.ArrayList;

import org.jibble.jmegahal.JMegaHal;
import org.jibble.jmegahal.Quad;

public class MegaDon extends JMegaHal {
	private static final long serialVersionUID = 7765138172318134490L;
	private Language language;
	
	public MegaDon() {
		language = new Language();
	}
	
	
	public String getResponse(String sentence) {
		//TODO getsentence many times, use best ones
		ArrayList<String> words = extractImportantWords(sentence);
		
		
		ArrayList<String> responses = new ArrayList<String>();
		for (String string : words) {
			
		}
		
		
		return null;
	}
	
	public String getResponse(String sentence, String name) {
		//TODO
		return getResponse(sentence);
	}
	
	private ArrayList<String> extractImportantWords(String sentence) {
		ArrayList<String> words = extractWords(sentence);
		for (int i = words.size(); i >= 0; --i) {
			if (language.matchWord(words.get(i), Language.NOT_IMPORTANT)) {
				words.remove(i);
			}
		}
		//TODO
		return words;
	}
	
	/**
	 * Removes punctuation from sentence and splits it.
	 * 
	 * @author jibble.org
	 * @param sentence a sentence
	 * @return list of words in sentence
	 */
	private static ArrayList<String> extractWords(String sentence) {
		sentence = sentence.trim();
        ArrayList<String> parts = new ArrayList<String>();
        char[] chars = sentence.toCharArray();
        int i = 0;
        boolean punctuation = false;
        StringBuffer buffer = new StringBuffer();
        while (i < chars.length) {
            char ch = chars[i];
            if ((WORD_CHARS.indexOf(ch) >= 0) == punctuation) {
                punctuation = !punctuation;
                String token = buffer.toString();
                if (token.length() > 0) {
                    parts.add(token);
                }
                buffer = new StringBuffer();
                //i++;
                continue;
            }
            buffer.append(ch);
            i++;
        }
        String lastToken = buffer.toString();
        if (lastToken.length() > 0) {
            parts.add(lastToken);
        }
        
        return parts;
	}
	
	@Override
	public void add(String sentence) {
		language.add(extractWords(sentence));
		super.add(sentence);
	}
	
	
}
