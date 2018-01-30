package com.haxademic.demo.draw.image;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.SaturationFilter;
import com.haxademic.core.draw.filters.shaders.ThresholdFilter;
import com.haxademic.core.draw.image.ImageSequenceRecorder;
import com.haxademic.core.hardware.webcam.IWebCamCallback;

import processing.core.PImage;

public class Demo_ImageSequenceRecorder 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ImageSequenceRecorder recorder;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 6 );
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
		p.appConfig.setProperty(AppSettings.FILLS_SCREEN, true );
	}
		
	public void setupFirstFrame () {
		p.webCamWrapper.setDelegate(this);
		recorder = new ImageSequenceRecorder(640, 480, 20);
	}

	public void drawApp() {
		p.background( 0 );
		
		p.pushMatrix();
		DrawUtil.setDrawCenter(p);
		DrawUtil.setCenterScreen(p);
		p.image(p.webCamWrapper.getImage(), 0, 0);
		p.popMatrix();
		
		DrawUtil.setDrawCorner(p);
		recorder.drawDebug(p.g);
	}

	@Override
	public void newFrame(PImage frame) {
		// set recorder frame
		recorder.addFrame(p.webCamWrapper.getImage());
		// do some post-processing
		SaturationFilter.instance(p).setSaturation(0);
		SaturationFilter.instance(p).applyTo(recorder.getCurFrame());
		// set debug staus
		p.debugView.setValue("Last WebCam frame", p.frameCount);
	}

}
