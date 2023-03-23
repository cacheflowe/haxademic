package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.image.ImageSequenceRecorder;
import com.haxademic.core.draw.image.ImageSequenceRecorder.IImageSequenceRecorderDelegate;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.system.shell.IScriptCallback;
import com.haxademic.core.system.shell.ScriptRunner;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_ImageSequenceRecorder 
extends PAppletHax
implements IWebCamCallback, IImageSequenceRecorderDelegate, IScriptCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ImageSequenceRecorder recorder;
	protected PGraphics camBuffer;
	protected PGraphics camBufferDesaturated;
	protected boolean shouldRecord = false;
	
	protected ScriptRunner scriptRunner;
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, false );
	}
		
	protected void firstFrame () {
		camBuffer = PG.newPG(640, 480);
		camBufferDesaturated = PG.newPG(640, 480);
//		recorder = new ImageSequenceRecorder(p.width/2, p.height, 60, this);
		recorder = new ImageSequenceRecorder(720, 1280, 60, this);
		WebCam.instance().setDelegate(this);
	}

	protected void drawApp() {
	    p.background(0);
	    
	    // reset recording
	    if(KeyboardState.keyTriggered(' ')) {
	        recorder.reset();
	        shouldRecord = true;
	    }
	    if(KeyboardState.keyTriggered('r')) {
	        recorder.saveToDisk();
//	        recorder.saveToDisk(FileUtil.haxademicOutputPath() + "frames-camera-3");
	    }

	    // record frames on some interval
	    int recordFrameSkip = 1; // 1=60fps, 2=30fps, etc
	    if(shouldRecord && FrameLoop.frameModLooped(recordFrameSkip)) { 
	        recorder.addFrame(camBufferDesaturated);
	    }

	    // draw camera & recording, side-by-side
		ImageUtil.cropFillCopyImage(camBuffer, p.g, 0, 0, p.width/2, p.height, true);
		ImageUtil.cropFillCopyImage(recorder.imageAtFrame(p.frameCount/recordFrameSkip), p.g, p.width/2, 0, p.width/2, p.height, true);
		recorder.updateSave();
		
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
		DebugView.setValue("recorder.recordProgress()", recorder.recordProgress());
		DebugView.setValue("recorder.saveProgress()", recorder.saveProgress());
	}

	///////////////////////////////////////////
	// IWebCamCallback
	///////////////////////////////////////////

	public void newFrame(PImage frame) {
		// set recorder frame - use buffer as intermediary to fix aspect ratio
		ImageUtil.copyImageFlipH(frame, camBuffer);
		DebugView.setValue("Last WebCam frame", p.frameCount);
		
		// add desaturated copy
		ImageUtil.copyImageFlipH(frame, camBufferDesaturated);
		SaturationFilter.instance().setSaturation(0);
		SaturationFilter.instance().applyTo(camBufferDesaturated);
	}

	///////////////////////////////////////////
	// IImageSequenceRecorderDelegate
	///////////////////////////////////////////
	
    public void recordingComplete(ImageSequenceRecorder recorder) {
        P.out("RECORDING COMPLETE");
    }

    public void savedToDisk(ImageSequenceRecorder recorder) {
        P.out("SAVING COMPLETE to", recorder.savePath());
        
        // compile video
        final IScriptCallback self = this;
        new Thread(new Runnable() { public void run() {
            scriptRunner = new ScriptRunner("image-sequence-to-video-tga", self);
            scriptRunner.runWithParams(recorder.savePath(), "60");
        }}).start();
    }

    ///////////////////////////////////////////
    // IScriptCallback
    ///////////////////////////////////////////

    public void scriptComplete() {
        P.out("Script complete!");
    }


}
