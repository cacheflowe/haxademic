package com.haxademic.core.hardware.mouse;

import com.haxademic.core.app.P;

import processing.event.MouseEvent;

public class MouseShutdown {
	
	// triple click to shut down
	protected int NUM_CLICKS = 3;
	protected int[] clickTimes = new int[NUM_CLICKS];
	protected int clickIndex = 0;
	
	// singleton
	public static MouseShutdown instance;
	public static MouseShutdown instance() {
		if(instance != null) return instance;
		instance = new MouseShutdown();
		return instance;
	}
	
	public MouseShutdown() {
		P.p.registerMethod("mouseEvent", this);
	}
	
	public void mouseEvent(MouseEvent event) {
		  // int x = event.getX();
		  // int y = event.getY();

		  switch (event.getAction()) {
		    case MouseEvent.PRESS:
		      break;
		    case MouseEvent.RELEASE:
		      break;
		    case MouseEvent.CLICK:
		    	click();
		      break;
		    case MouseEvent.DRAG:
		      break;
		    case MouseEvent.MOVE:
		      break;
		  }
		}

	protected void click() {
		// track click
		clickTimes[clickIndex] = P.p.millis();
		clickIndex = (clickIndex >= NUM_CLICKS - 1) ? 0 : clickIndex + 1; // check for triple click
		
		// check to see if clicks are recent
		for (int i = 0; i < clickTimes.length; i++) {
			if(clickTimes[i] < P.p.millis() - 1500) {
				return;
			}
		}
		P.p.exit();
	}
}
