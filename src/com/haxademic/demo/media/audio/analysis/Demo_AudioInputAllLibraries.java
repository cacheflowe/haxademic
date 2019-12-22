package com.haxademic.demo.media.audio.analysis;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.analysis.AudioInputESS;
import com.haxademic.core.media.audio.analysis.AudioInputMinim;
import com.haxademic.core.media.audio.analysis.AudioInputProcessing;
import com.haxademic.core.system.JavaInfo;

import krister.Ess.AudioInput;

public class Demo_AudioInputAllLibraries
extends PAppletHax { public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected AudioInputESS audioInputESS;
	protected AudioInputMinim audioInputMinim;
	protected AudioInputBeads audioInputBeads;
	protected AudioInputProcessing audioInputProcessing;
	
	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 600);
		Config.setProperty(AppSettings.HEIGHT, 300);
	}

	public void firstFrame() {
		JavaInfo.printAudioInfo();
		
//	    audioInputESS = new AudioInputESS();
		audioInputMinim = new AudioInputMinim();
//		audioInputBeads = new AudioInputBeads();
//		audioInputProcessing = new AudioInputProcessing();
	}
	
	public void drawApp() {
		background(0);
		if(audioInputESS != null) audioInputESS.update(p.g);
		if(audioInputMinim != null) audioInputMinim.update(p.g);
		if(audioInputBeads != null) audioInputBeads.update(p.g);
		if(audioInputProcessing != null) audioInputProcessing.update(p.g);
	}
	
	////////////////////////////
	// ESS-sepecific callback!
	// Since this isn't going through the AudioLineIn wrapper via PAppletHax callback, 
	// we need to implement this callback here and direct the PApplet update to the local AudioInputESS object
	////////////////////////////
	
	public void audioInputData(AudioInput theInput) {
		audioInputESS.audioInputCallback(theInput);
	}

}

