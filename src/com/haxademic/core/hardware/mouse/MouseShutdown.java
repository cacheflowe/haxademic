package com.haxademic.core.hardware.mouse;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;

import processing.event.MouseEvent;

public class MouseShutdown {
	
	// multiple clicks to shut down
	protected int numClicks = 3;
	protected int timeWindow = 1500;
	protected int[] clickTimes;
	protected int clickIndex = 0;
	
	public MouseShutdown(int numClicks, int timeWindow) {
		this.numClicks = numClicks;
		this.timeWindow = timeWindow;
		clickTimes = new int[numClicks];
		for (int i = 0; i < clickTimes.length; i++) clickTimes[i] = -99999;
		P.p.registerMethod(PRegisterableMethods.mouseEvent, this);
	}
	
	public void mouseEvent(MouseEvent event) {
		switch (event.getAction()) {
			case MouseEvent.CLICK:
				click();
				break;
			default:
				break;
		}
	}

	protected void click() {
		// track click
		int now = P.p.millis();
		clickTimes[clickIndex] = now;
		clickIndex = (clickIndex >= numClicks - 1) ? 0 : clickIndex + 1;
		
		// check to see if any clicks are old, 
		// or rather, all clicks are recent
		for (int i = 0; i < clickTimes.length; i++) {
			if(clickTimes[i] < now - timeWindow) {
				return;
			}
		}
		P.p.exit();
	}
}
