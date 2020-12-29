package com.haxademic.demo.media.audio.playback;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.ArrayUtil;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.FXAAFilter;
import com.haxademic.core.draw.filters.pshader.FakeLightingFilter;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.FloatBuffer;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.playback.DroneSampler;
import com.haxademic.core.media.audio.playback.WavPlayer;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;

public class Demo_DroneSampler
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// TODO: 
	// - Visualize the progress and pitch of currently-active players
	// - Add global pitch & volume multiplier for interactivity response
	// - How to prevent clicking? 
	//   -Is there a Gain function to lerp volume?
	//   - Switch to attack/decay instead of easing equation?
	
	// audio
	protected DroneSampler[] droneSamplers;
	
	// visual
	protected EasingFloat waveformMaxVal;
	protected FloatBuffer[] waveformLerped;
	protected float[] waveformLerpValues;
	protected PGraphics waveformLerpImg;
	protected PGraphics waveformTexture;
	protected PGraphics waveformTextureSmoothed;
	protected PGraphics waveformShaderTexture;
	protected PShaderHotSwap waveformShader;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 512 );
		Config.setProperty( AppSettings.HEIGHT, 520 );
		Config.setProperty( AppSettings.SHOW_DEBUG, false );
	}

	protected void firstFrame() {
		// create looping players
		droneSamplers = new DroneSampler[] {
				new DroneSampler("audio/communichords/bass", 5, 0.33f),
				new DroneSampler("audio/communichords/mid", 4, 0.33f),
				new DroneSampler("audio/ambiance", 2.5f, 0.33f),
		};
		
		// send Beads audio player analyzer to PAppletHax
		AudioIn.instance(new AudioInputBeads(WavPlayer.sharedContext()));
		
		// prepare to draw lerped waveform
		waveformMaxVal = new EasingFloat(0, 0.01f);
		waveformLerped = new FloatBuffer[AudioIn.waveform.length];
		waveformLerpValues = new float[AudioIn.waveform.length];
		for (int i = 0; i < waveformLerped.length; i++) {
			waveformLerped[i] = new FloatBuffer(90);
			waveformLerpValues[i] = 0;
		}
		waveformLerpImg = PG.newPG2DFast(AudioIn.waveform.length, 256);
		
		waveformTexture = PG.newPG(AudioIn.waveform.length, 8);
		waveformTextureSmoothed = PG.newPG(AudioIn.waveform.length, 8);
		DebugView.setTexture("waveformLerpImg", waveformLerpImg);
		DebugView.setTexture("waveformTexture", waveformTexture);
		DebugView.setTexture("waveformTextureSmoothed", waveformTextureSmoothed);
		
		// shader to draw stripes
		waveformShaderTexture = PG.newPG(512, 256);
		waveformShader = new PShaderHotSwap(
			FileUtil.getPath("haxademic/shaders/textures/cacheflowe-audio-stripes.glsl") 
		);
	}
	

	protected void drawApp() {
		p.background(0);
		for(int i=0; i < droneSamplers.length; i++) {
			droneSamplers[i].update();
			DebugView.setValue("droneSampler_"+i, droneSamplers[i].activePlayers());
		}
		updateWaveform();
		drawShader();
		p.image(waveformLerpImg, 0, 0);
		p.image(waveformTexture, 0, waveformLerpImg.height);
		p.image(waveformShaderTexture, 0, waveformLerpImg.height + waveformTexture.height);
	}
	

	
	protected void updateWaveform() {
		// lerp waveform normalization amp
		float absMaxVal = 0;	// normalize visual waveform
		for (int i = 0; i < waveformLerped.length; i++) {
			waveformLerped[i] = waveformLerped[i].update(AudioIn.waveform[i]);
			if(P.abs(waveformLerped[i].average()) > absMaxVal) absMaxVal = P.abs(waveformLerped[i].average()); 
		}
		waveformMaxVal.setTarget(absMaxVal);
		waveformMaxVal.update(true);
		float addAmp = 1f / waveformMaxVal.value();
		DebugView.setValue("addAmp", addAmp);
		DebugView.setValue("waveformMaxVal.value()", waveformMaxVal.value());
		
		// double lerp into final float[] arrayt and crossfade ends to loop
		for (int i = 0; i < waveformLerpValues.length; i++) {
			waveformLerpValues[i] = MathUtil.easeTo(waveformLerpValues[i], waveformLerped[i].average(), 5);
		}
		ArrayUtil.crossfadeEnds(waveformLerpValues, 0.075f);
		
		// draw waveform
		waveformLerpImg.beginDraw();
		waveformLerpImg.background(0);
		waveformLerpImg.noFill();
		waveformLerpImg.stroke(255);
		waveformLerpImg.strokeWeight(1);
		waveformLerpImg.push();
		waveformLerpImg.translate(0, waveformLerpImg.height / 2);
		waveformLerpImg.beginShape();
		for (int i = 0; i < waveformLerped.length; i++) {
			waveformLerpImg.vertex(i, waveformLerpValues[i] * 10f * waveformLerpImg.height/2);// * addAmp); // waveformLerpImg.height
		}
		waveformLerpImg.endShape();
		waveformLerpImg.pop();
		waveformLerpImg.endDraw();
		
		//////////////
		// update audio texture 
		//////////////
		
		// draw waveform pixel buffer
		waveformTexture.beginDraw();
		waveformTexture.background(0);
		waveformTexture.noStroke();
		for (int i = 0; i < waveformLerped.length; i++) {
			waveformTexture.fill(127 + 127f * waveformLerpValues[i] * 10f, 255);
			waveformTexture.rect(i, 0, 1, waveformTexture.height); // waveformTexture.height
		}
		waveformTexture.endDraw();
		
		BlurProcessingFilter.instance(p).setBlurSize(20);
		BlurProcessingFilter.instance(p).setSigma(20f);
		BlurProcessingFilter.instance(p).applyTo(waveformTexture);
		BlurProcessingFilter.instance(p).applyTo(waveformTexture);
		BlurProcessingFilter.instance(p).applyTo(waveformTexture);
		
		// lerp towards 2nd texture to further smooth displacement
		BlendTowardsTexture.instance(p).setSourceTexture(waveformTexture);
		BlendTowardsTexture.instance(p).setBlendLerp(0.25f);
		BlendTowardsTexture.instance(p).applyTo(waveformTextureSmoothed);
		
		// blur final result
//		BlurHFilter.instance(p).setBlurByPercent(0.99f, waveformTextureSmoothed.width);
//		BlurHFilter.instance(p).applyTo(waveformTextureSmoothed);
//		BlurHFilter.instance(p).applyTo(waveformTextureSmoothed);
//		BlurHFilter.instance(p).applyTo(waveformTextureSmoothed);
	}

	protected void drawShader() {
		// update shader
		waveformShader.update();
		waveformShader.shader().set("waveformTex", waveformTextureSmoothed);
		waveformShader.shader().set("color1", 1f, 1f, 1f);
		waveformShader.shader().set("color2", 0f, 0f, 0f);
		waveformShader.shader().set("zoom", 25f + 5f * P.sin(p.frameCount * 0.004f));
		waveformShader.shader().set("waveformTexZoom", 1f + 0.5f * P.sin(p.frameCount * 0.006f));
		waveformShader.shader().set("offset", FrameLoop.count(0.002f), FrameLoop.count(0.0f));
		waveformShader.shader().set("rotate", Mouse.xEasedNorm * P.TWO_PI);
		waveformShader.shader().set("fade", 0.5f);
		waveformShader.shader().set("amp", 0.6f + 0.3f * P.sin(P.PI + p.frameCount * 0.004f));
		
		// draw shader!
		waveformShaderTexture.filter(waveformShader.shader());
		
		// post-process
		FakeLightingFilter.instance(p).setAmbient(10f);
		FakeLightingFilter.instance(p).setGradAmp(0.4f);
		FakeLightingFilter.instance(p).setGradBlur(0.1f);
		FakeLightingFilter.instance(p).setSpecAmp(0.2f);
		FakeLightingFilter.instance(p).setDiffDark(0.8f);
		FakeLightingFilter.instance(p).setMap(pg);
		FakeLightingFilter.instance(p).applyTo(waveformShaderTexture);
		FXAAFilter.instance(p).applyTo(waveformShaderTexture);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') droneSamplers[0].update();
		if(p.key == '1') droneSamplers[0].startNextSound();
		if(p.key == '2') droneSamplers[1].startNextSound();
		if(p.key == '3') droneSamplers[2].startNextSound();
	}
	
}
