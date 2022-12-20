package com.haxademic.core.system;

import java.util.Timer;
import java.util.TimerTask;

import com.haxademic.core.app.P;

public class TimerTrigger {
	
	public interface ITimerTriggerDelegate {
		public void timerComplete(TimerTrigger trigger);
	}
	
	protected ITimerTriggerDelegate delegate;
	protected Timer timer;
	protected float startTime = 0;
	protected float endTime = 9999;
	protected float progress = 0;
	protected float duration = 0;
	protected boolean armed = false;
	
	public TimerTrigger(ITimerTriggerDelegate delegate) {
		this.delegate = delegate;
	}
	
	public void setTimer(int duration) {
	    cancel();
	    
	    // store time
	    startTime = P.p.millis();
	    endTime = startTime + duration;
	    progress = 0;
	    armed = true;
	    
	    // set timer
	    timer = new Timer();
	    timer.schedule(new TimerTask() { public void run() {
            timeComplete();
	    }}, duration);     // delay, [repeat]
	}
	
	protected void timeComplete() {
	    armed = false;
        delegate.timerComplete(this);
	}
	
	public void cancel() {
	    if(timer != null) timer.cancel();
	    timer = null;
	    armed = false;
	    progress = 0;
	}
	
	public float progress() {
	    if(!armed) {
	        return 0;
	    } else {
	        return (P.p.millis() - startTime) / (endTime - startTime);
	    }
	}
	
	public float timeLeft() {
		return endTime - P.p.millis();
	}
	
	public float duration() {
	    return duration;
	}
	
}
