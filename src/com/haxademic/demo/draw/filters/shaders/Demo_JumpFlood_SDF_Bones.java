package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.draw.filters.pshader.compound.ReactionDiffusionStepFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.DepthSilhouetteSmoothed;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PFont;
import processing.core.PGraphics;

public class Demo_JumpFlood_SDF_Bones
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShaderHotSwap shader;
	protected int jfaStep = 0;
	protected boolean jfaDirty = true;
	protected PGraphics jfaPG;

	protected DepthSilhouetteSmoothed depthSilhouetteSmoothed;

	protected int FRAMES = 180;
	
	protected void config() {
		Config.setAppSize(1024, 512);
		Config.setProperty(AppSettings.SHOW_UI, true);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}
	
	protected void firstFrame() {
		pg = PG.newPG32(p.width, p.height);
		jfaPG = PG.newDataPG(p.width, p.height);
		shader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/jump-flood.glsl"));
//		buildCamera();
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') reset();
		if(p.key == '1') step();
	}
	
	protected void reset() {
		jfaStep = 0;
		jfaDirty = true;
	}
	
	protected void step() {
		jfaStep++;
		jfaDirty = true;
	}
	
	protected void drawShape() {
		pg.beginDraw();
		pg.background(0);
		
		pg.push();
		PG.setDrawCenter(pg);
		PG.setCenterScreen(pg);

		// text
		String fontFile = DemoAssets.fontOpenSansPath;
		PFont font = FontCacher.getFont(fontFile, pg.height * 0.4f);
		FontCacher.setFontOnContext(pg, font, p.color(255), 1f, PTextAlign.CENTER, PTextAlign.CENTER);
		pg.text("BONES", 0, -p.height * 0.05f, p.width, p.height);

		pg.pop();
		
		// if camera
		if(depthSilhouetteSmoothed != null) {
			ImageUtil.cropFillCopyImage(depthSilhouetteSmoothed.image(), pg, true);
		}
		
		pg.endDraw();

		ReactionDiffusionStepFilter.applyTo(pg, 
			10, 
			1, 
			1,
			1, 
			FrameLoop.osc(0.05f, 3, 8),
			false,
			0.3f,
			0.5f
		);
		
		// zoom/rotate
		RotateFilter.instance().setZoom(1f);
		RotateFilter.instance().setRotation(0.0001f);
//		RotateFilter.instance().setOffset(0, 0.0002f);
		RotateFilter.instance().applyTo(pg);


		// copy to jfa buffer
		ImageUtil.copyImage(pg, jfaPG);
		// BlurProcessingFilter.instance().setBlurSize(20);
		// BlurProcessingFilter.instance().setSigma(20);
		// BlurProcessingFilter.instance().applyTo(jfaPG);
		// Pixelate2Filter.instance().setDivider(10);
		// Pixelate2Filter.instance().applyTo(jfaPG);

		// debug textures
		DebugView.setTexture("pg", pg);
		DebugView.setTexture("jfaPG", jfaPG);
	}
	
	protected void runJfaStep(float step) {
		shader.update();
		shader.shader().set("iter", (float) step);
		shader.shader().set("time", FrameLoop.progressRads() * -1f);
		// shader.shader().set("time", 0);
		jfaPG.filter(shader.shader());
	}
	
	
	protected void drawApp() {
		if(depthSilhouetteSmoothed != null) depthSilhouetteSmoothed.update();
		p.background(0);
		
		// full cycle of JFA -> SDF
		shader.update();
		
		// run through full sequence
		for (int i = 0; i < 15; i++) {
			if(i == 0) {
				drawShape();
			}
			runJfaStep(i);
		}

		// FXAAFilter.instance().applyTo(jfaPG);
		// BlurProcessingFilter.instance().setBlurSize(1);
		// BlurProcessingFilter.instance().setSigma(1);
		// BlurProcessingFilter.instance().applyTo(jfaPG);


		// manually step through for debugging
//		if(jfaDirty == true) {
//			if(jfaStep == 0) {
//				drawShape();
//			}
//			runJfaStep();
//			jfaDirty = false;
//		}

		
		// draw buffer to screen
		p.image(jfaPG, 0, 0);

		// split screen copy
		p.copy(pg, 0, 0, pg.width, pg.height / 2, 0, 0, p.width, p.height / 2);

		// show step
		String fontFile = DemoAssets.fontOpenSansPath;
		PFont font = FontCacher.getFont(fontFile, 24);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		// p.text("JFA Step: " + jfaStep, 40, p.height - font.getSize() - 40);
		
		// show shader compilation
		// shader.showShaderStatus(p.g);
	}
	
	// REALSENSE 
	
	protected void buildCamera() {
		// init depth cam
		DepthCamera.instance(DepthCameraType.Realsense);
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		depthSilhouetteSmoothed = new DepthSilhouetteSmoothed(depthCamera, 3);
		depthSilhouetteSmoothed.buildUI(false);
		
		// add camera images to debugview
		DebugView.setTexture("depthBuffer", depthSilhouetteSmoothed.depthBuffer());
		DebugView.setTexture("avgBuffer", depthSilhouetteSmoothed.avgBuffer());
		DebugView.setTexture("image", depthSilhouetteSmoothed.image());
	}
	
}
