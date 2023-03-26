package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.image.ImageSequenceRecorderStraightToDisk;
import com.haxademic.core.draw.image.ImageSequenceRecorderStraightToDisk.IImageSequenceRecorderDelegate;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.system.shell.IScriptCallback;
import com.haxademic.core.system.shell.ScriptRunner;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_ImageSequenceRecorderStraightToDisk 
extends PAppletHax
implements IWebCamCallback, IImageSequenceRecorderDelegate, IScriptCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ImageSequenceRecorderStraightToDisk recorder;
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
		recorder = new ImageSequenceRecorderStraightToDisk(720, 1280, 180, this);
		WebCam.instance().setDelegate(this);
	}

	protected void drawApp() {
	    p.background(0);
	    
	    // reset recording
	    if(KeyboardState.keyTriggered(' ')) {
	        recorder.reset();
	        shouldRecord = true;
	    }

	    // record frames on some interval
	    PGraphics curFramePG = null;
	    int recordFrameSkip = 2; // 1=60fps, 2=30fps, etc
	    if(shouldRecord && FrameLoop.frameModLooped(recordFrameSkip)) {
	        // copy image to buffer
	        curFramePG = recorder.addFrame(camBufferDesaturated);
	        // start threaded saving. curFramePG could be null at this point!
	        if(curFramePG != null) {
	            recorder.saveFrame(curFramePG);
	        }
	    }

	    // draw camera & recording, side-by-side
		ImageUtil.cropFillCopyImage(camBuffer, p.g, 0, 0, p.width/2, p.height, true);
		if(recorder.lastPGSaved() != null) {
		    ImageUtil.cropFillCopyImage(recorder.lastPGSaved(), p.g, p.width/2, 0, p.width/2, p.height, true);
		}
		
		// draw recorder debug
		recorder.drawDebug(p.g);
		
		// draw debug text
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 16);
		FontCacher.setFontOnContext(p.g, font, P.p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		p.text(
		        "Pool active/size: " + recorder.activePGCount() + " / " + recorder.poolSize() + FileUtil.NEWLINE + 
		        "recorder.saveProgress(): " + recorder.saveProgress()
		        , 100, p.height - 70);
		
		// draw recording indicator
		if(recorder.saveProgress() < 1) {
		    p.push();
		    p.translate(30, p.height - 80);
		    p.fill(255, 0, 0);
		    p.arc(30, 30, 60, 60, -P.HALF_PI, -P.HALF_PI + P.TWO_PI * recorder.saveProgress());
		    p.pop();
		}
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
	
    public void savedToDisk(ImageSequenceRecorderStraightToDisk recorder) {
        P.out("SAVING COMPLETE to", recorder.savePath());
        
        // compile video
        final IScriptCallback self = this;
        new Thread(new Runnable() { public void run() {
            scriptRunner = new ScriptRunner("image-sequence-to-video-tga", self);
            scriptRunner.runWithParams(recorder.savePath(), "30");
        }}).start();
    }

    ///////////////////////////////////////////
    // IScriptCallback
    ///////////////////////////////////////////

    public void scriptComplete() {
        // P.out("Script complete!");
    }


}
