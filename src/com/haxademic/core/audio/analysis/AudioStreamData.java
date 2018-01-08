package com.haxademic.core.audio.analysis;

import com.haxademic.core.app.P;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;

public class AudioStreamData {
	
	protected float[] frequencies = null;
	protected float[] freqsDampened = null;
	protected float[] waveform = null;
	protected float amp = 0;
	protected float gain = 1;
	protected EasingFloat beatOnset = new EasingFloat(0, 8);
	protected int beatFrame = 0;
	protected float progress = 0;
	
	public static float debugW = 300;
	public static float debugH = 300;
	protected float waveformAmp = 1;

	public AudioStreamData() {
		
	}
	
	// setters
	
	public void setFFTFrequencies(float[] freqs) {
		if(frequencies == null) {
			frequencies = new float[freqs.length];
			freqsDampened = new float[freqs.length];
			for(int i=0; i < freqsDampened.length; i++) freqsDampened[i] = 0;
		}
		System.arraycopy(freqs, 0, frequencies, 0, freqs.length);
		for(int i=0; i < frequencies.length; i++) frequencies[i] = P.abs(frequencies[i]);
	}
	
	public void setWaveformOffsets(float[] buffer) {
		if(waveform == null) waveform = new float[buffer.length];
		System.arraycopy(buffer, 0, waveform, 0, buffer.length);
	}
	
	public void calcFreqsDampened() {
		for(int i=0; i < frequencies.length; i++) {
			float lerpVal = (freqsDampened[i] < frequencies[i]) ? 0.3f : 0.15f;
			lerpVal = 0.2f;
			freqsDampened[i] = P.lerp(freqsDampened[i], frequencies[i], lerpVal);	
		}
	}
	
	public void calcAmpAverage() {
		if(frequencies == null) return;
		amp = 0;
		for(int i=0; i < frequencies.length; i++) amp += P.abs(frequencies[i]);
		amp /= (float) frequencies.length;
	}
	
	public void setAmp(float newAmp) {
		amp = newAmp;
	}
	
	public void setProgress(float prog) {
		progress = prog;
	}
	
	public void setGain(float newGain) {
		gain = newGain;
	}
	
	public void setBeat() {
		beatOnset.setCurrent(1);		
		beatOnset.setTarget(0);
		beatFrame = P.p.frameCount;
	}
	
	public void setWaveformAmp(float newAmp) {
		waveformAmp = newAmp;
	}
	
	// getters
	
	public float[] getFrequencies() {
		if(freqsDampened != null) return freqsDampened;
		else return frequencies;
	}
	
	public float[] getWaveform() {
		return waveform;
	}
	
	public float getAmp() {
		return amp;
	}
	
	public float getProgress() {
		return progress;
	}
	
	public boolean getIsBeat() {
		return P.p.frameCount == beatFrame;
	}
	
	// private
	
	public void update() {
		beatOnset.update();
		if(gain != 1) {
			for(int i=0; i < frequencies.length; i++) frequencies[i] *= gain;
			for(int i=0; i < freqsDampened.length; i++) freqsDampened[i] *= gain;
			for(int i=0; i < waveform.length; i++) waveform[i] *= gain;
		}
	}
	
	public void drawDebug(PGraphics pg) {
		// display config
		float rowHeight = 100;
		pg.textAlign(P.RIGHT, P.TOP);
		
		// draw background
		pg.fill(0);
		pg.stroke(255);
		pg.strokeWeight(2);
		pg.rect(0, 0, debugW, rowHeight);
		pg.rect(0, rowHeight, debugW, rowHeight);
		pg.rect(0, rowHeight * 2, debugW / 2, rowHeight * 0.5f);
		pg.rect(debugW / 2, rowHeight * 2, debugW / 2, rowHeight * 0.5f);
		pg.rect(0, rowHeight * 2.5f, debugW, rowHeight * 0.5f);
		
		// draw FFT
		pg.noFill();
		if(frequencies != null) {
			float fftLineW = (float) debugW / (float) frequencies.length; 
			pg.strokeWeight(fftLineW);
			pg.stroke(0,255,0);
			pg.noFill();
			for (int i = 0; i < frequencies.length; i++) {
				float fftVal = frequencies[i];
				float fftLineH = P.min(rowHeight, fftVal * rowHeight);
				pg.rect(i * fftLineW, rowHeight - fftLineH, fftLineW, fftLineH);
			}
			
			// # FFT values
			pg.fill(255);
			pg.text(""+frequencies.length, 0, 0, debugW, rowHeight);
		}
		
		if(freqsDampened != null) {
			float fftLineW = (float) debugW / (float) freqsDampened.length; 
			pg.strokeWeight(fftLineW);
			pg.stroke(255, 127);
			pg.noFill();
			for (int i = 0; i < freqsDampened.length; i++) {
				float fftVal = freqsDampened[i];
				float fftLineH = P.min(rowHeight, fftVal * rowHeight);
				pg.rect(i * fftLineW, rowHeight - fftLineH, fftLineW, fftLineH);
			}
			
			// # FFT values
			pg.fill(255);
			pg.text(""+freqsDampened.length, 0, 0, debugW, rowHeight);
		}
		
		
		
		// draw waveform
		// should be values: -1 - 1
		if(waveform != null) {
			float waveformLineW = (float) debugW / (float) waveform.length; 
			pg.strokeWeight(waveformLineW);
			pg.stroke(0,255,0);
			pg.noFill();
			for(int i=1; i < waveform.length; i++) {
				pg.line(
					(i-1) * waveformLineW, 
					rowHeight + rowHeight/2 + rowHeight * waveformAmp * waveform[i-1],
					i * waveformLineW, 
					rowHeight + rowHeight/2 + rowHeight * waveformAmp * waveform[i]
				);
			}
			
			// # waveform values
			pg.fill(255);
			pg.text(""+waveform.length, 0, rowHeight, debugW, rowHeight);
		}
		
		// draw beat detection
		pg.noStroke();
		pg.fill(255, 255 * beatOnset.value());
		pg.rect(0, rowHeight * 2, debugW / 2, rowHeight / 2);

		// draw overall amp
		pg.fill(255);
		pg.rect(debugW / 2, rowHeight * 2.5f, debugW / 2, -rowHeight * 0.5f * amp);
		
		// draw progress
		pg.fill(0,255 * gain,0);
		pg.rect(0, rowHeight * 2.5f, debugW * progress, rowHeight * 0.5f);
	}
}