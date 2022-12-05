package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.image.ImageSequenceRecorder;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.math.easing.EasingBoolean;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_DepthCameraRegion_ActiveUserRecorder
extends Demo_DepthCameraRegion {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PGraphics playbackPG;
    protected ImageSequenceRecorder recorder;

    protected void config() {
        super.config();
        Config.setProperty(AppSettings.DEPTH_CAM_RGB_ACTIVE, true);
    }
    

	protected void firstFrame() {
		super.firstFrame();
		playbackPG = PG.newPG(p.width / 2, p.height);
		recorder = new ImageSequenceRecorder(playbackPG.width, playbackPG.height, 60);
	}
	
	protected void updateCamera() {
	    PImage camRGB = DepthCamera.instance().camera.getRgbImage();
		// add recorder frame
        recorder.addFrame(camRGB);
        SaturationFilter.instance(p).setSaturation(0);
        SaturationFilter.instance(p).applyTo(recorder.getCurFrame());
	}
	
	protected void drawApp() {
		super.drawApp();
		updateCamera();
		
		// draw camera & recording, side-by-side
		PImage camRGB = DepthCamera.instance().camera.getRgbImage();
        ImageUtil.cropFillCopyImage(camRGB, p.g, 0, 0, p.width/2, p.height, true);
        ImageUtil.cropFillCopyImage(recorder.imageAtFrame(p.frameCount), p.g, p.width/2, 0, p.width/2, p.height, true);

        drawRecordingIndicator();
   	}
	
	protected void drawRecordingIndicator() {
        if(recorder.isRecording()) {
            p.push();
            p.translate(p.width/2 - 100, 40);
            p.fill(255, 0, 0);
            p.arc(30, 30, 60, 60, -P.HALF_PI, -P.HALF_PI + P.TWO_PI * recorder.recordProgress());
            p.pop();
        }
	}
	
    // IEasingBooleanCallback methods 
    
    public void booleanSwitched(EasingBoolean booleanSwitch, boolean value) {
       if(value == true) {
           recorder.reset();
       }
    }
    
}
