package moe.wyv.Sad_Bot.skills;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import moe.wyv.Sad_Bot.BotInstance;
import moe.wyv.Sad_Bot.Logger;
import moe.wyv.Sad_Bot.Message;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

/**
 * Skill will provide title, channel, and view count for YouTube links
 * 
 * @author fettuccine
 *
 */
public class YoutubeSkill implements BotSkill {

	/**
	 * Matches both youtube.com/ and youtu.be/ links
	 */
	private static final Pattern PATTERN = Pattern.compile(".*(youtu\\.?be(?:\\/([\\w-]{11})|\\.\\w{2,3}\\/watch\\?[^ ]*v=([\\w-]{11}))).*");
	/**
	 * Private Google API key
	 */
	private static final String API_KEY = "AIzaSyCWy7duV9SzK6sVMk1YlvnijO8p37reSzs"; 
	private static final DecimalFormat FORMAT = new DecimalFormat("###,###"); 
	private YouTube.Videos.List videoRequest;
	
	
	public YoutubeSkill() {
		//This code is in the constructor to try and make calls faster.
		//Content stolen from stackoverflow
		YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(),
		        new HttpRequestInitializer() {
		            public void initialize(HttpRequest request) throws IOException {
		            }
		        }).setApplicationName("video-test").build();
		
		try {
			videoRequest = youtube.videos().list("snippet,statistics,contentDetails");
			videoRequest.setKey(API_KEY);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//End stolen
	}
	
	
	@Override
	public Pattern getPattern() {
		return PATTERN;
	}

	@Override
	public String getName() {
		return "YoutubeSkill";
	}

	@Override
	public void action(BotInstance bot, Message message) {
		Matcher match = PATTERN.matcher(message.getMessage());
		match.find();
//		bot.send(message.getChannel(), "Youtube link found: "+ (match.find() ? match.group(1) : "<none>"));
		
		Logger.getInstance().log("[yt] "+match.group(1));
		
		try {
			//pick the one with the tag in it
			final String videoId = match.group(2).isEmpty() ? match.group(3) : match.group(2);
			videoRequest.setId(videoId);
			
			///Content stolen from stackoverflow 
			VideoListResponse listResponse = videoRequest.execute();
			List<Video> videoList = listResponse.getItems();
	
			Video targetVideo = videoList.iterator().next();
			///End stolen
			
			String title = targetVideo.getSnippet().getTitle();
			String views = FORMAT.format(targetVideo.getStatistics().getViewCount());
			String channel = targetVideo.getSnippet().getChannelTitle();
			
			bot.send(message.getChannel(), "[YT]["+channel+"]["+views+" views] " + title);
		
		} catch (IOException e) {
			System.err.println("%%% invalid link "+match.group(1)); //DEBUG
			return;
		}
	}
}
