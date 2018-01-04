package com.haxademic.sketch.audio;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.OpenGLUtil;

public class EQBandTriggers 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	float numElements;
	float[] lastAudioFrame;
	float[] bufferedValues;
	int[] eqBandTrigger;
	float ampThreshold = 0.2f;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "600" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "600" );
	}

	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );
		numElements = 40; // p.width;
		
		lastAudioFrame = new float[512];
		for(int i=0; i < 512; i++) lastAudioFrame[i] = 0;

		bufferedValues = new float[512];
		for(int i=0; i < 512; i++) bufferedValues[i] = 0;
		
		eqBandTrigger = new int[512];
		for(int i=0; i < 512; i++) eqBandTrigger[i] = 0;
	}

	public void drawApp() {
		background(0);
		p.noStroke();
		
		// set triggers if difference between last frame is above threshold
		for(int i=0; i < 512; i++) {
			if(_audioInput.getFFT().spectrum[i] - lastAudioFrame[i] > ampThreshold) {
				bufferedValues[i] = 1.0f;
			}
		}

		// draw triggered, buffered spectrum
		float eqStep = 512f / numElements;
		float barW = p.width / numElements;
		int eqIndex = 0;
		for( int i = 0; i < numElements; i++ ) {
			eqIndex = P.floor(i * eqStep);
			float eq = bufferedValues[eqIndex];
			p.fill(255f * eq);
			p.rect(i * barW, 0, barW, p.height);
		}
		
		// ease out buffer to draw
		for(int i=0; i < 512; i++) {
			bufferedValues[i] *= 0.5f;
		}
		
		// copy audio buffer
		for(int i=0; i < 512; i++) {
			lastAudioFrame[i] = _audioInput.getFFT().spectrum[i];
		}
	}
}
