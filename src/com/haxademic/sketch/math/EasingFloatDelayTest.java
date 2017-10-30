package com.haxademic.sketch.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.math.easing.EasingFloat;

import controlP5.ControlP5;

public class EasingFloatDelayTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public float easeFactor = 6f;
	protected EasingFloat[] easings;
	protected ControlP5 _cp5;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FPS, "60" );
	}


	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );
		easings = new EasingFloat[30];
		for (int i = 0; i < easings.length; i++) {
			easings[i] = new EasingFloat(p.width/2, 8f);
		}		
	}

	public void drawApp() {
		background(0);

		// set delay once in a while
		if(p.frameCount % 200 == 0) {
			for (EasingFloat easer : easings) {
				easer.setTarget(P.p.random(0, p.width));
				easer.setDelay((int) P.p.random(0, p.mouseX));
			}
		}
		
		// update objects
		DrawUtil.setDrawCenter(p);
		p.fill(255);
		for (int i = 0; i < easings.length; i++) {
			EasingFloat easer = easings[i];
			easer.update();
			p.ellipse(easer.value(), p.height / (easings.length - 1)  * i, 40, 40);
		}
	}

}
