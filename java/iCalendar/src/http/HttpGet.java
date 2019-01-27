package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 
 * I coded this in a previous project (very recent, 2 days ago)
 * 
 */
public class HttpGet {
	
	private String url;
	
	private boolean storeCookies;
	private HashMap<String, String> cookies;
	
	public HttpGet(String url, boolean storeCookies) {
		setUrl(url);
		this.storeCookies = storeCookies;
		
		if(this.storeCookies)
			cookies = new HashMap<String, String>();
	}
	
	public String get() {
		
		URL obj;
		
		//Form URL
		try {
			obj = new URL(url);
		} catch (MalformedURLException e) {
			System.out.println("Bad URL in HttpGet");
			return null;
		}
		
		HttpURLConnection con = null;
		//Open connection
		try {
			con = (HttpURLConnection)obj.openConnection();
		} catch (IOException e) {
			System.out.println("Could not establish connection to destination URL in HttpGet");
		}
		
		//If connection is open, proceed. Otherwise, return null.
		if(con != null) {
			
			try {
				//Set method as GET
				con.setRequestMethod("GET");
				//30s timeout for both connecting and reading.
				con.setConnectTimeout(30000);
				con.setReadTimeout(30000);
				//Set headers to represent a Chrome browser request
				setHeaders(con);
				//Receive HTML content
				try(
						BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))
						) {
					String inputLine; StringBuilder html = new StringBuilder();
					//Append htmlContent line by line
					while((inputLine = in.readLine()) != null) {
						html.append(inputLine.replace("\n", ""));
					}
					
					//Store cookies, if required.
					if(this.storeCookies) {
						Map<String, List<String>> rp = con.getHeaderFields();
						for(String key : rp.keySet()) {
							//If the header type is set cookie, set the cookie
							if(key != null && key.equals("Set-Cookie")) {
								
								for(String s : rp.get(key)) {
									//Split at ';'
									String[] content = s.split(";");
									//Then, split the first bit at "=" to get key, value
									content = content[0].split("=");
									//Set in hashmap
									cookies.put(content[0], content[1]);
								}
								
							}
						}
					}
					
					//Return html
					return html.toString();
					
				} catch(IOException e) {
					//This exception usually occurs if its a bad proxy, and there are many...
					//System.out.println("");
				}
			} catch (ProtocolException e) {
				System.out.println("Protocol exception in HttpGet");
			}
		}
		
		//Return null. If there was valid content, it would have been returned in return html.toString();
		return null;
	}

	public void setHeaders(HttpURLConnection con) {
		
		con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
		con.setRequestProperty("Cache-Control", "no-cache");
		con.setRequestProperty("Connection", "keep-alive");
		con.setRequestProperty("Pragma", "no-cache");
		con.setRequestProperty("Upgrade-Insecure-Requests", "1");
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
		if(this.storeCookies && this.cookies.isEmpty() == false) {
			//Build cookies
			StringBuilder s = new StringBuilder();
			for(String key : cookies.keySet()) {
				s.append(key + "=" + cookies.get(key) + "; ");
			}
			
			String finalCookies = s.toString();
			//Remove last character which is ';' as the last cookie does not have a ';'
			finalCookies = finalCookies.substring(0, finalCookies.length() - 2);
			//Set the cookie property
			con.setRequestProperty("Cookie", finalCookies);
		}
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public HashMap<String, String> getCookies() {
		return this.cookies;
	}

}
