package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.image.BufferColorObjectDetection;
import com.haxademic.core.hardware.webcam.IWebCamCallback;

import processing.core.PImage;

public class Demo_BufferColorObjectDetection 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected BufferColorObjectDetection colorObjectDetection;
	protected String COLOR_CLOSENESS_THRESHOLD = "COLOR_CLOSENESS_THRESHOLD";
	protected String COLOR_MIN_POINTS_DETECT_THRESHOLD = "COLOR_MIN_POINTS_DETECT_THRESHOLD";
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 5 );
	}
		
	public void setupFirstFrame () {
		p.webCamWrapper.setDelegate(this);
		p.prefsSliders.addSlider(COLOR_CLOSENESS_THRESHOLD, 0.95f, 0.9f, 1f, 0.001f, false);
		p.prefsSliders.addSlider(COLOR_MIN_POINTS_DETECT_THRESHOLD, 10, 5, 100, 1, false);
	}

	public void drawApp() {
		// set up context
		p.background(0);
		p.noStroke();
		
		// draw difference to screen
		if(colorObjectDetection != null) {
			p.fill(255);
			p.image(colorObjectDetection.sourceBuffer(), 0, 0);
			p.image(colorObjectDetection.outputBuffer(), colorObjectDetection.sourceBuffer().width, 0);
			
			p.fill(colorObjectDetection.colorCompare());
			p.rect(0, colorObjectDetection.outputBuffer().height, colorObjectDetection.outputBuffer().width * 2, 40);
		}
	}

	@Override
	public void newFrame(PImage frame) {
		// lazy-init color detection and update it with incoming webcam frames
		if(colorObjectDetection == null) colorObjectDetection = new BufferColorObjectDetection(frame, 0.25f);
		colorObjectDetection.colorClosenessThreshold(p.prefsSliders.value(COLOR_CLOSENESS_THRESHOLD));
		colorObjectDetection.minPointsThreshold((int) p.prefsSliders.value(COLOR_MIN_POINTS_DETECT_THRESHOLD));
		colorObjectDetection.debugging(true);
		colorObjectDetection.update(frame);
	}
	
	public void mousePressed() {
		super.mousePressed();
		if(colorObjectDetection != null) {
			colorObjectDetection.setColorFromSource(p.mouseX, p.mouseY);
		}
	}

}
