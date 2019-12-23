package com.haxademic.demo.draw.filters.shaders.float32;

import com.haxademic.core.app.PAppletHax;

public class Demo_NoiseLineFeedback 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

//	protected PGraphics2D buffer8;
//	protected DwGLTexture bufferCopy32;
//	protected PGraphics2D outputBuffer;
//	protected DwGLSLProgram shader32;
//	protected DwGLTexture feedbackTexture32 = new DwGLTexture();
//	
//	protected TextureShader noiseTexture;
//	protected PGraphics2D noiseBuffer;
//	protected DwGLTexture textureNoise = new DwGLTexture();
//
//
//	protected void config() {
//		Config.setProperty(AppSettings.LOOP_FRAMES, 160);
//		Config.setProperty(AppSettings.INIT_ESS_AUDIO, true);
//	}
//	
//	protected void firstFrame() {
//		// create buffers
//	    buffer8 = OpenGL32Util.newPGraphics2D(p.width, p.height);
//	    outputBuffer = OpenGL32Util.newPGraphics2D(p.width, p.height);
//	    bufferCopy32 = OpenGL32Util.newTexture32(buffer8.width, buffer8.height);
//	    feedbackTexture32 = OpenGL32Util.newTexture32(buffer8.width, buffer8.height);
//	    shader32 = OpenGL32Util.newShader(FileUtil.getFile("haxademic/shaders/float32/pixelflow-feedback-map.glsl"));
//	    
//	    // create buffers for simplex noise
//	    textureNoise = OpenGL32Util.newTexture32(p.width, p.height);
//		noiseTexture = new TextureShader(TextureShader.noise_simplex_2d_iq);
////		noiseTexture = new TextureShader(TextureShader.BWNoiseInfiniteZoom);
//	    noiseBuffer = (PGraphics2D) createGraphics(p.width, p.height, P2D);
//	}
//
//	public void drawApp() {
//		background(0);
//		
//		// update noise
//		noiseTexture.updateTime();
//		noiseTexture.shader().set("offset", 0f, p.frameCount * 0.004f);
//		noiseTexture.shader().set("zoom", 1.25f);
//		noiseBuffer.filter(noiseTexture.shader());
//	    OpenGL32Util.pGraphics2dToTexture32(noiseBuffer, textureNoise);
//	    DebugView.setTexture("noiseBuffer", noiseBuffer);
//		
//		// draw line 
//		buffer8.beginDraw();
//		buffer8.clear();
//		buffer8.noFill();
//		buffer8.strokeWeight(2.5f);
//		float noiseSpeed = 0.01f;// + 0.001f * P.sin(AnimationLoop.progressRads());
//		float noiseStart = p.frameCount * 0.01f;
//		for (int x = 0; x < p.width; x++) {
//			float audioAmp = 0.3f + 7f * AudioIn.audioFreq(x);
//			audioAmp = 1f;
//			buffer8.stroke(
//					audioAmp * (127 + 127 * P.sin((float)x * 0.002f + AnimationLoop.progressRads())),
//					audioAmp * (127 + 127 * P.sin((float)x * 0.005f + AnimationLoop.progressRads())),
//					audioAmp * (127 + 127 * P.sin((float)x * 0.01f + AnimationLoop.progressRads()))
//			);
//			float noiseY = p.noise(noiseStart + x * noiseSpeed) * 100f;
//			float noiseYNext = p.noise(noiseStart + (x+1) * noiseSpeed) * 100f;
//			buffer8.line(x, p.height * 0.4f + noiseY, x+1, p.height * 0.4f + noiseYNext);
//		}
//		buffer8.endDraw();
//		
//		// blur line
//		BlurHFilter.instance(p).setBlurByPercent(0.3f, buffer8.width);
//		BlurHFilter.instance(p).applyTo(buffer8);
//		BlurVFilter.instance(p).setBlurByPercent(0.3f, buffer8.width);
//		BlurVFilter.instance(p).applyTo(buffer8);
//		// and send to float32 copy
//		OpenGL32Util.pGraphics2dToTexture32(buffer8, bufferCopy32);
//
//				
//		// do feedback and copy back to pgraphics
//		// copy new line drawing in shader
//		int ITER = 10;
//		for (int i = 0; i < ITER; i++) {			
//		    OpenGL32Util.context().begin();
//		    OpenGL32Util.context().beginDraw(feedbackTexture32);
//		    shader32.begin();
//		    shader32.uniformTexture("texture", feedbackTexture32);
//		    shader32.uniformTexture("overlay", bufferCopy32);
//		    shader32.uniformTexture("map", textureNoise);
//		    shader32.uniform1f("amp", 0.0001f + 0.01f * Mouse.xNorm);
//		    shader32.uniform2f("resolution", buffer8.width, buffer8.height);
//		    shader32.drawFullScreenQuad();
//		    shader32.end();	  
//		    OpenGL32Util.context().endDraw();
//		    OpenGL32Util.context().end();
//		}
//	    
//	    // copy DWTexture back to output PGraphics after feedback shader
//	    OpenGL32Util.texture32ToPGraphics2d(feedbackTexture32, outputBuffer);
//		// draw feedback to screen
//		p.image(outputBuffer, 0, 0);
//	}
		
}