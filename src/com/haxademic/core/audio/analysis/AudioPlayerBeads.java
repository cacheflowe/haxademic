package com.haxademic.core.audio.analysis;

import com.haxademic.core.file.FileUtil;

import beads.AudioContext;
import beads.Bead;
import beads.FFT;
import beads.Gain;
import beads.PeakDetector;
import beads.PowerSpectrum;
import beads.SampleManager;
import beads.SamplePlayer;
import beads.ShortFrameSegmenter;
import beads.SpectralDifference;

public class AudioPlayerBeads {
	
	protected AudioContext audioContext;
	protected SamplePlayer player;
	protected Gain gain;
	protected ShortFrameSegmenter sfs;
	protected PowerSpectrum powerSpectrum;
	protected PeakDetector od;
	protected AudioStreamData audioData = new AudioStreamData();

	public AudioPlayerBeads(AudioContext ac, String audioFile) {
		// store global audio context
		audioContext = ac;
		
		// build sample player
		player = new SamplePlayer(audioContext, SampleManager.sample(FileUtil.getFile(audioFile)));
		player.setLoopType(SamplePlayer.LoopType.LOOP_FORWARDS);
		
		// build audio stream
		gain = new Gain(audioContext, 2, 0.2f);
		gain.addInput(player);
		audioContext.out.addInput(gain);
		powerSpectrum = analyzeAudio();
		detectBeats();
	}
	
	public AudioStreamData audioData() {
		return audioData;
	}
	
	public void start() {
		player.start(0);
//		player.reTrigger();
	}
	
	protected PowerSpectrum analyzeAudio() {
		sfs = new ShortFrameSegmenter(audioContext);
		sfs.addInput(gain);
		FFT fft = new FFT();
		PowerSpectrum ps = new PowerSpectrum();
		sfs.addListener(fft);
		fft.addListener(ps);
		gain.addDependent(sfs);
		return ps;
	}
	
	protected void detectBeats() {
		  // beat detection
		  SpectralDifference sd = new SpectralDifference(audioContext.getSampleRate());
		  powerSpectrum.addListener(sd);
		  od = new PeakDetector();
		  sd.addListener(od);

		  od.setThreshold(0.15f);
		  od.setAlpha(.9f);
		  od.addMessageListener(new Bead() {
			protected void messageReceived(Bead b)
			{
				audioData.setBeat();
			}
		  });

		  // common setup and stream init
		  gain.addDependent(sfs);
	}

	public void update() {
		float[] features = powerSpectrum.getFeatures();
		if(features != null) audioData.setFFTFrequencies(features);
		audioData.setWaveformOffsets(player.getOutBuffer(0));
		audioData.update();
	}
	
}
