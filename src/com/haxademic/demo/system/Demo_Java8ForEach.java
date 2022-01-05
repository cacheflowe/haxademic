package com.haxademic.demo.system;

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
	}
	
	protected void testOutput(String input) {
		P.error(input);
	}
	
	protected void drawApp() {
		background(0);
	}

}
