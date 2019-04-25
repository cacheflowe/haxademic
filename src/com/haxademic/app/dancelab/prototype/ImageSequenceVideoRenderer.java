package com.haxademic.app.dancelab.prototype;

import com.haxademic.app.dancelab.playback.Dancer;
import com.haxademic.app.dancelab.playback.DancerFramesData;
import com.haxademic.app.dancelab.playback.ImageSequencePlayer;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;

import processing.core.PGraphics;

public class ImageSequenceVideoRenderer
extends PAppletHax {

	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	int imgW;
	int imgH;

	ImageSequencePlayer imageSequencePlayer;
	protected int curFrame = 0;
	protected int startRenderFrame = 1000;
	
	PGraphics buffer;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 720 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1280 );
		p.appConfig.setProperty( AppSettings.RENDERER, P.P2D );
		p.appConfig.setProperty( AppSettings.FPS, 30 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_NONE );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, true );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, startRenderFrame);
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, startRenderFrame + 1800);
	}

	public void setup() {
		super.setup();
		imgW = p.width;
		imgH = p.height;
		
		buffer = p.createGraphics(p.width, p.height, P.P2D);
		buffer.noSmooth();
		
		imageSequencePlayer = new ImageSequencePlayer(imgW, imgH);
		Dancer newDancer = Dancer.MORGAN;
		imageSequencePlayer.setPlaybackFrameSequence(DancerFramesData.instance().getDancerFrames(newDancer));
		imageSequencePlayer.loadImages(newDancer.path, "jpg");
	}
	
	public void drawApp() {
		background(0);

		if(p.frameCount >= startRenderFrame) {
			curFrame++;
			DrawUtil.setDrawCenter(buffer);
			buffer.beginDraw();
			int origFrame = imageSequencePlayer.display(buffer, imgW * 0, 0, curFrame % 1800);
			buffer.endDraw();			
			p.image(buffer, 0, 0);
	
			fill(255);
			text("Framerate: " + (int)(frameRate), 10, 120);
			text("CurFrame: " + curFrame, 10, 140);
			text("Orig Secs: " + (4.0f * (origFrame / 30f)), 10, 160);
		}
	}
}
