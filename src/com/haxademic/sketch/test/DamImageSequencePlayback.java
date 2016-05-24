package com.haxademic.sketch.test;

import java.util.ArrayList;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.image.filters.shaders.ChromaColorFilter;
import com.haxademic.core.image.filters.shaders.PixelateFilter;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.Texture;

public class DamImageSequencePlayback
extends PAppletHax {

	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	int imgW;
	int imgH;
	
	ArrayList<ImageSequencePlayer> imgSeqPlayers;
	protected int sequenceLoadId = 0;
	protected ImageSequencePlayer currentLoadingPlayer = null;
	protected int lastLoadTime = -9999999;
	protected int curFrame = 0;
	protected int lastFrame = 9999999;
	
	PGraphics buffer;
	PGraphics preloadPG;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, Math.round(1360 * 1.5f) );
		p.appConfig.setProperty( AppSettings.HEIGHT, Math.round(768/3 * 1.5f) );
		p.appConfig.setProperty( AppSettings.RENDERER, P.P2D );
		p.appConfig.setProperty( AppSettings.SHOW_STATS, true );
	}

	public void setup() {
		super.setup();
		imgW = p.width / 9;
		imgH = p.height;
		
		buffer = p.createGraphics(p.width, p.height, P.P2D);
		preloadPG = p.createGraphics(imgW, imgH, P.P2D);
		
		imgSeqPlayers = new ArrayList<ImageSequencePlayer>();
		for (int i = 0; i < 5; i++) {
			imgSeqPlayers.add(new ImageSequencePlayer(imgW, imgH));
		}
	}
	
	protected void attemptLoadNextPlayer() {
		if(currentLoadingPlayer == null && p.millis() > lastLoadTime + 20000) {
			lastLoadTime = p.millis();
			currentLoadingPlayer = imgSeqPlayers.get(imgSeqPlayers.size()-1);	// load into the 5th sequence that's not active
			sequenceLoadId++;
			P.println("Loading sequence "+sequenceLoadId);
			currentLoadingPlayer.loadImages(FileUtil.getHaxademicDataPath() + "video/dancelab/image-sequence-test-"+sequenceLoadId+"/", "jpg");
			if(sequenceLoadId >= 4) sequenceLoadId = 0;
		}
	}

	public void cycleNewLoadedPlayer() {
		if(currentLoadingPlayer == null) return;
		if(currentLoadingPlayer.loaded == true) {
//			if(curFrame < lastFrame) {
				imgSeqPlayers.add(0, imgSeqPlayers.remove(imgSeqPlayers.size()-1));
				currentLoadingPlayer = null;
//			}
		}	
	}
	
	public void drawApp() {
		background(0);
		
		// map time to framerate - drops frames if needed
//		float curTime = (p.millis() % 15000f) / 1000f;
//		curFrame = Math.round(449 * curTime / 15f);
		float curTime = (p.millis() % 60000f) / 1000f;
		curFrame = Math.round(1799 * curTime / 60f);
		
		if(curFrame < lastFrame) {
			cycleNewLoadedPlayer();
			attemptLoadNextPlayer();			
		}
		
		buffer.beginDraw();
		if(imgSeqPlayers.size() > 0) imgSeqPlayers.get(0).display(imgW * 0, 0, curFrame);
		if(imgSeqPlayers.size() > 1) imgSeqPlayers.get(1).display(imgW * 1, 0, curFrame);
		if(imgSeqPlayers.size() > 2) imgSeqPlayers.get(2).display(imgW * 2, 0, curFrame);
		if(imgSeqPlayers.size() > 3) imgSeqPlayers.get(3).display(imgW * 3, 0, curFrame);
		if(imgSeqPlayers.size() > 0) imgSeqPlayers.get(0).display(imgW * 4, 0, curFrame);
		buffer.pushMatrix();
		buffer.translate(p.width, 0);
		buffer.scale(-1, 1);
		if(imgSeqPlayers.size() > 3) imgSeqPlayers.get(3).display(imgW * 3, 0, curFrame);
		if(imgSeqPlayers.size() > 2) imgSeqPlayers.get(2).display(imgW * 2, 0, curFrame);
		if(imgSeqPlayers.size() > 1) imgSeqPlayers.get(1).display(imgW * 1, 0, curFrame);
		if(imgSeqPlayers.size() > 0) imgSeqPlayers.get(0).display(imgW * 0, 0, curFrame);
		buffer.popMatrix();
		buffer.endDraw();
		
		
//		ChromaColorFilter.instance(p).setColorToReplace(0.71f, 0.99f, 0.02f);
//		ChromaColorFilter.instance(p).setSmoothing(0.8f);
//		ChromaColorFilter.instance(p).setThresholdSensitivity(0.73f);
//		ChromaColorFilter.instance(p).applyTo(buffer);

//		PixelateFilter.instance(p).setDivider(8, p.width, p.height);
//		PixelateFilter.instance(p).applyTo(buffer);
		
		p.image(buffer, 0, 0);


		fill(255);
		text("Framerate: " + (int)(frameRate), 10, 120);
		text("CurFrame: " + curFrame, 10, 140);
		
		lastFrame = curFrame;
	}

	
	class ImageSequencePlayer {
		
		ArrayList<PImage> imageSequence;
		boolean loaded = false;
		int imgW;
		int imgH;
		
		int[] frameIndexPlaybackSequence = new int[450*4];
		

		public ImageSequencePlayer(int w, int h) {
			imgW = w;
			imgH = h;
			imageSequence = new ArrayList<PImage>();
			buildPlaybackSequence();
		}
		
		public void loadImages(String imageDir, String format) {
			
			final String imagesDir = imageDir;
			final String formatStr = format;
			final int loadStartTime = p.millis();

			new Thread(new Runnable() { public void run() {
				// dump previous images
				dispose();
				System.gc();
				
				// load new image sequence
	    		ArrayList<String> files = FileUtil.getFilesInDirOfType(imagesDir, formatStr);
	    		for (int i = 0; i < files.size(); i++) {
    				// load a fresh image an populate the array for the first time
	    			PImage loadedImg = p.loadImage(imagesDir + files.get(i));
	    			//	preloadPG.image(loadedImg, 0, 0, imgW, imgH); // attempt to pre-cache images, though this doesn't seem to be necessary.
    				imageSequence.add(loadedImg);
    				loadedImg = null;
	    		}
	    		loaded = true;
	    		P.println("Sequence Load Time: "+((p.millis() - loadStartTime)/1000) + " seconds");
		    }}).start();	
		}
		
		public void display(int x, int y, int frame) {
			frame = frameIndexPlaybackSequence[frame];
			if(loaded == false) return;
			if(imageSequence.size() > frame) {
				buffer.image(imageSequence.get(frame), x, y, imgW, imgH);
			}
		}
		
		public void dispose() {
			// https://forum.processing.org/two/discussion/6898/how-to-correctly-release-pimage-memory
			// https://github.com/jeffThompson/ProcessingTeachingSketches/blob/master/Utilities/AvoidPImageMemoryLeaks/AvoidPImageMemoryLeaks.pde
			for (int i = 0; i < imageSequence.size(); i++) {
				Object cache = p.g.getCache(imageSequence.get(i));
				if (cache instanceof Texture) {
					((Texture) cache).disposeSourceBuffer();
				}
				p.g.removeCache(imageSequence.get(i));
			}
			imageSequence.clear();
		}
		
		protected void buildPlaybackSequence() {
			int frameNum = 0;
			int frameAdd = 1;
			for (int i = 0; i < frameIndexPlaybackSequence.length; i++) {
				if(i % 15 == 0) {
					frameAdd = MathUtil.randRange(-2, 2);
					if(frameAdd == 0) frameAdd = 1;
					if(frameNum < 30) frameAdd = P.abs(frameAdd);
					if(frameNum >= 450 - 30) frameAdd = P.abs(frameAdd) * -1;
				}
				
				frameIndexPlaybackSequence[i] = frameNum;
				frameNum += frameAdd;
			}
		}
	}
}
