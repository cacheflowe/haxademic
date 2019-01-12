package com.haxademic.core.hardware.gamepad;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.shared.InputState;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

public class GamepadListener {

	protected HashMap<String, InputState> gamePadValues = new HashMap<String, InputState>();
	protected int lastUpdatedFrame = 0;

	public GamepadListener() {
		init();
	}
	
	protected void init() {
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
					if(controllers[i].getType() == Controller.Type.STICK || controllers[i].getType() == Controller.Type.GAMEPAD) {
						// get input updates
						controllers[i].poll();
						EventQueue queue = controllers[i].getEventQueue();
						Event event = new Event();
						while (queue.getNextEvent(event)) {
							Component comp = event.getComponent();
							float value = event.getValue();
							P.p.gamepadState.setControlValue(comp.getName(), value);
							// P.p.gamepadState.setControlValue(controllers[i].getName() + " " + comp.getName(), value);
							// P.out(comp.getName(), value, comp.isAnalog(), comp.isRelative(), comp.getPollData());
						}
					}
				}
			}
			
		}}).start();
	}
}
