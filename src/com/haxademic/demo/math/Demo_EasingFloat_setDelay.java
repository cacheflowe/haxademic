package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.easing.EasingFloat;

public class Demo_EasingFloat_setDelay
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public float easeFactor = 6f;
	protected EasingFloat[] easings;

	public void setup() {
		super.setup();	
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
		PG.setDrawCenter(p);
		p.fill(255);
		for (int i = 0; i < easings.length; i++) {
			EasingFloat easer = easings[i];
			easer.update(true);
			p.ellipse(easer.value(), p.height / (easings.length - 1)  * i, 40, 40);
		}
	}

}
