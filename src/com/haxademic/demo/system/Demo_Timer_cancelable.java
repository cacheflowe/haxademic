package com.haxademic.demo.system;

import java.util.Timer;
import java.util.TimerTask;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.EasingColor;

public class Demo_Timer_cancelable
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected EasingColor bg = new EasingColor(0x000000, 0.1f);
	protected Timer timer;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 520 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 120 );
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == '1') startTimer();
		if(p.key == '2') killTimer();
	}
	
	protected void startTimer() {
		killTimer();
		bg.setTargetInt(0x00ff00);
		timer = new Timer();
		timer.schedule(new TimerTask() { public void run() {
			killTimer();
		}}, 4000, 1000);	 // delay, [repeat]
	}
	
	protected void killTimer() {
		bg.setCurrentInt(0xff0000);
		bg.setTargetInt(0x000000);
		if(timer != null) timer.cancel();
	}
	
	public void drawApp() {
		bg.update();
		p.background(bg.colorInt());
	}
}
