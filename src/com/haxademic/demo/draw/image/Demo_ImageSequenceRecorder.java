package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.image.ImageSequenceRecorder;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.keyboard.KeyboardState;
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
	protected boolean shouldRecord = false;
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, false );
	}
		
	protected void firstFrame () {
		camBuffer = PG.newPG(640, 480);
		recorder = new ImageSequenceRecorder(p.width/2, p.height, 60);
		WebCam.instance().setDelegate(this);
	}

	protected void drawApp() {
	    p.background(0);
	    
	    // reset recording
	    if(KeyboardState.keyTriggered(' ')) {
	        recorder.reset();
	        shouldRecord = true;
	    }

	    // draw camera & recording, side-by-side
		ImageUtil.cropFillCopyImage(camBuffer, p.g, 0, 0, p.width/2, p.height, true);
		ImageUtil.cropFillCopyImage(recorder.imageAtFrame(p.frameCount/2), p.g, p.width/2, 0, p.width/2, p.height, true);
		
		// draw recording indicator
		if(recorder.isRecording()) {
		    p.push();
		    p.translate(p.width/2 - 100, 40);
		    p.fill(255, 0, 0);
		    p.arc(30, 30, 60, 60, -P.HALF_PI, -P.HALF_PI + P.TWO_PI * recorder.recordProgress());
		    p.pop();
		}
		
		// draw recorder debug
		recorder.drawDebug(p.g);
	}

	@Override
	public void newFrame(PImage frame) {
		// set recorder frame - use buffer as intermediary to fix aspect ratio
		ImageUtil.copyImageFlipH(frame, camBuffer);
		DebugView.setValue("Last WebCam frame", p.frameCount);
		
		// record if we've triggered it
		if(shouldRecord) { 
    		recorder.addFrame(camBuffer);
    		// do some post-processing
    		SaturationFilter.instance(p).setSaturation(0);
    		SaturationFilter.instance(p).applyTo(recorder.getCurFrame());
		}
	}

}
