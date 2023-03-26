package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.FeedbackMapFilter;
import com.haxademic.core.draw.filters.pshader.ReflectFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.draw.textures.pgraphics.TextureEQGrid;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class Demo_FeedbackMapShader
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float frames = 240;
	protected float progress = 0;
	protected float progressRads = 0;
	protected int W = 800;
	protected int H = 800;
	protected PGraphics map;
	protected PShape xShape;
	protected SimplexNoiseTexture simplexNoise;
	protected BaseTexture audioTexture;
	protected TextureShader textureShader;
	
	protected String mapZoom = "mapZoom";
	protected String mapRot = "mapRot";
	protected String feedbackAmp = "feedbackAmp";
	protected String feedbackBrightStep = "feedbackBrightStep";
	protected String feedbackAlphaStep = "feedbackAlphaStep";
	protected String feedbackRadiansStart = "feedbackRadiansStart";
	protected String feedbackRadiansRange = "feedbackRadiansRange";

	protected String FEEDBACK_ITERS = "FEEDBACK_ITERS";

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, W );
		Config.setProperty( AppSettings.HEIGHT, H );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, P.round(1 + frames * 3) );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, P.round(1 + frames * 4) );
	}

	protected void firstFrame() {
		pg = PG.newPG32(pg.width, pg.height, false, false);

		map = PG.newPG(W, H);

		xShape = DemoAssets.shapeX().getTessellation();
		PShapeUtil.centerShape(xShape);
		PShapeUtil.scaleShapeToMaxAbsY(xShape, p.height * 0.35f);

		simplexNoise = new SimplexNoiseTexture(128, 128, true);
		audioTexture = new TextureEQGrid(128, 128);

		textureShader = new TextureShader(TextureShader.bw_voronoi);
		textureShader = new TextureShader(TextureShader.bw_clouds);
		
		UI.addSlider(mapZoom, 2, 0.1f, 15, 0.1f, false);
		UI.addSlider(mapRot, 0, 0, P.TWO_PI, 0.01f, false);
		UI.addSlider(feedbackAmp, 0.001f, 0.00001f, 0.005f, 0.00001f, false);
		UI.addSlider(feedbackBrightStep, 0f, -0.01f, 0.01f, 0.0001f, false);
		UI.addSlider(feedbackAlphaStep, 0f, -0.01f, 0.01f, 0.0001f, false);
		UI.addSlider(feedbackRadiansStart, 0f, 0, P.TWO_PI, 0.01f, false);
		UI.addSlider(feedbackRadiansRange, P.TWO_PI * 2f, -P.TWO_PI * 2f, P.TWO_PI * 2f, 0.1f, false);

		UI.addSlider(FEEDBACK_ITERS, 1, 0, 10, 1f, false);
	}

	protected void drawImg(PImage img) {
		if(img != null) {
			pg.beginDraw();
//			PG.setPImageAlpha(buffer, 0.5f);
			PG.setDrawCenter(pg);
			pg.tint(
					300 + 155 * P.sin(p.frameCount/50f),
					300 + 155 * P.sin(p.frameCount/80f),
					300 + 155 * P.sin(p.frameCount/90f),
					10);
			float scaleHeight = MathUtil.scaleToTarget(img.height, pg.height);
			pg.image(img, pg.width/2, pg.height/2, img.width * scaleHeight, img.height * scaleHeight);
			pg.endDraw();
		}
	}

	protected void drawXShape(boolean black) {
		pg.beginDraw();
		xShape.disableStyle();
		pg.fill(127f + 127f * P.sin(progressRads * 1f));
		if(black) {
			pg.fill(0);
//			pg.fill(MathUtil.randRange(0, 255), MathUtil.randRange(0, 255), MathUtil.randRange(0, 255));
		}
		pg.translate(pg.width/2, pg.height/2);
		pg.rotate(progressRads * 0.25f);
		pg.shape(xShape);
		pg.endDraw();
	}

	public void keyPressed() {
		super.keyPressed();
		if(key == ' ') drawXShape(true);
	}

	protected void updateMapAudio() {
		audioTexture.update();
		ReflectFilter.instance().applyTo(audioTexture.texture());
		map = audioTexture.texture();
	}

	protected void updateMapPerlin() {
		simplexNoise.update(
				UI.value(mapZoom), 
				UI.value(mapRot), 
				0, 
				0);
		ImageUtil.cropFillCopyImage(simplexNoise.texture(), map, true);
	}

	protected void updateMapWebcam() {
		map.beginDraw();
		ImageUtil.cropFillCopyImage(WebCam.instance().image(), map, true);
		map.endDraw();
	}

	protected void updateMapShader() {
		if(textureShader.shaderPath().equals(TextureShader.bw_voronoi)) textureShader.setTime(progressRads);
		else textureShader.updateTime();
		map.filter(textureShader.shader());
	}

	protected void applyFeedbackToBuffer() {
		FeedbackMapFilter.instance().setMap(map);
		FeedbackMapFilter.instance().setAmp(UI.value(feedbackAmp));
		FeedbackMapFilter.instance().setBrightnessStep(UI.value(feedbackBrightStep));
		FeedbackMapFilter.instance().setAlphaStep(UI.value(feedbackAlphaStep));
		FeedbackMapFilter.instance().setRadiansStart(UI.value(feedbackRadiansStart));
		FeedbackMapFilter.instance().setRadiansRange(UI.value(feedbackRadiansRange));
		for (int i = 0; i < UI.valueInt(FEEDBACK_ITERS); i++) FeedbackMapFilter.instance().applyTo(pg);
	}

	protected void blurMap() {
		BlurProcessingFilter.instance().setBlurSize(10);
		for(int i=0; i < 5; i++) BlurProcessingFilter.instance().applyTo(map);
//		MirrorFilter.instance().applyTo(map);
	}

	protected void drawApp() {
		progress = (p.frameCount % frames) / frames;
		progressRads = progress * P.TWO_PI;
		DebugView.setValue("progress", progress);

		background(255,0,0);

		// draw on top of image
//		drawXShape(false);
//		if(p.frameCount % 60 == 0) 
			drawXShape(false);

		// draw map
		updateMapPerlin();
//		updateMapAudio();
//		updateMapWebcam();
//		updateMapShader();

		// blur the map
		blurMap();

		// apply feedback
		applyFeedbackToBuffer();

		// draw again to see the full image on top
//		drawXShape(true);

		// draw to screen
		p.image(pg, 0, 0);

		// debug draw
		DebugView.setTexture("buffer", pg);
		DebugView.setTexture("map", map);
	}
}
