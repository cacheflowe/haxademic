package com.haxademic.demo.draw.cv;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class Demo_WebCamMotionDetection 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics backplate;
	protected PGraphics webcamBuffer;
	protected PGraphics differenceBuffer;
	protected PGraphics bwBuffer;
	protected PShader blendTowardsShader;
	protected PShader differenceShader;
	
	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1280 );
		Config.setProperty(AppSettings.HEIGHT, 720 );
	}
		
	protected void firstFrame () {
		WebCam.instance().setDelegate(this);
		
		backplate = p.createGraphics(p.width, p.height, PRenderers.P3D);
		webcamBuffer = p.createGraphics(p.width, p.height, PRenderers.P3D);
		differenceBuffer = p.createGraphics(p.width, p.height, PRenderers.P3D);
		bwBuffer = p.createGraphics(p.width, p.height, PRenderers.P3D);
		
		blendTowardsShader = p.loadShader(FileUtil.getPath("haxademic/shaders/filters/texture-blend-towards-texture.glsl"));
		differenceShader = p.loadShader(FileUtil.getPath("haxademic/shaders/filters/texture-difference-threshold.glsl"));
	}

	protected void drawApp() {
		// set up context
		p.background( 0 );
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
		
		// update difference calculation
		differenceBuffer.filter(differenceShader);

		// post-process difference buffer w/ threshold of black & white falloff, w/ blur to help smooth
		ImageUtil.cropFillCopyImage(differenceBuffer, bwBuffer, true);
		BlurHFilter.instance().setBlurByPercent(1f, (float) bwBuffer.width);
		BlurHFilter.instance().applyTo(bwBuffer);
		BlurVFilter.instance().setBlurByPercent(1f, (float) bwBuffer.height);
		BlurVFilter.instance().applyTo(bwBuffer);
		ThresholdFilter.instance().setCutoff(0.2f);
		ThresholdFilter.instance().applyTo(bwBuffer);
		
		// set debug info
		p.image(bwBuffer, 0, 0);
	}

	@Override
	public void newFrame(PImage frame) {
		// set textures for debug view
		DebugView.setValue("newframe", p.frameCount);
		DebugView.setTexture("webcamBuffer", webcamBuffer);
		DebugView.setTexture("backplate", backplate);
		DebugView.setTexture("differenceBuffer", differenceBuffer);
		DebugView.setTexture("bwBuffer", bwBuffer);
		
		// copy webcam to current buffer
		ImageUtil.cropFillCopyImage(WebCam.instance().image(), webcamBuffer, true);
		ImageUtil.flipH(webcamBuffer);

		// run target blend shader
		blendTowardsShader.set("targetTexture", webcamBuffer);
		backplate.filter(blendTowardsShader);
		
		// set difference shader textures
		differenceShader.set("tex1", backplate);
		differenceShader.set("tex2", webcamBuffer);
	}

}
