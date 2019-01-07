package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.math.easing.FloatBuffer;

import processing.core.PGraphics;
import processing.core.PImage;

public class BufferThresholdMonitor {
	
	protected PGraphics thresholdBuffer;
	protected FloatBuffer thresholdCalc;
	protected float cutoff = 0.5f;

	public BufferThresholdMonitor(int w, int h, int bufferAvgSize) {
		// smoothed activity average
		thresholdCalc = new FloatBuffer(bufferAvgSize);
		
		// frame buffers
		thresholdBuffer = P.p.createGraphics(w, h, PRenderers.P2D);
		OpenGLUtil.setTextureQualityLow(thresholdBuffer);
	}
	
	public BufferThresholdMonitor() {
		this(16, 16, 60);	// defaults
	}
	
	public float thresholdCalc() {
		return thresholdCalc.average();
	}
	
	public PGraphics thresholdBuffer() {
		return thresholdBuffer;
	}
	
	public void setCutoff(float cutoff) {
		this.cutoff = cutoff;
	}
	
	public float update(PImage newFrame) {			
		// copy previous frame, and current frame to buffer
		thresholdBuffer.copy(newFrame, 0, 0, newFrame.width, newFrame.height, 0, 0, thresholdBuffer.width, thresholdBuffer.height);

		// run threshold filter
		ThresholdFilter.instance(P.p).setCutoff(cutoff);
		ThresholdFilter.instance(P.p).applyTo(thresholdBuffer);
		
		// analyze diff pixels
		float numPixels = thresholdBuffer.width * thresholdBuffer.height;
		float whitePixels = 0;
		thresholdBuffer.loadPixels();
		for (int x = 0; x < thresholdBuffer.width; x++) {
			for (int y = 0; y < thresholdBuffer.height; y++) {
				int pixelColor = ImageUtil.getPixelColor(thresholdBuffer, x, y);
				float r = ColorUtil.redFromColorInt(pixelColor) / 255f;
				if(r > 0.5f) whitePixels += r;
			}			
		}
		
		// update float buffer
		thresholdCalc.update(whitePixels / numPixels);
		return thresholdCalc.average();
	}
}
