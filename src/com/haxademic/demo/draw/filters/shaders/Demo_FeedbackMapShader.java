package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
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

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class Demo_FeedbackMapShader
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float frames = 80;
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

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, W );
		p.appConfig.setProperty( AppSettings.HEIGHT, H );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, P.round(1 + frames * 3) );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, P.round(1 + frames * 4) );
	}

	public void setupFirstFrame() {


		map = PG.newPG(W, H);

		xShape = DemoAssets.shapeX().getTessellation();
		PShapeUtil.centerShape(xShape);
		PShapeUtil.scaleShapeToMaxAbsY(xShape, p.height * 0.35f);

		simplexNoise = new SimplexNoiseTexture(128, 128);
		audioTexture = new TextureEQGrid(128, 128);

		textureShader = new TextureShader(TextureShader.bw_voronoi);
		textureShader = new TextureShader(TextureShader.bw_clouds);
		
		p.ui.addSlider(mapZoom, 2, 0.1f, 15, 0.1f, false);
		p.ui.addSlider(mapRot, 0, 0, P.TWO_PI, 0.01f, false);
		p.ui.addSlider(feedbackAmp, 0.001f, 0.00001f, 0.005f, 0.00001f, false);
		p.ui.addSlider(feedbackBrightStep, 0f, -0.01f, 0.01f, 0.0001f, false);
		p.ui.addSlider(feedbackAlphaStep, 0f, -0.01f, 0.01f, 0.0001f, false);
		p.ui.addSlider(feedbackRadiansStart, 0f, 0, P.TWO_PI, 0.01f, false);
		p.ui.addSlider(feedbackRadiansRange, P.TWO_PI * 2f, -P.TWO_PI * 2f, P.TWO_PI * 2f, 0.1f, false);

		p.ui.addSlider(FEEDBACK_ITERS, 1, 0, 10, 1f, false);
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
		pg.fill(127f + 127f * P.sin(progressRads * 3f));
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
		ReflectFilter.instance(p).applyTo(audioTexture.texture());
		map = audioTexture.texture();
	}

	protected void updateMapPerlin() {
		simplexNoise.update(
				p.ui.value(mapZoom), 
				p.ui.value(mapRot), 
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
		FeedbackMapFilter.instance(p).setMap(map);
		FeedbackMapFilter.instance(p).setAmp(p.ui.value(feedbackAmp));
		FeedbackMapFilter.instance(p).setBrightnessStep(p.ui.value(feedbackBrightStep));
		FeedbackMapFilter.instance(p).setAlphaStep(p.ui.value(feedbackAlphaStep));
		FeedbackMapFilter.instance(p).setRadiansStart(p.ui.value(feedbackRadiansStart));
		FeedbackMapFilter.instance(p).setRadiansRange(p.ui.value(feedbackRadiansRange));
		for (int i = 0; i < p.ui.valueInt(FEEDBACK_ITERS); i++) FeedbackMapFilter.instance(p).applyTo(pg);
	}

	protected void blurMap() {
		BlurProcessingFilter.instance(p).setBlurSize(10);
		for(int i=0; i < 5; i++) BlurProcessingFilter.instance(p).applyTo(map);
//		MirrorFilter.instance(p).applyTo(map);
	}

	public void drawApp() {
		progress = (p.frameCount % frames) / frames;
		progressRads = progress * P.TWO_PI;
		p.debugView.setValue("progress", progress);

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
		p.debugView.setTexture("buffer", pg);
		p.debugView.setTexture("map", map);
	}
}
