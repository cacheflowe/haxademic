package com.haxademic.demo.audio;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.file.FileUtil;

public class Demo_WaveformAudioRender
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, true );
		p.appConfig.setProperty( AppSettings.RENDER_AUDIO_SIMULATION, true );
		p.appConfig.setProperty( AppSettings.RENDER_AUDIO_FILE, FileUtil.getFile("haxademic/audio/cacheflowe_bigger_loop.wav") );
	}
	
	public void drawApp() {
		p.background(0);

		// draw waveform
		p.stroke(255);
		p.strokeWeight(3);
		p.noFill();
		float startX = 0;
		float spacing = p.width / 512f;
		p.beginShape();
		for (int i = 0; i < p.audioData.waveform().length; i++ ) {
			float curY = p.height * 0.5f + p.audioData.waveform()[i] * 300f;
			p.vertex(startX + i * spacing, curY);
		}
		p.endShape();
		
		// draw spectrum
		startX = 0;
		spacing = p.width / 512f;
		p.beginShape();
		for (int i = 0; i < p.audioData.frequencies().length; i++ ) {
			float curY = p.height * 0.5f - p.audioData.frequencies()[i] * 300f;
			p.vertex(startX + i * spacing, curY);
		}
		p.endShape();

	}
}
