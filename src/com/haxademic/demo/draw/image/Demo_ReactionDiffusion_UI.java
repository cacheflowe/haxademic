package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeTwoColorsFilter;
import com.haxademic.core.draw.filters.pshader.FXAAFilter;
import com.haxademic.core.draw.filters.pshader.FakeLightingFilter;
import com.haxademic.core.draw.filters.pshader.FeedbackMapFilter;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.filters.pshader.SharpenFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.system.SystemUtil;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class Demo_ReactionDiffusion_UI 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// TODO:
	// - Add noise wavy shader in addition to the basic wavy sin() lines
	// - Test audio looping & pitch shifting in Beads (a la Communichords, but with audio FFT data)
	// - Find parameters & make a nice collection
	// - Blur values above 1 seem to trigger the broken R/D state with fine lines 
	// - Make a version of Blur & Sharpen that use a map for amplitude 
	
	// app
	protected boolean clearScreen = true;
	protected boolean seedQueue = false;
	protected PGraphics pgPost;

	// texture to mix
	protected PGraphics linesTexture;
	protected PShader gradientShader;

	// feedback map
	protected PGraphics map;
	protected SimplexNoiseTexture simplexNoise;
	
	// UI
	protected String FEEDBACK_AMP = "FEEDBACK_AMP";
	protected String FEEDBACK_ROTATE = "FEEDBACK_ROTATE";
	protected String FEEDBACK_OFFSET_X = "FEEDBACK_OFFSET_X";
	protected String FEEDBACK_OFFSET_Y = "FEEDBACK_OFFSET_Y";
	
	protected String mapZoom = "mapZoom";
	protected String mapRot = "mapRot";
	protected String feedbackAmp = "feedbackAmp";
	protected String feedbackBrightStep = "feedbackBrightStep";
	protected String feedbackAlphaStep = "feedbackAlphaStep";
	protected String feedbackRadiansStart = "feedbackRadiansStart";
	protected String feedbackRadiansRange = "feedbackRadiansRange";
	protected String FEEDBACK_ITERS = "FEEDBACK_ITERS";
	
	protected String DARKEN_AMP = "DARKEN_AMP";
	protected String RD_ITERATIONS = "RD_ITERATIONS";
	protected String RD_BLUR_AMP_X = "RD_BLUR_AMP_X";
	protected String RD_BLUR_AMP_Y = "RD_BLUR_AMP_Y";
	protected String RD_SHARPEN_AMP = "RD_SHARPEN_AMP";
	
	protected String TEXTURE_BLEND = "TEXTURE_BLEND";
	
	protected String FAKE_LIGHT_AMBIENT = "FAKE_LIGHT_AMBIENT";
	protected String FAKE_LIGHT_GRAD_AMP = "FAKE_LIGHT_GRAD_AMP";
	protected String FAKE_LIGHT_GRAD_BLUR = "FAKE_LIGHT_GRAD_BLUR";
	protected String FAKE_LIGHT_SPEC_AMP = "FAKE_LIGHT_SPEC_AMP";
	protected String FAKE_LIGHT_DIFF_DARK = "FAKE_LIGHT_DIFF_DARK";

	protected String FXAA_ACTIVE = "FXAA_ACTIVE";
	

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1920);
		p.appConfig.setProperty(AppSettings.HEIGHT, 1080);
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, 2000);
		p.appConfig.setProperty(AppSettings.SHOW_SLIDERS, true);
		p.appConfig.setProperty(AppSettings.FULLSCREEN, true);
		p.appConfig.setProperty(AppSettings.ALWAYS_ON_TOP, false);
	}
	
	/////////////////////////
	// INIT
	/////////////////////////
	
	protected void setupFirstFrame() {
		// main buffer & postFX buffer
		pgPost = PG.newPG(pg.width, pg.height);
		PG.setTextureRepeat(pg, true);
		PG.setTextureRepeat(pgPost, true);
		
		// feedback map
		map = PG.newPG(pg.width, pg.height);
		simplexNoise = new SimplexNoiseTexture(128, 128);
		
		// lines texture
		linesTexture = PG.newPG(pg.width, pg.height);
		gradientShader = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/cacheflowe-two-color-repeating-gradient.glsl"));

		buildUI();
	}
	
	protected void buildUI() {
		p.ui.addTitle("Feedback (Zoom/Rotate)");
		p.ui.addSlider(FEEDBACK_AMP, 0, 0.99f, 1.01f, 0.0001f);
		p.ui.addSlider(FEEDBACK_ROTATE, 0, -0.02f, 0.02f, 0.0001f);
		p.ui.addSlider(FEEDBACK_OFFSET_X, 0, -0.02f, 0.02f, 0.0001f);
		p.ui.addSlider(FEEDBACK_OFFSET_Y, 0, -0.02f, 0.02f, 0.0001f);
		
		p.ui.addTitle("Feedback (Map)");
		p.ui.addSlider(mapZoom, 2, 0.1f, 15, 0.1f, false);
		p.ui.addSlider(mapRot, 0, 0, P.TWO_PI, 0.01f, false);
		p.ui.addSlider(feedbackAmp, 0.001f, 0.00001f, 0.005f, 0.00001f, false);
		p.ui.addSlider(feedbackBrightStep, 0f, -0.01f, 0.01f, 0.0001f, false);
		p.ui.addSlider(feedbackAlphaStep, 0f, -0.01f, 0.01f, 0.0001f, false);
		p.ui.addSlider(feedbackRadiansStart, 0f, 0, P.TWO_PI, 0.01f, false);
		p.ui.addSlider(feedbackRadiansRange, P.TWO_PI * 2f, -P.TWO_PI * 2f, P.TWO_PI * 2f, 0.1f, false);
		p.ui.addSlider(FEEDBACK_ITERS, 1, 0, 10, 1f, false);
		
		p.ui.addTitle("Reaction/Diffusion");
		p.ui.addSlider(RD_ITERATIONS, 0, 0, 10, 1f);
		p.ui.addSlider(RD_BLUR_AMP_X, 0, 0, 6, 0.001f);
		p.ui.addSlider(RD_BLUR_AMP_Y, 0, 0, 6, 0.001f);
		p.ui.addSlider(RD_SHARPEN_AMP, 0, 0, 20, 0.01f);
		p.ui.addSlider(DARKEN_AMP, -10, -200, 200, 1f);
		
		p.ui.addTitle("Texture Blend");
		p.ui.addSlider(TEXTURE_BLEND, 0.01f, 0f, 1f, 0.01f);
		
		p.ui.addTitle("Fake Light Post FX");
		p.ui.addSlider(FAKE_LIGHT_AMBIENT, 2f, 0.3f, 6f, 0.01f);
		p.ui.addSlider(FAKE_LIGHT_GRAD_AMP, 0.66f, 0.1f, 6f, 0.01f);
		p.ui.addSlider(FAKE_LIGHT_GRAD_BLUR, 1f, 0.1f, 6f, 0.01f);
		p.ui.addSlider(FAKE_LIGHT_SPEC_AMP, 2.25f, 0.1f, 6f, 0.01f);
		p.ui.addSlider(FAKE_LIGHT_DIFF_DARK, 0.85f, 0.1f, 2f, 0.01f);
		
		p.ui.addTitle("More Post FX");
		p.ui.addSlider(FXAA_ACTIVE, 1, 0, 1, 1);

	}
	
	/////////////////////////
	// DRAW
	/////////////////////////
	
	protected void darkenCanvas() {
		if(p.ui.valueInt(RD_ITERATIONS) > 0) {
			BrightnessStepFilter.instance(p).setBrightnessStep(p.ui.valueEased(DARKEN_AMP)/255f);
			BrightnessStepFilter.instance(p).applyTo(pg);
		}
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
		RotateFilter.instance(p).setRotation(p.ui.valueEased(FEEDBACK_ROTATE));
		RotateFilter.instance(p).setZoom(p.ui.valueEased(FEEDBACK_AMP));
		RotateFilter.instance(p).setOffset(p.ui.valueEased(FEEDBACK_OFFSET_X), p.ui.valueEased(FEEDBACK_OFFSET_Y));
		RotateFilter.instance(p).applyTo(pg);
	}
	
	protected void applyRD() {
		for (int i = 0; i < p.ui.valueInt(RD_ITERATIONS); i++) {
			BlurHFilter.instance(p).setBlurByPercent(p.ui.valueEased(RD_BLUR_AMP_X), pg.width);
			BlurHFilter.instance(p).applyTo(pg);
			BlurVFilter.instance(p).setBlurByPercent(p.ui.valueEased(RD_BLUR_AMP_Y), pg.height);
			BlurVFilter.instance(p).applyTo(pg);
			SharpenFilter.instance(p).setSharpness(p.ui.valueEased(RD_SHARPEN_AMP));
			SharpenFilter.instance(p).applyTo(pg);
		}
		ThresholdFilter.instance(p).applyTo(pg);
	}
	
	protected void updateFeedbackMapNoise() {
		simplexNoise.update(
				p.ui.valueEased(mapZoom), 
				p.ui.valueEased(mapRot), 
				0, 
				0);
		ImageUtil.cropFillCopyImage(simplexNoise.texture(), map, true);
//		ImageUtil.cropFillCopyImage(simplexNoise.texture(), pgPost, true);
//		ImageUtil.cropFillCopyImage(simplexNoise.texture(), linesTexture, true);
	}

	protected void applyFeedback() {
		FeedbackMapFilter.instance(p).setMap(map);
		FeedbackMapFilter.instance(p).setAmp(p.ui.valueEased(feedbackAmp));
		FeedbackMapFilter.instance(p).setBrightnessStep(p.ui.valueEased(feedbackBrightStep));
		FeedbackMapFilter.instance(p).setAlphaStep(p.ui.valueEased(feedbackAlphaStep));
		FeedbackMapFilter.instance(p).setRadiansStart(p.ui.valueEased(feedbackRadiansStart));
		FeedbackMapFilter.instance(p).setRadiansRange(p.ui.valueEased(feedbackRadiansRange));
		for (int i = 0; i < p.ui.valueInt(FEEDBACK_ITERS); i++) FeedbackMapFilter.instance(p).applyTo(pg);
		
		// blur & threshold if R/D isn't going to do that for us
		if(p.ui.valueInt(RD_ITERATIONS) == 0) {
			BlurHFilter.instance(p).setBlurByPercent(p.ui.valueEased(RD_BLUR_AMP_X), pg.width);
			BlurHFilter.instance(p).applyTo(pg);
			BlurVFilter.instance(p).setBlurByPercent(p.ui.valueEased(RD_BLUR_AMP_Y), pg.height);
			BlurVFilter.instance(p).applyTo(pg);
			// does a similar thing to R/D
			ThresholdFilter.instance(p).applyTo(pg);
		}
	}
	
	protected void addBitmapSeed() {
//		if(p.frameCount % 200 == 0) {
		if(seedQueue) {
			// blend webcam
	//		pg.blendMode(PBlendModes.SCREEN);
	//		WebCam.instance().image();
			ImageUtil.drawImageCropFill(WebCam.instance().image(), pg, true);
			pg.blendMode(PBlendModes.BLEND);
			
			// desaturate completely
			SaturationFilter.instance(p).setSaturation(0);
			SaturationFilter.instance(p).applyTo(pg);
		}
		seedQueue = false;
	}
	
	protected void mixTexture() {
		BlendTowardsTexture.instance(p).setSourceTexture(linesTexture);
		BlendTowardsTexture.instance(p).setBlendLerp(p.ui.valueEased(TEXTURE_BLEND));
		BlendTowardsTexture.instance(p).applyTo(pg);
	}
	
	protected void updateLinesTexture() {
		// update other shader properties
		gradientShader.set("color1", 0f, 0f, 0f);
		gradientShader.set("color2", 1f, 1f, 1f);
		gradientShader.set("zoom", 40f + 20f * P.sin(p.loop.progressRads()));
		gradientShader.set("scrollY", loop.progress() * 4f);
		gradientShader.set("oscFreq", P.PI * 8f);
		gradientShader.set("oscAmp", 0.02f + 0.02f * P.sin(loop.progressRads()));
		gradientShader.set("fade", 0.1f);
		gradientShader.set("rotate", 0.15f);//p.loop.progressRads());
		linesTexture.filter(gradientShader);
	}
	
	public void drawApp() {
		p.background(0);
		
		// pre-draw
		updateFeedbackMapNoise();
		updateLinesTexture();
		
		// set context
		pg.beginDraw();
		if(p.frameCount <= 10 || clearScreen) pg.background(0);
		clearScreen = false;
		PG.setDrawCorner(pg);
		PG.setDrawFlat2d(pg, true);
		
		// fx steps
		addBitmapSeed();
		mixTexture();
		darkenCanvas();
		applyZoomRotate();
		applyFeedback();
		applyRD();
		
		// close context
		pg.endDraw();
		
		// copy to postFX buffer
		ImageUtil.copyImage(pg, pgPost);		// copy to 2nd buffer for postprocessing
//		setColorize();
		if(p.ui.valueInt(FXAA_ACTIVE) == 1) FXAAFilter.instance(p).applyTo(pgPost);
		setFakeLighting();
		
		// draw post to screen
		ImageUtil.cropFillCopyImage(pgPost, p.g, false);
	}
		
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'z') P.out(p.ui.valuesToJSON());
		if(p.key == ' ') clearScreen = true;
		if(p.key == 'c') seedQueue = true;
		if(p.key == 's') pgPost.save(FileUtil.getHaxademicOutputPath() + "_screenshots/" + SystemUtil.getTimestampFine() + ".png");
//		if(p.key == '1') p.ui.loadJSON(JSONObject.parse(CONFIG_1));
	}
	
}