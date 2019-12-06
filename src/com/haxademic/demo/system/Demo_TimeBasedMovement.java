package com.haxademic.demo.system;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.system.TimeFactoredFps;

public class Demo_TimeBasedMovement
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public TimeFactoredFps timeFactor;
	public float curX = 0;
	public float moveXPerFrame = 5;

	public void setupFirstFrame() {
		// base calculations off 60fps, regardless of what the actual fps is set to		
		timeFactor = new TimeFactoredFps( p, 60 );
	}

	public void drawApp() {
		p.background(0);
		
		p.fill(255);
		float timeX = curX % p.width;
		// P.println(timeX);
		p.rect( timeX, 100, 100, 100 );

		timeFactor.update();
		curX += P.round(moveXPerFrame * timeFactor.multiplier());
//		P.println(curX);
		p.debugView.setValue("target_fps: ",timeFactor.targetFps());
		p.debugView.setValue("actual_fps: ",timeFactor.actualFps());
		p.debugView.setValue("timeFactor: ",timeFactor.multiplier());
		
	}
}
