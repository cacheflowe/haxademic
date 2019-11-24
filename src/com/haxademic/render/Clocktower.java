package com.haxademic.render;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeTwoColorsFilter;
import com.haxademic.core.draw.filters.pshader.FakeLightingFilter;
import com.haxademic.core.draw.filters.pshader.GradientCoverWipe;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.draw.filters.pshader.SharpenFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.LinearFloat;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import processing.data.JSONObject;

public class Clocktower 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// loop / mode
	protected int FRAMES = 600;
	protected int configIndex = 0;
	protected String[] CONFIGS = new String[]{CONFIG_6, CONFIG_3, CONFIG_1, CONFIG_2, CONFIG_3, CONFIG_4, CONFIG_5};
	
	// assets
	protected PImage tower;
	protected PShape template;
	protected WindowObj[] windows;
	
	// simulation views
	protected PImage simulationBg;
	protected PGraphics simulationPg;
	protected PImage simulationBg2;
	protected PGraphics simulationPg2;
	
	// animation
	protected int MAX_PARTICLES = 8000;
	ArrayList<Parti> partis = new ArrayList<Parti>();
	protected PGraphics pgPost;
	protected LinearFloat swipeProgress = new LinearFloat(0, 0.025f);

	
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
	protected String SHOW_SIMULATION = "SHOW_SIMULATION";
	
	protected String FAKE_LIGHT_AMBIENT = "FAKE_LIGHT_AMBIENT";
	protected String FAKE_LIGHT_GRAD_AMP = "FAKE_LIGHT_GRAD_AMP";
	protected String FAKE_LIGHT_GRAD_BLUR = "FAKE_LIGHT_GRAD_BLUR";
	protected String FAKE_LIGHT_SPEC_AMP = "FAKE_LIGHT_SPEC_AMP";
	protected String FAKE_LIGHT_DIFF_DARK = "FAKE_LIGHT_DIFF_DARK";

	protected String NUM_PARTICLES = "NUM_PARTICLES";

	protected String COLOR_1 = "COLOR_1";
	protected String COLOR_2 = "COLOR_2";

	protected String RENDER_MODE = "RENDER_MODE";
	protected String TOP_LEFT_FULLSIZE = "TOP_LEFT_FULLSIZE";

	// IDEAS:
	// - Reaction-Diffusion
	// - Moire lines via shader, with lerping color texture to override b&w
	// - displacement shader w/noise
	// - Particles attracted to windows that turn on/off as attractors
	// 		- Does glowing window act as a perlin map, as an alternate particle style?
	// - GPU points mesh that deforms based on windows
	// - Snakes that shoot around windows, making turns like TRON light cycles
	// - Feedback emitting from windows
	// - Window shapes expand, rotate and use exclusion mode to overlap and make cool patterns
	// 		- Window shapes emit as particles from windows, easing out and settling into place
	// 		- Window shapes draw many instances, alpha trailing off, but acting like tenticles
	// - Clocks/time concepts
	// 		- Segmented font display in a big grid to match windows - ASCII fluid? ASCII video?
	// - Mountain ideas - Denver stuff 
	
	

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1600);
		p.appConfig.setProperty(AppSettings.HEIGHT, 900);
//		p.appConfig.setProperty(AppSettings.WIDTH, 3438);
//		p.appConfig.setProperty(AppSettings.HEIGHT, 1080);
		p.appConfig.setProperty(AppSettings.FULLSCREEN, true);
//		p.appConfig.setProperty(AppSettings.SCREEN_X, 0);
//		p.appConfig.setProperty(AppSettings.SCREEN_Y, 0);
		p.appConfig.setProperty(AppSettings.ALWAYS_ON_TOP, false);
//		p.appConfig.setProperty(AppSettings.WIDTH, 638);
//		p.appConfig.setProperty(AppSettings.HEIGHT, 720);
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_IMAGE_SEQUENCE, false);
		p.appConfig.setProperty(AppSettings.RENDERING_IMAGE_SEQUENCE_START_FRAME, 1 + FRAMES * 1);
		p.appConfig.setProperty(AppSettings.RENDERING_IMAGE_SEQUENCE_STOP_FRAME, FRAMES * 7);
	}
	
	/////////////////////////
	// INIT
	/////////////////////////
	
	protected void setupFirstFrame() {
		pg = P.p.createGraphics(3438, 1080, PRenderers.P3D);
		pgPost = P.p.createGraphics(3438, 1080, PRenderers.P3D);
		PG.setTextureRepeat(pg, true);
		PG.setTextureRepeat(pgPost, true);
		
		tower = P.getImage("images/_sketch/clocktower/clocktower.png");
		template = p.loadShape( FileUtil.getFile("images/_sketch/clocktower/clocktower.svg"));
		windows = new WindowObj[template.getChildCount()];
		
		buildUI();
		buildWindows();
		buildParticles();
		buildSimulation();

//		if(imageSequenceRenderer != null) imageSequenceRenderer.setPG(pgPost);
		if(imageSequenceRenderer != null) imageSequenceRenderer.setPG(simulationPg2);
	}
	
	protected void buildUI() {
		p.ui.addSlider(SHOW_SIMULATION, 1, 0, 1, 1f);
		p.ui.addSlider(FEEDBACK_AMP, 0, 0.99f, 1.01f, 0.0001f);
		p.ui.addSlider(FEEDBACK_ROTATE, 0, -0.02f, 0.02f, 0.0001f);
		p.ui.addSlider(FEEDBACK_OFFSET_X, 0, -0.02f, 0.02f, 0.0001f);
		p.ui.addSlider(FEEDBACK_OFFSET_Y, 0, -0.02f, 0.02f, 0.0001f);
		p.ui.addSlider(DARKEN_AMP, -10, -255, 255, 1f);
		p.ui.addSlider(RD_ITERATIONS, 0, 0, 10, 1f);
		p.ui.addSlider(RD_BLUR_AMP_X, 0, 0, 6, 0.001f);
		p.ui.addSlider(RD_BLUR_AMP_Y, 0, 0, 6, 0.001f);
		p.ui.addSlider(RD_SHARPEN_AMP, 0, 0, 20, 0.01f);
		p.ui.addSlider(FAKE_LIGHT_AMBIENT, 2f, 0.3f, 6f, 0.01f);
		p.ui.addSlider(FAKE_LIGHT_GRAD_AMP, 0.66f, 0.1f, 6f, 0.01f);
		p.ui.addSlider(FAKE_LIGHT_GRAD_BLUR, 1f, 0.1f, 6f, 0.01f);
		p.ui.addSlider(FAKE_LIGHT_SPEC_AMP, 2.25f, 0.1f, 6f, 0.01f);
		p.ui.addSlider(FAKE_LIGHT_DIFF_DARK, 0.85f, 0.1f, 2f, 0.01f);
		p.ui.addSlider(NUM_PARTICLES, 0, 0, MAX_PARTICLES, 10);
		p.ui.addSliderVector(COLOR_1, 0, 0, 255, 1, false);
		p.ui.addSliderVector(COLOR_2, 255, 0, 255, 1, false);
		p.ui.addSlider(RENDER_MODE, 0, 0, 1, 1);
		p.ui.addSlider(TOP_LEFT_FULLSIZE, 0, 0, 1, 1);
		p.ui.addWebInterface(true);
	}
	
	protected void buildWindows() {
		for (int j = 0; j < windows.length; j++) {
			windows[j] = new WindowObj(template.getChildren()[j]);
		}
	}
	
	protected void buildParticles() {
		for (int i = 0; i < MAX_PARTICLES; i++) {
			partis.add(new Parti());
		}
	}

	protected void buildSimulation() {
		simulationBg = P.getImage("images/_sketch/clocktower/clocktower-photo.jpg");
		simulationPg = P.p.createGraphics(simulationBg.width, simulationBg.height, PRenderers.P3D);
		simulationBg2 = P.getImage("images/_sketch/clocktower/clocktower-photo-2.png");
		simulationPg2 = P.p.createGraphics(simulationBg2.width, simulationBg2.height, PRenderers.P3D);
	}
	
	/////////////////////////
	// DRAW
	/////////////////////////
	
	protected void swipeForNextMode() {
		swipeProgress.setCurrent(0);
		swipeProgress.setTarget(1);	
	}
	
	protected void newMode() {
		// next mode
		configIndex++;
		if(configIndex >= CONFIGS.length) configIndex = 0;
		p.ui.loadValuesFromJSON(JSONObject.parse(CONFIGS[configIndex]));
//		p.ui.get(SHOW_SIMULATION).set(1);
	}
	
	protected void updateSwipe() {
		swipeProgress.update();
		GradientCoverWipe.instance(p).setColorTop(0f, 0f, 0f, 1f);
		GradientCoverWipe.instance(p).setColorBot(1f, 1f, 1f, 1f);
		GradientCoverWipe.instance(p).setProgress(swipeProgress.value());
		GradientCoverWipe.instance(p).setGradientEdge(1f);
		GradientCoverWipe.instance(p).applyTo(pg);
	}
	
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
		ColorizeTwoColorsFilter.instance(p).setColor1(p.ui.valueX(COLOR_1)/255f, p.ui.valueY(COLOR_1)/255f, p.ui.valueZ(COLOR_1)/255f);
		ColorizeTwoColorsFilter.instance(p).setColor2(p.ui.valueX(COLOR_2)/255f, p.ui.valueY(COLOR_2)/255f, p.ui.valueZ(COLOR_2)/255f);
		ColorizeTwoColorsFilter.instance(p).applyTo(pgPost);
	}
	
	protected void updateWindows() {
		for (int j = 0; j < windows.length; j++) {
			if(windows[j] != null) windows[j].draw(j);
		}
	}
	
	protected void updateParticles() {
		pg.stroke(255);
		pg.strokeWeight(3);
//		pg.blendMode(PBlendModes.EXCLUSION);
		for (int i = 0; i < p.ui.value(NUM_PARTICLES); i++) {
			partis.get(i).update();
		}
		pg.blendMode(PBlendModes.BLEND);
	}
	
	protected void drawTemplateOverlay(PGraphics pg) {
		pg.beginDraw();
		pg.blendMode(PBlendModes.BLEND);
		PG.resetPImageAlpha(pg);
		PG.setDrawCorner(pg);
		pg.shape(template, 0, 0);
		pg.endDraw();
	}
	
	protected void overlayTowerTexture() {
		PG.setPImageAlpha(p, 0.25f);
		ImageUtil.drawImageCropFill(tower, pgPost, false);
		PG.setPImageAlpha(p, 1f);
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
	
	protected void showSimulation() {
		if(p.ui.valueInt(SHOW_SIMULATION) == 1) {
			// draw simulation 1
			simulationPg.beginDraw();
			ImageUtil.copyImage(simulationBg, simulationPg);
			simulationPg.blendMode(PBlendModes.ADD);
			ImageUtil.drawTextureMappedRect(simulationPg, pgPost, 10, 20, 232, 565, 233, 303, 310, 305, 312, 562);
			simulationPg.blendMode(PBlendModes.BLEND);
			simulationPg.endDraw();
			p.image(simulationPg, 0, 0);
	
			// draw simulation 2
			simulationPg2.beginDraw();
			ImageUtil.copyImage(simulationBg2, simulationPg2);
			simulationPg2.blendMode(PBlendModes.ADD);
			ImageUtil.drawTextureMappedRect(simulationPg2, pgPost, 10, 20, 257, 560, 255, 81, 399, 86, 398, 552);
			simulationPg2.blendMode(PBlendModes.BLEND);
			simulationPg2.endDraw();
			p.image(simulationPg2, simulationPg.width, 0);
//			p.image(simulationPg2, 0, 0);
			p.image(simulationPg2, 0, 0, p.width, p.height);
		}
	}
	
	public void drawApp() {
		p.background(0);
		if(p.ui.valueInt(RENDER_MODE) == 1) {
			if(p.loop.loopCurFrame() == 1) newMode();
			if(p.loop.loopCurFrame() == FRAMES - 20) swipeForNextMode();
		}
		
		// set context
		pg.beginDraw();
		if(p.frameCount <= 10) pg.background(0);
		PG.setDrawCorner(pg);
		PG.setDrawFlat2d(pg, true);
		
		darkenCanvas();
		updateWindows();
		updateParticles();
		pg.endDraw();
		applyZoomRotate();
		applyRD();
		updateSwipe();
		
		ImageUtil.copyImage(pg, pgPost);		// copy to 2nd buffer for postprocessing
		setColorize();
		setFakeLighting();
		overlayTowerTexture();
		drawTemplateOverlay(pgPost);
		if(p.ui.valueInt(TOP_LEFT_FULLSIZE) == 1) {
			p.image(pgPost, 0, 0);
		} else {
			ImageUtil.cropFillCopyImage(pgPost, p.g, false);
		}
		showSimulation();
	}
		
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 's') P.out(p.ui.valuesToJSON());
		if(p.key == '1') p.ui.loadValuesFromJSON(JSONObject.parse(CONFIG_1));
		if(p.key == '2') p.ui.loadValuesFromJSON(JSONObject.parse(CONFIG_2));
		if(p.key == '3') p.ui.loadValuesFromJSON(JSONObject.parse(CONFIG_3));
		if(p.key == '4') p.ui.loadValuesFromJSON(JSONObject.parse(CONFIG_4));
		if(p.key == '5') p.ui.loadValuesFromJSON(JSONObject.parse(CONFIG_5));
		if(p.key == '6') p.ui.loadValuesFromJSON(JSONObject.parse(CONFIG_6));
		if(p.key == ' ') swipeForNextMode();
	}
	
	////////////////////////////////////
	// Window class
	////////////////////////////////////

	public class WindowObj {
		
		protected PShape windowShape;
		public PVector position;
		
		public WindowObj(PShape shape) {
			// get size * position of original shape
			float[] extents = {0,0,0,0,0,0};
			PShapeUtil.getShapeExtents(shape, extents);
			float centerX = (extents[0] + extents[1]) / 2f;
			float centerY = (extents[2] + extents[3]) / 2f;
			position = new PVector(centerX, centerY);
			
			// rebuild centered shape
			windowShape = PShapeUtil.clonePShape(p, shape.getTessellation());
			windowShape.setStroke(false);
			windowShape.disableStyle();
			PShapeUtil.centerShape(windowShape);
		}
		
		public void draw(int index) {
			// draw oscillated shape
			PG.setDrawCorner(pg);
			pg.pushMatrix();
			float newScale = 1.15f;// + 0.1f * P.sin(index + p.loop.progressRads() * 2f);
			pg.noStroke();
			pg.fill(
					127 + 127f * P.sin(index + p.loop.progressRads()), 
					127 + 127f * P.sin(index + 1 + p.loop.progressRads()), 
					127 + 127f * P.sin(index + 2 + p.loop.progressRads()));
			
			pg.fill(255);
			pg.translate(position.x, position.y);
			pg.shape(windowShape, 0, 0, windowShape.width * newScale, windowShape.height * newScale);
			pg.popMatrix();
			
//			pg.blendMode(PBlendModes.ADD);
			
			// draw outline
			PG.setDrawCenter(pg);
			pg.pushMatrix();
			pg.noFill();
			pg.stroke(255, 127);
			pg.strokeWeight(3);
			pg.translate(position.x, position.y);
			int numRects = P.round(10 + 10 * P.sin(P.PI + index + p.loop.progressRads()));
			for (int i = 0; i < numRects; i++) {
				float scaleUp = 1 + i * 0.2f + 0.15f * P.sin(index + p.loop.progressRads());
				// pg.rect(0, 0, windowShape.width * scaleUp, windowShape.height * scaleUp);
			}
			pg.popMatrix();
		}
	}
	
	////////////////////////////////////
	// Window class
	////////////////////////////////////
	
	public class Parti {
		
		public PVector pos = new PVector();
		public PVector lastPos = new PVector();
		public EasingFloat rads = new EasingFloat(0, MathUtil.randRangeDecimal(6f, 12f));
		public float speed = MathUtil.randRangeDecimal(8f, 20f);
		public WindowObj targetObj;
		
		public Parti() {
			rads.setCurrent(MathUtil.randRangeDecimal(0, P.TWO_PI));
			rads.setTarget(rads.value());
			pos.set(MathUtil.randRangeDecimal(0, pg.width), MathUtil.randRangeDecimal(0, pg.height));
			
			newTarget();
		}
		
		protected void newTarget() {

			// totally random index
			int windowIndex = MathUtil.randRange(0, windows.length - 1);
			// noise-based target
//			int windowIndex = P.round(windows.length * (-0.25f + 1.5f * p.noise(p.frameCount * 0.01f)));
			// go to each window in sequence
//			int windowIndex = P.round(windows.length/2 + windows.length/2 * MathUtil.saw(p.frameCount * 0.014f));
			
			// p.debugView.setValue("windowIndex", windowIndex);
			windowIndex = P.constrain(windowIndex, 0, windows.length - 1);
			targetObj = windows[windowIndex];
		}
		
		public void update() {
			lastPos.set(pos);
			if(targetObj != null) {
				if(pos.dist(targetObj.position) < 150) newTarget();
			} else {
				newTarget();
			}
			
			// loop rads for easing
			// float radsToTarget = MathUtil.getRadiansToTarget(pos.x, pos.y, p.mousePercentX() * pg.width, p.mousePercentY() * pg.height);
			float radsToTarget = MathUtil.getRadiansToTarget(pos.x, pos.y, targetObj.position.x, targetObj.position.y);

			// wrap target around
			float radsDiff = radsToTarget - rads.value();
			if(radsDiff > P.PI) radsToTarget -= P.TWO_PI;
			if(radsDiff < -P.PI) radsToTarget += P.TWO_PI;
		    rads.setTarget(radsToTarget);
		    rads.update();
		    // if lerp update wraps around, loop that
		    if(rads.value() > P.TWO_PI) rads.setCurrent(rads.value() - P.TWO_PI);
		    if(rads.value() < 0) rads.setCurrent(rads.value() + P.TWO_PI);
			float moveRads = rads.value();
			pos.add(speed * P.cos(moveRads), speed * P.sin(moveRads));
			
			pg.pushMatrix();
//			pg.translate(pos.x, pos.y);
//			pg.rotate(-rads.value());
//			pg.rect(0, 0, 8, 5);
			pg.line(lastPos.x, lastPos.y, pos.x, pos.y);
			pg.popMatrix();
		}
		
	}
	
	protected static String CONFIG_1 = "{\r\n" + 
			"  \"FEEDBACK_OFFSET_X\": 0.0010000037727877498,\r\n" + 
			"  \"FAKE_LIGHT_SPEC_AMP\": 2.0500032901763916,\r\n" + 
			"  \"NUM_PARTICLES\": 40,\r\n" + 
			"  \"FEEDBACK_OFFSET_Y\": 0,\r\n" + 
			"  \"RD_ITERATIONS\": 1,\r\n" + 
			"  \"RD_BLUR_AMP_Y\": 1.70099937915802,\r\n" + 
			"  \"DARKEN_AMP\": -77,\r\n" + 
			"  \"RD_BLUR_AMP_X\": 0.9940021634101868,\r\n" + 
			"  \"RD_SHARPEN_AMP\": 9.760029792785645,\r\n" + 
			"  \"SHOW_SIMULATION\": 0,\r\n" + 
			"  \"FAKE_LIGHT_GRAD_AMP\": 2.299994707107544,\r\n" + 
			"  \"FEEDBACK_ROTATE\": 0,\r\n" + 
			"  \"FAKE_LIGHT_DIFF_DARK\": 0.6300005912780762,\r\n" + 
			"  \"FAKE_LIGHT_GRAD_BLUR\": 2.7999982833862305,\r\n" + 
			"  \"FAKE_LIGHT_AMBIENT\": 2.3999996185302734,\r\n" + 
			"  \"FEEDBACK_AMP\": 0.9992989301681519\r\n" + 
			"}";

	protected static String CONFIG_2 = "{\r\n" + 
			"  \"FEEDBACK_OFFSET_X\": 0,\r\n" + 
			"  \"FAKE_LIGHT_SPEC_AMP\": 2.0500032901763916,\r\n" + 
			"  \"NUM_PARTICLES\": 0,\r\n" + 
			"  \"FEEDBACK_OFFSET_Y\": 0,\r\n" + 
			"  \"RD_ITERATIONS\": 3,\r\n" + 
			"  \"RD_BLUR_AMP_Y\": 1.70099937915802,\r\n" + 
			"  \"DARKEN_AMP\": 95,\r\n" + 
			"  \"RD_BLUR_AMP_X\": 0.9220037460327148,\r\n" + 
			"  \"RD_SHARPEN_AMP\": 9.760029792785645,\r\n" + 
			"  \"SHOW_SIMULATION\": 0,\r\n" + 
			"  \"FAKE_LIGHT_GRAD_AMP\": 2.299994707107544,\r\n" + 
			"  \"FEEDBACK_ROTATE\": 0.0026000109501183033,\r\n" + 
			"  \"FAKE_LIGHT_DIFF_DARK\": 0.6300005912780762,\r\n" + 
			"  \"FAKE_LIGHT_GRAD_BLUR\": 2.7999982833862305,\r\n" + 
			"  \"FAKE_LIGHT_AMBIENT\": 2.3999996185302734,\r\n" + 
			"  \"FEEDBACK_AMP\": 1.0000990629196167\r\n" + 
			"}";
	
	protected static String CONFIG_3 = "{\r\n" + 
			"  \"FEEDBACK_OFFSET_X\": 0,\r\n" + 
			"  \"FAKE_LIGHT_SPEC_AMP\": 2.0500032901763916,\r\n" + 
			"  \"NUM_PARTICLES\": 0,\r\n" + 
			"  \"FEEDBACK_OFFSET_Y\": 0,\r\n" + 
			"  \"RD_ITERATIONS\": 2,\r\n" + 
			"  \"RD_BLUR_AMP_Y\": 1.7000007629394531,\r\n" + 
			"  \"DARKEN_AMP\": -138,\r\n" + 
			"  \"RD_BLUR_AMP_X\": 1.0540040731430054,\r\n" + 
			"  \"RD_SHARPEN_AMP\": 13.620043754577637,\r\n" + 
			"  \"SHOW_SIMULATION\": 0,\r\n" + 
			"  \"FAKE_LIGHT_GRAD_AMP\": 2.299994707107544,\r\n" + 
			"  \"FEEDBACK_ROTATE\": 0,\r\n" + 
			"  \"FAKE_LIGHT_DIFF_DARK\": 0.6300005912780762,\r\n" + 
			"  \"FAKE_LIGHT_GRAD_BLUR\": 2.7999982833862305,\r\n" + 
			"  \"FAKE_LIGHT_AMBIENT\": 2.3999996185302734,\r\n" + 
			"  \"FEEDBACK_AMP\": 0.9991989135742188\r\n" + 
			"}";
	
	protected static String CONFIG_4 = "{\r\n" + 
			"  \"FEEDBACK_OFFSET_X\": 0,\r\n" + 
			"  \"FAKE_LIGHT_SPEC_AMP\": 2.0500032901763916,\r\n" + 
			"  \"NUM_PARTICLES\": 0,\r\n" + 
			"  \"FEEDBACK_OFFSET_Y\": 0,\r\n" + 
			"  \"RD_ITERATIONS\": 2,\r\n" + 
			"  \"RD_BLUR_AMP_Y\": 2.4900002479553223,\r\n" + 
			"  \"DARKEN_AMP\": -138,\r\n" + 
			"  \"RD_BLUR_AMP_X\": 1.2750041484832764,\r\n" + 
			"  \"RD_SHARPEN_AMP\": 19.550003051757812,\r\n" + 
			"  \"SHOW_SIMULATION\": 0,\r\n" + 
			"  \"FAKE_LIGHT_GRAD_AMP\": 2.299994707107544,\r\n" + 
			"  \"FEEDBACK_ROTATE\": 0,\r\n" + 
			"  \"FAKE_LIGHT_DIFF_DARK\": 0.6300005912780762,\r\n" + 
			"  \"FAKE_LIGHT_GRAD_BLUR\": 2.7999982833862305,\r\n" + 
			"  \"FAKE_LIGHT_AMBIENT\": 2.3999996185302734,\r\n" + 
			"  \"FEEDBACK_AMP\": 1.0001990795135498\r\n" + 
			"}";
	
	protected static String CONFIG_5 = "{\r\n" + 
			"  \"FEEDBACK_OFFSET_X\": 0,\r\n" + 
			"  \"FAKE_LIGHT_SPEC_AMP\": 2.0500032901763916,\r\n" + 
			"  \"NUM_PARTICLES\": 120,\r\n" + 
			"  \"FEEDBACK_OFFSET_Y\": 0,\r\n" + 
			"  \"RD_ITERATIONS\": 2,\r\n" + 
			"  \"RD_BLUR_AMP_Y\": 1.6199991703033447,\r\n" + 
			"  \"DARKEN_AMP\": -195,\r\n" + 
			"  \"RD_BLUR_AMP_X\": 0.691003143787384,\r\n" + 
			"  \"RD_SHARPEN_AMP\": 6.180019855499268,\r\n" + 
			"  \"SHOW_SIMULATION\": 0,\r\n" + 
			"  \"FAKE_LIGHT_GRAD_AMP\": 2.299994707107544,\r\n" + 
			"  \"FEEDBACK_ROTATE\": 0,\r\n" + 
			"  \"FAKE_LIGHT_DIFF_DARK\": 0.6300005912780762,\r\n" + 
			"  \"FAKE_LIGHT_GRAD_BLUR\": 2.7999982833862305,\r\n" + 
			"  \"FAKE_LIGHT_AMBIENT\": 2.3999996185302734,\r\n" + 
			"  \"FEEDBACK_AMP\": 0.9983987808227539\r\n" + 
			"}";
	
	protected static String CONFIG_6 = "{\r\n" + 
			"  \"FEEDBACK_OFFSET_X\": 0,\r\n" + 
			"  \"FAKE_LIGHT_SPEC_AMP\": 2.0500032901763916,\r\n" + 
			"  \"NUM_PARTICLES\": 0,\r\n" + 
			"  \"FEEDBACK_OFFSET_Y\": 0,\r\n" + 
			"  \"RD_ITERATIONS\": 10,\r\n" + 
			"  \"RD_BLUR_AMP_Y\": 0.8319985866546631,\r\n" + 
			"  \"DARKEN_AMP\": -127,\r\n" + 
			"  \"RD_BLUR_AMP_X\": 0.7600032091140747,\r\n" + 
			"  \"RD_SHARPEN_AMP\": 4.560054779052734,\r\n" + 
			"  \"SHOW_SIMULATION\": 0,\r\n" + 
			"  \"FAKE_LIGHT_GRAD_AMP\": 2.299994707107544,\r\n" + 
			"  \"FEEDBACK_ROTATE\": 0,\r\n" + 
			"  \"FAKE_LIGHT_DIFF_DARK\": 0.6300005912780762,\r\n" + 
			"  \"FAKE_LIGHT_GRAD_BLUR\": 2.7999982833862305,\r\n" + 
			"  \"FAKE_LIGHT_AMBIENT\": 2.3999996185302734,\r\n" + 
			"  \"FEEDBACK_AMP\": 0.9985998868942261\r\n" + 
			"}";

	
}