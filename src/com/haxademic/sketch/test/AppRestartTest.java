package com.haxademic.sketch.test;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.FloatBuffer;
import com.haxademic.core.system.AppRestart;

@SuppressWarnings("serial")
public class AppRestartTest
extends PAppletHax  
{
	protected FloatBuffer buff;
	
	public void setup() {
		super.setup();		
	}

	public void drawApp() {
		P.println(p.frameCount);
		if(p.frameCount == 90) AppRestart.restart( p );
		// TODO: write out restart command to a text file for diffing
	}
}
