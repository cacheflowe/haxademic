package com.haxademic.demo.draw.cv;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.cv.BufferFrameDifference;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_BufferFrameDifference_sheetDeform 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PImage texture;
	protected PImage textureFlipped;
	protected PShader displacementShader;
	protected BufferFrameDifference bufferFrameDifference;

	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 800);
		Config.setProperty(AppSettings.HEIGHT, 800);
	}
	
	protected void firstFrame() {
		// build sheet mesh
		texture = DemoAssets.squareTexture();
		shape = Shapes.createSheet(250, texture);
		shape.setTexture(texture);
		
		// webcam callback
		WebCam.instance().setDelegate(this);
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
		DebugView.setTexture("webcam", frame);
		DebugView.setTexture("differenceBuffer", bufferFrameDifference.differenceBuffer());
		DebugView.setValue("shape.getVertexCount();", shape.getVertexCount());
	}

	protected void drawApp() {
		// set context
		p.background(0);
		PG.setCenterScreen(p);
		
		// update shader & draw mesh
		if(bufferFrameDifference != null) {
			// deform mesh
			MeshDeformAndTextureFilter.instance(p).setDisplacementMap(bufferFrameDifference.differenceBuffer());
			MeshDeformAndTextureFilter.instance(p).setDisplaceAmp(300f);
			MeshDeformAndTextureFilter.instance(p).setSheetMode(true);
			MeshDeformAndTextureFilter.instance(p).applyTo(p);
			// set texture using PShape method
			shape.setTexture(textureFlipped);

			// draw shape
			p.shape(shape);
			p.resetShader();
		}
	}
		
}