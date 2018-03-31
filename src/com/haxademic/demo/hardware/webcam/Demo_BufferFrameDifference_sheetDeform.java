package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.filters.shaders.BlurHFilter;
import com.haxademic.core.draw.filters.shaders.BlurVFilter;
import com.haxademic.core.draw.image.BufferActivityMonitor;
import com.haxademic.core.draw.image.BufferFrameDifference;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.draw.shapes.Shapes;
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
	protected PShader displacementShader;
	protected BufferFrameDifference bufferFrameDifference;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 800);
		p.appConfig.setProperty(AppSettings.HEIGHT, 800);
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, 160);
		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 3 ); 
	}
	
	protected void setupFirstFrame() {
		// build sheet mesh
		texture = p.loadImage(FileUtil.getFile("images/window/flexweave-texture-square.png"));
		shape = Shapes.createSheet(100, texture);
		shape.setTexture(texture);
		p.debugView.setValue("shape.getVertexCount();", shape.getVertexCount());
		
		// load shader
		displacementShader = loadShader(
			FileUtil.getFile("shaders/vertex/brightness-displace-frag-texture.glsl"), 
			FileUtil.getFile("shaders/vertex/brightness-displace-sheet-vert.glsl")
		);
		
		// webcam
		p.webCamWrapper.setDelegate(this);
	}
	
	
	@Override
	public void newFrame(PImage frame) {
		// lazy load & compare frames
		if(bufferFrameDifference == null) {
			bufferFrameDifference = new BufferFrameDifference(frame.width, frame.height);
			bufferFrameDifference.diffThresh(0.1f);
			bufferFrameDifference.falloffBW(0.03f);
		}
		bufferFrameDifference.update(frame);
		// blur
		BlurHFilter.instance(p).setBlurByPercent(1f, bufferFrameDifference.differenceBuffer().width);
		BlurHFilter.instance(p).applyTo(bufferFrameDifference.differenceBuffer());
		BlurVFilter.instance(p).setBlurByPercent(1f, bufferFrameDifference.differenceBuffer().height);
		BlurVFilter.instance(p).applyTo(bufferFrameDifference.differenceBuffer());
		// debug view
		p.debugView.setTexture(p.webCamWrapper.getImage());
		p.debugView.setTexture(bufferFrameDifference.differenceBuffer());
	}

	public void drawApp() {
		background(0);
		
		// rotate
		p.translate(p.width/2f, p.height/2f, -100);
		p.rotateZ(P.PI);
		
		// draw mesh with texture or without
		if(bufferFrameDifference != null) {
			displacementShader.set("displacementMap", bufferFrameDifference.differenceBuffer());
			displacementShader.set("displaceStrength", 300f);
			p.shader(displacementShader);  
			p.shape(shape);
			p.resetShader();
		}
	}
		
}