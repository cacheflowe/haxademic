package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;

public class Demo_RandomUniformPointInCircle
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
//		Config.setProperty(AppSettings.SHOW_UI, true);
	}

	protected void firstFrame() {
	}

	protected void drawApp() {
		// fade 
		if(p.frameCount == 1) background(0);
		BrightnessStepFilter.instance(p).setBrightnessStep(-1/255f);
		BrightnessStepFilter.instance(p).applyTo(p);
		PG.setDrawCenter(p);
		p.noStroke();
		PG.setCenterScreen(p);

		for(int i=0; i < 10; i++) {
			// random point
			// https://twitter.com/incre_ment/status/1453728837829681155
			float randRads = P.p.random(0, P.TWO_PI);
			float radius = 240f * P.sqrt(P.p.random(1));
			
			p.fill(255);
			p.ellipse(P.cos(randRads) * radius, P.sin(randRads) * radius, 10, 10);
		}
	}
}
