package com.haxademic.demo.system;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.system.TimerTrigger;
import com.haxademic.core.system.TimerTrigger.ITimerTriggerDelegate;

public class Demo_TimerTrigger
extends PAppletHax
implements ITimerTriggerDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected TimerTrigger timerTrigger;
	protected EasingColor bg = new EasingColor(0x000000, 0.1f);

	protected void firstFrame() {
	    timerTrigger = new TimerTrigger(this);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == '1') startTimer();
		if(p.key == '2') killTimer();
	}
	
	protected void startTimer() {
		timerTrigger.setTimer(1000);
	}
	
	protected void killTimer() {
		timerTrigger.cancel();
	}
	
	protected void drawApp() {
	    bg.update();
		p.background(bg.colorInt());
		
		// make sure to update timer every frame
//		timerTrigger.update();
		
		// show timer progress
		p.fill(255);
		p.rect(0, p.height - 20, p.width * timerTrigger.progress(), 20);
	}


	/////////////////////////////
	// ITimerTriggerDelegate
	/////////////////////////////
	
    public void timerComplete(TimerTrigger trigger) {
        bg.setCurrentInt(0xffffffff).setTargetInt(0xff000000);
    }
}
