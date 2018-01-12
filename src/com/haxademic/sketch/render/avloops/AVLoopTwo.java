package com.haxademic.sketch.render.avloops;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.audio.analysis.AudioPlayerMinim;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PBlendModes;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.file.FileUtil;

import ddf.minim.Minim;
import processing.core.PGraphics;
import processing.opengl.PShader;

public class AVLoopTwo
extends PAppletHax { 
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Minim minim;
	protected AudioPlayerMinim kicks;
	protected AudioPlayerMinim snares;
	protected AudioPlayerMinim bass;
	protected int[] knobs;
	protected boolean midiActive = false;
	protected PGraphics buffer;
	protected PShader feedbackShader;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 900);
		p.appConfig.setProperty(AppSettings.HEIGHT, 900);
	}
	
	public void setupFirstFrame() {
		minim = new Minim(P.p);
	
		// load samples
		kicks = new AudioPlayerMinim(minim, "audio/crusher-loops/kicks.wav");
		snares = new AudioPlayerMinim(minim, "audio/crusher-loops/snares.wav");
		bass = new AudioPlayerMinim(minim, "audio/crusher-loops/bass-selekta.wav");
		
		// buffer
		buffer = p.createGraphics(p.width, p.height, PRenderers.P3D);
		feedbackShader = p.loadShader(FileUtil.getFile("shaders/filters/feedback-radial.glsl"));
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') relaunchAllLoops();
	}
	
	protected void relaunchAllLoops() {
		kicks.start();
		snares.start();
		bass.start();
	}
	
	public void drawApp() {
		background(0);
		p.noFill();
		stroke(255);
		
		// update analysis
		kicks.update();
		snares.update();
		bass.update();
		
		// randomly restart kick on snare beat
//		if(snares.loopStepped()) {
//			if(snares.loopCurStep() % 2 == 0 && MathUtil.randBoolean(p)) kicks.start();
//		}
		
		// start at center
		buffer.beginDraw();
		buffer.blendMode(PBlendModes.BLEND);

		feedbackShader.set("waveAmp", 0.0015f + 0.002f * bass.audioData().getAmp());
		feedbackShader.set("waveFreq", 2f + 160f * bass.audioData().getAmp());
		feedbackShader.set("samplemult", 0.95f );
		for (int i = 0; i < 1; i++) buffer.filter(feedbackShader); 

		buffer.pushMatrix();
		buffer.translate(buffer.width / 2, buffer.height / 2);
		buffer.blendMode(PBlendModes.ADD);
		
		// draw EQ lines for kick
		float freqs[] = kicks.audioData().getFrequencies();
		float numFreqs = freqs.length;
		float segmentRads = P.TWO_PI / numFreqs;
		int freqsWrap = P.ceil(numFreqs / 6f);
		for(int i=0; i < numFreqs; i++) {
			float amp = freqs[20 + (i % freqsWrap)];
			float radiusStart = (buffer.height * 0.2f);
			float radiusEnd = radiusStart + radiusStart * amp;
			float curRads = i * segmentRads;
			buffer.stroke(255 - 255 * kicks.audioData().getAmp(), 255, 127 - 127 * amp);
			buffer.line(
				P.cos(curRads) * radiusStart, P.sin(curRads) * radiusStart, 
				P.cos(curRads) * radiusEnd, P.sin(curRads) * radiusEnd
			);
		}
		
		// draw kicks waveform
		float waveform[] = kicks.audioData().getWaveform();
		numFreqs = waveform.length;
		segmentRads = P.TWO_PI / numFreqs;
		float baseRadius = buffer.height * 0.1f;
		float radiusAmp = baseRadius * 0.004f;
		for(int i=0; i < numFreqs; i++) {
			int iNext = (i + 1) % (int) numFreqs;
			float amp = waveform[i] * 100f;
			float ampNext = waveform[iNext] * 100f;
			float curRads = i * segmentRads;
			float radius = baseRadius + radiusAmp * amp;
			float nextRads = iNext * segmentRads;
			float nextRadius = baseRadius + radiusAmp * ampNext;
			buffer.stroke(255 - 127 * kicks.audioData().getAmp(), 255, 255 - 255 * kicks.audioData().getAmp());
			buffer.line(
					P.cos(curRads) * radius, P.sin(curRads) * radius, 
					P.cos(nextRads) * nextRadius, P.sin(nextRads) * nextRadius
					);
		}
		
		// draw EQ lines for snares
		freqs = snares.audioData().getFrequencies();
		numFreqs = freqs.length;
		freqsWrap = P.ceil(numFreqs / 12f);
		segmentRads = P.TWO_PI / numFreqs;
		for(int i=0; i < numFreqs; i++) {
			float amp = freqs[20 + (i % freqsWrap)];
			float radiusStart = (buffer.height * 0.2f);
			float radiusEnd = radiusStart + radiusStart * amp;
			float curRads = segmentRads/2f + i * segmentRads;
			buffer.stroke(255 - 255 * amp, 127, 255 * amp);
			buffer.line(
					P.cos(curRads) * radiusStart, P.sin(curRads) * radiusStart, 
					P.cos(curRads) * radiusEnd, P.sin(curRads) * radiusEnd
					);
		}
		
		// draw snares waveform
		waveform = snares.audioData().getWaveform();
		numFreqs = waveform.length;
		segmentRads = P.TWO_PI / numFreqs;
		baseRadius = buffer.height * 0.15f;
		radiusAmp = baseRadius * 0.005f;
		for(int i=0; i < numFreqs; i++) {
			int iNext = (i + 1) % (int) numFreqs;
			float amp = waveform[i] * 100f;
			float ampNext = waveform[iNext] * 100f;
			float curRads = i * segmentRads;
			float radius = baseRadius + radiusAmp * amp;
			float nextRads = iNext * segmentRads;
			float nextRadius = baseRadius + radiusAmp * ampNext;
			buffer.stroke(63, 127, 127 + 127 * waveform[i]);
			buffer.line(
					P.cos(curRads) * radius, P.sin(curRads) * radius, 
					P.cos(nextRads) * nextRadius, P.sin(nextRads) * nextRadius
					);
		}
		
		// draw EQ lines for bass
		freqs = bass.audioData().getFrequencies();
		numFreqs = freqs.length;
		freqsWrap = P.ceil(numFreqs / 4f);
		segmentRads = P.TWO_PI / numFreqs;
		for(int i=0; i < numFreqs; i++) {
			float amp = freqs[10 + (i % freqsWrap)];
			float radiusStart = (buffer.height * 0.2f);
			float radiusEnd = radiusStart + radiusStart * amp;
			float curRads = i * segmentRads;
			buffer.stroke(100 * amp, 255 - 127 * amp, 255 * amp);
			buffer.line(
					P.cos(curRads) * radiusStart, P.sin(curRads) * radiusStart, 
					P.cos(curRads) * radiusEnd, P.sin(curRads) * radiusEnd
					);
		}
		
		// draw bass waveform
		waveform = bass.audioData().getWaveform();
		numFreqs = waveform.length;
		segmentRads = P.TWO_PI / numFreqs;
		baseRadius = buffer.height * 0.05f;
		radiusAmp = baseRadius * 0.005f;
		buffer.noStroke();
		buffer.fill(100 * bass.audioData().getAmp(), 127, 255 - 255 * bass.audioData().getAmp());
		buffer.beginShape();
		for(int i=0; i < numFreqs; i++) {
			int iNext = (i + 1) % (int) numFreqs;
			float amp = waveform[i] * 100f;
			float ampNext = waveform[iNext] * 100f;
			float curRads = i * segmentRads;
			float radius = baseRadius + radiusAmp * amp;
			float nextRads = iNext * segmentRads;
			float nextRadius = baseRadius + radiusAmp * ampNext;
			buffer.vertex(
					P.cos(curRads) * radius, P.sin(curRads) * radius, 
					P.cos(nextRads) * nextRadius, P.sin(nextRads) * nextRadius
					);
		}
		buffer.endShape();
		
		buffer.popMatrix();
		buffer.endDraw();
		
		// draw to screen
		p.image(buffer, 0, 0);
		
		// draw debug
//		kicks.audioData().drawDebug(p.g);
//		p.translate(AudioStreamData.debugW, 0);
//		snares.audioData().drawDebug(p.g);
	}

}

