package com.haxademic.demo.system;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;

public class Demo_Java8ForEach
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		ArrayList<String> strings = new ArrayList<String>();
		strings.add("TEST");
		strings.add("TESTING");
		strings.add("ANOTHER");
		
		// static method definition
		strings.forEach(P::error);
		// local instance method
		strings.forEach(name -> testOutput(name));
		
		// switch state as assignment (Java 14)
		String day = "MONDAY";
		boolean isTodayHoliday = switch (day) {
		    case "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" -> false;
		    case "SATURDAY", "SUNDAY" -> true;
		    default -> throw new IllegalArgumentException("What's a " + day);
		};
		P.out("isTodayHoliday", isTodayHoliday);
		
		
		// multiline String (Java 14)
		String multiline = """
		    A quick brown fox jumps over a lazy dog; \n
		    the lazy dog howls loudly.
		""";
		P.out("multiline");
		P.out(multiline);
		
		// String indentation (Java 12)
		String testIndent = "Test sentence for indentation";
		testIndent.indent(4);
		P.out("testIndent");
		P.out(testIndent);
		
		// https://www.baeldung.com/java-11-new-features
		// httpclient (Java 11)
		HttpClient httpClient = HttpClient.newBuilder()
				  .version(HttpClient.Version.HTTP_2)
				  .connectTimeout(Duration.ofSeconds(20))
				  .build();
				HttpRequest httpRequest = HttpRequest.newBuilder()
				  .GET()
				  .uri(URI.create("https://cacheflowe.com"))
				  .build();
		try {
			HttpResponse httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
			P.out(httpResponse.body());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	protected void testOutput(String input) {
		P.error(input);
	}
	
	protected void drawApp() {
		background(0);
	}

}
