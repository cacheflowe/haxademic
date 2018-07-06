package com.haxademic.demo.draw.textures.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.image.PerlinTexture;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_PerlinShaderIQ 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics perlinBuffer;
	protected PShader perlinShader;

	protected void setupFirstFrame() {
		// create noise buffer
		perlinBuffer = p.createGraphics(p.width, p.height, PRenderers.P3D);
		perlinShader = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/noise-simplex-2d-iq.glsl"));
	}

	public void drawApp() {
		background(0);
		
		// update perlin texture
		perlinShader.set("offset", 0f, p.frameCount * 0.07f);
		perlinShader.set("zoom", 3f + 2f * P.sin(p.frameCount * 0.01f));
		perlinShader.set("rotation", P.sin(p.frameCount * 0.01f) * 0.4f);
		perlinBuffer.filter(perlinShader);
		p.debugView.setTexture(perlinBuffer);
		
		// draw to screen
		p.image(perlinBuffer, 0, 0);  
	}
		
}