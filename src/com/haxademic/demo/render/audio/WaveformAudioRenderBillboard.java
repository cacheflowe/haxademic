package com.haxademic.demo.render.audio;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.BadTVLinesFilter;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class WaveformAudioRenderBillboard
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PImage backgroundImg;
	protected PGraphics waveform;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FPS, 30 );
		p.appConfig.setProperty( AppSettings.WIDTH, 424 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 282 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, true );
		p.appConfig.setProperty( AppSettings.RENDER_AUDIO_FILE, FileUtil.getFile("audio/cacheflowe_bigger_loop_padded.wav") );
	}

	public void setup() {
		super.setup();
		p.noStroke();
		backgroundImg = p.loadImage(FileUtil.getFile("images/computers/billboard-advertising.jpg"));
		waveform = p.createGraphics(300, 120, P.P2D);
		waveform.smooth(AppSettings.SMOOTH_HIGH);
	}
	
	public void drawApp() {
		// background 
		p.image(backgroundImg, 0, 0);
		BadTVLinesFilter.instance(p).setTime(100f+(float)p.frameCount/1000f);
		BadTVLinesFilter.instance(p).applyTo(p);
		
		// update waveform texture
		waveform.beginDraw();
		if(p.frameCount == 1) waveform.background(255);
		DrawUtil.feedback(waveform, 255, 0.25f, -4f);

		// draw waveform
		waveform.stroke(0);
		waveform.strokeWeight(0.85f);
		waveform.noFill();
		// float startX = 0;
		// float spacing = waveform.width / 512f;
		// THIS NEED FIXING VIA THE NEW SHARED AUDIO DATA OBJECTS
//		waveform.beginShape();
//		for (int i = 0; i < _waveformData._waveform.length; i++ ) {
//			float curY = waveform.height * 0.5f + _waveformData._waveform[i] * 80f;
//			waveform.vertex(startX + i * spacing, curY);
//		}
		waveform.endShape();
		waveform.endDraw();
				
		// map waveform to rectangle
		p.beginShape(QUADS);
		p.texture(waveform);
		p.vertex(84,  62,  0, 			0, 0);
		p.vertex(344, 113, 0, 			waveform.width, 0);
		p.vertex(348, 199, 0, 			waveform.width, waveform.height);
		p.vertex(84,  162, 0, 			0, waveform.height);
		p.endShape();
	}
}
