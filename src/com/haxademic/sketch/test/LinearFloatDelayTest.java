package com.haxademic.sketch.test;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import controlP5.ControlP5;

public class LinearFloatDelayTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected LinearFloat[] easings;
	protected ControlP5 _cp5;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FPS, "60" );
	}


	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );
		easings = new LinearFloat[30];
		for (int i = 0; i < easings.length; i++) {
			easings[i] = new LinearFloat(0, 0.02f);
		}		
	}

	public void drawApp() {
		background(0);

		// set delay once in a while
		if(p.frameCount % 100 == 0) {
			int target = (p.frameCount % 200 == 0) ? 1 : 0;
			for (int i = 0; i < easings.length; i++) {
				LinearFloat easer = easings[i];
				easer.setTarget(target);
				easer.setDelay(i);
			}
		}
		
		// update objects
		DrawUtil.setDrawCenter(p);
		p.fill(255);
		for (int i = 0; i < easings.length; i++) {
			LinearFloat easer = easings[i];
			easer.update();
			float easedValue = Penner.easeInOutExpo(easer.value(), 0, 1, 1);
			p.ellipse(easedValue * (float) p.width, p.height / (easings.length - 1)  * i, 40, 40);
		}
	}

}
