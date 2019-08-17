package com.haxademic.core.hardware.keyboard;

import java.awt.Component;
import java.awt.event.KeyEvent;

@SuppressWarnings("serial")
public class KeyCodes
extends KeyEvent {

	public KeyCodes(Component source, int id, long when, int modifiers, int keyCode, char keyChar) {
		super(source, id, when, modifiers, keyCode, keyChar);
	}

}
