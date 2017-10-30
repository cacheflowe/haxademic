package com.haxademic.sketch.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import controlP5.ControlP5;

public class LinearFloatDelayTestCurtainBox
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected LinearFloat[] easingsL;
	protected LinearFloat[] easingsR;
	protected ControlP5 _cp5;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FPS, 60 );
//		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, true );
//		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
//		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 400 );
	}


	public void setup() {
		super.setup();	
		easingsL = new LinearFloat[40];
		easingsR = new LinearFloat[40];
		for (int i = 0; i < easingsL.length; i++) easingsL[i] = new LinearFloat(0, 0.02f);
		for (int i = 0; i < easingsR.length; i++) easingsR[i] = new LinearFloat(1, 0.02f);
	}

	public void drawApp() {
		background(0);

		// set delay once in a while
		int target = 0;
		if(p.frameCount % 100 == 0) {
			if(p.frameCount % 400 == 100) target = 1;
			for (int i = 0; i < easingsL.length; i++) {
				LinearFloat easer = easingsL[i];
				easer.setTarget(target);
				easer.setDelay(i);
			}
		}
		if(p.frameCount % 100 == 0) {
			target = 1;
			if(p.frameCount % 400 == 200) target = 0;
			for (int i = 0; i < easingsR.length; i++) {
				LinearFloat easer = easingsR[i];
				easer.setTarget(target);
				easer.setDelay(i);
			}
		}
		
		// update objects
		float segmentH = p.height / (easingsL.length - 1);
		DrawUtil.setDrawCenter(p);
		p.fill(255);
		p.noStroke();
		p.strokeCap(P.ROUND);
		p.beginShape();
		for (int i = 2; i < easingsL.length - 1; i++) {
			LinearFloat easer = easingsL[i];
			easer.update();
			float easedValue = Penner.easeInOutExpo(easer.value(), 0, 1, 1);
			p.vertex(P.map(easedValue, 0, 1, p.width * 0.2f, p.width * 0.7f), segmentH  * i);
		}
		for (int i = 2; i < easingsR.length - 1; i++) {
			int revI = easingsR.length - i;
			LinearFloat easer = easingsR[revI];
			easer.update();
			float easedValue = Penner.easeInOutExpo(easer.value(), 0, 1, 1);
			p.vertex(P.map(easedValue, 0, 1, p.width * 0.3f, p.width * 0.8f), segmentH  * revI);
		}
//		p.vertex(p.width * 0.8f, segmentH  * (easingsL.length-2));
//		p.vertex(p.width * 0.8f, segmentH  * 2);
		p.endShape(P.CLOSE);
	}

}
