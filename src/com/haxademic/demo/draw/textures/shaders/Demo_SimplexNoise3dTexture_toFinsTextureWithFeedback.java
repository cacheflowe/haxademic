package com.haxademic.demo.draw.textures.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.ColorizeFromTexture;
import com.haxademic.core.draw.filters.pshader.ColorizeTwoColorsFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.FeedbackMapFilter;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.textures.SimplexNoise3dTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PShape;

public class Demo_SimplexNoise3dTexture_toFinsTextureWithFeedback 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	
	// noise
	protected SimplexNoise3dTexture noiseTexture;
	protected String ZOOM = "ZOOM";
	protected String ROTATION = "ROTATION";
	protected String OFFSET_X = "OFFSET_X";
	protected String OFFSET_Y = "OFFSET_Y";
	protected String OFFSET_Z = "OFFSET_Z";
	protected String FRACTAL_MODE = "FRACTAL_MODE";
	protected String X_REPEAT_MODE = "X_REPEAT_MODE";

	// feedback
	protected String feedbackAmp = "feedbackAmp";
	protected String feedbackBrightStep = "feedbackBrightStep";
	protected String feedbackAlphaStep = "feedbackAlphaStep";
	protected String feedbackRadiansStart = "feedbackRadiansStart";
	protected String feedbackRadiansRange = "feedbackRadiansRange";
	protected String FEEDBACK_ITERS = "FEEDBACK_ITERS";

	// mix in to texture
	protected String BLEND_LERP = "BLEND_LERP";
	
	// cylinder graphics
	protected PGraphics texture;

	// shape
	protected String ROT_X = "ROT_X";
	protected String ROT_Y = "ROT_Y";
	protected String INNER_RADIUS = "INNER_RADIUS";

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1024 );
		Config.setProperty( AppSettings.HEIGHT, 1024 );
		Config.setProperty(AppSettings.SHOW_UI, true);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 3000);
	}
	
	protected void firstFrame() {
		// come up with cylinder texture size
		int texH = 300;
		int texW = P.round(texH * P.TWO_PI);
		
		// init noise controls
		UI.addTitle("Noise controls");
		UI.addSlider(ZOOM, 1f, 0.01f, 20f, 0.01f, false);
		UI.addSlider(ROTATION, 0f, 0, P.TWO_PI, 0.01f, false);
		UI.addSlider(OFFSET_X, 0f, -100f, 100f, 0.01f, false);
		UI.addSlider(OFFSET_Y, 0f, -100f, 100f, 0.01f, false);
		UI.addSlider(OFFSET_Z, 0f, -100f, 100f, 0.01f, false);
		UI.addToggle(FRACTAL_MODE, false, false);
		UI.addToggle(X_REPEAT_MODE, true, false);

		// init noise object
//		TextureShader.HOT_SWAP = true;
		noiseTexture = new SimplexNoise3dTexture(P.round(100 * P.TWO_PI), 100, true);
		noiseTexture.update(0.07f, 0, 0, 0, 0, false, true);
		DebugView.setTexture("noiseTexture.texture()", noiseTexture.texture());
		
		// build display texture
		texture = PG.newPG32(texW, texH, true, false);
		UI.addSlider(BLEND_LERP, 0.01f, 0, 1, 0.001f, false);
		DebugView.setTexture("texture", texture);
		
		// feedback
		UI.addSlider(feedbackAmp, 0.001f, 0.00001f, 0.005f, 0.00001f, false);
		UI.addSlider(feedbackBrightStep, 0f, -0.01f, 0.01f, 0.0001f, false);
		UI.addSlider(feedbackAlphaStep, 0f, -0.01f, 0.01f, 0.0001f, false);
		UI.addSlider(feedbackRadiansStart, 0f, 0, P.TWO_PI, 0.01f, false);
		UI.addSlider(feedbackRadiansRange, P.TWO_PI * 2f, -P.TWO_PI * 2f, P.TWO_PI * 2f, 0.1f, false);
		UI.addSlider(FEEDBACK_ITERS, 1, 0, 10, 1f, false);

		// init cylinder
		UI.addTitle("Shape controls");
		UI.addSlider(ROT_X, -0.5f, -P.TWO_PI, P.TWO_PI, 0.01f, false);
		UI.addSlider(ROT_Y, 0f, -P.TWO_PI, P.TWO_PI, 0.01f, false);
		UI.addSlider(INNER_RADIUS, 0.15f, 0.01f, 1f, 0.01f, false);
		
		shape = Shapes.createCan(p.width * 0.25f, p.height * 0.35f, 1000); 
		shape.setTexture(noiseTexture.texture());
		shape.setTexture(texture);
	}

	protected void drawApp() {
		background(30);
		
		// automate UI sliders
		UI.setValue(ZOOM, FrameLoop.osc(0.001f, 1f, 5f));
		UI.setValue(feedbackAmp, FrameLoop.osc(0.02f, 0.0001f, 0.0005f));
		UI.setValue(feedbackBrightStep, FrameLoop.osc(0.02f, -0.002f, -0.0001f));
//		UI.setValue(OFFSET_X, FrameLoop.count(-0.02f));
//		UI.setValue(OFFSET_Y, FrameLoop.osc(0.01f, 1f, -1f));
		UI.setValue(OFFSET_Z, FrameLoop.osc(0.003f, -1f, 1f));
//		UI.setValue(ROT_Y, FrameLoop.count(0.0075f));
//		UI.setValue(ROT_X, FrameLoop.osc(0.01f, -0.6f, -0.3f));

		
		// update perlin texture
		noiseTexture.update(
				UI.valueEased(ZOOM),
				UI.valueEased(ROTATION),
				UI.valueEased(OFFSET_X),
				UI.valueEased(OFFSET_Y),
				UI.valueEased(OFFSET_Z),
				UI.valueToggle(FRACTAL_MODE),
				UI.valueToggle(X_REPEAT_MODE)
		);
		
		// update texture
		BlendTowardsTexture.instance(p).setSourceTexture(noiseTexture.texture());
		BlendTowardsTexture.instance(p).setBlendLerp(UI.value(BLEND_LERP));
		BlendTowardsTexture.instance(p).applyTo(texture);
		
		// colorize sometimes
//		if(FrameLoop.frameMod(120)) {
//			// full blend to gray map
//			BlendTowardsTexture.instance(p).setBlendLerp(1);
//			BlendTowardsTexture.instance(p).applyTo(texture);
//			
//			// then colorize
//			int rand = MathUtil.randRange(0, 2);
//			switch (rand) {
//				case 0:
//					ColorizeFromTexture.instance(p).setTexture(ImageGradient.BLACK_HOLE());
//					break;
//				case 1:
//					ColorizeFromTexture.instance(p).setTexture(ImageGradient.SPARKS_FLAMES());
//					break;
//				case 2:
//					ColorizeFromTexture.instance(p).setTexture(ImageGradient.PASTELS());
//					break;
//				default:
//					break;
//			}
////			ColorizeFromTexture.instance(p).applyTo(texture);
//			
//			int color1 = ColorsHax.COLOR_GROUPS[MathUtil.randIndex(ColorsHax.COLOR_GROUPS.length)][MathUtil.randIndex(5)];
//			int color2 = ColorsHax.COLOR_GROUPS[MathUtil.randIndex(ColorsHax.COLOR_GROUPS.length)][MathUtil.randIndex(5)];
//			ColorizeTwoColorsFilter.instance(p).setColor1(EasingColor.redFromColorIntNorm(color1), EasingColor.greenFromColorIntNorm(color1), EasingColor.blueFromColorIntNorm(color1));
//			ColorizeTwoColorsFilter.instance(p).setColor2(EasingColor.redFromColorIntNorm(color2), EasingColor.greenFromColorIntNorm(color2), EasingColor.blueFromColorIntNorm(color2));
//			ColorizeTwoColorsFilter.instance(p).applyTo(texture);
//		}
		
		// draw circles
		float numCircles = 6;
		float panelW = texture.width / (numCircles);
		float squishEllipse = 0.8f;
		texture.beginDraw();
		texture.noStroke();
		PG.setDrawCenter(texture);
		for (int i = 0; i < numCircles; i++) {
			float circleSize = (panelW * 0.65f) + 20 * P.sin(p.frameCount * 0.04f + i);
			int curColor = ColorsHax.COLOR_GROUPS[0][i % 5];
			curColor = p.lerpColor(curColor, p.color(255), 0.5f + 0.5f * P.sin(p.frameCount * 0.04f + i));
			texture.fill(curColor);
			texture.ellipse(panelW/2 + (i) * panelW, texture.height/2, circleSize, circleSize * squishEllipse);
		}
		texture.endDraw();
		
		// apply feedback
		FeedbackMapFilter.instance(p).setMap(noiseTexture.texture());
		FeedbackMapFilter.instance(p).setAmp(UI.value(feedbackAmp));
		FeedbackMapFilter.instance(p).setBrightnessStep(UI.value(feedbackBrightStep));
		FeedbackMapFilter.instance(p).setAlphaStep(UI.value(feedbackAlphaStep));
		FeedbackMapFilter.instance(p).setRadiansStart(UI.value(feedbackRadiansStart));
		FeedbackMapFilter.instance(p).setRadiansRange(UI.value(feedbackRadiansRange));
		for (int i = 0; i < UI.valueInt(FEEDBACK_ITERS); i++) FeedbackMapFilter.instance(p).applyTo(texture);
		
		// add contrast
		ContrastFilter.instance(p).setContrast(1.01f);
		ContrastFilter.instance(p).applyTo(texture);
		
		// draw cylinder 
//		PG.setBetterLights(p);
//		p.lights();
		PG.setCenterScreen(p);
		p.rotateX(UI.valueEased(ROT_X));
		p.rotateY(UI.valueEased(ROT_Y));
		
//		p.shape(shape);
		{
			float numFins = 6;
			float numSegments = numFins * 2;
			float segmentRads = P.TWO_PI / numSegments;
			float radius = p.width * 0.2f;
			float innerRadius = radius * UI.valueEased(INNER_RADIUS);
			float finsH = p.height * 0.4f;
			p.beginShape(P.QUADS);
			p.fill(255,0,0);
			p.stroke(255);
			p.texture(texture);
			p.textureMode(P.NORMAL);
			for (int i = 0; i < numSegments; i++) {
				float curRadius = (i % 2 == 0) ? radius : innerRadius;
				float nextRadius = (i % 2 == 0) ? innerRadius : radius;
				float rads = i * segmentRads;
				float radsNext = (i+1) * segmentRads;
				float progress = rads / P.TWO_PI;
				float progressNext = (rads + segmentRads) / P.TWO_PI;
				float curX = P.cos(rads) * curRadius;
				float curZ = P.sin(rads) * curRadius;
				float nextX = P.cos(radsNext) * nextRadius;
				float nextZ = P.sin(radsNext) * nextRadius;
				float topY = -finsH/2f;
				float botY = finsH/2f;
				float curU = progress;
				float nextU = progressNext;
				p.vertex(curX, topY, curZ, curU, 0);
				p.vertex(nextX, topY, nextZ, nextU, 0);
				p.vertex(nextX, botY, nextZ, nextU, 1);
				p.vertex(curX, botY, curZ, curU, 1);
			}
			p.endShape();
		}
	}
	
}