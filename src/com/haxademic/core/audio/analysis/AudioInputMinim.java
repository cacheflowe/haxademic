package com.haxademic.core.audio.analysis;

import com.haxademic.core.app.P;

import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;
import processing.core.PGraphics;

public class AudioInputMinim {

	protected Minim minim;
	protected AudioInput audioInput;
	protected FFT _fft;
	protected BeatDetect _beatDetection;
	protected float spectrumDampened[];
	protected float spectrumMinim[];
	protected float spectrumAvgDampened[];
	protected float spectrumDb[];
	protected float dampening = 0.75f;
	protected int _averages = 32;
	protected AudioStreamData audioStreamData = new AudioStreamData();

	public AudioInputMinim() {
		minim = new Minim( P.p );
		audioInput = minim.getLineIn();
		
		_fft = new FFT(audioInput.bufferSize(), audioInput.sampleRate());
		_fft.linAverages( _averages );
		spectrumAvgDampened = new float[_averages];
		spectrumDampened = new float[audioInput.bufferSize()];
		spectrumMinim = new float[audioInput.bufferSize() / 2];
		spectrumDb = new float[audioInput.bufferSize()];
		for(int i = 0; i < _fft.specSize(); i++) {
			spectrumDampened[i] = 0;
			if(i < spectrumMinim.length) spectrumMinim[i] = 0;
			spectrumDb[i] = 0;
		}

		// a beat detection object song SOUND_ENERGY mode with a sensitivity of 300 milliseconds
		_beatDetection = new BeatDetect();
		_beatDetection.detectMode(BeatDetect.SOUND_ENERGY);
		_beatDetection.setSensitivity(300);
	}
	
	public void update(PGraphics pg) {
		_fft.forward( audioInput.mix );
		_beatDetection.detect( audioInput.mix );
		if( _beatDetection.isOnset() == true ) audioStreamData.setBeat();
		
		float timeSize = _fft.timeSize() * ( _fft.timeSize() / P.p._fps );
		
		  // calculate levels
//			float volMax = _audioInput.mix.level();  
		for(int i = 0; i < _fft.specSize(); i++) {
			spectrumDampened[i] = ( spectrumDampened[i] < _fft.getBand(i) ) ? _fft.getBand(i) : spectrumDampened[i] * dampening;
			if(i < spectrumMinim.length) spectrumMinim[i] = _fft.getBand(i);
//				spectrumDampened[i] = P.constrain( spectrumDampened[i] / volMax, 0, 1 );
			spectrumDb[i] = 10 - -1f * P.log( 1 * spectrumDampened[i] / timeSize );
		}
		for(int i = 0; i < _fft.avgSize(); i++) {
			spectrumAvgDampened[i] = ( spectrumAvgDampened[i] < _fft.getAvg(i) ) ? _fft.getAvg(i) : spectrumAvgDampened[i] * dampening;
		}
		
		// update audio data arrays
		//		audioInputDataMinim.setFFTFrequencies(spectrumDampened);
		audioStreamData.setFFTFrequencies(spectrumMinim);
		audioStreamData.setWaveformOffsets(audioInput.mix.toArray());
		audioStreamData.setAmp(audioInput.mix.level());
		audioStreamData.update();

		// debug draw
		if(pg != null) audioStreamData.drawDebug(pg);
	}
}
