package com.haxademic.app.blobs;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.VignetteAltFilter;
import com.haxademic.core.draw.filters.pshader.WobbleFilter;
import com.haxademic.core.draw.image.TickerScroller;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class BlindSpotColored
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float numSegments = 10f;
	protected float numShapes = 500f;
	protected float frames = 800f;
	protected float progress;
	protected float progressRadians;

	protected PGraphics buffer;
	protected PGraphics colors;
	
	protected PShader maskShader;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1000 );
		p.appConfig.setProperty( AppSettings.RENDERER, P.P2D );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, true );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, Math.round(frames) );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, Math.round(frames * 2) );
	}


	public void setupFirstFrame() {
	}
	
	protected void buildObjects() {
		buffer = p.createGraphics(p.width, p.height, P.P3D);
		buffer.smooth(8);
		
		colors = p.createGraphics(p.width, p.height, P.P2D);
		colors.smooth(8);
		
		builtGradientTextureLoop();
		
		maskShader = loadShader(FileUtil.getFile("haxademic/shaders/filters/three-texture-map-opacity-fade.glsl"));

	}

	public void drawApp() {
		if(p.frameCount == 1) buildObjects();
		p.background(255);
		
		progress = (p.frameCount % frames) / frames;
		progressRadians = progress * P.TWO_PI;

		drawStar();
//		ContrastFilter.instance(p).setContrast(3f);
//		ContrastFilter.instance(p).applyTo(buffer);
		updateColorsTexture();
		
		
//		buffer.blendMode(PBlendModes.BLEND);
//		buffer.blendMode(PBlendModes.ADD);
		
		maskShader.set("map", buffer );
		maskShader.set("tex1", tickerFXBuffer );
		p.filter(maskShader);

//		p.image(buffer, 0, 0);
//		p.image(tickerFXBuffer, 0, 0);
		
	}
	
	protected void drawStar() {
		buffer.beginDraw();
		
		if(p.frameCount == 1) buffer.background(255);
		
		float starProgress = progress * 3f;
		float starProgressRadians = starProgress * P.TWO_PI;
		
		buffer.blendMode(P.BLEND);
		PG.feedback(buffer, p.color(255), 0.15f, 1f);
		PG.setDrawFlat2d(buffer, true);
		
		buffer.blendMode(P.SUBTRACT);
		buffer.noFill();
		buffer.noStroke();
		buffer.strokeWeight(0.85f);
				
		int innerColor = p.color(255, 10);
		int outerColor = p.color(255, 0, 255, 00);
		
		buffer.pushMatrix();

		float halfW = width/2;
		float halfH = height/2;
		float baseRadius = halfH/2.3f;

		float segmentRadians = P.TWO_PI / numSegments;
		float shapeRadians = P.TWO_PI / numShapes;
		
		
		buffer.translate(halfW, halfH);
		buffer.rotateY(starProgressRadians);
//		buffer.rotateX(starProgressRadians);
//		buffer.rotateZ(-starProgressRadians);
		
		/*
		PG.setDrawCenter(buffer);
		boolean drawEllipses = false;
		if(drawEllipses == true) {
			for (int i = 0; i < numShapes; i++) {
				float size = i * buffer.width / 2f / numShapes;
				buffer.stroke(0, 100);
				buffer.ellipse(0, 0, size, size);
		//		buffer.sphere(100);
				buffer.rotateY(shapeRadians);
			}
		}
		*/
		
		buffer.pushMatrix();
		for (float i = 0; i < numShapes; i++) {
//			buffer.rotateY(shapeRadians);
//			buffer.rotateX(shapeRadians);
//			buffer.rotateZ(shapeRadians);
			buffer.rotateY(1f + p.noise(i, i * 0.25f, i * 0.65f));
			buffer.rotateX(1f + p.noise(i * 0.25f, i, i * 0.65f));
			buffer.rotateZ(1f + p.noise(i * 0.65f, i, i * 0.25f));
			outerColor = p.color(
				100 + 40f * sin(i), 
				120 + 50f * sin(i), 
				160 + 90f * sin(i), 
				10
			);
			
			float radiusOscillations = 5f + 3f * P.sin(i/3f);
			for(float r=0; r < P.TWO_PI; r += segmentRadians) {
				float r2 = r + segmentRadians;
				buffer.beginShape();
				buffer.stroke(innerColor);
//				buffer.fill(innerColor);
				buffer.vertex(0,0);
				buffer.stroke(outerColor);
//				buffer.fill(outerColor);
				float curRadius = baseRadius + (baseRadius * 0.6f * P.sin(starProgressRadians + i+r*radiusOscillations));
				float nextRadius = baseRadius + (baseRadius * 0.6f * P.sin(starProgressRadians + i+r2*radiusOscillations));
				buffer.vertex(P.sin(r) * curRadius, P.cos(r) * curRadius);
				buffer.vertex(P.sin(r2) * nextRadius, P.cos(r2) * nextRadius);
				buffer.endShape(P.CLOSE);
			}
		}
		buffer.popMatrix();
		
		buffer.popMatrix();
		
		buffer.endDraw();
		
		// post process
//		ColorDistortionFilter.instance(p).applyTo(buffer);
//		SphereDistortionFilter.instance(p).setAmplitude(0.5f);
//		SphereDistortionFilter.instance(p).applyTo(buffer);
//		DilateFilter.instance(p).applyTo(buffer);
//		CubicLensDistortionFilter.instance(p).setTime( 10f + 3f * P.sin(progressRadians));
//		CubicLensDistortionFilter.instance(p).applyTo(buffer);
		VignetteAltFilter.instance(p).setDarkness(-4);
		VignetteAltFilter.instance(p).setSpread(0.2f);
		VignetteAltFilter.instance(p).applyTo(buffer);
	}

	
	
	
	
	protected int COLOR_1 = ColorUtil.colorFromHex("#7B73DB");
	protected int COLOR_2 = ColorUtil.colorFromHex("#9B6CBB");
	protected int COLOR_3 = ColorUtil.colorFromHex("#FC655F");
	protected int COLOR_4 = ColorUtil.colorFromHex("#FD8C6B");

	protected TickerScroller ticker;
	protected PGraphics tickerFXBuffer;

	protected float wobbleStrength = .03f;
	protected float wobbleSize = 3f;

	protected void builtGradientTextureLoop() {
		int textureW = p.width * 4;
		int textureH = p.height * 1;
		int gradientW = textureW / 4;
		PGraphics img = p.createGraphics(textureW, textureH, P.P2D);
		img.smooth(8);
		tickerFXBuffer = p.createGraphics(textureW, textureH, P.P2D);
		tickerFXBuffer.smooth(8);
		img.beginDraw();
		img.noStroke();
		img.translate(gradientW / 2, textureH/2);
		Gradients.linear(img, gradientW, textureH, COLOR_1, COLOR_3);
		img.translate(gradientW, 0);
		Gradients.linear(img, gradientW, textureH, COLOR_3, COLOR_2);
		img.translate(gradientW, 0);
		Gradients.linear(img, gradientW, textureH, COLOR_2, COLOR_4);
		img.translate(gradientW, 0);
		Gradients.linear(img, gradientW, textureH, COLOR_4, COLOR_1);
		img.endDraw();
		BlurHFilter.instance(p).setBlurByPercent(0.75f, img.width);
		BlurHFilter.instance(p).applyTo(img);
//		BlurHFilter.instance(p).applyTo(img);
//		BlurHFilter.instance(p).applyTo(img);
		
		ticker = new TickerScroller(img, p.color(255), textureW, textureH, (float)textureW / (float)frames);
	}
	
	protected void updateColorsTexture() {
		// update textures
		ticker.update();
		tickerFXBuffer.beginDraw();
		PG.setDrawCenter(tickerFXBuffer);
		tickerFXBuffer.translate(tickerFXBuffer.width/2, tickerFXBuffer.height/2);
		tickerFXBuffer.rotate(progressRadians);
		tickerFXBuffer.scale(7);
		tickerFXBuffer.image(ticker.image(), 0, 0);
		tickerFXBuffer.endDraw();
		WobbleFilter.instance(p).setTime(P.sin(progressRadians * 3f) * 0.9f);
		WobbleFilter.instance(p).setStrength(wobbleStrength);
		WobbleFilter.instance(p).setSize(wobbleSize);
//		WobbleFilter.instance(p).applyTo(tickerFXBuffer);
		
//		SphereDistortionFilter.instance(p).setAmplitude(2.5f);
//		SphereDistortionFilter.instance(p).applyTo(tickerFXBuffer);
//		BlurHFilter.instance(p).applyTo(tickerFXBuffer);
//		BlurHFilter.instance(p).applyTo(tickerFXBuffer);
//		BlurHFilter.instance(p).applyTo(tickerFXBuffer);
//		BlurHFilter.instance(p).applyTo(tickerFXBuffer);


	}

}