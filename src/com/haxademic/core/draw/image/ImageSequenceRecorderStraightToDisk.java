package com.haxademic.core.draw.image;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.text.StringUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class ImageSequenceRecorderStraightToDisk {
    
    public interface IImageSequenceRecorderDelegate {
        public void savedToDisk(ImageSequenceRecorderStraightToDisk recorder);
    }

    public static boolean DEBUG = false;
    
	protected int width;
	protected int height;
	protected int numFrames;
	protected int frameIndex = 0;
	protected int saveIndex = 0;
	protected ArrayList<PGraphics> images;
	protected PGraphics lastPGSaved;
	
	protected String savePath;
	protected boolean savingBusy;
	protected IImageSequenceRecorderDelegate delegate;
	
	public ImageSequenceRecorderStraightToDisk(int width, int height, int frames) {
	    this(width, height, frames, null);
	}
	
	public ImageSequenceRecorderStraightToDisk(int width, int height, int frames, IImageSequenceRecorderDelegate delegate) {
		this.width = width;
		this.height = height;
		this.numFrames = frames;
		this.delegate = delegate;
		saveIndex = numFrames;
		images = new ArrayList<PGraphics>();
	}
	

	///////////////////////////////
    // frame indexes
    ///////////////////////////////
    
	public int frameIndex() {
		return safeIndex();
	}
	
	protected int safeIndex() {
	    return P.constrain(frameIndex, 0, numFrames - 1);
	}
	
	public boolean isComplete() {
	    return (frameIndex < numFrames - 1) ? false : true;
	}

    ////////////////
    // SAVING
    ////////////////

    public boolean isSaving() {
        return (saveIndex < numFrames - 1) ? true : false;
    }

    public float saveProgress() {
        return (float) saveIndex / (numFrames - 1);
    }

    protected void frameSavedToDisk(int saveFrameIndex) {
        if(saveFrameIndex == numFrames - 1 && delegate != null) {
            delegate.savedToDisk(this);
        }
    }

	
	///////////////////////////////
	// set/get save path
	///////////////////////////////
	
	public String reset() {
	    return reset(FileUtil.haxademicOutputPath() + "_image-sequence-recordings" + FileUtil.SEPARATOR + SystemUtil.getTimestamp() + FileUtil.SEPARATOR);
	}
	
	public String reset(String savePath) {
	    // reset frame index
	    frameIndex = -1;
	    
	    // set save path
	    if(isSaving()) return savePath;
	    this.savePath = FileUtil.safeDirPath(savePath);
	    FileUtil.createDir(this.savePath);
	    saveIndex = -1;
	    return this.savePath;
	}

	public String savePath() {
	    return savePath;
	}
	

    ///////////////////////////////
    // public update/save functions 
    ///////////////////////////////

	public PGraphics addFrame(PImage img) {
	    boolean wasComplete = isComplete();
	    PGraphics curPG = null;
	    if(!wasComplete) {
	        frameIndex++;
	        curPG = getBuffer().pg();
	        ImageUtil.cropFillCopyImage(img, curPG, true);
	        lastPGSaved = curPG;
	    }
		return curPG;
	}
	
	// need to pass frame back in, in case we did additional drawing/compositing
	public void saveFrame(PGraphics curPG) {
	    PGFrame frame = pgFrameFromPG(curPG);
	    if(frame == null) return;  // when recording is done, pg is null, so bail
	    frame.saveFrameThreaded();
	}
	
	public PGraphics lastPGSaved() {
	    return lastPGSaved;
	}
	
	///////////////////////////////
    // Debug
    ///////////////////////////////
    
	
	public void drawDebug(PGraphics pg) {
		drawDebug(pg, false);
	}
	
	public void drawDebug(PGraphics pg, boolean openContext) {
		if(openContext) pg.beginDraw();
		int x = 0;
		for (int i = 0; i < pgPool.size(); i++) {
		    if(pgPool.get(i).available()) {
		        float frameW = (float) pg.width / (float) numFrames;
		        float frameH = frameW * ((float) height / (float) width);
		        pg.image(pgPool.get(i).pg(), x, 0, frameW, frameH);
		        x += frameW * i;
		    }
		}
		if(openContext) pg.endDraw();
	}
	
	///////////////////////////////////
	// PGraphics pool
	///////////////////////////////////

    protected ArrayList<PGFrame> pgPool = new ArrayList<>();
	
	protected PGFrame getBuffer() {
	    PGFrame availablePG = findAvailablePG();
	    if(availablePG != null) {
	        availablePG.setAvailable(false);
	        return availablePG;
	    } else {
	        PGFrame newPG = new PGFrame(width, height);
	        newPG.setAvailable(false);
	        pgPool.add(newPG);
	        return newPG;
	    }
	}
	
	protected PGFrame findAvailablePG() {
	    for (int i = 0; i < pgPool.size(); i++) {
            if(pgPool.get(i).available()) return pgPool.get(i);
        }
	    return null;
	}
	
    protected PGFrame pgFrameFromPG(PGraphics pg) {
        for (int i = 0; i < pgPool.size(); i++) {
            if(pgPool.get(i).pg() == pg) return pgPool.get(i);
        }
        return null;
    }
    
    public int activePGCount() {
        int activeCount = 0;
        for (int i = 0; i < pgPool.size(); i++) {
            if(pgPool.get(i).available() == false) activeCount++;
        }
        return activeCount;
    }
    
    public int poolSize() {
        return pgPool.size();
    }
    
    //////////////////////////////
    // Helper object for buffer pool & threaded file-saving
	//////////////////////////////
	
	public class PGFrame {
	    
	    protected PGraphics pg; 
	    protected boolean available = true; 
	    
	    public PGFrame(int w, int h) {
	        pg = PG.newPG(w, h);
	    }
	    
	    public PGraphics pg() {
	        return pg;
	    }
	    
	    public boolean available() {
	        return available;
	    }
	    
	    public void setAvailable(boolean available) {
	        this.available = available;
	    }
	    
	    public void saveFrameThreaded() {
	        saveIndex++;
	        final int curSaveIndex = saveIndex;
	        String filePath = savePath + StringUtil.paddedNumberString(5, saveIndex + 1) + ".tga";
	        PImage img = pg.get(); // needed for threaded saving. memory implications don't seem bad after testing
	        new Thread(new Runnable() { public void run() {
	            img.save(filePath);
	            if(DEBUG) P.out("saved:", filePath);
	            available = true;
	            frameSavedToDisk(curSaveIndex);
	        }}).start();
	    }
	    
	}
	
}
