package com.haxademic.core.media.audio.analysis;

import com.haxademic.core.app.P;

import processing.sound.Amplitude;
import processing.sound.AudioIn;
import processing.sound.FFT;
import processing.sound.Waveform;

public class AudioInputProcessing
implements IAudioInput {

	protected AudioIn audioInput;
	protected FFT fft;
	protected int bands = 256;
	protected float[] spectrum = new float[bands];
	protected Amplitude rms;
	protected Waveform waveform;

	protected AudioStreamData audioStreamData = new AudioStreamData();
	
	public AudioInputProcessing() {
		fft = new FFT(P.p, bands);
		rms = new Amplitude(P.p);
		audioInput = new AudioIn(P.p, 0);
		rms.input(audioInput);
		fft.input(audioInput);
		waveform = new Waveform(P.p, bands);
		waveform.input(audioInput);
		audioInput.start();
	}
	
	// update methods -------------------------------------
	
	public AudioStreamData audioData() { return audioStreamData; }
	public void drawDebugBuffer() { audioStreamData.drawDebug(); }
	public void drawDataBuffers() {
		audioStreamData.drawBufferFFT();
		audioStreamData.drawBufferWaveform();
	}
		
	public void update() {
		// analyze input
		fft.analyze(spectrum);
		for (int i = 0; i < spectrum.length; i++) spectrum[i] *= 10f;
		waveform.analyze();
		float curAmp = rms.analyze() * 2f;
		
		// update audio data object
		audioStreamData.setFFTFrequencies(spectrum);
		audioStreamData.setWaveformOffsets(waveform.data);
		audioStreamData.setAmp(curAmp);
		audioStreamData.update();
	}
	
}
