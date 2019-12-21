package com.haxademic.sketch.audio.processing;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;

import processing.sound.AudioIn;
import processing.sound.FFT;

public class EQProcessingSoundLib 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	FFT fft;
	AudioIn in;
	int bands = 256;
	float[] spectrum = new float[bands];

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "800" );
		Config.setProperty( AppSettings.HEIGHT, "600" );
	}

	public void firstFrame() {
	

		// Create an Input stream which is routed into the Amplitude analyzer
		fft = new FFT(this, bands);
		in = new AudioIn(this, 0);

		// start the Audio Input
		in.start();

		// patch the AudioIn
		fft.input(in);
	}

	public void drawApp() {
		p.background(255);
		fft.analyze(spectrum);

		for(int i = 0; i < bands; i++){
			// The result of the FFT is normalized
			// draw the line for frequency band i scaling it up by 5 to get more amplitude.
			line( i, height, i, height - spectrum[i]*height*5 );
		} 
	}
	
	//	public void exit() {
	//		fft.dispose();
	//		in.stop();
	//		super.exit();
	//	}
}

