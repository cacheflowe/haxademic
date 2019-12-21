package com.haxademic.demo.media.audio.analysis;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;

import krister.Ess.AudioInput;
import krister.Ess.Ess;
import krister.Ess.FFT;

public class Demo_ESS_Basic
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected AudioInput audioInput;
	protected FFT fft;
	protected int bufferSize = 512;

	public void setupFirstFrame() {
		Ess.start(P.p); 
		
		fft = new FFT( bufferSize * 2 );
		fft.equalizer(true);
		fft.limits(.005f, .05f);
		fft.damp(0.13f);
		fft.averages(32);

		audioInput = new AudioInput( bufferSize );
		audioInput.start();
	}

	public void drawApp() {
		// draw background color based on max eq value
		DebugView.setValue("fft.max", fft.max);
		p.background(fft.max * 3000f);
		p.fill(255);
		
		// draw waveform, averages and full spectrum
		drawWaveform(audioInput.buffer, p.height * 0.33f / 2f, p.height * 0.33f);
		drawSpectrum(fft.averages, p.height * 0.66f, p.height * 0.33f);
		drawSpectrum(fft.spectrum, p.height, p.height * 0.33f);
	}
	
	protected void drawWaveform(float[] values, float startY, float colH) {
		p.stroke(255);
		float colWidth = (float) p.width / (float) values.length;
		for (int i = 0; i < values.length - 1; i++) {
			p.line(i * colWidth, startY + values[i] * colH, (i + 1) * colWidth, startY + values[i + 1] * colH);
		};
	}
	
	protected void drawSpectrum(float[] values, float startY, float colH) {
		p.noStroke();
		float colWidth = (float) p.width / (float) values.length;
		for (int i = 0; i < values.length; i++) {
			p.rect(i * colWidth, startY, colWidth, values[i] * -colH);
		};
	}
	
	///////////////////////////////////////
	// EQUIRED ESS CALLBACK IN PAPPLET
	///////////////////////////////////////

	public void audioInputData( AudioInput theInput ) {
		fft.getSpectrum(theInput);
		fft.getLevel(theInput);
	}

}