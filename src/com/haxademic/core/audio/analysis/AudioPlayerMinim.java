package com.haxademic.core.audio.analysis;

import com.haxademic.core.app.P;
import com.haxademic.core.audio.analysis.input.AudioStreamData;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;

public class AudioPlayerMinim {
	
	protected Minim minim;
	protected AudioPlayer player;
	protected FFT fft;
	protected float[] spectrum;
	protected BeatDetect beatDetection;
	protected float progress = 0;
	protected int lastPosition = 0;
	protected boolean looped = true;
	protected boolean loopStepped = true;
	protected int loopSteps = 8;
	protected int loopCurStep = 0;

	protected AudioStreamData audioData = new AudioStreamData();


	public AudioPlayerMinim(Minim m, String audioFile) {
		minim = m;

		player = minim.loadFile(audioFile, 512);
		player.loop();

		fft = new FFT( player.bufferSize(), player.sampleRate() );
		spectrum = new float[player.bufferSize() / 2];
		
		beatDetection = new BeatDetect();
		beatDetection.detectMode(BeatDetect.SOUND_ENERGY);
		beatDetection.setSensitivity(300);
	}
	
	public AudioStreamData audioData() {
		return audioData;
	}
	
	public boolean looped() {
		return looped;
	}
	
	public boolean loopStepped() {
		return loopStepped;
	}
	
	public int loopCurStep() {
		return loopCurStep;
	}
	
	public void start() {
		player.rewind();
		player.loop();
		player.cue(0);
	}
	
	public void setVolume(float vol) {
		float gainn = P.map(vol, 0, 1, -50, 0);
		player.setGain(gainn);
	}

	public void update() {
		// check loop
		looped = (player.position() < lastPosition);
		lastPosition = player.position();
		progress = (float) player.position() / (float) player.length();
		// subdivide loops
		float prevLoopStep = loopCurStep; 
		loopCurStep = P.floor(progress * (float) loopSteps);
		loopStepped = (prevLoopStep != loopCurStep); 

		// analyze
		fft.forward(player.mix);
		for(int i=0; i < spectrum.length; i++) spectrum[i] = fft.getBand(i);
		beatDetection.detect( player.mix );

		// set analysis data
		audioData.setFFTFrequencies(spectrum);
		audioData.setWaveformOffsets(player.mix.toArray());
		audioData.calcFreqsDampened();
		if( beatDetection.isOnset() == true ) audioData.setBeat();
		audioData.setAmp(player.mix.level());
		audioData.setProgress(progress);
		audioData.update();
	}
	
}
