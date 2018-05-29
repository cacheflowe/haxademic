package com.haxademic.demo.draw.shaders.textures;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shaders.textures.TextureShader;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;
import com.jogamp.opengl.GL;
import com.thomasdiewald.pixelflow.java.DwPixelFlow;
import com.thomasdiewald.pixelflow.java.dwgl.DwGLSLProgram;
import com.thomasdiewald.pixelflow.java.dwgl.DwGLTexture;
import com.thomasdiewald.pixelflow.java.imageprocessing.filter.DwFilter;

import processing.opengl.PGraphics2D;

public class Demo_Float32ShaderMap 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected DwPixelFlow pixelflow;
	protected PGraphics2D buffer;
	protected DwGLSLProgram shader32;
	protected DwGLTexture texture32 = new DwGLTexture();
	
	protected TextureShader noiseTexture;
	protected PGraphics2D noiseBuffer;
	protected DwGLTexture textureNoise = new DwGLTexture();


	protected void setupFirstFrame() {
	    pixelflow = new DwPixelFlow(this);
	    shader32 = pixelflow.createShader(FileUtil.getFile("haxademic/shaders/vertex/pixelflow-test-map.glsl"));
	    texture32.resize(pixelflow, GL.GL_RGBA32F, p.width, p.height, GL.GL_RGBA, GL.GL_FLOAT, GL.GL_NEAREST, GL.GL_REPEAT, 4, 4);	// GL_LINEAR -> GL_NEAREST?
	    textureNoise.resize(pixelflow, GL.GL_RGBA8, p.width, p.height, GL.GL_RGBA, GL.GL_FLOAT, GL.GL_NEAREST, GL.GL_REPEAT, 4, 4);	// GL_LINEAR -> GL_NEAREST?
	    buffer = (PGraphics2D) createGraphics(p.width, p.height, P2D);
	    
		noiseTexture = new TextureShader(TextureShader.noise_simplex_2d_iq);
	    noiseBuffer = (PGraphics2D) createGraphics(p.width, p.height, P2D);

		// draw something to the pg
	    buffer.beginDraw();
	    ImageUtil.drawImageCropFill(DemoAssets.smallTexture(), buffer, true);
	    buffer.endDraw();

		// send PGraphics image into float32 texture
	    DwFilter.get(pixelflow).copy.apply(buffer, texture32);
	}

	public void drawApp() {
		// update noise
		noiseTexture.updateTime();
		noiseTexture.shader().set("offset", 0f, p.frameCount * 0.01f);
		noiseTexture.shader().set("rotation", 0f, p.frameCount * 0.01f);
		noiseTexture.shader().set("zoom", 2f);
		noiseBuffer.filter(noiseTexture.shader());
	    DwFilter.get(pixelflow).copy.apply(noiseBuffer, textureNoise);
	    p.debugView.setTexture(noiseBuffer);
		
		// Draw shader output to DwGLTexture
	    pixelflow.begin();
	    pixelflow.beginDraw(texture32);
	    shader32.begin();
	    shader32.uniformTexture("tex", texture32);
	    shader32.uniformTexture("map", textureNoise);
	    shader32.uniform2f("wh", buffer.width, buffer.height);
	    shader32.drawFullScreenQuad();
	    shader32.end();	  
	    pixelflow.endDraw();
	    pixelflow.end();
	    
	    // copy DWTexture back to PGraphics
	    DwFilter.get(pixelflow).copy.apply(texture32, buffer);

	    // Render PGraphics object to screen
	    p.image(buffer, 0, 0);
	}
}