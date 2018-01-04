package com.haxademic.sketch.audio.ess;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.OpenGLUtil;

public class BeatDetectTest 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
//	protected ControlP5 _cp5;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FPS, "60" );
	}


	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );
	}

	public void drawApp() {
		background(0);
		
		P.println(_audioInput.beats);
		
		p._audioInput.detector.drawGraph();
	}

}
