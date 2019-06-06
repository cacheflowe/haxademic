package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeTwoColorsFilter;
import com.haxademic.core.draw.filters.pshader.FakeLightingFilter;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.draw.filters.pshader.SharpenFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class ReactionDiffusion 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// assets
	protected PImage seed;
	protected PShape template;
	protected boolean clearScreen = true;
	
	// simulation views
	protected PImage simulationBg;
	protected PGraphics simulationPg;
	protected PImage simulationBg2;
	protected PGraphics simulationPg2;
	
	// animation
	protected int MAX_PARTICLES = 8000;
	protected PGraphics pgPost;

	
	// UI
	protected String FEEDBACK_AMP = "FEEDBACK_AMP";
	protected String FEEDBACK_ROTATE = "FEEDBACK_ROTATE";
	protected String FEEDBACK_OFFSET_X = "FEEDBACK_OFFSET_X";
	protected String FEEDBACK_OFFSET_Y = "FEEDBACK_OFFSET_Y";
	protected String DARKEN_AMP = "DARKEN_AMP";
	protected String RD_ITERATIONS = "RD_ITERATIONS";
	protected String RD_BLUR_AMP_X = "RD_BLUR_AMP_X";
	protected String RD_BLUR_AMP_Y = "RD_BLUR_AMP_Y";
	protected String RD_SHARPEN_AMP = "RD_SHARPEN_AMP";
	
	protected String FAKE_LIGHT_AMBIENT = "FAKE_LIGHT_AMBIENT";
	protected String FAKE_LIGHT_GRAD_AMP = "FAKE_LIGHT_GRAD_AMP";
	protected String FAKE_LIGHT_GRAD_BLUR = "FAKE_LIGHT_GRAD_BLUR";
	protected String FAKE_LIGHT_SPEC_AMP = "FAKE_LIGHT_SPEC_AMP";
	protected String FAKE_LIGHT_DIFF_DARK = "FAKE_LIGHT_DIFF_DARK";
	

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1600);
		p.appConfig.setProperty(AppSettings.HEIGHT, 900);
	}
	
	/////////////////////////
	// INIT
	/////////////////////////
	
	protected void setupFirstFrame() {
		pg = P.p.createGraphics(2550, 3300, PRenderers.P3D);
		pgPost = P.p.createGraphics(2550, 3300, PRenderers.P3D);
		DrawUtil.setTextureRepeat(pg, true);
		DrawUtil.setTextureRepeat(pgPost, true);
		
		seed = P.getImage("images/_sketch/wolfe-white-2.png");
		
		buildUI();
	}
	
	protected void buildUI() {
		p.ui.addSlider(FEEDBACK_AMP, 0, 0.99f, 1.01f, 0.0001f);
		p.ui.addSlider(FEEDBACK_ROTATE, 0, -0.02f, 0.02f, 0.0001f);
		p.ui.addSlider(FEEDBACK_OFFSET_X, 0, -0.02f, 0.02f, 0.0001f);
		p.ui.addSlider(FEEDBACK_OFFSET_Y, 0, -0.02f, 0.02f, 0.0001f);
		p.ui.addSlider(DARKEN_AMP, -10, -200, 200, 1f);
		p.ui.addSlider(RD_ITERATIONS, 0, 0, 10, 1f);
		p.ui.addSlider(RD_BLUR_AMP_X, 0, 0, 6, 0.001f);
		p.ui.addSlider(RD_BLUR_AMP_Y, 0, 0, 6, 0.001f);
		p.ui.addSlider(RD_SHARPEN_AMP, 0, 0, 20, 0.01f);
		p.ui.addSlider(FAKE_LIGHT_AMBIENT, 2f, 0.3f, 6f, 0.01f);
		p.ui.addSlider(FAKE_LIGHT_GRAD_AMP, 0.66f, 0.1f, 6f, 0.01f);
		p.ui.addSlider(FAKE_LIGHT_GRAD_BLUR, 1f, 0.1f, 6f, 0.01f);
		p.ui.addSlider(FAKE_LIGHT_SPEC_AMP, 2.25f, 0.1f, 6f, 0.01f);
		p.ui.addSlider(FAKE_LIGHT_DIFF_DARK, 0.85f, 0.1f, 2f, 0.01f);
	}
	
	/////////////////////////
	// DRAW
	/////////////////////////
	
	protected void darkenCanvas() {
		BrightnessStepFilter.instance(p).setBrightnessStep(p.ui.value(DARKEN_AMP)/255f);
		BrightnessStepFilter.instance(p).applyTo(pg);
	}

	protected void setFakeLighting() {
		FakeLightingFilter.instance(p).setAmbient(p.ui.value(FAKE_LIGHT_AMBIENT));
		FakeLightingFilter.instance(p).setGradAmp(p.ui.value(FAKE_LIGHT_GRAD_AMP));
		FakeLightingFilter.instance(p).setGradBlur(p.ui.value(FAKE_LIGHT_GRAD_BLUR));
		FakeLightingFilter.instance(p).setSpecAmp(p.ui.value(FAKE_LIGHT_SPEC_AMP));
		FakeLightingFilter.instance(p).setDiffDark(p.ui.value(FAKE_LIGHT_DIFF_DARK));
		FakeLightingFilter.instance(p).applyTo(pgPost);
	}
	
	protected void setColorize() {
		ColorizeTwoColorsFilter.instance(p).setColor1(1f,  0.7f,  1f);
		ColorizeTwoColorsFilter.instance(p).setColor2(0f,  0f,  0f);
		ColorizeTwoColorsFilter.instance(p).applyTo(pgPost);
	}
	
	
	protected void applyZoomRotate() {
		p.debugView.setValue("p.ui.value(FEEDBACK_OFFSET_X)/255f", p.ui.value(FEEDBACK_OFFSET_X)/255f);
		RotateFilter.instance(p).setRotation(p.ui.value(FEEDBACK_ROTATE));
		RotateFilter.instance(p).setZoom(p.ui.value(FEEDBACK_AMP));
		RotateFilter.instance(p).setOffset(p.ui.value(FEEDBACK_OFFSET_X), p.ui.value(FEEDBACK_OFFSET_Y));
		RotateFilter.instance(p).applyTo(pg);
	}
	
	protected void applyRD() {
		for (int i = 0; i < p.ui.valueInt(RD_ITERATIONS); i++) {
			// TODO: I messed this up and didn't use pg dimensions to power the R/D effect. Now all of the presets use that hardcoded number.
			BlurHFilter.instance(p).setBlurByPercent(p.ui.value(RD_BLUR_AMP_X), 1600);
			BlurHFilter.instance(p).applyTo(pg);
			BlurVFilter.instance(p).setBlurByPercent(p.ui.value(RD_BLUR_AMP_Y), 900);
			BlurVFilter.instance(p).applyTo(pg);
			SharpenFilter.instance(p).setSharpness(p.ui.value(RD_SHARPEN_AMP));
			SharpenFilter.instance(p).applyTo(pg);
		}
	}
	
	public void drawApp() {
		p.background(0);
		
		// set context
		pg.beginDraw();
		if(p.frameCount <= 10 || clearScreen) pg.background(0);
		clearScreen = false;
		DrawUtil.setDrawCorner(pg);
		DrawUtil.setDrawFlat2d(pg, true);
		
		darkenCanvas();
		if(p.frameCount % 300 > 0) pg.image(seed, 0, 0);
		pg.endDraw();
		if(p.frameCount % 300 > 0) applyZoomRotate();
		applyRD();
		
		ImageUtil.copyImage(pg, pgPost);		// copy to 2nd buffer for postprocessing
//		setColorize();
		setFakeLighting();
		ImageUtil.cropFillCopyImage(pgPost, p.g, false);
	}
		
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'z') P.out(p.ui.valuesToJSON());
		if(p.key == ' ') clearScreen = true;
		if(p.key == 's') pgPost.save(FileUtil.getHaxademicOutputPath() + "_screenshots/" + SystemUtil.getTimestampFine(p) + ".png");
//		if(p.key == '1') p.ui.loadJSON(JSONObject.parse(CONFIG_1));
	}
	
	
	protected String CONFIG_1 = "{\r\n" + 
			"  \"FEEDBACK_OFFSET_X\": -0.00189999642316252,\r\n" + 
			"  \"FAKE_LIGHT_SPEC_AMP\": 2.0500032901763916,\r\n" + 
			"  \"FEEDBACK_OFFSET_Y\": 2.4112523533403873E-8,\r\n" + 
			"  \"RD_ITERATIONS\": 4,\r\n" + 
			"  \"RD_BLUR_AMP_Y\": 0.41699957847595215,\r\n" + 
			"  \"DARKEN_AMP\": 0,\r\n" + 
			"  \"RD_BLUR_AMP_X\": 0.9390028715133667,\r\n" + 
			"  \"RD_SHARPEN_AMP\": 12.28007698059082,\r\n" + 
			"  \"FAKE_LIGHT_GRAD_AMP\": 2.299994707107544,\r\n" + 
			"  \"FEEDBACK_ROTATE\": 1.000110714812763E-4,\r\n" + 
			"  \"FAKE_LIGHT_DIFF_DARK\": 0.6300005912780762,\r\n" + 
			"  \"FAKE_LIGHT_GRAD_BLUR\": 2.7999982833862305,\r\n" + 
			"  \"FAKE_LIGHT_AMBIENT\": 2.3999996185302734,\r\n" + 
			"  \"FEEDBACK_AMP\": 0.9985998868942261\r\n" + 
			"}\r\n";
}