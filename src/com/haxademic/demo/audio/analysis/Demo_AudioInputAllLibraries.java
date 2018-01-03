package com.haxademic.demo.audio.analysis;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.audio.analysis.AudioInputBeads;
import com.haxademic.core.audio.analysis.AudioInputESS;
import com.haxademic.core.audio.analysis.AudioInputMinim;
import com.haxademic.core.audio.analysis.AudioInputProcessingSound;
import com.haxademic.core.audio.analysis.AudioStreamData;
import com.haxademic.core.constants.AppSettings;

import krister.Ess.AudioInput;

public class Demo_AudioInputAllLibraries
extends PAppletHax { public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected AudioInputESS audioInputESS;
	protected AudioInputMinim audioInputMinim;
	protected AudioInputBeads audioInputBeads;
	protected AudioInputProcessingSound audioInputProcessingSound;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 600);
		p.appConfig.setProperty( AppSettings.HEIGHT, 600);
		p.appConfig.setProperty( AppSettings.INIT_ESS_AUDIO, false);
		p.appConfig.setProperty( AppSettings.INIT_MINIM_AUDIO, false);
	}

	public void setupFirstFrame() {
		audioInputESS = new AudioInputESS();
		audioInputMinim = new AudioInputMinim();
		audioInputBeads = new AudioInputBeads();
		// audioInputProcessingSound = new AudioInputProcessingSound();
	}
	
	public void drawApp() {
		background(0);
		
		// ESS
		audioInputESS.update(p.g);
		
		// Minim
		p.g.translate(AudioStreamData.debugW, 0);
		audioInputMinim.update(p.g);

		// Beads
		p.g.translate(-AudioStreamData.debugW, AudioStreamData.debugH);
		audioInputBeads.update(p.g);

		// Processing sound lib
		// p.g.translate(AudioStreamData.debugW, 0);
		// audioInputProcessingSound.update(p.g);
	}
	
	////////////////////////////
	// ESS-sepecific callback
	////////////////////////////
	
	public void audioInputData(AudioInput theInput) {
		audioInputESS.audioInputCallback(theInput);
	}

}

