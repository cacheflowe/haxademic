package com.haxademic.core.audio.analysis.input;

import beads.AudioContext;
import beads.Bead;
import beads.FFT;
import beads.Gain;
import beads.PeakDetector;
import beads.PowerSpectrum;
import beads.ShortFrameSegmenter;
import beads.SpectralDifference;
import processing.core.PGraphics;

public class AudioInputBeads
implements IAudioInput {

	protected AudioContext ac;
	protected Gain gain;
	protected float[] freqs;
	protected PowerSpectrum ps;
	protected PeakDetector od;
	protected AudioStreamData audioStreamData = new AudioStreamData();
	protected boolean beatDirty = false;
	
	public AudioInputBeads() {
		  ac = new AudioContext();
		  ShortFrameSegmenter sfs = new ShortFrameSegmenter(ac);
		  FFT fft = new FFT();
		  gain = new Gain(ac, 2);
		  ps = new PowerSpectrum();
		  sfs.setChunkSize(2048);
		  sfs.setHopSize(441);
		  sfs.addInput(gain);   // sfs.addInput(ac.out);
		  sfs.addListener(fft);
		  fft.addListener(ps);
		  
		  // beat detection
		  SpectralDifference sd = new SpectralDifference(ac.getSampleRate());
		  ps.addListener(sd);
		  od = new PeakDetector();
		  sd.addListener(od);
		  /*
		   * These parameters will need to be adjusted based on the 
		   * type of music. This demo uses the mouse position to adjust 
		   * them dynamically.
		   * mouse.x controls Threshold, mouse.y controls Alpha
		   */
		  od.setThreshold(0.2f);
		  od.setAlpha(.9f);
		  od.addMessageListener(
		  	new Bead() {
		  		protected void messageReceived(Bead b) {
		  			beatDirty = true;
		  		}
		  	}
		  );

		  // common setup and stream init
		  ac.out.addDependent(sfs);
		  gain.addInput(ac.getAudioInput());
		  
		  ac.start();
	}
	
	public AudioStreamData audioData() {
		return audioStreamData;
	}
	
	public void update(PGraphics pg) {
		// get FFT spectrum from Beads
		if(ps == null) return;
		float[] features = ps.getFeatures();
		
		// update audio data object
		if(features != null) {
			// make a lower-amplitude copy
			if(freqs == null) freqs = new float[features.length];
			for (int i = 0; i < features.length; i++) {
				freqs[i] = features[i] * 0.1f;
			}
			
			audioStreamData.setFFTFrequencies(freqs);
			audioStreamData.calcFreqsDampened();
		}
		audioStreamData.setWaveformOffsets(gain.getOutBuffer(0));
		if(beatDirty) {
			beatDirty = false;
  			audioStreamData.setBeat();
		}
		// audioStreamData.setAmp(od.getLastOnsetValue());
		audioStreamData.calcAmpAverage();
		audioStreamData.update();

		// debug draw
		if(pg != null) audioStreamData.drawDebug(pg);
	}
	
}
