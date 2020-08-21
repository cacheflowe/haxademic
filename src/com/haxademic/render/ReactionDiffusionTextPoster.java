package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
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
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class ReactionDiffusionTextPoster 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

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
	

	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1600);
		Config.setProperty(AppSettings.HEIGHT, 900);
	}
	
	/////////////////////////
	// INIT
	/////////////////////////
	
	protected void firstFrame() {
		pg = P.p.createGraphics(2550, 3300, PRenderers.P3D);
		pgPost = P.p.createGraphics(2550, 3300, PRenderers.P3D);
		PG.setTextureRepeat(pg, true);
		PG.setTextureRepeat(pgPost, true);
		
		seed = P.getImage("images/_sketch/wolfe-white-2.png");
		
		buildUI();
	}
	
	protected void buildUI() {
		UI.addSlider(FEEDBACK_AMP, 0, 0.99f, 1.01f, 0.0001f);
		UI.addSlider(FEEDBACK_ROTATE, 0, -0.02f, 0.02f, 0.0001f);
		UI.addSlider(FEEDBACK_OFFSET_X, 0, -0.02f, 0.02f, 0.0001f);
		UI.addSlider(FEEDBACK_OFFSET_Y, 0, -0.02f, 0.02f, 0.0001f);
		UI.addSlider(DARKEN_AMP, -10, -200, 200, 1f);
		UI.addSlider(RD_ITERATIONS, 0, 0, 10, 1f);
		UI.addSlider(RD_BLUR_AMP_X, 0, 0, 6, 0.001f);
		UI.addSlider(RD_BLUR_AMP_Y, 0, 0, 6, 0.001f);
		UI.addSlider(RD_SHARPEN_AMP, 0, 0, 20, 0.01f);
		UI.addSlider(FAKE_LIGHT_AMBIENT, 2f, 0.3f, 6f, 0.01f);
		UI.addSlider(FAKE_LIGHT_GRAD_AMP, 0.66f, 0.1f, 6f, 0.01f);
		UI.addSlider(FAKE_LIGHT_GRAD_BLUR, 1f, 0.1f, 6f, 0.01f);
		UI.addSlider(FAKE_LIGHT_SPEC_AMP, 2.25f, 0.1f, 6f, 0.01f);
		UI.addSlider(FAKE_LIGHT_DIFF_DARK, 0.85f, 0.1f, 2f, 0.01f);
	}
	
	/////////////////////////
	// DRAW
	/////////////////////////
	
	protected void darkenCanvas() {
		BrightnessStepFilter.instance(p).setBrightnessStep(UI.value(DARKEN_AMP)/255f);
		BrightnessStepFilter.instance(p).applyTo(pg);
	}

	protected void setFakeLighting() {
		FakeLightingFilter.instance(p).setAmbient(UI.value(FAKE_LIGHT_AMBIENT));
		FakeLightingFilter.instance(p).setGradAmp(UI.value(FAKE_LIGHT_GRAD_AMP));
		FakeLightingFilter.instance(p).setGradBlur(UI.value(FAKE_LIGHT_GRAD_BLUR));
		FakeLightingFilter.instance(p).setSpecAmp(UI.value(FAKE_LIGHT_SPEC_AMP));
		FakeLightingFilter.instance(p).setDiffDark(UI.value(FAKE_LIGHT_DIFF_DARK));
		FakeLightingFilter.instance(p).setMap(pgPost);
		FakeLightingFilter.instance(p).applyTo(pgPost);
	}
	
	protected void setColorize() {
		ColorizeTwoColorsFilter.instance(p).setColor1(1f,  0.7f,  1f);
		ColorizeTwoColorsFilter.instance(p).setColor2(0f,  0f,  0f);
		ColorizeTwoColorsFilter.instance(p).applyTo(pgPost);
	}
	
	
	protected void applyZoomRotate() {
		DebugView.setValue("UI.value(FEEDBACK_OFFSET_X)/255f", UI.value(FEEDBACK_OFFSET_X)/255f);
		RotateFilter.instance(p).setRotation(UI.value(FEEDBACK_ROTATE));
		RotateFilter.instance(p).setZoom(UI.value(FEEDBACK_AMP));
		RotateFilter.instance(p).setOffset(UI.value(FEEDBACK_OFFSET_X), UI.value(FEEDBACK_OFFSET_Y));
		RotateFilter.instance(p).applyTo(pg);
	}
	
	protected void applyRD() {
		for (int i = 0; i < UI.valueInt(RD_ITERATIONS); i++) {
			// TODO: I messed this up and didn't use pg dimensions to power the R/D effect. Now all of the presets use that hardcoded number.
			BlurHFilter.instance(p).setBlurByPercent(UI.value(RD_BLUR_AMP_X), 1600);
			BlurHFilter.instance(p).applyTo(pg);
			BlurVFilter.instance(p).setBlurByPercent(UI.value(RD_BLUR_AMP_Y), 900);
			BlurVFilter.instance(p).applyTo(pg);
			SharpenFilter.instance(p).setSharpness(UI.value(RD_SHARPEN_AMP));
			SharpenFilter.instance(p).applyTo(pg);
		}
	}
	
	protected void drawApp() {
		p.background(0);
		
		// set context
		pg.beginDraw();
		if(p.frameCount <= 10 || clearScreen) pg.background(0);
		clearScreen = false;
		PG.setDrawCorner(pg);
		PG.setDrawFlat2d(pg, true);
		
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
		if(p.key == 'z') P.out(UI.valuesToJSON());
		if(p.key == ' ') clearScreen = true;
		if(p.key == 's') pgPost.save(FileUtil.haxademicOutputPath() + "_screenshots/" + SystemUtil.getTimestampFine() + ".png");
//		if(p.key == '1') UI.loadJSON(JSONObject.parse(CONFIG_1));
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