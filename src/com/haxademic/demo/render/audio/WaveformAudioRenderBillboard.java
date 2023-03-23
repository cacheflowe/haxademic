package com.haxademic.demo.render.audio;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BadTVLinesFilter;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class WaveformAudioRenderBillboard
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PImage backgroundImg;
	protected PGraphics waveform;

	protected void config() {
		Config.setProperty( AppSettings.FPS, 30 );
		Config.setProperty( AppSettings.WIDTH, 424 );
		Config.setProperty( AppSettings.HEIGHT, 282 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		Config.setProperty( AppSettings.RENDERING_MOVIE, true );
		Config.setProperty( AppSettings.RENDER_AUDIO_FILE, FileUtil.getPath("audio/cacheflowe_bigger_loop_padded.wav") );
	}

	protected void firstFrame() {

		p.noStroke();
		backgroundImg = p.loadImage(FileUtil.getPath("images/computers/billboard-advertising.jpg"));
		waveform = p.createGraphics(300, 120, P.P2D);
		waveform.smooth(AppSettings.SMOOTH_HIGH);
	}
	
	protected void drawApp() {
		// background 
		p.image(backgroundImg, 0, 0);
		BadTVLinesFilter.instance().setTime(100f+(float)p.frameCount/1000f);
		BadTVLinesFilter.instance().setOnContext(p);
		
		// update waveform texture
		waveform.beginDraw();
		if(p.frameCount == 1) waveform.background(255);
		PG.feedback(waveform, 255, 0.25f, -4f);

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
