package moe.wyv.Sad_Bot.web;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * This class contains methods for retrieving HTML or JSON like data from the web.
 * 
 * @author fettuccine
 * @author com.mkyong
 *
 */
public abstract class Web {
	private final static String USER_AGENT = "Mozilla/5.0";
	
	/**
	 * HTTP GET request
	 * 
	 * @param url target location
	 * @return HTTP response
	 * @throws IOException error getting response
	 */
	public static String httpGet(String url) throws IOException {
		return httpGet(url, new String[0][0]);
	}
	
	/**
	 * HTTP GET request with headers
	 * 
	 * @param url target location
	 * @param properties array of headers in format [ name, value ]
	 * @return HTTP response
	 * @throws IOException error getting response
	 */
	public static String httpGet(String url, String[][] properties) throws IOException {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add request header
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		for (String[] property : properties) {
			con.setRequestProperty(property[0], property[1]);
		}

//		int responseCode = con.getResponseCode();
//		System.out.println("\nSending 'GET' request to URL : " + url);
//		System.out.println("Response Code : " + responseCode);
		
//		for (int i = 0; i < VALID_ERRORS.length; i++) {
//			if (VALID_ERRORS[i] == responseCode) {
//				System.out.println("!!!ERROR!");
//			}
//		}
		
		BufferedReader in = null;
		StringBuffer response = new StringBuffer();
		try {
			in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (IOException e) {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ee) {
					ee.printStackTrace();
				}
			}
			throw(e);
		} 

		return response.toString();

	}

	// HTTP POST request
	/**
	 * Not complete
	 * 
	 * @param url target location
	 * @param properties HTTP headers
	 * @return response from server
	 * @throws Exception internet is hard
	 */
	@Deprecated
	public static String httpPost(String url, String[][] properties) throws Exception {

		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		for (String[] property : properties) {
			con.setRequestProperty(property[0], property[1]);
		}

		String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

		// Send post request
		con.setDoOutput(true);
		
		DataOutputStream wr = null;
		try {
			wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
		} catch (IOException e) {
			if (wr != null) {
				try {
					wr.close();
				} catch (IOException ee) {
					ee.printStackTrace();
				}
			}
			throw(e);
		}

//		int responseCode = con.getResponseCode();
//		System.out.println("\nSending 'POST' request to URL : " + url);
//		System.out.println("Post parameters : " + urlParameters);
//		System.out.println("Response Code : " + responseCode);

		BufferedReader in = null;
		StringBuffer response = new StringBuffer();
		try {
			in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (IOException e) {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ee) {
					ee.printStackTrace();
				}
			}
			throw(e);
		}

		return response.toString();
	}
}
