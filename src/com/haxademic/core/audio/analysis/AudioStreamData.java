package com.haxademic.core.audio.analysis;

import com.haxademic.core.app.P;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;

public class AudioStreamData {
	
	protected float[] frequencies = null;
	protected float[] waveform = null;
	protected float amp = 0;
	protected EasingFloat beatOnset = new EasingFloat(0, 8);
	
	public static float debugW = 300;
	public static float debugH = 300;
	protected float waveformAmp = 5;

	public AudioStreamData() {
		
	}
	
	public void setFFTFrequencies(float[] freqs) {
		if(frequencies == null) frequencies = new float[freqs.length];
		System.arraycopy(freqs, 0, frequencies, 0, freqs.length);
	}
	
	public void setWaveformOffsets(float[] buffer) {
		if(waveform == null) waveform = new float[buffer.length];
		System.arraycopy(buffer, 0, waveform, 0, buffer.length);
	}
	
	public void setAmp(float newAmp) {
		amp = newAmp;
	}
	
	public void setBeat() {
		beatOnset.setCurrent(1);		
		beatOnset.setTarget(0);
	}
	
	public void update() {
		beatOnset.update();
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
		pg.rect(0, rowHeight * 2, debugW / 2, rowHeight);
		pg.rect(debugW / 2, rowHeight * 2, debugW / 2, rowHeight);
		
		// draw FFT
		if(frequencies != null) {
			float fftLineW = (float) debugW / (float) frequencies.length; 
			pg.strokeWeight(fftLineW);
			pg.fill(0,255,0);
			for (int i = 0; i < frequencies.length; i++) {
				float fftVal = frequencies[i];
				float fftLineH = P.min(rowHeight, fftVal * rowHeight);
				pg.rect(i * fftLineW, rowHeight - fftLineH, fftLineW, fftLineH);
			}
			
			// # FFT values
			pg.fill(255);
			pg.text(""+frequencies.length, 0, 0, debugW, rowHeight);
		}
		
		// draw waveform
		// should be values: -1 - 1
		if(waveform != null) {
			float waveformLineW = (float) debugW / (float) waveform.length; 
			pg.strokeWeight(waveformLineW);
			pg.fill(0,255,0);
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
		pg.fill(255, 255 * beatOnset.value());
		pg.rect(0, rowHeight * 2, debugW / 2, rowHeight);

		// draw overall amp
		pg.fill(255);
		pg.rect(debugW / 2, rowHeight, debugW / 2, -rowHeight * amp);
	}
}