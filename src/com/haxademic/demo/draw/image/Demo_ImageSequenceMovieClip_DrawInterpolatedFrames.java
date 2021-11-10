package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageSequenceMovieClip;
import com.haxademic.core.draw.image.ImageSequenceMovieClip.IImageSequenceMovieClipDelegate;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;

import processing.core.PImage;

public class Demo_ImageSequenceMovieClip_DrawInterpolatedFrames
extends PAppletHax
implements IImageSequenceMovieClipDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected ImageSequenceMovieClip imageSequence;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 720 );
	}

	protected void firstFrame() {
		String imagePath = FileUtil.getPath("haxademic/images/floaty-blob.anim/");
		imageSequence = new ImageSequenceMovieClip(imagePath, "png", 18).setDelegate(this);
	}
	
	protected void drawApp() {
		p.background(0);

		// cache images
		imageSequence.preCacheImages(p.g);
		imageSequence.update();
		
		// draw interploated frames
		if(imageSequence.isLoaded()) {
			float interpProgress = Mouse.xNorm * (imageSequence.numImages() - 1);
			for (int i = 0; i < imageSequence.numImages(); i++) {
				PImage img = imageSequence.getFrame(i);
				float distFromIndex = P.abs(i - interpProgress);
				if(distFromIndex < 1f) {
					PG.setPImageAlpha(p, 1f - distFromIndex);
					p.image(img, 0, 0);
					PG.resetPImageAlpha(p);
				}
			}
			
			DebugView.setValue("interpProgress", interpProgress);
			DebugView.setValue("imageSequence.numImages()", imageSequence.numImages());
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
