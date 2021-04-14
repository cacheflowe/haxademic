package com.haxademic.core.hardware.gamepad;

import com.haxademic.core.app.P;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

public class GamepadListener {

	public GamepadListener() {
		new Thread(new Runnable() { public void run() {

			// look for controllers via JInput
			Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
			
			if (controllers.length == 0) {
				P.error("JInput Found no controllers.");
			} else {
				P.out("Gamepads found:");
				for (int i = 0; i < controllers.length; i++) {
					P.out("["+i+"] " + controllers[i].getName(), " | ", controllers[i].getType());
				}
			}

			while (true) {
				// poll each device
				for (int i = 0; i < controllers.length; i++) {
					// On OS X, Gamepad showed up as GAMEPAD, and on Windows, UNKNOWN
//					if(controllers[i].getType() != Controller.Type.MOUSE && controllers[i].getType() != Controller.Type.KEYBOARD) {
						// get input updates
						controllers[i].poll();
						EventQueue queue = controllers[i].getEventQueue();
						Event event = new Event();
						while (queue.getNextEvent(event)) {
							Component comp = event.getComponent();
							float value = event.getValue();
							GamepadState.instance().setControlValue(comp.getName(), value);
						}
//					}
				}
			}
			
		}}).start();
	}
}
