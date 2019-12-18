package com.haxademic.core.media.audio.analysis;

import com.haxademic.core.app.P;

import beads.AudioContext;
import beads.Bead;
import beads.BiquadFilter;
import beads.FFT;
import beads.Gain;
import beads.PeakDetector;
import beads.PowerSpectrum;
import beads.RampBuffer;
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
	protected BiquadFilter fftFilter;
	protected AudioStreamData audioStreamData = new AudioStreamData();
	protected boolean beatDirty = false;
	protected int beatTimeThresh = 300;
	protected int lastBeatTime = 0;
	protected boolean audioInput = true;
	
	public AudioInputBeads() {
		  ac = new AudioContext();
		  
		  ShortFrameSegmenter sfs = new ShortFrameSegmenter(ac);
		  gain = new Gain(ac, 2);
		  FFT fft = new FFT();
		  ps = new PowerSpectrum();
		  sfs.setChunkSize(512);
		  sfs.setHopSize(256);
		  sfs.addInput(gain);   // sfs.addInput(ac.out);
		  sfs.addListener(fft);
		  fft.addListener(ps);
		  ac.out.addDependent(sfs);
		  
		  addBeatDetection();
		  
		  // common setup and stream init
		  gain.addInput(ac.getAudioInput());
		  
		  ac.start();
	}
	
	public AudioInputBeads(AudioContext exsitingAc) {
          ac = exsitingAc;
          audioInput = false;
          
          // make a filter to chill out the lowest frequencies of an audio file
//          fftFilter = new BiquadFilter(ac, BiquadFilter.BP_PEAK, 1900.0f, 0.6f);// connect the SamplePlayer to the filterfilter1.addInput(sp);
          fftFilter = new BiquadFilter(ac, BiquadFilter.BP_PEAK, 5000.0f, 0.7f);// connect the SamplePlayer to the filterfilter1.addInput(sp);
          fftFilter.addInput(ac.out);
			  
		  ShortFrameSegmenter sfs = new ShortFrameSegmenter(ac);
		  sfs.addInput(fftFilter); // sfs.addInput(ac.out);
		  FFT fft = new FFT();
		  ps = new PowerSpectrum();
		  sfs.setChunkSize(512);
		  sfs.setHopSize(256);
		  sfs.addListener(fft);
		  fft.addListener(ps);
		  ac.out.addDependent(sfs);

		  addBeatDetection();
	}
	
	public void addBeatDetection() {
		  // beat detection
		  SpectralDifference sd = new SpectralDifference(ac.getSampleRate());
		  ps.addListener(sd);
		  od = new PeakDetector();
		  sd.addListener(od);
		  od.setThreshold(0.85f);
		  od.setResetDelay(250);
		  od.setAlpha(0.1f);
		  od.setFilter(new RampBuffer().generateBuffer(64));		
		  od.addMessageListener(
		  	new Bead() {
		  		protected void messageReceived(Bead b) {
		  			beatDirty = true;
		  		}
		  	}
		  );
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
				freqs[i] = features[i] * 15f * window(features.length, i);
			}
			
			audioStreamData.setFFTFrequencies(freqs);
			audioStreamData.calcFreqsDampened();
		}
		if(audioInput) {
			audioStreamData.setWaveformOffsets(gain.getOutBuffer(0));
		} else {
			audioStreamData.setWaveformOffsets(ac.out.getOutBuffer(0));
		}
		if(beatDirty && P.p.millis() > lastBeatTime + beatTimeThresh) {
			lastBeatTime = P.p.millis();
			beatDirty = false;
			audioStreamData.setBeat();
		}

		// audioStreamData.setAmp(od.getLastOnsetValue());
		audioStreamData.calcAmpAverage();
		audioStreamData.update();

		// debug draw
		if(pg != null) audioStreamData.drawDebug(pg);
	}
	
	protected float window(int length, int index) {
		//float alpha = 0.25f;
		
		// bartlett-hann window
		return (float) (0.62f - 0.48f * P.abs(index / (length - 1) - 0.5f) - 0.38f * P.cos(P.TWO_PI * index / (length - 1f)));
		// bartlett window: 
		// return 2f / (length - 1) * ((length - 1) / 2f - Math.abs(index - (length - 1) / 2f));
		// blackman window: https://github.com/ddf/Minim/blob/master/src/main/java/ddf/minim/analysis/BlackmanWindow.java
		// float a0 = (1 - alpha) / 2f;
		// float a1 = 0.5f;
		// float a2 = alpha / 2f;
		// return a0 - a1 * (float) P.cos(P.TWO_PI * index / (length - 1)) + a2 * (float) P.cos(4f * P.PI * index / (length - 1));
		// triangle window: https://github.com/ddf/Minim/blob/master/src/main/java/ddf/minim/analysis/TriangularWindow.java
		// return 2f / length * (length / 2f - Math.abs(index - (length - 1) / 2f));
		// gauss window: https://github.com/ddf/Minim/blob/master/src/main/java/ddf/minim/analysis/GaussWindow.java
		// return (float) Math.pow(Math.E, -0.5 * Math.pow((index - (length - 1) / (double) 2) / (alpha * (length - 1) / (double) 2), (double) 2));
	}
}
