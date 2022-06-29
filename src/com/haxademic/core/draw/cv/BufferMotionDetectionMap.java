package com.haxademic.core.draw.cv;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class BufferMotionDetectionMap {

	protected PImage source;
	protected float scale = 1;
	protected float blur = 0.5f;
	protected float thresholdCutoff = 0.2f;
	protected float blendLerp = 0.1f;
	protected float diffThresh = 0.035f;
	protected float falloffBW = 0.05f;
	protected int bufferW;
	protected int bufferH;
	protected PGraphics backplate;
	protected PGraphics newFrameBuffer;
	protected PGraphics differenceBuffer;
	protected PGraphics bwBuffer;
	protected PShader blendTowardsShader;
	protected PShader differenceShader;
	
	public BufferMotionDetectionMap(PImage source, float scale) {
		this.source = source;
		this.scale = scale;
		bufferW = P.round(scale * source.width);
		bufferH = P.round(scale * source.height);
		// P.println("Motion detection buffer size: ", bufferW, ", ", bufferH);
		buildBuffers();
	}
	
	protected void buildBuffers() {
		backplate = P.p.createGraphics(bufferW, bufferH, PRenderers.P3D);
		newFrameBuffer = P.p.createGraphics(bufferW, bufferH, PRenderers.P3D);
		differenceBuffer = P.p.createGraphics(bufferW, bufferH, PRenderers.P3D);
		bwBuffer = P.p.createGraphics(bufferW, bufferH, PRenderers.P3D);
		
		backplate.noSmooth();
		OpenGLUtil.setTextureQualityLow(backplate);
		newFrameBuffer.noSmooth();
		OpenGLUtil.setTextureQualityLow(newFrameBuffer);
		differenceBuffer.noSmooth();
		OpenGLUtil.setTextureQualityLow(differenceBuffer);
		OpenGLUtil.setTextureQualityLow(bwBuffer);
		
		blendTowardsShader = P.p.loadShader(FileUtil.getPath("haxademic/shaders/filters/texture-blend-towards-texture.glsl"));
		differenceShader = P.p.loadShader(FileUtil.getPath("haxademic/shaders/filters/texture-difference-threshold.glsl"));
	}
	
	public PGraphics newFrameBuffer() { return newFrameBuffer; }
	public PGraphics backplate() { return backplate; }
	public PGraphics differenceBuffer() { return differenceBuffer; }
	public PGraphics bwBuffer() { return bwBuffer; }
	
	public void setThresholdCutoff(float thresholdCutoff) { this.thresholdCutoff = thresholdCutoff; }
	public void setBlur(float blur) { this.blur = blur; }
	public void setBlendLerp(float blendLerp) { this.blendLerp = blendLerp; }
	public void setDiffThresh(float diffThresh) { this.diffThresh = diffThresh; }
	public void setFalloffBW(float falloffBW) { this.falloffBW = falloffBW; }
	
	public void loadPixels() {
		bwBuffer.loadPixels();
	}
	
	public boolean pixelActive(int x, int y) {
		int pixelColor = ImageUtil.getPixelColor(bwBuffer, P.floor(x * scale), P.floor(y * scale));
		float redColor = (float) ColorUtil.redFromColorInt(pixelColor) / 255f;
		return redColor > 0.5f;
	}
	
	public void updateSource(PImage newFrame) {
		// copy webcam to current buffer
		newFrameBuffer.copy(newFrame, 0, 0, newFrame.width, newFrame.height, 0, 0, newFrameBuffer.width, newFrameBuffer.height);

		// run target blend shader on backplate
		blendTowardsShader.set("targetTexture", newFrameBuffer);
		blendTowardsShader.set("blendLerp", blendLerp);
		backplate.filter(blendTowardsShader);

		// set difference shader textures
		differenceShader.set("tex1", backplate);
		differenceShader.set("tex2", newFrameBuffer);
		differenceShader.set("diffThresh", diffThresh);
		differenceShader.set("falloffBW", falloffBW);
		
		// update difference calculation
		differenceBuffer.filter(differenceShader);

		// post-process difference buffer w/ threshold of black & white falloff, w/ blur to help smooth
		ImageUtil.cropFillCopyImage(differenceBuffer, bwBuffer, true);
		BlurHFilter.instance(P.p).setBlurByPercent(blur, (float) bwBuffer.width);
		BlurHFilter.instance(P.p).applyTo(bwBuffer);
		BlurVFilter.instance(P.p).setBlurByPercent(blur, (float) bwBuffer.height);
		BlurVFilter.instance(P.p).applyTo(bwBuffer);
		ThresholdFilter.instance(P.p).setCutoff(thresholdCutoff);
		ThresholdFilter.instance(P.p).applyTo(bwBuffer);
	}
}

