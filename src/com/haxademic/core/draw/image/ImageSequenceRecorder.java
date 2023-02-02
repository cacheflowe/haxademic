package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.text.StringUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class ImageSequenceRecorder {
    
    public interface IImageSequenceRecorderDelegate {
        public void recordingComplete(ImageSequenceRecorder recorder);
        public void savedToDisk(ImageSequenceRecorder recorder);
    }

    public static boolean DEBUG = false;
    
	protected int width;
	protected int height;
	protected int numFrames;
	protected int frameIndex = 0;
	protected int saveIndex = 0;
	protected PGraphics[] images;
	
	protected String savePath;
	protected boolean savingBusy;
	protected IImageSequenceRecorderDelegate delegate;
	
	public ImageSequenceRecorder(int width, int height, int frames) {
	    this(width, height, frames, null);
	}
	
	public ImageSequenceRecorder(int width, int height, int frames, IImageSequenceRecorderDelegate delegate) {
		this.width = width;
		this.height = height;
		this.numFrames = frames;
		this.delegate = delegate;
		saveIndex = numFrames;
		buildFrames();
	}
	
	protected void buildFrames() {
		images = new PGraphics[numFrames];
		for (int i = 0; i < numFrames; i++) {
			images[i] = PG.newPG(width, height);
		}
	}
	
	public PGraphics[] images() {
		return images;
	}
	
	public PGraphics imageAtFrame(int frame) {
	    return images[frame % numFrames];
	}
	
	public int frameIndex() {
		return safeIndex();
	}
	
	protected int safeIndex() {
	    return P.constrain(frameIndex, 0, numFrames - 1);
	}
	
	public void reset() {
	    frameIndex = -1;
	}
	
	public int addFrame(PImage img) {
	    boolean wasComplete = isComplete();
	    if(!wasComplete) {
	        frameIndex++;
	        ImageUtil.cropFillCopyImage(img, images[frameIndex], true);
	    }
	    if(!wasComplete && isComplete() && delegate != null) delegate.recordingComplete(this);
		return frameIndex;
	}
	
	public boolean isComplete() {
	    return (frameIndex < numFrames - 1) ? false : true;
	}
	
	public boolean isRecording() {
	    return !isComplete();
	}
	
	public float recordProgress() {
	    return (float) safeIndex() / (numFrames - 1);
	}
	
	public PGraphics getCurFrame() {
		return images[safeIndex()];
	}
	
	public void drawDebug(PGraphics pg) {
		drawDebug(pg, false);
	}
	
	public void drawDebug(PGraphics pg, boolean openContext) {
		if(openContext) pg.beginDraw();
		float frameW = (float) pg.width / (float) numFrames;
		float frameH = frameW * ((float) height / (float) width);
		for (int i = 0; i < safeIndex(); i++) {
			float x = frameW * i;
			pg.image(images[i], x, 0, frameW, frameH);
		}
		if(openContext) pg.endDraw();
	}
	
	////////////////
	// SAVING
	////////////////

	public String saveToDisk() {
	    String autoSavePath = FileUtil.haxademicOutputPath() + "_image-sequence-recordings" + FileUtil.SEPARATOR + SystemUtil.getTimestamp() + FileUtil.SEPARATOR;
	    return saveToDisk(autoSavePath);
	}

	public String saveToDisk(String savePath) {
	    if(isSaving()) return savePath;
	    this.savePath = FileUtil.safeDirPath(savePath);
	    FileUtil.createDir(this.savePath);
	    saveIndex = -1;
	    return this.savePath;
	}
	
	public String savePath() {
	    return savePath;
	}

	public void updateSave() {
	    if(savingBusy == true) return;
	    
	    boolean wasSaving = isSaving();
	    if(wasSaving) {
	        saveIndex++;
	        savingBusy = true;
	        String filePath = savePath + StringUtil.paddedNumberString(5, saveIndex + 1) + ".tga";
	        PImage img = images[saveIndex].get(); // needed for threaded saving. memory implications don't seem bad after testing
	        new Thread(new Runnable() { public void run() {
	            img.save(filePath);
	            if(DEBUG) P.out("saved:", filePath);
	            savingBusy = false;
	        }}).start();
	    }
	    if(wasSaving && !isSaving() && delegate != null) delegate.savedToDisk(this);
	}

	public boolean isSaving() {
	    return (saveIndex < numFrames - 1) ? true : false;
	}

	public float saveProgress() {
	    return (float) saveIndex / numFrames;
	}


}
