package com.haxademic.app.dancelab.prototype;

import java.util.ArrayList;

import com.haxademic.app.dancelab.playback.Dancer;
import com.haxademic.app.dancelab.playback.ImageSequencePlayer;
import com.haxademic.app.dancelab.playback.PlaybackFramesData;
import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;

import processing.core.PGraphics;

public class ImageSequenceMultiuserPlayback
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
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, Math.round(1360 * 1.5f) );
		p.appConfig.setProperty( AppSettings.HEIGHT, Math.round(768/3 * 1.5f) );
		p.appConfig.setProperty( AppSettings.RENDERER, P.P2D );
		p.appConfig.setProperty( AppSettings.SHOW_STATS, true );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_NONE );
	}

	public void setup() {
		super.setup();
		imgW = p.width / 9;
		imgH = p.height;
		
		buffer = p.createGraphics(p.width, p.height, P.P2D);
		buffer.noSmooth();
		
		imgSeqPlayers = new ArrayList<ImageSequencePlayer>();
		P.println("Put below back to 5 sequences");
		for (int i = 0; i < 1; i++) {
			imgSeqPlayers.add(new ImageSequencePlayer(imgW, imgH));
		}
	}
	
	protected void attemptLoadNextPlayer() {
		if(currentLoadingPlayer == null && p.millis() > lastLoadTime + 20000) {
			lastLoadTime = p.millis();
			currentLoadingPlayer = imgSeqPlayers.get(imgSeqPlayers.size()-1);	// load into the 5th sequence that's not active
			sequenceLoadId++;
			P.println("Loading sequence "+sequenceLoadId);
			Dancer newDancer = Dancer.AMY;
			currentLoadingPlayer.setPlaybackFrameSequence(PlaybackFramesData.instance().getDancerFrames(newDancer));
			currentLoadingPlayer.loadImages(newDancer.path, "jpg");
			if(sequenceLoadId >= 4) sequenceLoadId = 0;
		}
	}

	public void cycleNewLoadedPlayer() {
		if(currentLoadingPlayer == null) return;
		if(currentLoadingPlayer.isLoaded() == true) {
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
		
		if(currentLoadingPlayer != null && currentLoadingPlayer.isLoaded() == true) {
			cycleNewLoadedPlayer();
		}
		if(curFrame < lastFrame && p.frameCount < 100) {	// REMOVE THIS frameCount condition!!!!!
//			cycleNewLoadedPlayer();
			attemptLoadNextPlayer();			
		}
		
		DrawUtil.setDrawCenter(buffer);
		
		buffer.beginDraw();
		if(imgSeqPlayers.size() > 0) imgSeqPlayers.get(0).display(buffer, imgW * 0, 0, curFrame);
		if(imgSeqPlayers.size() > 1) imgSeqPlayers.get(1).display(buffer, imgW * 1, 0, curFrame);
		if(imgSeqPlayers.size() > 2) imgSeqPlayers.get(2).display(buffer, imgW * 2, 0, curFrame);
		if(imgSeqPlayers.size() > 3) imgSeqPlayers.get(3).display(buffer, imgW * 3, 0, curFrame);
		if(imgSeqPlayers.size() > 0) imgSeqPlayers.get(0).display(buffer, imgW * 4, 0, curFrame);
		buffer.pushMatrix();
		buffer.translate(p.width, 0);
		buffer.scale(-1, 1);
		if(imgSeqPlayers.size() > 3) imgSeqPlayers.get(3).display(buffer, imgW * 3, 0, curFrame);
		if(imgSeqPlayers.size() > 2) imgSeqPlayers.get(2).display(buffer, imgW * 2, 0, curFrame);
		if(imgSeqPlayers.size() > 1) imgSeqPlayers.get(1).display(buffer, imgW * 1, 0, curFrame);
		if(imgSeqPlayers.size() > 0) imgSeqPlayers.get(0).display(buffer, imgW * 0, 0, curFrame);
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
	
}


