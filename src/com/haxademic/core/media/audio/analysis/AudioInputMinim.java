package com.haxademic.core.media.audio.analysis;

import com.haxademic.core.app.P;

import ddf.minim.AudioInput;
import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;
import processing.core.PGraphics;

public class AudioInputMinim
implements IAudioInput {

	protected Minim minim;
	protected AudioInput audioInput;
	protected FFT fft;
	protected BeatDetect beatDetection;
	protected float spectrumDampened[];
	protected float spectrum[];
	protected float spectrumAvgDampened[];
	protected float spectrumDb[];
	protected float dampening = 0.75f;
	protected int averages = 32;
	protected AudioStreamData audioStreamData = new AudioStreamData();

	public AudioInputMinim() {
		minim = new Minim( P.p );
		AudioOutput out = minim.getLineOut();
		audioInput = minim.getLineIn(Minim.STEREO, out.bufferSize(), out.sampleRate(), out.getFormat().getSampleSizeInBits());
		
		fft = new FFT(audioInput.bufferSize(), audioInput.sampleRate());
		fft.linAverages( averages );
		spectrumAvgDampened = new float[averages];
		spectrumDampened = new float[audioInput.bufferSize()];
		spectrum = new float[audioInput.bufferSize() / 2];
		spectrumDb = new float[audioInput.bufferSize()];
		for(int i = 0; i < fft.specSize(); i++) {
			spectrumDampened[i] = 0;
			if(i < spectrum.length) spectrum[i] = 0;
			spectrumDb[i] = 0;
		}

		// a beat detection object song SOUND_ENERGY mode with a sensitivity of 300 milliseconds
		beatDetection = new BeatDetect();
		beatDetection.detectMode(BeatDetect.SOUND_ENERGY);
		beatDetection.setSensitivity(300);
	}
	
	public AudioStreamData audioData() {
		return audioStreamData;
	}
	
	public void update(PGraphics pg) {
		// analyze
		fft.forward( audioInput.mix );
		beatDetection.detect( audioInput.mix );
		
		float timeSize = fft.timeSize() * ( fft.timeSize() / P.p._fps );
		
		// calculate levels
		for(int i = 0; i < fft.specSize(); i++) {
			spectrumDampened[i] = ( spectrumDampened[i] < fft.getBand(i) ) ? fft.getBand(i) : spectrumDampened[i] * dampening;
			if(i < spectrum.length) spectrum[i] = fft.getBand(i);
//				spectrumDampened[i] = P.constrain( spectrumDampened[i] / volMax, 0, 1 );
			spectrumDb[i] = 10 - -1f * P.log( 1 * spectrumDampened[i] / timeSize );
		}
		for(int i = 0; i < fft.avgSize(); i++) {
			spectrumAvgDampened[i] = ( spectrumAvgDampened[i] < fft.getAvg(i) ) ? fft.getAvg(i) : spectrumAvgDampened[i] * dampening;
		}
		
		// update audio data arrays
		//		audioInputDataMinim.setFFTFrequencies(spectrumDampened);
		audioStreamData.setFFTFrequencies(spectrum);
//		audioStreamData.setFFTFrequencies(fft.getSpectrumReal());
		audioStreamData.setWaveformOffsets(audioInput.mix.toArray());
		audioStreamData.setAmp(audioInput.mix.level());
		audioStreamData.calcFreqsDampened();
		if( beatDetection.isOnset() == true ) audioStreamData.setBeat();
		audioStreamData.update();

		// debug draw
		if(pg != null) audioStreamData.drawDebug(pg);
	}
}
