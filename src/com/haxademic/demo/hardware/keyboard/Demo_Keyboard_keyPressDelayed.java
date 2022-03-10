package com.haxademic.demo.hardware.keyboard;

import java.awt.event.KeyEvent;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.hardware.keyboard.Keyboard;

public class Demo_Keyboard_keyPressDelayed
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}

	protected void firstFrame() {
		Keyboard.keyPressDelayed(KeyEvent.VK_3, 1000);
		Keyboard.keyPressDelayed(KeyEvent.VK_6, 2000);
		Keyboard.keyPressDelayed(KeyEvent.VK_E, 3000);
		Keyboard.keyPressDelayed(KeyEvent.VK_J, 4000);
		Keyboard.keyPressDelayed(KeyEvent.VK_WINDOWS, 5000);
		Keyboard.keyPressDelayed(KeyEvent.VK_WINDOWS, 6000);
		Keyboard.keyPressDelayed(KeyEvent.VK_ESCAPE, 7000);
	}
	
	protected void drawApp() {
		p.background(0);
//		if(p.frameCount == 360) Keyboard.keyCommandMinimizeWindow();
		p.text("key: " + p.key + " keyCode: " + p.keyCode, 300, 20);
	}

}