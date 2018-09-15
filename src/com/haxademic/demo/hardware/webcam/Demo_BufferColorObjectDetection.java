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
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 70 );
	}
		
	public void setupFirstFrame () {
		p.webCamWrapper.setDelegate(this);
	}

	public void drawApp() {
		// set up context
		p.background(0);
		
		// draw difference to screen
		if(colorObjectDetection != null) {
			p.fill(255);
			p.image(colorObjectDetection.sourceBuffer(), 0, 0);
			p.image(colorObjectDetection.outputBuffer(), colorObjectDetection.sourceBuffer().width, 0);
			p.fill(colorObjectDetection.colorCompare());
			p.rect(0, colorObjectDetection.outputBuffer().height, colorObjectDetection.outputBuffer().width, 10);
		}
	}

	@Override
	public void newFrame(PImage frame) {
		if(colorObjectDetection == null) {
			colorObjectDetection = new BufferColorObjectDetection(frame, 0.2f);
		}
		colorObjectDetection.update(frame);
	}
	
	public void mousePressed() {
		super.mousePressed();
		if(colorObjectDetection != null) {
			colorObjectDetection.setColorFromSource(p.mouseX, p.mouseY);
		}
	}

}
