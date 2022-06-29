package com.haxademic.core.draw.cv;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class BufferFrameDifference {
	
	protected PGraphics frame1;
	protected PGraphics frame2;
	protected PGraphics differenceBuffer;
	protected PShader differenceShader;
	protected boolean pingPong = false;
	protected float falloffBW = 0.1f;
	protected float diffThresh = 0.025f;

	public BufferFrameDifference(int w, int h) {
		// frame buffers
		frame1 = PG.newPG2DFast(w, h);
		frame2 = PG.newPG2DFast(w, h);
		differenceBuffer = PG.newPG2DFast(w, h);
		differenceShader = P.p.loadShader(FileUtil.getPath("haxademic/shaders/filters/texture-difference-threshold.glsl"));
	}
	
	public PGraphics differenceBuffer() {
		return differenceBuffer;
	}
	
	public void falloffBW(float newFalloff) { falloffBW = newFalloff; }
	public void diffThresh(float newThresh) { diffThresh = newThresh; }
	
	// ping pong - always store the newset and previous frames
	public void update(PImage newFrame) {	
		pingPong = !pingPong;
		
		// copy previous frame, and current frame to buffer
		if(pingPong) ImageUtil.copyImage(newFrame, frame1);
		else         ImageUtil.copyImage(newFrame, frame2);

		// set difference shader textures
		differenceShader.set("tex1", frame2);
		differenceShader.set("tex2", frame1);
		differenceShader.set("falloffBW", falloffBW);
		differenceShader.set("diffThresh", diffThresh);
		differenceBuffer.filter(differenceShader);
	}

	// set both frames manually
	public void update(PImage frame1, PImage frame2) {	
		differenceShader.set("tex1", frame1);
		differenceShader.set("tex2", frame2);
		differenceShader.set("falloffBW", falloffBW);
		differenceShader.set("diffThresh", diffThresh);
		differenceBuffer.filter(differenceShader);
	}
}
