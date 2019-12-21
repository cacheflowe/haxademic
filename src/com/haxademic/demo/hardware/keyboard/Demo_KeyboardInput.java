package com.haxademic.demo.hardware.keyboard;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;

public class Demo_KeyboardInput
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
		Config.setProperty( AppSettings.FPS, 90 );
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.keyCode == 8) P.println("DELETE");
		if (p.key == P.CODED && p.keyCode == P.RIGHT) P.println("RIGHT ARROW");
		if (p.key == P.CODED && p.keyCode == P.LEFT) P.println("LEFT ARROW");
		P.println("key:", p.key, "keyCode:", p.keyCode);
	}

	public void drawApp() {
		p.background(0);
		p.text("key: " + p.key + " keyCode: " + p.keyCode, 20, 20);
	}

}