package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
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
		activityAmp = new FloatBuffer(bufferAvgSize);
		
		// frame buffers
		prevFrame = PG.newPG2DFast(w, h);
		curFrame = PG.newPG2DFast(w, h);
		differenceBuffer = PG.newPG2DFast(w, h);
		
		OpenGLUtil.setTextureQualityLow(prevFrame);
		OpenGLUtil.setTextureQualityLow(curFrame);
		OpenGLUtil.setTextureQualityLow(differenceBuffer);
		
		// frame diff buffer/shader
		differenceShader = P.p.loadShader(FileUtil.getPath("haxademic/shaders/filters/texture-difference-threshold.glsl"));
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
		ImageUtil.copyImage(curFrame, prevFrame);
		ImageUtil.copyImage(newFrame, curFrame);

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
				if(r > 0.5f) curActivity += r;
			}			
		}
		
		// update float buffer
		activityAmp.update(curActivity / maxActivity);
		return activityAmp.average();
	}
}
