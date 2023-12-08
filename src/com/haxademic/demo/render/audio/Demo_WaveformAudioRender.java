package com.haxademic.demo.render.audio;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;

public class Demo_WaveformAudioRender
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 720 );
		Config.setProperty( AppSettings.RENDERING_MOVIE, true );
		Config.setProperty( AppSettings.RENDER_AUDIO_SIMULATION, true );
		Config.setProperty( AppSettings.RENDER_AUDIO_FILE, FileUtil.getPath("haxademic/audio/cacheflowe_bigger_loop.wav") );
	}
	
	protected void firstFrame() {
		AudioIn.instance();
		// Renderer.instance().videoRenderer.setPG(pg);
	}

	protected void drawApp() {
		p.background(0);

		// draw waveform
		p.stroke(255);
		p.strokeWeight(3);
		p.noFill();
		float startX = 0;
		float spacing = p.width / 512f;
		p.beginShape();
		for (int i = 0; i < AudioIn.waveform.length; i++ ) {
			float curY = p.height * 0.33f + AudioIn.waveform[i] * 300f;
			p.vertex(startX + i * spacing, curY);
		}
		p.endShape();
		
		// draw spectrum
		startX = 0;
		spacing = p.width / 512f;
		p.beginShape();
		for (int i = 0; i < AudioIn.frequencies.length; i++ ) {
			float curY = p.height * 0.66f - AudioIn.frequencies[i] * 300f;
			p.vertex(startX + i * spacing, curY);
		}
		p.endShape();

	}
}
