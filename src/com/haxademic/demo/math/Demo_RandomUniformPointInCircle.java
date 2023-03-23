package com.haxademic.demo.math;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.ui.UI;

import processing.core.PVector;

public class Demo_RandomUniformPointInCircle
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String WEIGHT = "WEIGHT";
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);
	}

	protected void firstFrame() {
		UI.addSlider(WEIGHT, 0.5f, 0, 2, 0.01f, false);
	}

	protected void drawApp() {
		// fade  out
		if(p.frameCount == 1) background(0);
		BrightnessStepFilter.instance().setBrightnessStep(-3/255f);
		BrightnessStepFilter.instance().setOnContext(p);
		PG.setDrawCenter(p);
		p.noStroke();
		PG.setCenterScreen(p);

		// draw 10 random points
		for(int i=0; i < 10; i++) {
			// random, evenly-distributed circle position
			float circleSize = p.width * 0.3f;
			PVector circlePos = MathUtil.randCircleCoordinate(circleSize, UI.value(WEIGHT));

			// draw!
			p.fill(255);
			p.ellipse(circlePos.x, circlePos.y, 10, 10);
		}
	}
}
