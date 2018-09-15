package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.filters.pshader.ErosionFilter;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class BufferColorObjectDetection {

	protected PGraphics source;
	protected PGraphics bufferOutput;
	protected int colorCompare;
	protected float scale = 1;
	protected float colorClosenessThreshold = 0.95f;
	protected int bufferW;
	protected int bufferH;
	protected PShader colorDistanceFilter;
	protected EasingFloat x = new EasingFloat(0.5f, 0.5f);
	protected EasingFloat y = new EasingFloat(0.5f, 0.5f);

	public BufferColorObjectDetection(PImage sourceImg, float scale) {
		this.scale = scale;
		bufferW = P.round(scale * sourceImg.width);
		bufferH = P.round(scale * sourceImg.height);
		source = P.p.createGraphics(bufferW, bufferH, PRenderers.P2D);
		bufferOutput = P.p.createGraphics(bufferW, bufferH, PRenderers.P2D);
		colorDistanceFilter = P.p.loadShader(FileUtil.getFile("haxademic/shaders/filters/color-distance.glsl"));
		setColorCompare(1f, 1f, 1f);
	}
	
	public PGraphics outputBuffer() {
		return bufferOutput;
	}
	
	public PGraphics sourceBuffer() {
		return source;
	}
	
	public float x() {
		return x.value();
	}
	
	public float y() {
		return y.value();
	}
	
	public int colorCompare() {
		return colorCompare;
	}
	
	public void loadPixels() {
		source.loadPixels();
	}
	
	public void setColorFromSource(int x, int y) {
		loadPixels();
		int pixelColor = ImageUtil.getPixelColor(source, x, y);
		float r = ColorUtil.redFromColorInt(pixelColor) / 255f;
		float g = ColorUtil.greenFromColorInt(pixelColor) / 255f;
		float b = ColorUtil.blueFromColorInt(pixelColor) / 255f;
		setColorCompare(r, g, b);
	}
	
	public void setColorCompare(float r, float g, float b) {
		colorDistanceFilter.set("colorCompare", r, g, b);
		colorCompare = P.p.color(r * 255f, g * 255f, b * 255f);
	}
	
	public void update(PImage newFrame) {
		// copy webcam to current buffer
		ImageUtil.copyImage(newFrame, source);
		SaturationFilter.instance(P.p).setSaturation(2f);
		SaturationFilter.instance(P.p).applyTo(source);

		// run color distance shader and post-process to map color closeness to white
		// should this use a shader posterize effect?
		ImageUtil.copyImage(source, bufferOutput);
		bufferOutput.filter(colorDistanceFilter);
		InvertFilter.instance(P.p).applyTo(bufferOutput);
		ThresholdFilter.instance(P.p).setCutoff(colorClosenessThreshold);
		ThresholdFilter.instance(P.p).applyTo(bufferOutput);
		ErosionFilter.instance(P.p).applyTo(bufferOutput);
		
		// loop through pixels
		float totalChecked = 0;
		float totalCounted = 0;
		float totalX = 0;
		float totalY = 0;
		bufferOutput.loadPixels();
		for (int x = 0; x < bufferOutput.width; x++) {
			for (int y = 0; y < bufferOutput.height; y++) {
				int pixelColor = ImageUtil.getPixelColor(bufferOutput, x, y);
				float r = ColorUtil.redFromColorInt(pixelColor);
				if(r > 127) {
					totalCounted++;
					totalX += x;
					totalY += y;
				}
				totalChecked++;
			}
		}
		P.p.debugView.setValue("totalChecked", totalChecked);
		
		// calc normalized center of mass / position
		if(totalCounted > 10) {
			float avgX = totalX / totalCounted;
			float avgY = totalY / totalCounted;
			x.setTarget(avgX / bufferOutput.width);
			y.setTarget(avgY / bufferOutput.height);
		}
		
		// lerp normalized output
		x.update();
		y.update();
		
		// draw debug output
		bufferOutput.beginDraw();
		bufferOutput.stroke(255, 0, 0);
		bufferOutput.noFill();
		bufferOutput.ellipse(x.value() * bufferOutput.width - 3, y.value() * bufferOutput.height - 3, 6, 6);
		bufferOutput.endDraw();
	}
}

