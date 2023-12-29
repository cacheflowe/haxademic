package com.haxademic.demo.media.audio.analysis;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioIn.AudioInputLibrary;

public class Demo_EQBandDistribute 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void firstFrame() {
		AudioUtil.setPrimaryMixer();
		// AudioUtil.getAudioMixerIndex("Microphone Array");
		AudioIn.instance();
		// AudioIn.instance(AudioInputLibrary.Beads);
		// AudioIn.instance(AudioInputLibrary.ESS);
		// AudioIn.instance(AudioInputLibrary.Minim);
		// AudioIn.instance(AudioInputLibrary.Processing);
		
		AudioIn.drawDebugBuffer();
	}
	
	protected void drawApp() {
		// update audio textures/buffers
//		DebugView.setTexture("AudioIn.bufferDebug", AudioIn.bufferDebug());
//		DebugView.setTexture("AudioIn.bufferFFT", AudioIn.bufferFFT());
//		DebugView.setTexture("AudioIn.bufferWaveform", AudioIn.bufferWaveform());
		
		p.background(0);
		p.noStroke();

		// draw bars
		float numElements = p.width;
		float eqStep = 512f / numElements;
		float barW = numElements / 512f;
		int eqIndex = 0;
		for(int i=0; i < numElements; i++) {
			eqIndex = P.floor(i * eqStep);
			float eq = AudioIn.audioFreq(eqIndex);
			p.fill(255f * eq);
			p.rect(i * barW, 0, barW, p.height);
		}

		// draw circle
		float audioAmp = AudioIn.amplitude();
		p.push();
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
		p.fill(255f);
		p.ellipse(0, 0, audioAmp * 1000f, audioAmp * 1000f);
		PG.setDrawCorner(p);
		p.pop();
	}
}
