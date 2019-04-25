package com.haxademic.app.dancelab.playback;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.Texture;

public class ImageSequencePlayer {
	
	ArrayList<PImage> imageSequence;
	boolean loaded = false;
	int imgW;
	int imgH;
	
	int[] frameIndexPlaybackSequence;
	

	public ImageSequencePlayer(int w, int h) {
		imgW = w;
		imgH = h;
		imageSequence = new ArrayList<PImage>();
		// buildRandomPlaybackSequence();
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	public void setPlaybackFrameSequence(int[] framesSequence) {
		frameIndexPlaybackSequence = framesSequence;
	}
	
	public void loadImages(String imageDir, String format) {
		
		final String imagesDir = imageDir;
		final String formatStr = format;
		final int loadStartTime = P.p.millis();

		new Thread(new Runnable() { public void run() {
			// dump previous images
			dispose();
			System.gc();
			
			// load new image sequence
    		ArrayList<String> files = FileUtil.getFilesInDirOfType(imagesDir, formatStr);
    		for (int i = 0; i < files.size(); i++) {
				// load a fresh image an populate the array for the first time
    			PImage loadedImg = P.p.loadImage(imagesDir + files.get(i));
    			//	preloadPG.image(loadedImg, 0, 0, imgW, imgH); // attempt to pre-cache images, though this doesn't seem to be necessary.
				imageSequence.add(loadedImg);
				loadedImg = null;
    		}
    		loaded = true;
    		P.println("Sequence Load Time: "+((P.p.millis() - loadStartTime)/1000) + " seconds");
	    }}).start();	
	}
	
	public int display(PGraphics pg, int x, int y, int frame) {
		boolean flipped = (frameIndexPlaybackSequence[frame] < 0);
		frame = P.abs(frameIndexPlaybackSequence[frame]);
		if(loaded == false) return -1;
		if(imageSequence.size() > frame) {
			pg.pushMatrix();
			pg.translate(x + imgW/2, y + imgH/2);
			if(flipped == true) pg.scale(-1, 1);
			pg.image(imageSequence.get(frame), 0, 0, imgW, imgH);
			pg.popMatrix();
		}
		return frame;
	}
	
	public void dispose() {
		// https://forum.processing.org/two/discussion/6898/how-to-correctly-release-pimage-memory
		// https://github.com/jeffThompson/ProcessingTeachingSketches/blob/master/Utilities/AvoidPImageMemoryLeaks/AvoidPImageMemoryLeaks.pde
		for (int i = 0; i < imageSequence.size(); i++) {
			Object cache = P.p.g.getCache(imageSequence.get(i));
			if (cache instanceof Texture) {
				((Texture) cache).disposeSourceBuffer();
			}
			P.p.g.removeCache(imageSequence.get(i));
		}
		imageSequence.clear();
	}
	
	protected void buildRandomPlaybackSequence() {
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
