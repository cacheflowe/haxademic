package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.image.ImageSequenceMovieClip;
import com.haxademic.core.draw.image.ImageSequenceMovieClip.IImageSequenceMovieClipDelegate;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.hardware.mouse.Mouse;

public class Demo_ImageSequenceMovieClip
extends PAppletHax
implements IImageSequenceMovieClipDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected ImageSequenceMovieClip imageSequence;
	protected ImageSequenceMovieClip imageSequenceCopy;
	protected ImageSequenceMovieClip imageSequenceManual;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1344 );
		Config.setProperty( AppSettings.HEIGHT, 700 );
		Config.setProperty( AppSettings.FULLSCREEN, false );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		String imagePath = FileUtil.getPath("haxademic/images/floaty-blob.anim/");
		imageSequence = new ImageSequenceMovieClip(imagePath, "png", 18).setDelegate(this);
	}
	
	protected void drawApp() {
		p.background(0);
		
		if(p.frameCount % 100 == 50) {
			if(Mouse.xNorm < 0.5f)
				imageSequence.play();
			else 
				imageSequence.loop();
		}
		imageSequence.preCacheImages(p.g);
		imageSequence.update();
		
		p.image(imageSequence.image(), 0, 0);
		
		DebugView.setValue("imageSequence.isFinished()", imageSequence.isFinished());

		// create a copy & play it offset
		if(imageSequenceCopy == null && imageSequence.numImages() > 0) {
			imageSequenceCopy = imageSequence.copy();
			imageSequenceManual = imageSequence.copy();
		}
		if(imageSequenceCopy != null) {
			if(p.frameCount % 100 == 70) {
				imageSequenceCopy.play();
			}
			imageSequenceCopy.update();
			p.image(imageSequenceCopy.image(), 0, 50);
			DebugView.setValue("imageSequenceCopy.isFinished()", imageSequenceCopy.isFinished());
			
			// manual copy
			p.image(imageSequenceManual.getFrameByProgress(Mouse.xNorm), 0, 100);
		}
		
		
		// test seek methods
		if(KeyboardState.keyTriggered(' ')) {
			imageSequence.play();
//			imageSequence.setFrame(9);
//			imageSequence.setFrameByProgress(0.5f);
			imageSequence.seek(0.5f);
			P.out("numFrames", imageSequence.numImages());
		}
	}
	
	////////////////////////////////
	// IImageSequenceMovieClipDelegate methods
	////////////////////////////////
	
	public void movieClipHasFileList(ImageSequenceMovieClip movieClip) {
		P.out("IImageSequenceMovieClipDelegate.movieClipLoaded()", movieClip.numImages());
	}

	public void movieClipLoaded(ImageSequenceMovieClip movieClip) {
		P.out("IImageSequenceMovieClipDelegate.movieClipLoaded()", movieClip.numImages());
	}
	
	public void movieClipPreCached(ImageSequenceMovieClip movieClip) {
		P.out("IImageSequenceMovieClipDelegate.movieClipPreCached()", movieClip.numImages());
	}
	
	public void movieClipFinished(ImageSequenceMovieClip movieClip) {
		P.out("IImageSequenceMovieClipDelegate.movieClipFinished()");
	}
	
}
