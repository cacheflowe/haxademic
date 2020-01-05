package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.context.pg32.PGraphics32;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class Demo_FluidGPU_fail 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// Ported from: https://github.com/PavelDoGreat/WebGL-Fluid-Simulation/
	
	protected SimplexNoiseTexture displaceTexture;
	
	protected int simSize = 256;
	
	// buffers
//	protected PGraphics pgDye;	// uses own dye res
	protected PGraphics pgVelocity;
	protected PGraphics pgVelocity2;
	protected PGraphics pgDivergence;
	protected PGraphics pgCurl;
	protected PGraphics pgPressure;

	// shaders w/controls
	protected PShaderHotSwap splatShader;
	protected PShaderHotSwap blurShader;
	protected PShaderHotSwap curlShader;
	protected PShaderHotSwap vorticityShader;
	protected PShaderHotSwap pressureShader;
	protected PShaderHotSwap divergenceShader;
	protected PShaderHotSwap gradientSubtractShader;
	protected PShaderHotSwap advectionShader;
	// clearShader can be replaced with BrightnessStepFilter
	
	protected String SPLAT_SIZE = "SPLAT_SIZE";
	protected String DISSIPATION = "DISSIPATION";
	protected String PRESSURE = "PRESSURE";

	protected void config() {
		Config.setAppSize(1280, 860);
	}
	
	protected void firstFrame() {
		// set up main buffer - 32-bit!
		pg = PG.newPG32(p.width, p.height, false, false);
		pg.beginDraw();
		pg.background(0);
		pg.endDraw();
		
		// sim buffers
		pgVelocity = 	PGraphics32.createGraphics(p, simSize, simSize); 		DebugView.setTexture("pgVelocity", pgVelocity);
		pgVelocity2 = 	PGraphics32.createGraphics(p, simSize, simSize); 		DebugView.setTexture("pgVelocity2", pgVelocity2);
		pgCurl = 		PGraphics32.createGraphics(p, simSize, simSize); 		DebugView.setTexture("pgCurl", pgCurl);
		pgDivergence = 	PGraphics32.createGraphics(p, simSize, simSize); 		DebugView.setTexture("pgDivergence", pgDivergence);
		pgPressure = 	PGraphics32.createGraphics(p, simSize, simSize); 		DebugView.setTexture("pgPressure", pgPressure);
		
		// load displacement shader & texture
		splatShader = 				new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/interactive/fluid-splat.glsl"));
		blurShader = 				new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/interactive/fluid-blur.glsl"));
		curlShader = 				new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/interactive/fluid-curl.glsl"));
		vorticityShader = 			new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/interactive/fluid-vorticity.glsl"));
		pressureShader = 			new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/interactive/fluid-pressure.glsl"));
		divergenceShader = 			new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/interactive/fluid-divergence.glsl"));
		gradientSubtractShader = 	new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/interactive/fluid-gradient-subtract.glsl"));
		advectionShader = 			new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/interactive/fluid-advection.glsl"));
		
		// UI
		UI.addTitle("Splat");
		UI.addSlider(SPLAT_SIZE, 0.05f, 0, 0.1f, 0.0001f);
		UI.addTitle("Advection");
		UI.addSlider(DISSIPATION, 0.02f, 0, 0.1f, 0.0001f);
		UI.addTitle("Pressure");
		UI.addSlider(PRESSURE, 30, 0, 255, 1);
		
		// displacement map for testing
		displaceTexture = new SimplexNoiseTexture(256, 256);
		DebugView.setTexture("displacement map", displaceTexture.texture());
	}

	protected void drawApp() {
		// set up context
		if(FrameLoop.count() == 1) p.background(0);
		p.noStroke();
		
		displaceTexture.zoom(4f);
		displaceTexture.update();
		
		/////////////////////////////
		// begin fluid sim
		/////////////////////////////
		
		// add user input to velocity & dye buffers?
		if(P.abs(Mouse.xSpeed) > 0 || P.abs(Mouse.ySpeed) > 0) {
			// splat the velocity buffer
	//		splatShader.shader().set("color", P.map(Mouse.xSpeed/2f, -10, 10, 0, 1), P.map(Mouse.ySpeed/-2f, -10, 10, 0, 1), 0);
			splatShader.shader().set("point", Mouse.xNorm, 1f - Mouse.yNorm);
			splatShader.shader().set("color", Mouse.xSpeed/10f,Mouse.ySpeed/-10f, 0f);
			splatShader.shader().set("radius", UI.value(SPLAT_SIZE));
			splatShader.update();
			pgVelocity.filter(splatShader.shader());
			
			// splat with actual colors for main buffer
			splatShader.shader().set("color", 
					0.5f + 0.5f * P.sin(FrameLoop.count(0.12f)),
					0.5f + 0.5f * P.sin(FrameLoop.count(0.1f)),
					0.5f + 0.5f * P.sin(FrameLoop.count(0.04f))
					);
			splatShader.update();
			pg.filter(splatShader.shader());
		}
		
		// blur velocity
		BlurHFilter.instance(p).applyTo(pgVelocity);
		BlurHFilter.instance(p).setBlurByPercent(1f, pgVelocity.width);
		BlurVFilter.instance(p).applyTo(pgVelocity);
		BlurVFilter.instance(p).setBlurByPercent(1f, pgVelocity.height);
		
		// update curl texture from velocity
		curlShader.shader().set("uVelocity", pgVelocity);
		curlShader.update();
		DebugView.setValue("curlShader.isValid", curlShader.isValid());
		pgCurl.filter(curlShader.shader());

		// update velocity buffer w/vorticity shader
		vorticityShader.shader().set("uVelocity", pgVelocity);
		vorticityShader.shader().set("uCurl", pgCurl);
		vorticityShader.update();
		DebugView.setValue("vorticityShader.isValid", vorticityShader.isValid());
//		pgVelocity.filter(vorticityShader.shader());
		
		// update divergence buffer w/velocity map
		divergenceShader.shader().set("uVelocity", pgVelocity);
		divergenceShader.update();
		DebugView.setValue("divergenceShader.isValid", divergenceShader.isValid());
		pgDivergence.filter(divergenceShader.shader());
		
		// lerp pressure down
		BrightnessStepFilter.instance(p).setBrightnessStep(-UI.value(PRESSURE)/255f);
		BrightnessStepFilter.instance(p).applyTo(pgPressure);
		
		// step pressure
		pressureShader.shader().set("uDivergence", pgDivergence);
		pressureShader.update();
		DebugView.setValue("pressureShader.isValid", pressureShader.isValid());
		int PRESSURE_ITERATIONS = 40;
		for (int i = 0; i < PRESSURE_ITERATIONS; i++) {
			pgPressure.filter(pressureShader.shader());
		}

		// update velocity with gradient subtract step
		gradientSubtractShader.shader().set("uPressure", pgPressure);
		gradientSubtractShader.update();
		DebugView.setValue("gradientSubtractShader.isValid", gradientSubtractShader.isValid());
		pgVelocity.filter(gradientSubtractShader.shader());
			
		// fade velocity away with advection, using the velocity map
		advectionShader.shader().set("uVelocity", pgVelocity);
		advectionShader.shader().set("dissipation", UI.value(DISSIPATION));
		advectionShader.update();
//		pgVelocity2.beginDraw();
//		pgVelocity2.background(0);
//		ImageUtil.copyImage(pgVelocity, pgVelocity2);
//		pgVelocity2.filter(advectionShader.shader());
//		pgVelocity2.endDraw();

		// dye step
		advectionShader.shader().set("uVelocity", pgVelocity);
		advectionShader.shader().set("dissipation", UI.value(DISSIPATION));
		advectionShader.update();
		pg.filter(advectionShader.shader());
		
		// copy velocity2 back to velocity
//		pgVelocity.beginDraw();
//		pgVelocity.background(0);
//		ImageUtil.copyImage(pgVelocity2, pgVelocity);
//		pgVelocity.endDraw();

		/////////////////////////////
		// end fluid sim
		/////////////////////////////
		
		
		/*
		// add user interaction on top
		splatShader.shader().set("point", Mouse.xNorm, 1f - Mouse.yNorm);
		splatShader.update();
		pg.filter(splatShader.shader());
		
		// fade velocity away with advection, using the velocity map
		advectionShader.shader().set("uVelocity", displaceTexture.texture());
		advectionShader.shader().set("dissipation", UI.value(DISSIPATION));
		advectionShader.update();
		pgVelocity.filter(advectionShader.shader());
		*/
		
		// TESTING
		/*
		// test of advection shader on main buffer
		pg.filter(advectionShader.shader());
		// test of blur shader
		if(Mouse.xNorm > 0.5f) {
			for (int i = 0; i < 15; i++) {
				displaceTexture.texture().filter(blurShader.shader());
			}
		}
		*/
		

//		// draw fluid buffer to screen
		p.image(pg, 0, 0);
	}
}
