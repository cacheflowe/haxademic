package com.haxademic.demo.render.audio;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;

public class Demo_WaveformAudioRenderExtras
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected StringBufferLog log = new StringBufferLog(30);
	protected StringBufferLog log2 = new StringBufferLog(30);

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 720 );
		Config.setProperty( AppSettings.RENDERING_MOVIE, true );
		Config.setProperty( AppSettings.RENDER_AUDIO_SIMULATION, false );
		Config.setProperty( AppSettings.RENDER_AUDIO_FILE, FileUtil.getPath("haxademic/audio/cacheflowe_bigger_loop.wav") );
		Config.setProperty( AppSettings.RENDER_AUDIO_FILE, FileUtil.getPath("haxademic/audio/brim-beat-4.wav") );
	}
	
	protected void firstFrame() {
		AudioIn.instance();
		// Renderer.instance().videoRenderer.setPG(pg);
	}

	protected void drawApp() {
		p.background(0);

		// // draw waveform
		// p.stroke(255);
		// p.strokeWeight(3);
		// p.noFill();
		// float startX = 0;
		// float spacing = p.width / 512f;
		// p.beginShape();
		// for (int i = 0; i < AudioIn.waveform.length; i++ ) {
		// 	float curY = p.height * 0.33f + AudioIn.waveform[i] * 300f;
		// 	p.vertex(startX + i * spacing, curY);
		// }
		// p.endShape();

		pg.push();
		log.update("freq[10] = "+AudioIn.frequencies[10]);
		log.printToScreen(p.g, 50, 50);
		log2.update("freq[30] = "+AudioIn.frequencies[30]);
		log2.printToScreen(p.g, 250, 50);
		pg.pop();
		
		// draw spectrum
		float startX = 450;
		float spacing = p.width / 512f;
		p.beginShape();
		p.noFill();
		p.stroke(255);
		for (int i = 0; i < AudioIn.frequencies.length / 2; i++ ) {
			float curY = p.height * 0.5f - AudioIn.frequencies[i] * 200f;
			p.vertex(startX + i * spacing, curY);
		}
		p.endShape();

	}
}
