package com.haxademic.core.draw.image;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.Texture;

public class ImageSequenceMovieClip {

	static public PImage BLANK_IMAGE;

	ArrayList<String> imagesToLoad = null;
	String imagesDir;
	int loadStartTime;
	int loadFinishTime = -1;
	int numImages = 0;
	int imagesLoaded = 0;
	boolean debug = false;
	int preCacheFrame = 0;
	
	ArrayList<PImage> imageSequence;
	static float DEFAULT_FPS = 30;
	float fps;
	int curFrame = -1;
	int startTime = -1;
	int pauseTime = -1;
	int[] frameIndexPlaybackSequence = null;
	float playbackProgress = 0;
	
	boolean isFlipped = false;
	boolean isPlaying = false;
	boolean isLooping = false;
	
	int tint;
	

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
		if(debug == true) P.println("Loading for:", imagesDir);
		loadImages(imagesDir, format);
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
	
	public void setFramesSequence(int[] framesSequence) {
		this.frameIndexPlaybackSequence = framesSequence;
	}
	
	public void setTint(int tintColor) {
		tint = tintColor;
	}
	
	public int tint() {
		return tint;
	}
	
	public PImage getFrame(int index) {
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
		return imagesLoaded == numImages;
	}
	
	public float loadProgress() {
		return (float)imagesLoaded / (float)numImages;
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

	public void stop() {
		reset();
	}
	
	public void pause() {
		if(pauseTime == -1) pauseTime = P.p.millis();
	}
	
	public void unpause() {
		pauseTime = -1;
	}
	
	public void seek(float progress) {
		curFrame = P.round(progress * (playbackFrames() - 1));
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
	
	public int numImageFiles() {
		return numImages;
	}
	
	public int numImagesLoaded() {
		return imagesLoaded;
	}
	
	public PImage image() {
		if(isPlaying == false || playbackFrames() <= curFrame) {
			return BLANK_IMAGE;
		} else {
			if(frameIndexPlaybackSequence != null) {
				isFlipped = (frameIndexPlaybackSequence[curFrame] < 0);
				int frameIndex = P.abs(frameIndexPlaybackSequence[curFrame]);
				if(frameIndex < imageSequence.size()) {
					return imageSequence.get(frameIndex);
				} else {
					return BLANK_IMAGE;	
				}
			} else {
				if(curFrame < imageSequence.size()) {
					return imageSequence.get(curFrame);
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
			numImages = imagesToLoad.size();
		}}).start();	
	}
	
	boolean isBusy = false;
	public void loadNextImage() {
		if(imagesToLoad == null) return;
		if(imagesToLoad.size() == 0) return;
		if(isBusy == true) return;
		isBusy = true;
		
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
			} else {
				if(debug == true) P.println("loading image "+imagesLoaded+" / "+numImages+" :: "+imagesToLoad.get(0));
			}
		}}).start();
	}
	
	public void preCacheImages() {
		if(preCacheFrame < imageSequence.size() && preCacheFrame != -1) {
			P.p.image(imageSequence.get(preCacheFrame), P.p.width * 3, P.p.height * 3, 10, 10);
//			P.p.image(imageSequence.get(preCacheFrame), 0, 0, imageSequence.get(preCacheFrame).width, imageSequence.get(preCacheFrame).height);
			P.println("Pre-caching frame: ", preCacheFrame);
			preCacheFrame++;
		} else if(preCacheFrame == numImages && preCacheFrame != -1) {
			 P.println("Done pre-caching");
			preCacheFrame = -1;
		}
	}
	
	///////////////////////////////////
	// Cleanup
	///////////////////////////////////
	
	public void dispose() {
		// https://forum.processing.org/two/discussion/6898/how-to-correctly-release-pimage-memory
		// https://github.com/jeffThompson/ProcessingTeachingSketches/blob/master/Utilities/AvoidPImageMemoryLeaks/AvoidPImageMemoryLeaks.pde
		// https://forum.processing.org/one/topic/pimage-memory-leak-example.html
		for (int i = 0; i < imageSequence.size(); i++) {
			Object cache = P.p.g.getCache(imageSequence.get(i));
			P.p.g.removeCache(imageSequence.get(i));
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
		return (frameIndexPlaybackSequence != null) ? frameIndexPlaybackSequence.length : numImages;
	}
	
	public void update() {
		if(isLoaded() == false) {
			loadNextImage();
		} else {
			if(isPlaying == true && isPaused() == false) {
				playbackProgress = (P.p.millis() - startTime) / 1000f;
				if(playbackProgress > duration()) {
					resetPlayhead();
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
