package com.haxademic.demo.draw.image;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.OpticalFlow;

public class Demo_OpticalFlow 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected OpticalFlow opticalFlow;

	public void setupFirstFrame () {
		//		p.prefsSliders.addSlider(COLOR_CLOSENESS_THRESHOLD, 0.95f, 0.9f, 1f, 0.001f, false);
		//		p.prefsSliders.addSlider(COLOR_MIN_POINTS_DETECT_THRESHOLD, 10, 1, 100, 1, false);
	}

	public void drawApp() {
		// set up context
		p.background(0);
		p.noStroke();

		// draw mouse point to offscreen buffer
		pg.beginDraw();
		pg.background(0);
		pg.noStroke();
		pg.fill(255);
		DrawUtil.setDrawCenter(pg);
		pg.ellipse(p.mouseX, p.mouseY, 80, 80);
		pg.endDraw();

		// lazy-init color detection and update it with `pg`
		if(opticalFlow == null) {
			float detectionScaleDown = 0.15f;
			opticalFlow = new OpticalFlow(pg, detectionScaleDown);
		}
		opticalFlow.smoothing(0.02f);
		opticalFlow.update(pg);

		// draw input view to screen
		p.image(pg, 0, 0);
		
		// draw debug flow results
		opticalFlow.debugDraw(p.g);
		
		// check vector getter for a specific position
		float[] vecResult = opticalFlow.getVectorAt(0.5f, 0.5f);
		p.debugView.setValue("OpticalFlow.getVectorAt(0.5f, 0.5f) x", vecResult[0]);
		p.debugView.setValue("OpticalFlow.getVectorAt(0.5f, 0.5f) y", vecResult[1]);
		vecResult = opticalFlow.getVectorAt(1f, 1f);
		p.debugView.setValue("OpticalFlow.getVectorAt(1f, 1f) x", vecResult[0]);
		p.debugView.setValue("OpticalFlow.getVectorAt(1f, 1f) y", vecResult[1]);
		vecResult = opticalFlow.getVectorAt(0f, 0f);
		p.debugView.setValue("OpticalFlow.getVectorAt(0f, 0f) x", vecResult[0]);
		p.debugView.setValue("OpticalFlow.getVectorAt(0f, 0f) y", vecResult[1]);
	}

}
