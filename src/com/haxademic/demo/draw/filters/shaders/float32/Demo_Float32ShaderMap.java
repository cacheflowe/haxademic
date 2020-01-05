package com.haxademic.demo.draw.filters.shaders.float32;

import com.haxademic.core.app.PAppletHax;

public class Demo_Float32ShaderMap 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

//	protected PGraphics2D buffer8;
//	protected DwGLSLProgram shader32;
//	protected DwGLTexture texture32 = new DwGLTexture();
//	
//	protected TextureShader noiseTexture;
//	protected PGraphics2D noiseBuffer;
//	protected DwGLTexture textureNoise = new DwGLTexture();
//
//
//	protected void firstFrame() {
//		// create buffers
//	    buffer8 = OpenGL32Util.newPGraphics2D(p.width, p.height);
//	    texture32 = OpenGL32Util.newTexture32(buffer8.width, buffer8.height);
//	    shader32 = OpenGL32Util.newShader(FileUtil.getFile("haxademic/shaders/float32/pixelflow-test-map.glsl"));
//	    
//	    // create buffers for simplex noise
//	    textureNoise = OpenGL32Util.newTexture8(p.width, p.height);
//		noiseTexture = new TextureShader(TextureShader.noise_simplex_2d_iq);
//	    noiseBuffer = (PGraphics2D) createGraphics(p.width, p.height, P2D);
//
//		// draw something to the pg
//	    ImageUtil.drawImageCropFill(DemoAssets.textureJupiter(), buffer8, true, true);
//
//		// send PGraphics image into float32 texture
//	    OpenGL32Util.pGraphics2dToTexture32(buffer8, texture32);
//	}
//
//	protected void drawApp() {
//		// update noise
//		noiseTexture.updateTime();
////		noiseTexture.shader().set("offset", 0f, p.frameCount * 0.004f);
////		noiseTexture.shader().set("rotation", 0f, p.frameCount * 0.004f);
//		noiseTexture.shader().set("zoom", 2.5f);
//		noiseBuffer.filter(noiseTexture.shader());
//	    OpenGL32Util.pGraphics2dToTexture32(noiseBuffer, textureNoise);
//	    DebugView.setTexture("noiseBuffer", noiseBuffer);
//		
//		// Draw shader output to DwGLTexture
//	    OpenGL32Util.context().begin();
//	    OpenGL32Util.context().beginDraw(texture32);
//	    shader32.begin();
//	    shader32.uniformTexture("texture", texture32);
//	    shader32.uniformTexture("map", textureNoise);
//	    shader32.uniform1f("time", p.frameCount * 0.05f);
//	    shader32.uniform2f("resolution", buffer8.width, buffer8.height);
//	    shader32.drawFullScreenQuad();
//	    shader32.end();	  
//	    OpenGL32Util.context().endDraw();
//	    OpenGL32Util.context().end();
//	    
//	    // copy DWTexture back to PGraphics
//	    OpenGL32Util.texture32ToPGraphics2d(texture32, buffer8);
//
//	    // Render PGraphics object to screen
//	    p.image(buffer8, 0, 0);
//	}
}