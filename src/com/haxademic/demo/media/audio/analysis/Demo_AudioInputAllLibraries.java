package com.haxademic.demo.media.audio.analysis;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.media.audio.analysis.AudioInputESS;
import com.haxademic.core.media.audio.analysis.AudioInputMinim;
import com.haxademic.core.media.audio.analysis.IAudioInput;
import com.haxademic.core.system.JavaInfo;

import krister.Ess.AudioInput;

public class Demo_AudioInputAllLibraries
extends PAppletHax { public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected IAudioInput audioInput;
	
	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 600);
		Config.setProperty(AppSettings.HEIGHT, 300);
	}

	protected void firstFrame() {
		JavaInfo.printAudioInfo();
		
//	    audioInput = new AudioInputESS();
		audioInput = new AudioInputMinim();
//		audioInput = new AudioInputBeads();
//		audioInput = new AudioInputProcessing();
	}
	
	protected void drawApp() {
		background(0);
		audioInput.update();
		audioInput.drawDataBuffers();
		audioInput.drawDebugBuffer();
		
		p.image(audioInput.audioData().debugBuffer, 0, 0);
	}
	
	////////////////////////////
	// ESS-sepecific callback!
	// Since this isn't going through the AudioLineIn wrapper via PAppletHax callback, 
	// we need to implement this callback here and direct the PApplet update to the local AudioInputESS object
	////////////////////////////
	
	public void audioInputData(AudioInput theInput) {
		((AudioInputESS) audioInput).audioInputCallback(theInput);
	}

}

