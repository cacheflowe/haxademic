package com.haxademic.core.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import com.haxademic.core.app.P;

public class HttpRequest {
	
	public interface IHttpRequestCallback {
		public void postSuccess(String responseText, int responseCode);
		public void postFailure(String errorMessage);
	}

	protected String requestURL;
	protected String responseOut;
	protected IHttpRequestCallback delegate;
	protected Thread requestThread;
	protected String responseText = null;
	protected int responseCode = 0;
	public static boolean DEBUG = false;

	public HttpRequest(IHttpRequestCallback delegate) {
		this.delegate = delegate;
	}
	
	public void send(String requestURL) {
		this.requestURL = requestURL;
		new Thread(new Runnable() { public void run() {
			makeRequest();
		}}).start();
	}

	protected void makeRequest() {
		// create request id
		responseCode = 0;
		
		try {
			URL url = new URL(requestURL);
			HttpURLConnection httpcon = (HttpURLConnection) ((url.openConnection()));
			HttpURLConnection.setFollowRedirects(true);
			httpcon.setDoOutput(true);
	//		httpcon.setRequestProperty("Content-Type", "application/json");
	//		httpcon.setRequestProperty("Accept", "application/json");
			httpcon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			httpcon.setRequestMethod("GET");
			
			// pull user/pass from inline auth in url if it exists, and apply to the connection
			if (url.getUserInfo() != null) {
				String authStr = Base64.getEncoder().encodeToString(url.getUserInfo().getBytes());
				httpcon.setRequestProperty("Authorization", "Basic " + authStr);
			}
			httpcon.connect();
			getFullResponse(httpcon);

		} catch (ProtocolException e) {
			e.printStackTrace();
			if(delegate != null) delegate.postFailure(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			if(delegate != null) delegate.postFailure(e.getMessage());
		}
	} 
	
	// from: https://github.com/eugenp/tutorials/blob/master/core-java-modules/core-java-networking/src/main/java/com/baeldung/http/FullResponseBuilder.java
	protected void getFullResponse(HttpURLConnection con) throws IOException {
        StringBuilder fullResponseBuilder = new StringBuilder();

        fullResponseBuilder.append(con.getResponseCode())
            .append(" ")
            .append(con.getResponseMessage())
            .append("\n");

        con.getHeaderFields()
            .entrySet()
            .stream()
            .filter(entry -> entry.getKey() != null)
            .forEach(entry -> {

                fullResponseBuilder.append(entry.getKey())
                    .append(": ");

                List<String> headerValues = entry.getValue();
                Iterator<String> it = headerValues.iterator();
                if (it.hasNext()) {
                    fullResponseBuilder.append(it.next());

                    while (it.hasNext()) {
                        fullResponseBuilder.append(", ")
                            .append(it.next());
                    }
                }

                fullResponseBuilder.append("\n");
            });

        Reader streamReader = null;

        responseCode = con.getResponseCode();
        if (responseCode > 299) {
            streamReader = new InputStreamReader(con.getErrorStream());
        } else {
            streamReader = new InputStreamReader(con.getInputStream());
        }

        BufferedReader in = new BufferedReader(streamReader);
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();

        fullResponseBuilder.append("Response: ").append(content);
        if(DEBUG) {
        	P.out("===========================");
        	P.out(fullResponseBuilder.toString());
        	P.out("===========================");
        }
        
		if(delegate != null) delegate.postSuccess(content.toString(), responseCode);
    }
}		
