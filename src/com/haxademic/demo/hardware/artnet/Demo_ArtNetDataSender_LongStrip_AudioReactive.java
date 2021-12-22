package com.haxademic.demo.hardware.artnet;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.dmx.artnet.ArtNetDataSender;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioIn.AudioInputLibrary;

import processing.core.PGraphics;

public class Demo_ArtNetDataSender_LongStrip_AudioReactive
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArtNetDataSender artNetDataSender;
//	protected int numPixels = 256 * 3;//400;
	protected int numPixels = 600;
	
	protected PGraphics debugPG;

	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
		Config.setProperty(AppSettings.SHOW_UI, true);
	}
	
	protected void firstFrame() {
		AudioIn.instance(AudioInputLibrary.ESS);

		// build artnet obj
		artNetDataSender = new ArtNetDataSender("192.168.1.100", 0, numPixels);
		
		// build debug buffer for visualizing artnet data array
		debugPG = PG.newPG(128, 128);
		DebugView.setTexture("debugPG", debugPG);
	}

	protected void drawApp() {
		background(0);
		createColors();
		artNetDataSender.send();
		artNetDataSender.drawDebug(debugPG, true);
	}
	
	protected void createColors() {
		for(int i=0; i < numPixels; i++) {
			// get fft freq
			// float c = AudioIn.audioFreq(i) * 20;
			float c = AudioIn.audioWave(i) * 50;
			artNetDataSender.setColorAtIndex(i, c, c, c);
		}
	}
}