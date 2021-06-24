package com.haxademic.core.hardware.keyboard;

import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import com.haxademic.core.system.SystemUtil;

public class Keyboard {
	
	// Singleton instance
	
	public static Keyboard instance;
	
	public static Keyboard instance() {
		if(instance != null) return instance;
		instance = new Keyboard();
		return instance;
	}
	
	// constructor
	
	public Keyboard() {
		// P.p.registerMethod(PRegisterableMethods.pre, this);
	}
	
	public void pre() {
	}
	
	public static Robot keyboardRobot;
	public static Robot keyboardRobot() {
		try {
			if(keyboardRobot == null) keyboardRobot = new Robot();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return keyboardRobot;
	}
	
	// key press automation
	
	public static void keyPress(int keyCode) {
		keyboardRobot().keyPress(keyCode);
	}
	
	public static void keyRelease(int keyCode) {
		keyboardRobot().keyRelease(keyCode);
	}
	
	public static void keyPressDelayed(int keyCode, int timeoutMs) {
		// build callback object
		ActionListener keyPressAction = new ActionListener() {public void actionPerformed(ActionEvent e) {
			keyPress(keyCode);
			keyRelease(keyCode);
		}};
		
		// trigger key after a delay
		SystemUtil.setTimeout(keyPressAction, timeoutMs);
	}
	
	public static void keyCommandPaste() {
		keyboardRobot().keyPress(KeyEvent.VK_CONTROL);
		keyboardRobot().keyPress(KeyEvent.VK_V);
		keyboardRobot().keyRelease(KeyEvent.VK_V);
		keyboardRobot().keyRelease(KeyEvent.VK_CONTROL);
	}
	
}
