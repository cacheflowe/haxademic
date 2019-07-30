package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.OpenGL32Util;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.file.FileUtil;
import com.thomasdiewald.pixelflow.java.dwgl.DwGLSLProgram;
import com.thomasdiewald.pixelflow.java.dwgl.DwGLTexture;

import processing.opengl.PGraphics2D;

public class NoiseLineFeedback32 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics2D buffer8;
	protected DwGLTexture bufferCopy32;
	protected PGraphics2D outputBuffer;
	protected DwGLSLProgram shader32;
	protected DwGLTexture feedbackTexture32 = new DwGLTexture();
	
	protected TextureShader noiseTexture;
	protected PGraphics2D noiseBuffer;
	protected DwGLTexture textureNoise = new DwGLTexture();


	protected void overridePropsFile() {
		int FRAMES = 180;
		p.appConfig.setProperty(AppSettings.WIDTH, 1024);
		p.appConfig.setProperty(AppSettings.HEIGHT, 768);
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2 );
	}
	
	protected void setupFirstFrame() {
		// create buffers
	    buffer8 = OpenGL32Util.newPGraphics2D(p.width, p.height);
	    outputBuffer = OpenGL32Util.newPGraphics2D(p.width, p.height);
	    bufferCopy32 = OpenGL32Util.newTexture32(buffer8.width, buffer8.height);
	    feedbackTexture32 = OpenGL32Util.newTexture32(buffer8.width, buffer8.height);
	    shader32 = OpenGL32Util.newShader(FileUtil.getFile("haxademic/shaders/float32/pixelflow-feedback-map.glsl"));
	    
	    // create buffers for simplex noise
	    textureNoise = OpenGL32Util.newTexture32(p.width, p.height);
		noiseTexture = new TextureShader(TextureShader.noise_simplex_2d_iq);
//		noiseTexture = new TextureShader(TextureShader.BWNoiseInfiniteZoom);
	    noiseBuffer = (PGraphics2D) createGraphics(p.width, p.height, P2D);
	}
	
	protected float noiseAtX(float x) {
		float noiseSpeed = 0.01f;// + 0.001f * P.sin(p.loop.progressRads());
		float amp = p.height * 0.4f;
		float noiseCur = -0.5f + p.noise(x * noiseSpeed);
		float osc = 0.5f + 0.5f * P.sin(p.loop.progressRads());
		return osc * noiseCur * amp;
	}

	public void drawApp() {
		background(0);
		
		// update noise
		noiseTexture.updateTime();
//		noiseTexture.shader().set("offset", 0f, p.frameCount * 0.004f);
//		noiseTexture.shader().set("offset", 0f, 10f * p.mousePercentY());
		noiseTexture.shader().set("offset", 0f, 1f + 0.2f * P.sin(p.loop.progressRads()));
		noiseTexture.shader().set("rotation", 0.2f * P.sin(p.loop.progressRads()));
		noiseTexture.shader().set("zoom", 1.2f + 0.6f * P.sin(p.loop.progressRads()));
		noiseBuffer.filter(noiseTexture.shader());
	    OpenGL32Util.pGraphics2dToTexture32(noiseBuffer, textureNoise);
	    p.debugView.setTexture("noiseBuffer", noiseBuffer);
		
		// draw line 
		buffer8.beginDraw();
		buffer8.clear();
		buffer8.noFill();
		buffer8.strokeWeight(2.5f);
		for (int x = 0; x < p.width; x++) {
			buffer8.stroke(
					(180 + 40 * P.sin(1f + (float)x/300f * p.loop.progressRads() * 2f)),
					(180 + 55 * P.sin(2f + (float)x/300f * p.loop.progressRads())),
					(150 + 85 * P.sin(3f + (float)x/300f * p.loop.progressRads() * 3f))
			);
			float noiseY = noiseAtX(x);
			float noiseYNext = noiseAtX(x+1);
			buffer8.line(x, p.height * 0.5f + noiseY, x+1, p.height * 0.5f + noiseYNext);
		}
		buffer8.endDraw();
		
		// blur line
//		BlurHFilter.instance(p).setBlurByPercent(0.3f, buffer8.width);
//		BlurHFilter.instance(p).applyTo(buffer8);
		BlurVFilter.instance(p).setBlurByPercent(0.3f, buffer8.height);
		BlurVFilter.instance(p).applyTo(buffer8);
		// and send to float32 copy
		OpenGL32Util.pGraphics2dToTexture32(buffer8, bufferCopy32);

				
		// do feedback and copy back to pgraphics
		// copy new line drawing in shader
		int ITER = 50;
		for (int i = 0; i < ITER; i++) {			
		    OpenGL32Util.context().begin();
		    OpenGL32Util.context().beginDraw(feedbackTexture32);
		    shader32.begin();
		    shader32.uniformTexture("texture", feedbackTexture32);
		    shader32.uniformTexture("overlay", bufferCopy32);
		    shader32.uniformTexture("map", textureNoise);
//		    shader32.uniform1f("amp", 0.0001f + 0.01f * p.mousePercentX());
		    shader32.uniform1f("amp", 0.0004f);
		    shader32.uniform2f("resolution", buffer8.width, buffer8.height);
		    shader32.drawFullScreenQuad();
		    shader32.end();	  
		    OpenGL32Util.context().endDraw();
		    OpenGL32Util.context().end();
		}
	    
	    // copy DWTexture back to output PGraphics after feedback shader
	    OpenGL32Util.texture32ToPGraphics2d(feedbackTexture32, outputBuffer);
		// draw feedback to screen
		p.image(outputBuffer, 0, 0);
		
		// postprocessing
		BrightnessFilter.instance(p).setBrightness(2.7f);
		BrightnessFilter.instance(p).applyTo(p);
		ContrastFilter.instance(p).setContrast(1.35f);
		ContrastFilter.instance(p).applyTo(p);
		VignetteFilter.instance(p).setDarkness(0.6f);
		VignetteFilter.instance(p).applyTo(p);
		
//		BlurHFilter.instance(p).setBlurByPercent(0.1f, p.width);
//		BlurHFilter.instance(p).applyTo(p);
//		BlurVFilter.instance(p).setBlurByPercent(0.1f, p.height);
//		BlurVFilter.instance(p).applyTo(p);

	}
		
}