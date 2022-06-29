package com.haxademic.demo.draw.cv;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.cv.ColorObjectDetection;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.ui.UI;

import processing.core.PImage;

public class Demo_ColorObjectDetection 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ColorObjectDetection colorObjectDetection;
	protected String COLOR_CLOSENESS_THRESHOLD = "COLOR_CLOSENESS_THRESHOLD";
	protected String COLOR_MIN_POINTS_DETECT_THRESHOLD = "COLOR_MIN_POINTS_DETECT_THRESHOLD";
	
	protected void firstFrame () {
		// init webcam
		WebCam.instance().setDelegate(this);
		
		// add UI
		UI.addTitle("ColorObjectDetection config");
		UI.addSlider(COLOR_CLOSENESS_THRESHOLD, 0.95f, 0.9f, 1f, 0.001f, false);
		UI.addSlider(COLOR_MIN_POINTS_DETECT_THRESHOLD, 10, 5, 100, 1, false);
	}

	protected void drawApp() {
		// set up context
		p.background(0);
		p.noStroke();
		
		// draw difference to screen
		if(colorObjectDetection != null) {
			// draw small images that do the work
			p.fill(255);
			p.image(colorObjectDetection.sourceBuffer(), 0, 0);
			p.image(colorObjectDetection.analysisBuffer(), colorObjectDetection.sourceBuffer().width, 0);
			
			p.fill(colorObjectDetection.colorCompare());
			p.rect(0, colorObjectDetection.analysisBuffer().height, colorObjectDetection.analysisBuffer().width * 2, 20);
			
			// use results to draw on top of original image
			p.push();
			p.translate(0, 150);
			p.image(WebCam.instance().image(), 0, 0);
			p.fill(0, 255, 0);
			p.ellipse(colorObjectDetection.x() * WebCam.instance().image().width, colorObjectDetection.y() * WebCam.instance().image().height, 30, 30);
			p.pop();
		}
	}

	@Override
	public void newFrame(PImage frame) {
		// lazy-init color detection and update it with incoming webcam frames
		if(colorObjectDetection == null) colorObjectDetection = new ColorObjectDetection(frame, 0.15f);
		colorObjectDetection.colorClosenessThreshold(UI.value(COLOR_CLOSENESS_THRESHOLD));
		colorObjectDetection.minPointsThreshold((int) UI.value(COLOR_MIN_POINTS_DETECT_THRESHOLD));
		colorObjectDetection.debugging(true);
		colorObjectDetection.update(frame);
		DebugView.setValue("colorObjectDetection.isActive()", colorObjectDetection.isActive());
	}
	
	public void mousePressed() {
		super.mousePressed();
		if(colorObjectDetection != null) {
			colorObjectDetection.setColorFromSource(p.mouseX, p.mouseY);
		}
	}

}
