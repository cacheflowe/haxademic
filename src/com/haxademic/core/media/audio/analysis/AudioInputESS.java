package com.haxademic.core.media.audio.analysis;

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
		if(rendering) return; // don't override with microphone data
		
		// update audio data object
		audioStreamData.setFFTFrequencies(fft.spectrum);
		audioStreamData.setWaveformOffsets(audioInput.buffer);
		audioStreamData.setAmp(fft.max * 20f);
		audioStreamData.freqsCopyDampened();
		audioStreamData.update();

		// debug draw
		if(pg != null) audioStreamData.drawDebug(pg);
	}
	
	// override microphone input with audio file played in Renderer
	public void updateForRender(AudioChannel audioPlayer, int pos) {		
		// disable normal PApplet callback below
		rendering = true;
		// read spectrum from audio player and set on data object
		if(pos >= 0) {
			pos = P.constrain(pos, 0, audioPlayer.size - audioPlayer.buffer.length);
			fft.getSpectrum(audioPlayer.samples, pos);
		} else {
			fft.getSpectrum(audioPlayer);
		}
		audioStreamData.setFFTFrequencies(fft.spectrum);
		audioStreamData.setWaveformOffsets(audioPlayer.buffer);	// this looks super slow during simulation, but should be good during a real render
		fft.getLevel(audioPlayer);
		audioStreamData.setAmp(fft.max * 20f);
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
