package com.haxademic.core.audio.analysis;

import com.haxademic.core.app.P;

import processing.core.PGraphics;
import processing.sound.Amplitude;
import processing.sound.AudioIn;
import processing.sound.FFT;

public class AudioInputProcessingSound {

	// protected AudioDevice device;
	protected AudioIn audioInput;
	protected FFT fft;
	protected int bands = 256;
	protected float[] spectrum = new float[bands];
	protected Amplitude rms;

	protected AudioStreamData audioStreamData = new AudioStreamData();
	
	public AudioInputProcessingSound() {
		// device = new AudioDevice(P.p, 44000, bands);
		
		fft = new FFT(P.p, bands);
		rms = new Amplitude(P.p);
		audioInput = new AudioIn(P.p, 0);
		rms.input(audioInput);
		fft.input(audioInput);
		audioInput.start();
	}
	
	public void update(PGraphics pg) {
		// analyze input
		fft.analyze(spectrum);
		float curAmp = rms.analyze();
		P.p.debugView.setValue("curAmp", curAmp);
		
		// update audio data object
		audioStreamData.setFFTFrequencies(spectrum);
//		audioStreamData.setWaveformOffsets(???);
		audioStreamData.setAmp(curAmp);
		audioStreamData.update();

		// debug draw
		if(pg != null) audioStreamData.drawDebug(pg);
	}
	
}
