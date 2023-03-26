package com.haxademic.demo.media.audio.playback;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;

import beads.AudioContext;
import beads.Buffer;
import beads.Gain;
import beads.WavePlayer;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_WebcamToSoundBuffer 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected WavePlayer wp;
	protected PGraphics audioBufferPG;
	protected PGraphics audioBufferLerped;
	protected float[] audioBuffer;
	
	protected void firstFrame () {
		// init webcam
		WebCam.instance().setDelegate(this);
		
		// init audio
		AudioContext ac = AudioUtil.getBeadsContext();
		AudioIn.instance(new AudioInputBeads(ac));
	
		// try changing Buffer.SINE to Buffer.SQUARE
		wp = new WavePlayer(ac, 440.0f, Buffer.SINE);
		Gain g = new Gain(ac, 1, 1);
		g.addInput(wp);
		ac.out.addInput(g);
		ac.start();
		audioBuffer = wp.getBuffer().buf;
		audioBufferPG = PG.newPG(audioBuffer.length, 64);
		audioBufferLerped = PG.newPG(audioBuffer.length, 64);
	}

	protected void drawApp() {
		// set up context
		p.background(0);
		p.noStroke();
		
		// update audio
		updateAudio();
		
		// draw to screen
		ImageUtil.cropFillCopyImage(pg, p.g, false);
		
		// debug draw
		p.rect(0, 300, p.width, 1);
		float lastVal = audioBuffer[0];
		int bufferLength = audioBuffer.length;
		float samplerStep = pg.width / (float) bufferLength;
		for (int j = 1; j < audioBuffer.length; j++) {
			p.noFill();
			p.stroke(255);
			p.line(samplerStep * (j-1), pg.height/2 + 100f * lastVal, samplerStep * j, pg.height/2 + 100f * audioBuffer[j]);
			lastVal = audioBuffer[j];
		}
	}
	
	protected void updateAudio() {
		PImage camFrame = WebCam.instance().image();
		ImageUtil.cropFillCopyImage(camFrame, pg, true);
		ImageUtil.cropFillCopyImage(camFrame, audioBufferPG, true);
		ImageUtil.flipH(pg);
		ImageUtil.flipH(audioBufferPG);
		
		for (int i = 0; i < 5; i++) {			
			BlurHFilter.instance().setBlurByPercent(Mouse.xNorm * 1f, pg.width);
			BlurHFilter.instance().applyTo(audioBufferPG);
			BlurVFilter.instance().setBlurByPercent(Mouse.yNorm * 1f, pg.height);
			BlurVFilter.instance().applyTo(audioBufferPG);
		}
		ContrastFilter.instance().setContrast(1.9f);
		ContrastFilter.instance().applyTo(audioBufferPG);

		BlendTowardsTexture.instance().setSourceTexture(audioBufferPG);
		BlendTowardsTexture.instance().setBlendLerp(0.2f);
		BlendTowardsTexture.instance().applyTo(audioBufferLerped);
		DebugView.setTexture("audioBufferPG", audioBufferPG);
		DebugView.setTexture("audioBufferLerped", audioBufferLerped);
		
		audioBufferLerped.loadPixels();
		
		// step through image to fill audio buffer
		int bufferLength = audioBuffer.length;
		float samplerStep = audioBufferLerped.width / (float) bufferLength;
		DebugView.setValue("bufferLength", bufferLength);
		DebugView.setValue("samplerStep", samplerStep);
		for (int i = 0; i < bufferLength; i++) {
			int sampleX = P.floor(i * samplerStep * 1) % audioBufferLerped.width;
			int sampleY = P.floor(audioBufferLerped.height / 2);
			int pixelColor = ImageUtil.getPixelColor(audioBufferLerped, sampleX, sampleY);
			float brightnessNorm = p.brightness(pixelColor)/255f;
			float redd = p.red(pixelColor)/255f;
			float waveVal = P.map(redd, 0, 1, -1, 1);
			float sineVal = P.sin(i * 0.01f);
			float bufferWaveVal = waveVal * brightnessNorm * 2f;
			// audioBuffer[i] = P.lerp(sineVal, bufferWaveVal, 0.95f);
			audioBuffer[i] = bufferWaveVal;
		}
		
//		wp.setFrequency(120 + 40f * P.sin(p.frameCount * 0.001f));
		wp.setFrequency(40 + 20f * p.noise(p.frameCount * 0.001f)); //  + 5 * P.sin(p.frameCount * 0.1f)
		
		// draw after
		pg.beginDraw();
		pg.fill(255);
		pg.noStroke();
		pg.rect(0, pg.height/2, pg.width, 1);
		pg.endDraw();
	}

	@Override
	public void newFrame(PImage frame) {
//		frame.loadPixels();
	}
	
	public void mousePressed() {
		super.mousePressed();
	}

}
