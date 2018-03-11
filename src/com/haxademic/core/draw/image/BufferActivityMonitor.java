package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.FloatBuffer;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class BufferActivityMonitor {
	
	protected PGraphics prevFrame;
	protected PGraphics curFrame;
	protected PGraphics differenceBuffer;
	protected PShader differenceShader;
	protected FloatBuffer activityAmp;

	public BufferActivityMonitor(int w, int h, int bufferAvgSize) {
		// smoothed activity average
		activityAmp = new FloatBuffer(60);
		
		// frame buffers
		prevFrame = P.p.createGraphics(w, h, PRenderers.P2D);
		curFrame = P.p.createGraphics(w, h, PRenderers.P2D);
		differenceBuffer = P.p.createGraphics(w, h, PRenderers.P2D);
		
		// frame diff buffer/shader
		differenceShader = P.p.loadShader(FileUtil.getFile("shaders/filters/texture-difference-threshold.glsl"));
	}
	
	public BufferActivityMonitor() {
		this(32, 32, 60);	// defaults
	}
	
	public float activityAmp() {
		return activityAmp.average();
	}
	
	public PGraphics differenceBuffer() {
		return differenceBuffer;
	}
	
	public float update(PImage newFrame) {			
		// copy previous frame, and current frame to buffer
		prevFrame.copy(curFrame, 0, 0, curFrame.width, curFrame.height, 0, 0, curFrame.width, curFrame.height);
		curFrame.copy(newFrame, 0, 0, newFrame.width, newFrame.height, 0, 0, curFrame.width, curFrame.height);

		// set difference shader textures
		differenceShader.set("tex1", curFrame);
		differenceShader.set("tex2", prevFrame);
		differenceShader.set("falloffBW", 0.1f);
		differenceShader.set("diffThresh", 0.025f);
		differenceBuffer.filter(differenceShader);
		
		// analyze diff pixels
		float maxActivity = differenceBuffer.width * differenceBuffer.height;
		float curActivity = 0;
		differenceBuffer.loadPixels();
		for (int x = 0; x < differenceBuffer.width; x++) {
			for (int y = 0; y < differenceBuffer.height; y++) {
				int pixelColor = ImageUtil.getPixelColor(differenceBuffer, x, y);
				float r = ColorUtil.redFromColorInt(pixelColor) / 255f;
				curActivity += r;
			}			
		}
		
		// update float buffer
		activityAmp.update(curActivity / maxActivity);
		return activityAmp.average();
	}
}
