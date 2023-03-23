package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.DisplacementMapFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.SimplexNoise3dTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

public class Demo_BlurByMap_Dev
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShaderHotSwap shaderH;
	protected PShaderHotSwap shaderV;
	protected PShaderHotSwap shaderS;
	
	protected SimplexNoise3dTexture noiseTexture;

	protected void firstFrame() {
		// init blur shaders
		shaderH = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/blur-horizontal-map.glsl"));	
		shaderV = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/blur-vertical-map.glsl"));	
		shaderS = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/sharpen-map.glsl"));	

		// init noise object
		noiseTexture = new SimplexNoise3dTexture(p.width, p.height, true);
		noiseTexture.update(0.07f, 0, 0, 0, 0, false, false);
		DebugView.setTexture("noise", noiseTexture.texture());
	}
	
	protected void drawApp() {
		if(p.frameCount == 1) PG.setTextureRepeat(p.g, true);
		p.background(0);
		
		// update blur map
		noiseTexture.update(
				FrameLoop.osc(0.004f, 0.8f, 1f),	// zoom
				FrameLoop.count(0.004f),			// rotation
				0,									// offset x
				0,									// offset y
				FrameLoop.count(0.002f),			// offset z
				false,								// fractal mode
				false								// xRepeat mode
		);
		ContrastFilter.instance().setContrast(1.5f);
		ContrastFilter.instance().applyTo(noiseTexture.texture());
		
		// draw to pg
		pg.beginDraw();
		float colorOsc = FrameLoop.osc(0.01f, 0, 255);
		colorOsc = Mouse.xNorm * 255;
		if(colorOsc > 50 || frameCount % 3000 == 0) {
//			PG.drawGrid(pg, p.color(0, colorOsc), p.color(255, colorOsc), pg.width/40, pg.height/40, 5);
			PG.setPImageAlpha(pg, colorOsc/255f);
			ImageUtil.drawImageCropFill(DemoAssets.justin(), pg, true);
			PG.resetPImageAlpha(pg);
		}
		
		GrainFilter.instance().setCrossfade(0.1f);
		GrainFilter.instance().setTime(p.frameCount);

		
		// update shaders & apply to screen
		shaderH.shader().set("ampMin", 0.3f);
		shaderH.shader().set("ampMax", 1.5f);
		shaderH.shader().set("ampMap", noiseTexture.texture());
		shaderH.update();
		shaderV.shader().set("ampMin", 0.3f);
		shaderV.shader().set("ampMax", 1.5f);
		shaderV.shader().set("ampMap", noiseTexture.texture());
		shaderV.update();
		shaderS.shader().set("ampMin", 3f);
		shaderS.shader().set("ampMax", 16f);
		shaderS.shader().set("ampMap", noiseTexture.texture());
		shaderS.update();
		
		DisplacementMapFilter.instance().setMap(noiseTexture.texture());
		DisplacementMapFilter.instance().setMode(3);
		DisplacementMapFilter.instance().setRotRange(P.TWO_PI * 2f);
		DisplacementMapFilter.instance().setAmp(0.002f);

		BrightnessStepFilter.instance().setBrightnessStep(-70f/255f);
		
		for (int i = 0; i < 1; i++) {
			BrightnessStepFilter.instance().applyTo(pg);
			DisplacementMapFilter.instance.applyTo(pg);
			GrainFilter.instance.applyTo(pg);	// add jitter
			pg.filter(shaderH.shader());
			pg.filter(shaderV.shader());
			pg.filter(shaderH.shader());
			pg.filter(shaderV.shader());
			pg.filter(shaderS.shader());
		}
		
		DisplacementMapFilter.instance.applyTo(pg);
		
		SaturationFilter.instance().setSaturation(0f);
		SaturationFilter.instance().applyTo(pg);
		pg.endDraw();
		
		p.image(pg, 0, 0);
		
		shaderH.showShaderStatus(p.g);
		shaderV.showShaderStatus(p.g);
		shaderS.showShaderStatus(p.g);
	}

}
