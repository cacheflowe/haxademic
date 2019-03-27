package com.haxademic.core.audio.analysis.input;

import com.haxademic.core.app.P;

import krister.Ess.AudioChannel;
import krister.Ess.AudioInput;
import krister.Ess.Ess;
import krister.Ess.FFT;
import processing.core.PGraphics;

public class AudioInputESS
implements IAudioInput {

	protected FFT fft;
	protected AudioInput audioInput;
	protected AudioInputESSBeatDetect detector;
	protected int bufferSize = 512;
	protected int beatTimeThresh = 300;
	protected int lastBeatTime = 0;
	protected AudioStreamData audioStreamData = new AudioStreamData();
	protected boolean rendering = false;
	
	public AudioInputESS() {
		Ess.start(P.p); 
		audioInput = new AudioInput( bufferSize );
		fft = new FFT( bufferSize * 2 );
		fft.equalizer(true);
		// set default props
		fft.limits(.005f, .05f);
		fft.damp(0.18f); // 0.13f	// higher numbers = less dampening
		fft.averages(32);
		detector = new AudioInputESSBeatDetect(bufferSize, 44100);
		detector.detectMode(AudioInputESSBeatDetect.SOUND_ENERGY); //FREQ_ENERGY
		audioInput.start();
	}
	
	public AudioStreamData audioData() {
		return audioStreamData;
	}
	
	public FFT fft() {
		return fft;
	}
	
	public void update(PGraphics pg) {
		// update audio data object
		audioStreamData.setFFTFrequencies(fft.spectrum);
		audioStreamData.setWaveformOffsets(audioInput.buffer);
		audioStreamData.setAmp(fft.max * 20f);
		audioStreamData.freqsCopyDampened();
		audioStreamData.update();

		// debug draw
		if(pg != null) audioStreamData.drawDebug(pg);
	}
	
	public void updateForRender(AudioChannel audioPlayer, int pos) {		
		// disable normal PApplet callback below
		rendering = true;
		// read spectrum from audio player and set on data object
		pos = P.constrain(pos, 0, audioPlayer.size - audioPlayer.buffer.length);
		fft.getSpectrum( audioPlayer.samples, pos );
		audioStreamData.setFFTFrequencies(fft.spectrum);
		// set waveform data (updates slowly for some reason, so we lerp)
		// audioStreamData.setWaveformOffsets(audioPlayer.buffer2);	// buffer ?? 
		audioStreamData.lerpWaveformOffsets(audioPlayer.buffer2, 0.2f);	// buffer ?? 
		// set level
		fft.getLevel(audioPlayer);
		audioStreamData.setAmp(fft.max * 20f);
		// copy & update
		audioStreamData.freqsCopyDampened();
		audioStreamData.update();
	}
	
	public void audioInputCallback(AudioInput theInput) {
		if(rendering) return;
		fft.getSpectrum(theInput);
		detector.detect(theInput);
		if(detector.isOnset() && P.p.millis() > lastBeatTime + beatTimeThresh) {
			lastBeatTime = P.p.millis();
			audioStreamData.setBeat();
		}
	}
}
