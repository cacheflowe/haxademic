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
		keyPress(KeyEvent.VK_CONTROL);
		keyPress(KeyEvent.VK_V);
		keyRelease(KeyEvent.VK_V);
		keyRelease(KeyEvent.VK_CONTROL);
	}
	
	public static void keyCommandMinimizeWindowWinKey() {
		keyPress(KeyEvent.VK_WINDOWS);
		keyPress(KeyEvent.VK_DOWN);
		keyRelease(KeyEvent.VK_DOWN);
		keyRelease(KeyEvent.VK_WINDOWS);

	}
	
	public static void keyCommandMinimizeWindow() {
		keyPress(KeyEvent.VK_ALT);
		keyPress(KeyEvent.VK_SPACE);
		keyPress(KeyEvent.VK_N);
		keyRelease(KeyEvent.VK_ALT);
		keyRelease(KeyEvent.VK_SPACE);
		keyRelease(KeyEvent.VK_N);
		
//		ActionListener keyPressMinimizeDevPanel1 = new ActionListener() {public void actionPerformed(ActionEvent e) {
//			Keyboard.keyPress(KeyEvent.VK_ALT);
//			Keyboard.keyPress(KeyEvent.VK_SPACE);
//		}};
//		ActionListener keyPressMinimizeDevPanel2 = new ActionListener() {public void actionPerformed(ActionEvent e) {
//			Keyboard.keyPress(KeyEvent.VK_N);
//			Keyboard.keyRelease(KeyEvent.VK_ALT);
//			Keyboard.keyRelease(KeyEvent.VK_SPACE);
//			Keyboard.keyRelease(KeyEvent.VK_N);
//		}};
//
//		SystemUtil.setTimeout(keyPressMinimizeDevPanel1, 500);
//		SystemUtil.setTimeout(keyPressMinimizeDevPanel2, 1000);
	}
	
}
