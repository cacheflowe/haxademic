package com.haxademic.sketch.audio.beads;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.math.easing.EasingFloat;

import beads.AudioContext;
import beads.Bead;
import beads.FFT;
import beads.Gain;
import beads.PeakDetector;
import beads.PowerSpectrum;
import beads.ShortFrameSegmenter;
import beads.SpectralDifference;

public class LoopPlayer_Beads
extends PAppletHax { public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	AudioContext ac;
	PowerSpectrum ps;
	PeakDetector od;
	EasingFloat beatOnset = new EasingFloat(0, 8);

	protected void overridePropsFile() {
		//	p.appConfig.setProperty( AppSettings.WIDTH, 800 );
	}

	public void setupFirstFrame2() {
		  ac = new AudioContext();
		  Gain g = new Gain(ac, 2, 0.4f);
		  g.addInput(ac.getAudioInput());
		  ac.out.addInput(g);
		  // selectInput("Select an audio file:", "fileSelected");
		  
		  /*
		   * To analyse a signal, build an analysis chain.
		   */
		  ShortFrameSegmenter sfs = new ShortFrameSegmenter(ac);
		  sfs.addInput(g);	// ac.out - this send input to speaker out
		  FFT fft = new FFT();
		  ps = new PowerSpectrum();
		  sfs.addListener(fft);
		  fft.addListener(ps);
		  g.addDependent(sfs);	// ac.out.addDependent
		  //and begin
		  ac.start();
	}
		  
	public void setupFirstFrame() {
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
		  			beatOnset.setCurrent(1);		
		  			beatOnset.setTarget(0);		
		  		}
		  	}
		  );


		  ac.out.addDependent(sfs);
		  ac.out.addInput(ac.getAudioInput());
		  
		  ac.start();
		  ac.out.setGain(-1);
	}
	
	
	
	public void drawApp() {
		background(0);
		stroke(255, 102, 204);
		
		// show FFT spectrum
		if(ps == null) return;
		float[] features = ps.getFeatures();
		// FFT length of 255
		if(features != null) {
			p.debugView.setValue("ps.getFeatures().length", features.length);
			//scan across the pixels
			for(int i = 0; i < width; i++) {
				int featureIndex = i * features.length / width;
				int vOffset = height - 1 - Math.min((int)(features[featureIndex] * height), height - 1);
				line(i,height,i,vOffset);
			}
		}
		
		// beat detection
		beatOnset.update();
		fill(255, 255 * beatOnset.value());
		p.rect(0, 0, p.width, p.height);
	}
}
