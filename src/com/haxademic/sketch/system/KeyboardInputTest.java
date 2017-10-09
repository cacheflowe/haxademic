package com.haxademic.sketch.system;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

public class KeyboardInputTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected boolean DEBUG_MODE = true;

	protected void overridePropsFile() {
		appConfig.setProperty( AppSettings.FPS, 90 );
	}

	public void setup() {
		super.setup();
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'd') DEBUG_MODE = !DEBUG_MODE;
		if(p.keyCode == 8) P.println("DELETE");
		if (p.key == P.CODED && p.keyCode == P.RIGHT) P.println("RIGHT ARROW");
		if (p.key == P.CODED && p.keyCode == P.LEFT) P.println("LEFT ARROW");
		P.println("key:", p.key, "keyCode:", p.keyCode);
	}

	public void drawApp() {
		p.background(0);
	}

}