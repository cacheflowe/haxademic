package com.haxademic.core.draw.image;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.Texture;

public class ImageSequenceMovieClip {

	public interface IImageSequenceMovieClipDelegate {
		public void movieClipHasFileList(ImageSequenceMovieClip movieClip);
		public void movieClipLoaded(ImageSequenceMovieClip movieClip);
		public void movieClipPreCached(ImageSequenceMovieClip movieClip);
		public void movieClipFinished(ImageSequenceMovieClip movieClip);
	}
	
	static public PImage BLANK_IMAGE;

	protected IImageSequenceMovieClipDelegate delegate;
	protected ArrayList<String> imagesToLoad = null;
	protected String imagesDir;
	protected int loadStartTime;
	protected int loadFinishTime = -1;
	protected int totalImages = 0;
	protected int imagesLoaded = 0;
	protected boolean debug = false;
	protected int preCacheFrame = 0;
	
	protected ArrayList<PImage> imageSequence;
	protected static float DEFAULT_FPS = 30;
	protected float fps;
	protected int curFrame = -1;
	protected int startTime = -1;
	protected int pauseTime = -1;
	protected int[] frameIndexPlaybackSequence = null;
	protected float playbackProgress = 0;
	
	protected boolean isFlipped = false;
	protected boolean isPlaying = false;
	protected boolean isLooping = false;
	
	protected int tint;
	

	/*
	 * 	Usage:
	 * 	ImageSequenceMovieClip clip = new ImageSequenceMovieClip("video/test/blast_hole_alpha/", "png"); 
	 **/

	public ImageSequenceMovieClip(String imagesDir, String format, float fps, int[] framesSequence) {
		this.fps = fps;
		this.frameIndexPlaybackSequence = framesSequence;
		if(BLANK_IMAGE == null) BLANK_IMAGE = P.p.createImage(32, 32, P.ARGB);
		imageSequence = new ArrayList<PImage>();
		tint = P.p.color(255);
		if(imagesDir != null) {
			if(debug == true) P.println("Loading for:", imagesDir);
			loadImages(imagesDir, format);
		}
	}
	
	public ImageSequenceMovieClip(String imagesDir, String format, float fps) {
		this(imagesDir, format, fps, null);
	}
	
	public ImageSequenceMovieClip(String imagesDir, String format, int[] framesSequence) {
		this(imagesDir, format, DEFAULT_FPS, framesSequence);
	}
	
	public ImageSequenceMovieClip(String imagesDir, String format) {
		this(imagesDir, format, DEFAULT_FPS, null);
	}

	public ImageSequenceMovieClip(ArrayList<PImage> images, float fps) {	// for already-loaded images array
		this(null, null, fps, null);
		imageSequence = images;
		imagesLoaded = totalImages = imageSequence.size();
	}
	
	public ImageSequenceMovieClip(ArrayList<PImage> images, float fps, int numFrames) {		// for copy() - will load as original loads
		this(null, null, fps, null);
		imageSequence = images;
		imagesLoaded = totalImages = numFrames;
	}
	
	public ImageSequenceMovieClip setDelegate(IImageSequenceMovieClipDelegate delegate) {
		this.delegate = delegate;
		return this;
	}
	
	public ImageSequenceMovieClip setFramesSequence(int[] framesSequence) {
		this.frameIndexPlaybackSequence = framesSequence;
		return this;
	}
	
	public ImageSequenceMovieClip copy() {
		if(totalImages == 0) P.error("ImageSequenceMovieClip.copy() must be called after threaded image loading has started on the original");
		return new ImageSequenceMovieClip(imageSequence, fps, totalImages);
	}
	
	public void setFps(float fps) {
		this.fps = fps;
	}
	
	public void setTint(int tintColor) {
		tint = tintColor;
	}
	
	public int tint() {
		return tint;
	}
	
	public PImage getFrame(int index) {
		index = index % imageSequence.size();
		if(index < imageSequence.size()) {
			return imageSequence.get(index);
		} else {
			return BLANK_IMAGE;
		}
	}
	
	public PImage getFrameByProgress(float progress) {
		int index = P.floor(progress * (float) totalImages) % totalImages;
		if(index < imageSequence.size()) {
			return imageSequence.get(index);
		} else {
			return BLANK_IMAGE;
		}
	}
	
	///////////////////////////////////
	// Load/Play state
	///////////////////////////////////
	
	public boolean isLoaded() {
		return imagesLoaded == totalImages;
	}
	
	public float loadProgress() {
		return (float)imagesLoaded / (float)totalImages;
	}
	
	public boolean isFlipped() {
		return isFlipped;
	}
	
	public void setFlipped(boolean flipped) {
		isFlipped = flipped;
	}
	
	public boolean isPlaying() {
		return isPlaying;
	}
	
	public boolean isPaused() {
		return (pauseTime != -1);
	}
	
	public float progress() {
		return playbackProgress / duration();
	}
	
	public int loadedTime() {
		return loadFinishTime;
	}
	
	public void loop() {
		unpause();
		play();
		isLooping = true;
	}
	
	public void stopLooping() {
		isLooping = false;
	}
	
	public void play() {
		isPlaying = true;
		isLooping = false;
		if(isPaused() == true) {
			startTime = P.p.millis() - (pauseTime - startTime);
		} else {
			curFrame = 0;
			startTime = P.p.millis();
		}
		unpause();
	}
	
	public void playFromStart() {
		reset();
		play();
	}
	
	public void reset() {
		isPlaying = false;
		isLooping = false;
		unpause();
		curFrame = 0;
		playbackProgress = 0;
		startTime = -1;
	}
	
	// TODO: clean this up w/reset()
	protected void resetPlayhead() {
		if(isLooping == true) {
			playbackProgress = 0;
			curFrame = 0;
			startTime = P.p.millis();
		} else {
			reset();
			isPlaying = false;
			startTime = -1;
		}
	}

	public void seek(float progress) {
		curFrame = P.round(progress * (playbackFrames() - 1));
		setStartTimeFromCurFrame();
	}
	
	public void setFrame(int frameIndex) {
		curFrame = P.constrain(frameIndex, 0, playbackFrames() - 1);
		setStartTimeFromCurFrame();
	}
	
	public void setFrameByProgress(float progress) {
		curFrame = P.floor(progress * (float) totalImages) % totalImages;
		setStartTimeFromCurFrame();
	}
	
	protected void setStartTimeFromCurFrame() {
		// helper to enable seeking with the above methods
		startTime = P.round(P.p.millis() - curFrame * (1f / fps) * 1000f);
	}
	
	public void stop() {
		reset();
	}
	
	public void pause() {
		if(pauseTime == -1) pauseTime = P.p.millis();
	}
	
	public void unpause() {
		pauseTime = -1;
	}
	
	public int curFrame() {
		return curFrame;
	}
	
	public float curTime() {
		return playbackProgress;
	}
		
	public float duration() {
		return playbackFrames() / fps;
	}
	
	public boolean isFinished() {
		return curFrame == playbackFrames() - 1;
	}
	
	public int numImages() {
		return totalImages;
	}
	
	public int numImagesLoaded() {
		return imagesLoaded;
	}
	
	public PImage image() {
		int safeCurFrame = (imageSequence.size() > 0) ? P.constrain(curFrame, 0, imageSequence.size() - 1) : 0;
		if(isPlaying == false || playbackFrames() <= safeCurFrame) {
			return BLANK_IMAGE;
		} else {
			if(frameIndexPlaybackSequence != null) {
				isFlipped = (frameIndexPlaybackSequence[safeCurFrame] < 0);
				int frameIndex = P.abs(frameIndexPlaybackSequence[safeCurFrame]);
				if(frameIndex < imageSequence.size()) {
					return imageSequence.get(frameIndex);
				} else {
					return BLANK_IMAGE;	
				}
			} else {
				if(safeCurFrame < imageSequence.size()) {
					return imageSequence.get(safeCurFrame);
				} else {
					return BLANK_IMAGE;
				}
			}
		}
	}

	///////////////////////////////////
	// Load images
	///////////////////////////////////
	
	public void loadImages(String imageDir, String format) {
		imagesDir = imageDir;
		loadStartTime = P.p.millis();
		imagesLoaded = 0;
		
		new Thread(new Runnable() { public void run() {
			// dump previous images
//			dispose();
//			System.gc();

			// read next directory
			imagesToLoad = FileUtil.getFilesInDirOfType(imageDir, format);
			totalImages = imagesToLoad.size();
		}}).start();	
	}
	
	boolean isBusy = false;
	public void loadNextImage() {
		// if we're  not done or busy, keep loading
		if(imagesToLoad == null) return;
		if(imagesToLoad.size() == 0) return;
		if(isBusy == true) return;
		isBusy = true;
		
		// load next image on a separate thread
		ImageSequenceMovieClip self = this;
		new Thread(new Runnable() { public void run() {
//			PImage loadedImg = P.p.requestImage(imagesDir + imagesToLoad.get(0));
			PImage loadedImg = P.p.loadImage(imagesDir + imagesToLoad.get(0));
			imageSequence.add(loadedImg);
			imagesToLoad.remove(0);
			imagesLoaded++;
			isBusy = false;
			if(imagesToLoad.size() == 0) {
				loadFinishTime = P.p.millis();
				imagesToLoad = null;
				if(debug == true) P.println("Sequence Load Time: "+((P.p.millis() - loadStartTime)/1000) + " seconds");
				if(delegate != null) delegate.movieClipLoaded(self);
			} else {
				if(debug == true) P.println("loading image "+imagesLoaded+" / "+totalImages+" :: "+imagesToLoad.get(0));
			}
		}}).start();
	}
	
	public void preCacheImages(PGraphics pg) {
		if(preCacheFrame < imageSequence.size() && preCacheFrame != -1) {
			// draw image to buffer, which caches into Processing's PGrpahics Texture cache
			// doing this once, upfront will make performance faster after everything's loaded
			pg.image(imageSequence.get(preCacheFrame), pg.width * 3, pg.height * 3, 10, 10);
			preCacheFrame++;
		} else if(preCacheFrame == totalImages && preCacheFrame != -1) {
			if(delegate != null) delegate.movieClipPreCached(this);
			preCacheFrame = -1;
		}
	}
	
	///////////////////////////////////
	// Cleanup
	///////////////////////////////////
	
	public void dispose(PGraphics pg) {
		// https://forum.processing.org/two/discussion/6898/how-to-correctly-release-pimage-memory
		// https://github.com/jeffThompson/ProcessingTeachingSketches/blob/master/Utilities/AvoidPImageMemoryLeaks/AvoidPImageMemoryLeaks.pde
		// https://forum.processing.org/one/topic/pimage-memory-leak-example.html
		for (int i = 0; i < imageSequence.size(); i++) {
			Object cache = pg.getCache(imageSequence.get(i));
			pg.removeCache(imageSequence.get(i));
			if (cache instanceof Texture) {
				((Texture) cache).disposeSourceBuffer();
			}
		}
		imageSequence.clear();
	}
	
	///////////////////////////////////
	// Animation
	///////////////////////////////////
	
	protected int playbackFrames() {
		return (frameIndexPlaybackSequence != null) ? frameIndexPlaybackSequence.length : totalImages;
	}
	
	public void update() {
		if(isLoaded() == false) {
			loadNextImage();
		} else {
			if(isPlaying == true && isPaused() == false) {
				playbackProgress = (P.p.millis() - startTime) / 1000f;
				if(playbackProgress > duration()) {
					resetPlayhead();
					if(delegate != null) delegate.movieClipFinished(this);
				}
				curFrame = P.floor(playbackProgress * fps);
			}
		}
	}
	
	public void drawToPGraphics(PGraphics pg, float x, float y, float scale) {
		pg.pushMatrix();
		pg.translate(x, y);
		if(isFlipped == true) pg.scale(-1, 1);
		pg.image(image(), 0, 0, image().width * scale, image().height * scale);
		pg.popMatrix();
	}
	
}
