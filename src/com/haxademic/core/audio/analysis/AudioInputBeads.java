package com.haxademic.core.audio.analysis;

import beads.AudioContext;
import beads.Bead;
import beads.FFT;
import beads.PeakDetector;
import beads.PowerSpectrum;
import beads.ShortFrameSegmenter;
import beads.SpectralDifference;
import processing.core.PGraphics;

public class AudioInputBeads {

	protected AudioContext ac;
	protected PowerSpectrum ps;
	protected PeakDetector od;
	protected AudioStreamData audioStreamData = new AudioStreamData();
	
	public AudioInputBeads() {
		  ac = new AudioContext();
		  ShortFrameSegmenter sfs = new ShortFrameSegmenter(ac);
		  FFT fft = new FFT();
		  ps = new PowerSpectrum();
		  sfs.setChunkSize(2048);
		  sfs.setHopSize(441);
		  sfs.addInput(ac.out);
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
		  	new Bead(){
		  		protected void messageReceived(Bead b)
		  		{
		  			audioStreamData.setBeat();
		  		}
		  	}
		  );

		  // common setup and stream init
		  ac.out.addDependent(sfs);
		  ac.out.addInput(ac.getAudioInput());
		  
		  ac.start();
//		  ac.out.setGain(-1);
	}
	
	public void update(PGraphics pg) {
		// get FFT spectrum from Beads
		if(ps == null) return;
		float[] features = ps.getFeatures();
		
		// update audio data object
		if(features != null) audioStreamData.setFFTFrequencies(features);
		audioStreamData.setWaveformOffsets(ac.out.getOutBuffer(0));
		// audioStreamData.setAmp(od.getLastOnsetValue());
		audioStreamData.calcAmpAverage();
		audioStreamData.update();

		// debug draw
		if(pg != null) audioStreamData.drawDebug(pg);
	}
	
}
