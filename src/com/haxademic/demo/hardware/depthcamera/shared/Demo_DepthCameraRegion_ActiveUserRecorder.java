package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.image.ImageFramesHistory;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.math.easing.EasingBoolean;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_DepthCameraRegion_ActiveUserRecorder
extends Demo_DepthCameraRegion {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PGraphics playbackPG;
    protected ImageFramesHistory recorder;

	protected void firstFrame() {
		super.firstFrame();
		playbackPG = PG.newPG(p.width / 2, p.height);
		recorder = new ImageFramesHistory(playbackPG.width, playbackPG.height, 60);
	}
	
	protected void drawForcedPerspectiveBox() {
	    PImage camRGB = DepthCamera.instance().camera.getRgbImage();
		// add recorder frame
        recorder.addFrame(camRGB);
        SaturationFilter.instance(p).setSaturation(0);
        SaturationFilter.instance(p).applyTo(recorder.getCurFrame());
	}
	
	protected void drawApp() { 
		super.drawApp();
		drawForcedPerspectiveBox();
	}
	
    // IEasingBooleanCallback methods 
    
    public void booleanSwitched(EasingBoolean booleanSwitch, boolean value) {
       if(value == true) {
           recorder.reset();
           
       }
    }
    
}
