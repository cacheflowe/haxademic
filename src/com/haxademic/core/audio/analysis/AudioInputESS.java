package com.haxademic.core.audio.analysis;

import com.haxademic.core.app.P;

import krister.Ess.AudioInput;
import krister.Ess.Ess;
import krister.Ess.FFT;
import processing.core.PGraphics;

public class AudioInputESS {

	protected FFT fft;
	protected AudioInput audioInput;
	protected BeatDetect detector;
	protected int bufferSize = 512;
	protected int gain = 0;
	protected final int GAIN_STEP = 3;
	protected int[] beats = { 0, 0, 0, 0 }; 
	protected int[] curBeats = new int[4];
	protected AudioStreamData audioStreamData = new AudioStreamData();
	
	public AudioInputESS() {
		Ess.start(P.p); 
		audioInput = new AudioInput( bufferSize );
		fft = new FFT( bufferSize * 2 );
		fft.equalizer(true);
		// set default props
		fft.limits(.005f, .05f);
		fft.damp(0.13f);
		fft.averages(32);
		detector = new BeatDetect(bufferSize, 44100);
		detector.detectMode(BeatDetect.SOUND_ENERGY); //FREQ_ENERGY
		audioInput.start();
	}
	
	public void update(PGraphics pg) {
		// update audio data object
		audioStreamData.setFFTFrequencies(fft.spectrum);
		audioStreamData.setWaveformOffsets(audioInput.buffer);
		audioStreamData.update();

		// debug draw
		if(pg != null) audioStreamData.drawDebug(pg);
	}
	
	public void audioInputCallback(AudioInput theInput) {
		fft.getSpectrum(theInput);
		detector.detect(theInput);
		if(detector.isOnset()) audioStreamData.setBeat();
	}
}
