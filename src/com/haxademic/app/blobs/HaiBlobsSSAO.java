package com.haxademic.app.blobs;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.WobbleFilter;
import com.haxademic.core.draw.image.TickerScroller;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeSolid;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class HaiBlobsSSAO 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected boolean DEBUG_MODE = false;
	
	// gradient colors
//	protected int COLOR_1 = ColorUtil.colorFromHex("#7B73DB");
//	protected int COLOR_2 = ColorUtil.colorFromHex("#9B6CBB");
//	protected int COLOR_3 = ColorUtil.colorFromHex("#FC655F");
//	protected int COLOR_4 = ColorUtil.colorFromHex("#FD8C6B");
	//inverted
	protected int COLOR_1 = ColorUtil.colorFromHex("#848927");
	protected int COLOR_2 = ColorUtil.colorFromHex("#659146");
	protected int COLOR_3 = ColorUtil.colorFromHex("#05999c");
	protected int COLOR_4 = ColorUtil.colorFromHex("#047390");
	
	// objects
	protected PGraphics sphereTexture1;
	protected PGraphics sphereTexture2;
	protected PShapeSolid shapeIcos_solid;
	protected PShapeSolid shapeIcos__wire;
	protected PGraphics overlayMask;
	protected TickerScroller ticker;
	protected PGraphics tickerFXBuffer;

	// animation/noise/layout props
	protected int _frames = 1500;
	protected int icosaDetail = 4;
	protected int noiseSeed = 853;
	protected boolean debugNoiseSeed = false;
	protected float circleMaskScale = 0.36f;
	protected float blobScale = 0.26f;	// 0.36f
	protected float blobDeformAmp = 0.3f; // 0.7f
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
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 1000 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)_frames + 1 );
	}

	public void firstFrame() {
		p.noStroke();
		p.noiseSeed(noiseSeed);
		buildSSAO();
		OpenGLUtil.setQuality(p.g, OpenGLUtil.GL_QUALITY_HIGH);
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
//		shapeIcos_solid = newSolidIcos(p.width * blobScale, ticker.image());
//		shapeIcos_solid = newSolidIcos(p.width * blobScale, null);
		
		float icosaSize = p.width * blobScale;
		
		PShape icos = Icosahedron.createIcosahedronGrouped(p, icosaDetail, ticker.image(), p.color(255), p.color(0, 0), 0.004f);
		PShapeUtil.scaleShapeToExtent(icos, icosaSize);
//		PShapeUtil.scaleObjToExtentVerticesAdjust(icos, icosaSize);
		shapeIcos_solid = new PShapeSolid(icos);
		
//		PShape icosWire = Icosahedron.createIcosahedronGrouped(p, icosaDetail, null, p.color(255,0,0,0), p.color(0), 0.004f);
//		PShapeUtil.scaleShapeToExtent(icosWire, icosaSize);
//		shapeIcos__wire = new PShapeSolid(icosWire);
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
		int textureW = p.width * 1;
		int textureH = p.height;
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

//	protected PShapeSolid newSolidIcosGrouped(float size, PImage texture) {
//		PShape icos = Icosahedron.createIcosahedronGrouped(p, icosaDetail, texture);
//		PShapeUtil.scaleSvgToExtent(icos, size);
//		return new PShapeSolid(icos);
//	}
	

	public void drawApp() {
		if(p.frameCount == 1) initObjects();
		p.background(0);
//		PG.setDrawCenter(p);
		
		// get progress
		progress = ((float)(p.frameCount%_frames)/_frames);
		easedPercent = Penner.easeInOutQuart(progress % 1);
		progressRadians = progress * P.TWO_PI;
		
		// update textures
		ticker.update();
		tickerFXBuffer.beginDraw();
		PG.setDrawCenter(tickerFXBuffer);
		tickerFXBuffer.translate(tickerFXBuffer.width/2, tickerFXBuffer.height/2);
		tickerFXBuffer.rotate(progressRadians);
		tickerFXBuffer.scale(2);
		tickerFXBuffer.image(ticker.image(), 0, 0);
		tickerFXBuffer.endDraw();
		WobbleFilter.instance(p).setTime(P.sin(progressRadians * 3f) * 0.9f);
		WobbleFilter.instance(p).setStrength(wobbleStrength);
		WobbleFilter.instance(p).setSize(wobbleSize);
		WobbleFilter.instance(p).applyTo(tickerFXBuffer);

		// draw blob
		drawTextureOnSphere();
		
		// hide controls
		p.translate(100000, 0);
	}
	
	protected void drawTextureOnSphere() {
		blobDeformAmp = 1.2f;
		noiseOctaves = 4;
		
		buffer.beginDraw();
		buffer.clear();
		buffer.pushMatrix();
		buffer.translate(p.width * 0.5f, p.height * 0.5f);
		buffer.rotateY(P.sin(progressRadians));
		PG.setDrawCorner(buffer);
		PG.setDrawCorner(p);

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
//		shapeIcos_solid.setVertexColorWithAudio(p.color(255,0,0));
//		shapeIcos_solid.updateWithTrig(false, progress * 1f, 0.12f, 3.f);
//		shapeIcos_solid.updateWithTrigGradient(progress * 1f, 0.12f, 3.f, ticker.image());
//		shapeIcos_solid.updateWithNoise(progress * P.TWO_PI, 1f, blobDeformAmp, noiseOctaves, noiseFalloff);
		shapeIcos_solid.updateWithTrigAndNoiseCombo(progress * P.TWO_PI, 0.1f, 0.2f, 0.75f, 1f, blobDeformAmp, noiseOctaves, noiseFalloff);
//		shapeIcos__wire.updateWithTrigAndNoiseCombo(progress * P.TWO_PI, 0.10f, 3.f, 0.75f, 1f, blobDeformAmp, noiseOctaves, noiseFalloff);
		
		buffer.pushMatrix();
		// buffer.rotateX(P.map(p.mouseX, 0, p.width, 0, P.TWO_PI));
		buffer.shape(shapeIcos_solid.shape());
//		PShapeUtil.drawTriangles(p, shapeIcos_solid.shape());
//		buffer.shape(shapeIcos__wire.shape());
		buffer.popMatrix();
		buffer.popMatrix();
		
		buffer.endDraw();

		// depth shader & SSAO
		drawDepth();
		
		// flat overlay
		PG.setDrawFlat2d(p, true);
		if(drawsOverlay == true) p.image(overlayMask, 0, 0);
		
		p.image(buffer, 0, 0);
		p.blendMode(PBlendModes.SCREEN);
		p.image(ssao, 0, 0);
		p.blendMode(PBlendModes.BLEND);

		if(DEBUG_MODE == true) {
			p.image(tickerFXBuffer, 0, 0);
//			p.image(depth, 0, 0);
			p.image(ssao, 0, 0);
			// p.image(imageCycler.image(), 0, 0);
			// p.image(ticker.image(), 0, 0);
		}
		PG.setDrawFlat2d(p, false);
		
//		InvertFilter.instance(p).applyTo(p);

	}

	
	
	
	
	
	
	
	
	
	PGraphics buffer;
	PGraphics depth;
	PGraphics ssao;
	PShader depthShader;
	PShader ssaoShader;

	protected String onlyAO = "onlyAO";
	protected String aoClamp = "aoClamp";
	protected String lumInfluence = "lumInfluence";
	protected String cameraNear = "cameraNear";
	protected String cameraFar = "cameraFar";
	protected String samples = "samples";
	protected String radius = "radius";
	protected String useNoise = "useNoise";
	protected String noiseAmount = "noiseAmount";
	protected String diffArea = "diffArea";
	protected String gDisplace = "gDisplace";
	protected String diffMult = "diffMult";
	protected String gaussMult = "gaussMult";

	
	
	protected void buildSSAO() {
		// values for render:
//		cameraNear = 100f;
//		cameraFar = 1130f;
//		onlyAO = false;
//		aoClamp = 0.89f;
//		lumInfluence = 1.3f;
//		samples = 128; // more
//		useNoise = false;
//		noiseAmount = 0;
//		diffArea = 3.9f;
//		gDisplace = 0;
//		diffMult = 1000; // more
//		gaussMult = -0.04f;

		UI.addSlider(onlyAO, 0, 0, 1, 1, false);
		UI.addSlider(aoClamp, 2f, -5f, 5f, 0.01f, false);
		UI.addSlider(lumInfluence, -1.5f, -5f, 5f, 0.01f, false);
		UI.addSlider(cameraNear, 300, 1, 500, 1, false);
		UI.addSlider(cameraFar, 1280, 500f, 2000f, 1, false);
		UI.addSlider(samples, 32, 2, 128, 1, false);
		UI.addSlider(radius, 30, 0, 100, 0.02f, false);
		UI.addSlider(diffArea, 0.5f, 0, 5, 0.01f, false);
		UI.addSlider(gDisplace, 1.0f, 0, 5, 0.01f, false);
		UI.addSlider(diffMult, 200f, 1, 1000, 1f, false);
		UI.addSlider(gaussMult, -2, -4, 4, 0.01f, false);
		UI.addSlider(useNoise, 1, 0, 1, 1, false);
		UI.addSlider(noiseAmount, 0.00003f, 0.00003f, 0.003f, 0.00001f, false);

		buffer = p.createGraphics(p.width, p.height, P.P3D);
		buffer.smooth(OpenGLUtil.SMOOTH_HIGH);
		OpenGLUtil.setTextureQualityHigh(buffer);
		
		depth = p.createGraphics(p.width, p.height, P.P3D);
		depth.smooth(OpenGLUtil.SMOOTH_HIGH);
		OpenGLUtil.setTextureQualityHigh(depth);

		ssao = p.createGraphics(p.width, p.height, P.P3D);
		ssao.smooth(OpenGLUtil.SMOOTH_HIGH);
		OpenGLUtil.setTextureQualityHigh(ssao);
		
		depthShader = loadShader(
				FileUtil.getPath("haxademic/shaders/vertex/depth-frag.glsl"), 
				FileUtil.getPath("haxademic/shaders/vertex/depth-vert.glsl")
				);

		ssaoShader = loadShader(
			FileUtil.getPath("haxademic/shaders/vertex/ssao-frag.glsl"), 
			FileUtil.getPath("haxademic/shaders/vertex/ssao-vert.glsl")
		);
		ssaoShader.set("size", (float) p.width, (float) p.height );
		ssaoShader.set("tDiffuse", buffer );
		ssaoShader.set("tDepth", depth );
	}
	
	protected void drawDepth() {
		depthShader.set("near", UI.value(cameraNear));
		depthShader.set("far", UI.value(cameraFar));

		ssaoShader.set("onlyAO", UI.value(onlyAO) == 1);
		ssaoShader.set("aoClamp", UI.value(aoClamp));
		ssaoShader.set("lumInfluence", UI.value(lumInfluence));
		ssaoShader.set("cameraNear", UI.value(cameraNear));
		ssaoShader.set("cameraFar", UI.value(cameraFar));
		
		ssaoShader.set("samples", UI.valueInt(samples));
		ssaoShader.set("radius", UI.value(radius));
		ssaoShader.set("useNoise", UI.value(useNoise) == 1);
		ssaoShader.set("noiseAmount", UI.value(noiseAmount));
		ssaoShader.set("diffArea", UI.value(diffArea));
		ssaoShader.set("gDisplace", UI.value(gDisplace));
		ssaoShader.set("diffMult", UI.value(diffMult));
		ssaoShader.set("gaussMult", UI.value(gaussMult));

		
		
		// draw shapes to get depth map
		depth.beginDraw();
		depth.clear();
		depth.translate(depth.width/2, depth.height/2);
		depth.rotateY(P.sin(progressRadians));
		depth.shader(depthShader);
		depth.fill(0);
		depth.noStroke();
//		PShapeUtil.drawTriangles(depth, shapeIcos__wire.shape(), null, objScale);
		PShapeUtil.drawTriangles(depth, shapeIcos_solid.shape(), null, objScale);
		depth.endDraw();
		
		ssaoShader.set("tDiffuse", buffer );
		ssaoShader.set("tDepth", depth );
		ssao.filter(ssaoShader);
		
//		BlurVFilter.instance(p).setBlurByPercent(0.3f, ssao.width);
//		BlurHFilter.instance(p).setBlurByPercent(0.3f, ssao.width);
//		BlurVFilter.instance(p).applyTo(ssao);
//		BlurHFilter.instance(p).applyTo(ssao);
	}
}
