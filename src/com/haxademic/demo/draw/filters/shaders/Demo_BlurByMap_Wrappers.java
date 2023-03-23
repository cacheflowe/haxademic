package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurHMapFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BlurVMapFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.DisplacementMapFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.draw.filters.pshader.SharpenMapFilter;
import com.haxademic.core.draw.image.ImageCacher;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.draw.textures.SimplexNoise3dTexture;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PFont;
import processing.core.PGraphics;

public class Demo_BlurByMap_Wrappers
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected SimplexNoise3dTexture noiseTexture;
	protected PGraphics basicMap;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1024 );
		Config.setProperty( AppSettings.HEIGHT, 1024 );
	}
	
	protected void firstFrame() {
		// better pg?
		pg = PG.newPG32(p.width, p.height, true, false);
		
		// init noise object
		noiseTexture = new SimplexNoise3dTexture(p.width, p.height, true);
		noiseTexture.update(0.07f, 0, 0, 0, 0, false, false);
		DebugView.setTexture("noise", noiseTexture.texture());
		
		// extra map
		basicMap = PG.newPG32(256, 256, true, false);
		DebugView.setTexture("basicMap", basicMap);
	}
	
	protected void drawApp() {
		if(p.frameCount == 1) PG.setTextureRepeat(p.g, true);
		p.background(0);
		PG.setDrawCorner(p.g);
		
		///////////////////////
		// update basic map
		///////////////////////
		basicMap.beginDraw();
		PG.setCenterScreen(basicMap);
		PG.setDrawCenter(basicMap);
		basicMap.background(0);
		basicMap.noStroke();
		basicMap.fill(255);
		basicMap.ellipse(0, 0, basicMap.width / 2, basicMap.height / 2);
		basicMap.endDraw();

		// replace basic map with another image?
		BlendTowardsTexture.instance().setSourceTexture(ImageCacher.get("images/_sketch/kamala.jpg"));
		BlendTowardsTexture.instance().setFlipY(true);
		BlendTowardsTexture.instance().setBlendLerp(0.995f);
//		BlendTowardsTexture.instance().applyTo(basicMap);
		ImageUtil.cropFillCopyImage(ImageCacher.get("images/_sketch/kamala.jpg"), basicMap, true);
		InvertFilter.instance().applyTo(basicMap);
		ContrastFilter.instance().setContrast(3.f);
		ContrastFilter.instance().applyTo(basicMap);
		
		// blur basic map circle
		BlurHFilter.instance().setBlurByPercent(1f, basicMap.width);
		BlurVFilter.instance().setBlurByPercent(1f, basicMap.height);
		for (int i = 0; i < 10; i++) {
//			BlurHFilter.instance().applyTo(basicMap);
//			BlurVFilter.instance().applyTo(basicMap);
		}
		
		///////////////////////
		// update noise map
		///////////////////////
		noiseTexture.update(
				FrameLoop.osc(0.004f, 0.8f, 1f),	// zoom
				FrameLoop.count(0.004f),			// rotation
				0,									// offset x
				0,									// offset y
				FrameLoop.count(0.002f),			// offset z
				false,								// fractal mode
				false								// xRepeat mode
		);
		ContrastFilter.instance().setContrast(3.f);
		ContrastFilter.instance().applyTo(noiseTexture.texture());
		
		// Draw seed
		pg.beginDraw();
		PG.setDrawCorner(pg);
		float colorOsc = FrameLoop.osc(0.01f, 0, 255);
		colorOsc = Mouse.xNorm * 255;
//		if(colorOsc > 50) {
//			PG.drawGrid(pg, p.color(0, colorOsc), p.color(255, colorOsc), pg.width/40, pg.height/40, 5);
//			PG.setPImageAlpha(pg, P.map(colorOsc, 50, 255, 0, 255));
//			ImageUtil.drawImageCropFill(ImageCacher.get("images/_sketch/kamala.jpg"), pg, true);
//			PG.resetPImageAlpha(pg);
//		}
		
		///////////////////////
		// add text
		///////////////////////
		String fontFile = DemoAssets.fontOpenSansPath;
		PFont font = FontCacher.getFont(fontFile, 280);
		FontCacher.setFontOnContext(pg, font, p.color(255), 1f, PTextAlign.CENTER, PTextAlign.CENTER);
//		StrokeText.draw(pg, "PETE", 0, -40, pg.width, pg.height, p.color(255), p.color(0), 10, 36);
		
		///////////////////////
		// set R/D uniforms
		///////////////////////
		GrainFilter.instance().setCrossfade(0.1f);
		GrainFilter.instance().setTime(p.frameCount);

		PGraphics rdMap = (Mouse.yNorm > 0.5f) ? basicMap : noiseTexture.texture();
		rdMap = basicMap;
		BlurHMapFilter.instance().setMap(rdMap);
		BlurHMapFilter.instance().setAmpMin(0.4f);
		BlurHMapFilter.instance().setAmpMax(1.25f);
		BlurVMapFilter.instance().setMap(rdMap);
		BlurVMapFilter.instance().setAmpMin(0.6f);
		BlurVMapFilter.instance().setAmpMax(1.25f);
		SharpenMapFilter.instance().setMap(rdMap);
		SharpenMapFilter.instance().setAmpMin(2f);
		SharpenMapFilter.instance().setAmpMax(4f);
		
		DisplacementMapFilter.instance().setMap(noiseTexture.texture());
		DisplacementMapFilter.instance().setMode(3);
		DisplacementMapFilter.instance().setRotRange(P.TWO_PI * 2f);
		DisplacementMapFilter.instance().setAmp(0.0001f);
		
		RotateFilter.instance().setRotation(0);
		RotateFilter.instance().setZoom(0.9998f);
		RotateFilter.instance().setOffset(0.0003f, 0.00001f);

		BrightnessStepFilter.instance().setBrightnessStep((-255f * Mouse.xNorm)/255f);

		///////////////////////
		// auto loop
		///////////////////////
//		BrightnessStepFilter.instance().setBrightnessStep(FrameLoop.osc(0.003f, -200, -40)/255f);
//		DisplacementMapFilter.instance().setAmp(FrameLoop.osc(0.003f, 0f, 0.004f));
		
		///////////////////////
		// Do R/D
		///////////////////////
		for (int i = 0; i < 5; i++) {
			BrightnessStepFilter.instance().applyTo(pg);
			RotateFilter.instance().applyTo(pg);
			DisplacementMapFilter.instance().applyTo(pg);
			GrainFilter.instance().applyTo(pg);	// add jitter. not sure if this helps. might prevent from going full black or white
			BlurHMapFilter.instance().applyTo(pg);
			BlurVMapFilter.instance().applyTo(pg);
			BlurHMapFilter.instance().applyTo(pg);
			BlurVMapFilter.instance().applyTo(pg);
			SharpenMapFilter.instance().applyTo(pg);
		}
		
//		SaturationFilter.instance().setSaturation(0f);
//		SaturationFilter.instance().applyTo(pg);
//		ThresholdFilter.instance().applyTo(pg);
		pg.endDraw();
		
		///////////////////////
		// Draw to screen
		///////////////////////
		p.image(pg, 0, 0);
		
		///////////////////////
		// Post processing
		///////////////////////
//		FakeLightingFilter.instance().applyTo(p.g);
//		EdgesFilter.instance().applyTo(p.g);
//		FXAAFilter.instance().applyTo(p.g);
	}

}
