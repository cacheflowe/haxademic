package com.haxademic.sketch.test;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.OpenGLUtil;

@SuppressWarnings("serial")
public class BeatDetectTest 
extends PAppletHax {
	
//	protected ControlP5 _cp5;

	protected void overridePropsFile() {
		_appConfig.setProperty( "fps", "60" );
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
