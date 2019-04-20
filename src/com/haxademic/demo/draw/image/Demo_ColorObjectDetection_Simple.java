package com.haxademic.demo.draw.image;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.ColorObjectDetection;

public class Demo_ColorObjectDetection_Simple 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ColorObjectDetection colorObjectDetection;
	protected String COLOR_CLOSENESS_THRESHOLD = "COLOR_CLOSENESS_THRESHOLD";
	protected String COLOR_MIN_POINTS_DETECT_THRESHOLD = "COLOR_MIN_POINTS_DETECT_THRESHOLD";
	
	public void setupFirstFrame () {
		p.ui.addSlider(COLOR_CLOSENESS_THRESHOLD, 0.95f, 0.9f, 1f, 0.001f, false);
		p.ui.addSlider(COLOR_MIN_POINTS_DETECT_THRESHOLD, 10, 1, 100, 1, false);
	}

	public void drawApp() {
		// set up context
		p.background(0);
		p.noStroke();
		
		// draw mouse point to offscreen buffer
		pg.beginDraw();
		pg.background(0);
		pg.noStroke();
		pg.fill(255, 0, 0);
		DrawUtil.setDrawCenter(pg);
		pg.ellipse(p.mouseX, p.mouseY, 100, 100);
		pg.endDraw();
		
		// lazy-init color detection and update it with `pg`
		if(colorObjectDetection == null) {
			float detectionScaleDown = 0.25f;
			colorObjectDetection = new ColorObjectDetection(pg, detectionScaleDown);
		}
		colorObjectDetection.colorClosenessThreshold(p.ui.value(COLOR_CLOSENESS_THRESHOLD));
		colorObjectDetection.minPointsThreshold((int) p.ui.value(COLOR_MIN_POINTS_DETECT_THRESHOLD));
		colorObjectDetection.setColorCompare(1, 0, 0);
		colorObjectDetection.debugging(true);
		colorObjectDetection.update(pg);
		
		// draw debug view to screen
		p.stroke(255);
		p.noFill();
		p.image(colorObjectDetection.sourceBuffer(), 0, 0);
		p.rect(0, 0, colorObjectDetection.sourceBuffer().width, colorObjectDetection.sourceBuffer().height);
		p.image(colorObjectDetection.analysisBuffer(), colorObjectDetection.sourceBuffer().width, 0);
		p.rect(colorObjectDetection.sourceBuffer().width, 0, colorObjectDetection.analysisBuffer().width, colorObjectDetection.analysisBuffer().height);
		
		// set debug values
		p.debugView.setValue("BufferColorObjectDetection x", colorObjectDetection.x());
		p.debugView.setValue("BufferColorObjectDetection y", colorObjectDetection.y());
	}

}
