package com.haxademic.demo.media.audio.analysis;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.filters.pshader.compound.ReactionDiffusionStepFilter;
import com.haxademic.core.math.easing.FloatBuffer;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioIn.AudioInputLibrary;
import com.haxademic.core.render.FrameLoop;

public class Demo_EQBandsPolar 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected FloatBuffer ampBuffer = new FloatBuffer(6);
	
	protected void config() {
	    Config.setAppSize(1024, 1024);
	}
	
	protected void firstFrame() {
		AudioUtil.setPrimaryMixer();
//		AudioIn.instance(AudioInputLibrary.Beads);
		AudioIn.instance(AudioInputLibrary.ESS);
//		AudioIn.instance(AudioInputLibrary.Minim);
//		AudioIn.instance(AudioInputLibrary.Processing);
		
		AudioIn.drawDebugBuffer();
		AudioIn.drawBufferFFT();
		AudioIn.drawBufferWaveform();
	}
	
	protected void drawApp() {
		// update audio textures/buffers
//		DebugView.setTexture("AudioIn.bufferDebug", AudioIn.bufferDebug());
//		DebugView.setTexture("AudioIn.bufferFFT", AudioIn.bufferFFT());
//		DebugView.setTexture("AudioIn.bufferWaveform", AudioIn.bufferWaveform());
		pg.beginDraw();
	    if(p.frameCount == 1) {
	      pg.background(0);
	      pg.noStroke();
	    }
	    
	    PG.setCenterScreen(pg);
	    PG.setDrawCenter(pg);
	    

		// draw radial FFT
		float circleSize = 8;
		float numElements = 128;
		float radius = 30 + 20 * P.sin(p.frameCount * 0.01f);
		float halfElements = numElements / 2;
		float eqStep = 1; // 512f / numElements;
		float segmentRads = P.TWO_PI / numElements;
		float radsOffset = P.HALF_PI; // FrameLoop.count(0.01f);
		
		for(int i=0; i < numElements; i++) {
		    // get FT eq val
            int eqIndex = P.floor(i * eqStep);
            int eqIndexLoop = P.floor((i % halfElements) * eqStep);
			if(i >= halfElements) eqIndex -= eqIndexLoop * 2; 
//			if(frameCount % 300 == 0) P.out(eqIndex);
			float eq = AudioIn.audioFreq(eqIndex) * 3f;
			
			float curRads = i * segmentRads + radsOffset;
			float x = P.cos(curRads) * radius;
			float y = P.sin(curRads) * radius;
			pg.fill(255f * eq);
			pg.ellipse(x, y, circleSize, circleSize);
		}
		
		// draw middle circle
		pg.fill(0);
		pg.circle(0, 0, radius);

		// draw circle
		float audioAmp = AudioIn.amplitude();
		ampBuffer.update(audioAmp);

		// post fx
		RotateFilter.instance().setZoom(1f - 0.6f * ampBuffer.average());
		RotateFilter.instance().applyTo(pg);
		
		ReactionDiffusionStepFilter.applyTo(pg, 2, 3, 0.3f, 0.3f, 0.8f);
		
		VignetteFilter.instance().setDarkness(0.9f - 2f * ampBuffer.average());
		VignetteFilter.instance().setSpread(0.15f + 2f * ampBuffer.average());
		VignetteFilter.instance().applyTo(pg);
		
		pg.endDraw();
		
		p.image(pg, 0, 0);
	}
}

