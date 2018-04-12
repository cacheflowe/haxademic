package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.filters.shaders.BlurHFilter;
import com.haxademic.core.draw.filters.shaders.BlurVFilter;
import com.haxademic.core.draw.image.BufferFrameDifference;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.webcam.IWebCamCallback;

import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_BufferFrameDifference_sheetDeform 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PImage texture;
	protected PImage textureFlipped;
	protected PShader displacementShader;
	protected BufferFrameDifference bufferFrameDifference;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 800);
		p.appConfig.setProperty(AppSettings.HEIGHT, 800);
		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 15 ); 
	}
	
	protected void setupFirstFrame() {
		// build sheet mesh
		texture = DemoAssets.squareTexture();
		shape = Shapes.createSheet(250, texture);
		shape.setTexture(texture);
		
		// load shader
		displacementShader = loadShader(
			FileUtil.getFile("shaders/vertex/brightness-displace-frag-texture.glsl"), 
			FileUtil.getFile("shaders/vertex/brightness-displace-sheet-vert.glsl")
		);
		
		// webcam callback
		p.webCamWrapper.setDelegate(this);
	}
	
	
	@Override
	public void newFrame(PImage frame) {
		// lazy init frame difference object
		if(bufferFrameDifference == null) {
			bufferFrameDifference = new BufferFrameDifference(frame.width, frame.height);
			bufferFrameDifference.diffThresh(0.15f);
			bufferFrameDifference.falloffBW(0.18f);
			textureFlipped = p.createGraphics(frame.width, frame.height, PRenderers.P2D);
		}
		
		// flip webcam frame & update frame difference object
		ImageUtil.copyImage(frame, textureFlipped);
		ImageUtil.flipH(textureFlipped);
		bufferFrameDifference.update(textureFlipped);
		
		// blur to smooth out displacement
		BlurHFilter.instance(p).setBlurByPercent(1f, bufferFrameDifference.differenceBuffer().width);
		BlurHFilter.instance(p).applyTo(bufferFrameDifference.differenceBuffer());
		BlurVFilter.instance(p).setBlurByPercent(1f, bufferFrameDifference.differenceBuffer().height);
		BlurVFilter.instance(p).applyTo(bufferFrameDifference.differenceBuffer());
		
		// debug view
		p.debugView.setTexture(p.webCamWrapper.getImage());
		p.debugView.setTexture(bufferFrameDifference.differenceBuffer());
		p.debugView.setValue("shape.getVertexCount();", shape.getVertexCount());
	}

	public void drawApp() {
		p.background(0);
		
		// update shader & draw mesh
		if(bufferFrameDifference != null) {
			shape.setTexture(textureFlipped);	// set texture to webcam
			displacementShader.set("displacementMap", bufferFrameDifference.differenceBuffer());
			displacementShader.set("displaceStrength", 300f);
			p.shader(displacementShader);  
			p.translate(p.width/2f, p.height/2f, -100);
			p.shape(shape);
			p.resetShader();
		}
	}
		
}