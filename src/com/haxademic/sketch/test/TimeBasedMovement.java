package com.haxademic.sketch.test;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.system.TimeFactoredFps;

@SuppressWarnings("serial")
public class TimeBasedMovement
extends PAppletHax  
{
	public TimeFactoredFps timeFactor;
	public float curX = 0;
	public float moveXPerFrame = 5;

	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "480" );
		_appConfig.setProperty( "fps", "60" );
	}

	public void setup() {
		super.setup();
		
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
//		P.println("target_fps: "+timeFactor.targetFps()+" / actual_fps: "+timeFactor.actualFps()+" / timeFactor: "+timeFactor.multiplier());
		
	}
}
