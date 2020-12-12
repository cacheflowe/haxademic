package com.haxademic.demo.hardware.serial;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.serial.LedStripLPD8806;
import com.haxademic.core.hardware.serial.SerialDevice.ISerialDeviceDelegate;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioIn.AudioInputLibrary;

import processing.serial.Serial;

public class Demo_LedStripLPD8806_AudioInput 
extends PAppletHax
implements ISerialDeviceDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float numElements;
	protected float[] lastAudioFrame;
	protected float[] bufferedValues;
	protected int[] eqBandTrigger;
	protected float ampThreshold = 0.2f;
	
	protected LedStripLPD8806 ledStrip;
	
	protected void firstFrame() {
		AudioIn.instance(AudioInputLibrary.ESS);

		numElements = 40; // p.width;
		
		lastAudioFrame = new float[512];
		for(int i=0; i < 512; i++) lastAudioFrame[i] = 0;

		bufferedValues = new float[512];
		for(int i=0; i < 512; i++) bufferedValues[i] = 0;
		
		eqBandTrigger = new int[512];
		for(int i=0; i < 512; i++) eqBandTrigger[i] = 0;
		
		ledStrip = new LedStripLPD8806(this, 0, 115200, 32); 
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
		
		// send to lights
		ledStrip.update(p.get(), 0.4f, 0.85f);
	}
	
	public void newDataAvailable(Serial serialDevice) {
		// log incoming messages
		String inputStr = serialDevice.readString();
		DebugView.setValue("[Serial in]", inputStr);
	}
}
