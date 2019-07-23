package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.image.ImageSequenceRecorder;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class Demo_ImageSequenceRecorder_Slitscan 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ImageSequenceRecorder recorder;
	protected PGraphics camBuffer;
	protected PGraphics noiseBuffer;
	protected PGraphics slitscanOutputBuffer;
	protected PGraphics slitscanLerpedBuffer;
	protected PShader slitscanShader;
	protected PShader lerpToTexture;
	protected TextureShader noiseTexture;
	protected int numFrames = 15;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, false );
		p.appConfig.setProperty(AppSettings.FILLS_SCREEN, false );
	}
		
	public void setupFirstFrame () {
		camBuffer = p.createGraphics(640, 480, PRenderers.P3D);
		noiseBuffer = p.createGraphics(640, 480, PRenderers.P3D);
		slitscanOutputBuffer = p.createGraphics(640, 480, PRenderers.P3D);
		slitscanLerpedBuffer = p.createGraphics(640, 480, PRenderers.P3D);
		lerpToTexture = P.p.loadShader(FileUtil.getFile("haxademic/shaders/filters/texture-blend-towards-texture.glsl"));
		recorder = new ImageSequenceRecorder(camBuffer.width, camBuffer.height, numFrames);
		WebCam.instance().setDelegate(this);
		slitscanShader = p.loadShader(FileUtil.getFile("haxademic/shaders/filters/slitscan-texture-map.glsl"));
		noiseTexture = new TextureShader(TextureShader.noise_simplex_2d_iq);
	}

	public void drawApp() {
		p.background( 0 );
		debugView.setTexture(camBuffer);

		// update noise
		noiseTexture.updateTime();
		/*
			uniform float zoom = 1.;
			uniform float rotation = 0.;
		 */
		noiseTexture.shader().set("offset", 0f, p.frameCount * 0.01f);
		noiseTexture.shader().set("rotation", 0f, p.frameCount * 0.01f);
		noiseTexture.shader().set("zoom", 2f);
		noiseBuffer.filter(noiseTexture.shader());
		ContrastFilter.instance(p).setContrast(2f);
		ContrastFilter.instance(p).applyTo(noiseBuffer);
		debugView.setTexture(noiseBuffer);
		
		// debug draw recorder object frames
		PG.setDrawCorner(p);
		// recorder.drawDebug(p.g);	// kills the rest of the drawing
		
		// slitscanShader
		slitscanShader.set("lerpAmp", 0.3f); // p.mousePercentX()
		slitscanShader.set("map", noiseBuffer);
		for (int i = 0; i < numFrames; i++) {
//			slitscanShader.set("frame_"+((i + recorder.frameIndex()) % numFrames), recorder.images()[i]);		// scary mode
			int shaderFrame = i - 1 - recorder.frameIndex() % numFrames;
			while(shaderFrame < 0) shaderFrame += numFrames;
			slitscanShader.set("frame_"+shaderFrame, recorder.images()[i]);
		}
		slitscanOutputBuffer.filter(slitscanShader);
		debugView.setTexture(slitscanOutputBuffer);
		
		// lerp the slitscan to next buffer
		lerpToTexture.set("blendLerp", 0.3f);
		lerpToTexture.set("targetTexture", slitscanOutputBuffer);
		slitscanLerpedBuffer.filter(lerpToTexture);

		
		// draw live webcam
		p.pushMatrix();
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
//		p.image(camBuffer, 0, 0);
//		p.image(slitscanOutputBuffer, 0, 0);
		p.image(slitscanLerpedBuffer, 0, 0);
		p.popMatrix();
		
	}

	@Override
	public void newFrame(PImage frame) {
		// set recorder frame - use buffer as intermediary to fix aspect ratio
		ImageUtil.copyImageFlipH(frame, camBuffer);
		recorder.addFrame(camBuffer);
		// do some post-processing
		SaturationFilter.instance(p).setSaturation(0);
		SaturationFilter.instance(p).applyTo(recorder.getCurFrame());
		// set debug staus
		p.debugView.setValue("Last WebCam frame", p.frameCount);
	}

}
