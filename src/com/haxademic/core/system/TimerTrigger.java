package com.haxademic.core.system;

import com.haxademic.core.app.P;

public class TimerTrigger {
	
	public interface ITimerTriggerDelegate {
		public void timerComplete(TimerTrigger trigger);
	}
	
	protected ITimerTriggerDelegate delegate;
	protected float startTime = 0;
	protected float endTime = 9999;
	protected float progress = 0;
	protected boolean armed = false;
	
	public TimerTrigger(ITimerTriggerDelegate delegate) {
		this.delegate = delegate;
	}
	
	public void setTimer(int addMS) {
		startTime = P.p.millis();
		endTime = startTime + addMS;
		progress = 0;
		armed = true;
	}
	
	public void update() {
		if(armed == false) return;
		progress = (P.p.millis() - startTime) / (endTime - startTime);
		if(progress >= 1) {
			armed = false;
			delegate.timerComplete(this);
		}
	}
	
	public float progress() {
		return progress;
	}
	
}
