package com.haxademic.core.hardware.keyboard;

import java.awt.event.KeyEvent;

public class Keyboard {

	public static int keyCodeFromChar(char inputChar) {
		return KeyEvent.getExtendedKeyCodeForChar(inputChar);
	}

}
