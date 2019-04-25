package com.haxademic.app.blobs;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.draw.filters.pshader.WobbleFilter;
import com.haxademic.core.draw.image.TickerScroller;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeSolid;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class HaiBlobs 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected boolean DEBUG_MODE = false;
	
	// gradient colors
//	protected int COLOR_1 = ColorUtil.colorFromHex("#7B73DB");
//	protected int COLOR_2 = ColorUtil.colorFromHex("#9B6CBB");
//	protected int COLOR_3 = ColorUtil.colorFromHex("#FC655F");
//	protected int COLOR_4 = ColorUtil.colorFromHex("#FD8C6B");
	//inverted
//	protected int COLOR_1 = ColorUtil.colorFromHex("#848927");
//	protected int COLOR_2 = ColorUtil.colorFromHex("#659146");
//	protected int COLOR_3 = ColorUtil.colorFromHex("#05999c");
//	protected int COLOR_4 = ColorUtil.colorFromHex("#047390");
	//inverted - new
//	protected int COLOR_1 = ColorUtil.colorFromHex("#de6f0e");
//	protected int COLOR_2 = ColorUtil.colorFromHex("#ae220b");
//	protected int COLOR_3 = ColorUtil.colorFromHex("#2f8c45");
//	protected int COLOR_4 = ColorUtil.colorFromHex("#0c7498");
	//inverted - final
	protected int COLOR_1 = ColorUtil.colorFromHex("#0ca9a8");
	protected int COLOR_2 = ColorUtil.colorFromHex("#0d7999");
	protected int COLOR_3 = ColorUtil.colorFromHex("#2f8c45");
	protected int COLOR_4 = ColorUtil.colorFromHex("#b0220b");
	protected int COLOR_5 = ColorUtil.colorFromHex("#e26e0d");
	protected int COLOR_6 = ColorUtil.colorFromHex("#89902b");
	
	// objects
	protected PGraphics sphereTexture1;
	protected PGraphics sphereTexture2;
	protected PShapeSolid shapeIcos_solid;
	protected PShapeSolid shapeIcos__wire;
	protected PShapeSolid curShape;
	protected PGraphics overlayMask;
	protected TickerScroller ticker;
	protected PGraphics tickerFXBuffer;
	protected PGraphics buffer;


	// animation/noise/layout props
	protected float textureCycleToDeformLoops = 4;
	protected float animationSeconds = 2.4f;
	protected int _frames = P.round(30 * textureCycleToDeformLoops * animationSeconds);
	protected int icosaDetail = 5;
	protected int noiseSeed = 853;
	protected boolean debugNoiseSeed = false;
	protected float circleMaskScale = 0.36f;
	protected float blobScale = 0.26f;	// 0.36f
	protected float blobDeformAmp = 0.3f; // 0.7f
	protected float spreadMult = 2.64277f; 	// 	2.64277f  //	9.165f	//	0.781f	//	5.57249f	//  0.21055f
	protected int noiseOctaves = 3;
	protected float noiseFalloff = 0.55f;
//	protected float wobbleStrength = .3f;
//	protected float wobbleSize = 8f;
	protected float wobbleStrength = .03f;
	protected float wobbleSize = 5f;
	protected boolean drawsOverlay = false;
	
	protected float objScale = 1;

	// animation progress
	protected float progress;
	protected float easedPercent;
	protected float progressRadians;

	// lighting props
	public float directionLight = 180;
	public float emissiveMaterial = 1f; // 5f
	public float ambientLight = 50f;
	public float specularMaterial = 50f;
	public float specularLight = 100f;
	public float shininessVal = 5f; // 50f
	public float lightsFalloffVal = 0.12f;
	public float lightsFalloffConstantVal = 0.12f;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1080 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1080 );
		p.appConfig.setProperty( AppSettings.FPS, 30 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, true );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)_frames + 1 );
	}

	public void setup() {
		super.setup();
		p.noStroke();
		p.noiseSeed(noiseSeed);
		OpenGLUtil.setQuality(p.g, OpenGLUtil.GL_QUALITY_HIGH);

		buffer = p.createGraphics(p.width, p.height, P.P3D);
		buffer.smooth(OpenGLUtil.SMOOTH_HIGH);
		OpenGLUtil.setTextureQualityHigh(buffer);
	}
	
	public void mouseMoved() {
		super.mouseMoved();
		if(debugNoiseSeed == false) return;
		int newSeed = (int)P.map(p.mouseX, 0, p.width, 0, 1000f);
		P.println("newSeed", newSeed);
		p.noiseSeed(newSeed);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'd') DEBUG_MODE = !DEBUG_MODE;
	}
	
	protected void initObjects() {
		buildOverlay();
		builtGradientTextureLoop();
		buildSpheres();
	}
	
	protected void buildSpheres() {
		float icosaSize = p.width * blobScale;
		// build texture-mapped shape
		PShape icos = Icosahedron.createIcosahedronGrouped(p, icosaDetail, ticker.image(), -1, -1, 0.004f);
		PShapeUtil.scaleShapeToExtent(icos, icosaSize);
		shapeIcos_solid = new PShapeSolid(icos);
		// wireframe test
		PShape icosWire = Icosahedron.createIcosahedronGrouped(p, icosaDetail, null, p.color(255,255,255,255), p.color(0), 0.004f);
		PShapeUtil.scaleShapeToExtent(icosWire, icosaSize);
		shapeIcos__wire = new PShapeSolid(icosWire);
		// set cur shape
		curShape = shapeIcos__wire;
		curShape = shapeIcos_solid;
	}
	
	protected void buildOverlay() {		
		overlayMask = p.createGraphics(p.width, p.height, P.P2D);
		overlayMask.smooth(8);
		overlayMask.beginDraw();
		overlayMask.clear();

		overlayMask.fill(0);
		overlayMask.noStroke();
		overlayMask.beginShape();
		// Exterior part of shape, clockwise winding
		overlayMask.vertex(0, 0);
		overlayMask.vertex(overlayMask.width, 0);
		overlayMask.vertex(overlayMask.width, overlayMask.height);
		overlayMask.vertex(0, p.height);
		// Interior part of shape, counter-clockwise winding
		overlayMask.beginContour();
		float segments = 360f;
		float segmentRads = P.TWO_PI / segments;
		float radius = overlayMask.width * circleMaskScale;
		for(float i = 0; i < segments; i++) {
			overlayMask.vertex(overlayMask.width * 0.5f + radius * P.cos(-i * segmentRads), overlayMask.height * 0.5f + radius * P.sin(-i * segmentRads));
		}
		overlayMask.endContour();
		overlayMask.endShape(CLOSE);	
		overlayMask.endDraw();
	}
	
	protected void builtGradientTextureLoop() {
		int textureW = p.width * 4;
		int textureH = p.height;
		int gradientW = textureW / 6;
		PGraphics img = p.createGraphics(textureW, textureH, P.P2D);
		img.smooth(8);
		tickerFXBuffer = p.createGraphics(textureW, textureH, P.P2D);
		tickerFXBuffer.smooth(8);
		img.beginDraw();
		img.noStroke();
		img.translate(gradientW / 2, textureH/2);
		Gradients.linear(img, gradientW, textureH, COLOR_1, COLOR_2);
		img.translate(gradientW, 0);
		Gradients.linear(img, gradientW, textureH, COLOR_2, COLOR_3);
		img.translate(gradientW, 0);
		Gradients.linear(img, gradientW, textureH, COLOR_3, COLOR_4);
		img.translate(gradientW, 0);
		Gradients.linear(img, gradientW, textureH, COLOR_4, COLOR_5);
		img.translate(gradientW, 0);
		Gradients.linear(img, gradientW, textureH, COLOR_5, COLOR_6);
		img.translate(gradientW, 0);
		Gradients.linear(img, gradientW, textureH, COLOR_6, COLOR_1);
		img.endDraw();
		BlurHFilter.instance(p).setBlurByPercent(0.5f, img.width);
		BlurHFilter.instance(p).setBlurByPercent(0.5f, img.width);
		BlurHFilter.instance(p).setBlurByPercent(0.5f, img.width);
		BlurHFilter.instance(p).setBlurByPercent(0.5f, img.width);
		
		ticker = new TickerScroller(img, p.color(255), textureW, textureH, (float)textureW / (float)_frames);
	}
	
	protected PShapeSolid newSolidIcos(float size, PImage texture) {
		PShape group = createShape(GROUP);
		PShape icos = Icosahedron.createIcosahedron(p.g, icosaDetail, texture);
		PShapeUtil.scaleShapeToExtent(icos, size);
		group.addChild(icos);
		return new PShapeSolid(group);
	}	

	public void drawApp() {
		if(p.frameCount == 1) initObjects();
		p.background(0);
		
		// get progress
		progress = ((float)(p.frameCount%_frames)/_frames);
		easedPercent = Penner.easeInOutQuart(progress % 1, 0, 1, 1);
		progressRadians = progress * P.TWO_PI;
		
		updateTextures();
		drawTextureOnSphere();
		
		// hide controls
		if(DEBUG_MODE == false) p.translate(100000, 0);
	}
	
	protected void updateTextures() {
		ticker.update();
		tickerFXBuffer.beginDraw();
		DrawUtil.setDrawCenter(tickerFXBuffer);
		tickerFXBuffer.translate(tickerFXBuffer.width/2, tickerFXBuffer.height/2);
		tickerFXBuffer.rotate(progressRadians);
		tickerFXBuffer.scale(6);
		tickerFXBuffer.image(ticker.image(), 0, 0);
		tickerFXBuffer.endDraw();
		WobbleFilter.instance(p).setTime(P.sin(progressRadians * 3f) * 0.9f);
		WobbleFilter.instance(p).setStrength(wobbleStrength);
		WobbleFilter.instance(p).setSize(wobbleSize);
		WobbleFilter.instance(p).applyTo(tickerFXBuffer);	
	}
	
	protected void drawTextureOnSphere() {
		// set up sphere drawing
		buffer.beginDraw();
		buffer.clear();
		buffer.pushMatrix();
		buffer.translate(p.width * 0.5f, p.height * 0.5f);
		DrawUtil.setDrawCorner(buffer);
		DrawUtil.setDrawCorner(p);

		// set texture on sphere
//		shapeIcos_solid.shape().setTexture(ticker.image());
		shapeIcos_solid.shape().setTexture(tickerFXBuffer);
		
		// lights
//		buffer.ambient(127);
		buffer.lightSpecular(230, 230, 230); 
//		buffer.directionalLight(200, 200, 200, -0.0f, -0.0f, 1); 
		buffer.directionalLight(directionLight, directionLight, directionLight, 0.0f, 0.0f, -1); 
//		buffer.specular(p.color(200)); 
//		buffer.shininess(15.0f); 
		
		////////////////////////////////
		// global lights & materials setup
		////////////////////////////////
		// basic global lights:
		buffer.lightFalloff(lightsFalloffConstantVal, lightsFalloffVal, 0.0f);
		buffer.ambientLight(ambientLight, ambientLight, ambientLight);
//		buffer.lightSpecular(specularLight, specularLight, specularLight);

		// materials:
		buffer.emissive(emissiveMaterial, emissiveMaterial, emissiveMaterial);
		buffer.specular(specularMaterial, specularMaterial, specularMaterial);
		buffer.shininess(shininessVal);	// affects the specular blur

		// draw solid sphere
//		float spreadMult = P.map(p.mouseX, 0, p.width, 0.1f, 20f);
//		P.println("spreadMult",spreadMult);
//		shapeIcos_solid.setVertexColorWithAudio(p.color(255,0,0));
//		shapeIcos_solid.updateWithTrig(false, progress * 1f, 0.12f, 3.f);
		curShape.updateWithTrig(false, progress * textureCycleToDeformLoops, 0.12f, spreadMult);
//		shapeIcos_solid.updateWithTrigGradient(progress * 1f, 0.12f, 3.f, ticker.image());
//		shapeIcos_solid.updateWithNoise(progress * P.TWO_PI, 1f, blobDeformAmp, noiseOctaves, noiseFalloff);
//		shapeIcos_solid.updateWithTrigAndNoiseCombo(progress * P.TWO_PI, 0.10f, 3.f, 0.75f, 1f, blobDeformAmp, noiseOctaves, noiseFalloff);
//		shapeIcos__wire.updateWithNoise(progress * P.TWO_PI, 1f, blobDeformAmp, noiseOctaves, noiseFalloff);
//		shapeIcos__wire.updateWithTrigAndNoiseCombo(progress * P.TWO_PI, 0.10f, 3.f, 0.75f, 1f, blobDeformAmp, noiseOctaves, noiseFalloff);
		
		buffer.pushMatrix();
		// buffer.rotateX(P.map(p.mouseX, 0, p.width, 0, P.TWO_PI));
		buffer.shape(curShape.shape());
		buffer.popMatrix();
		buffer.popMatrix();
		
		buffer.endDraw();

		
		// flat overlay
		DrawUtil.setDrawFlat2d(p, true);
		if(drawsOverlay == true) p.image(overlayMask, 0, 0);
		
		p.image(buffer, 0, 0);

		if(DEBUG_MODE == true) {
			p.image(tickerFXBuffer, 0, 0);
		}
		DrawUtil.setDrawFlat2d(p, false);
		
		InvertFilter.instance(p).applyTo(p);
	}
	
}
