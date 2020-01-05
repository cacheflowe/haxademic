package com.haxademic.demo.draw.image;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.image.ImageSequenceRecorder;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_ImageSequenceRecorder 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ImageSequenceRecorder recorder;
	protected PGraphics camBuffer;
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true );
		Config.setProperty(AppSettings.FILLS_SCREEN, true );
	}
		
	protected void firstFrame () {
		camBuffer = p.createGraphics(640, 480, PRenderers.P3D);
		recorder = new ImageSequenceRecorder(camBuffer.width, camBuffer.height, 20);
		WebCam.instance().setDelegate(this);
	}

	protected void drawApp() {
		p.background( 0 );
				
		p.pushMatrix();
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
		p.image(camBuffer, 0, 0);
		p.popMatrix();
		
		PG.setDrawCorner(p);
		recorder.drawDebug(p.g);
	}

	@Override
	public void newFrame(PImage frame) {
		// set recorder frame - use buffer as intermediary to fix aspect ratio
		ImageUtil.copyImageFlipH(frame, camBuffer);
		recorder.addFrame(camBuffer);
		// do some post-processing
		SaturationFilter.instance(p).setSaturation(0);
		SaturationFilter.instance(p).applyTo(recorder.getCurFrame());
		// set debug staus
		DebugView.setValue("Last WebCam frame", p.frameCount);
	}

}
