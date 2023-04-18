package com.haxademic.demo.media.audio.analysis;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioIn.AudioInputLibrary;

public class Demo_EQBandTriggers 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	float numElements;
	float[] lastAudioFrame;
	float[] bufferedValues;
	int[] eqBandTrigger;
	float ampThreshold = 0.2f;
	
	protected void firstFrame() {
		AudioUtil.setPrimaryMixer();
		AudioIn.instance(AudioInputLibrary.Beads);
		
		numElements = 40; // p.width;
		
		lastAudioFrame = new float[512];
		for(int i=0; i < 512; i++) lastAudioFrame[i] = 0;

		bufferedValues = new float[512];
		for(int i=0; i < 512; i++) bufferedValues[i] = 0;
		
		eqBandTrigger = new int[512];
		for(int i=0; i < 512; i++) eqBandTrigger[i] = 0;
	}

	protected void drawApp() {
		background(0);
		p.noStroke();
		
		// set triggers if difference between last frame is above threshold
		for(int i=0; i < 512; i++) {
			if(AudioIn.audioFreq(i) - lastAudioFrame[i] > ampThreshold) {
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
			lastAudioFrame[i] = AudioIn.audioFreq(i) ;
		}
	}
}
