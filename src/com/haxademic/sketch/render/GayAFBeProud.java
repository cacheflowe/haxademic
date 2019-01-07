package com.haxademic.sketch.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.TextToPShape;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;

public class GayAFBeProud 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PGraphics rainbowBuffer;
	protected PGraphics meshTextureBuffer;
	protected PGraphics textTextureBuffer;
	protected TiledTexture tiledRainbow;
	
	protected TextToPShape textToPShape;
	protected PShape wordGay;
	protected float wordGayDepth;
	protected PShape wordAF;
	protected float wordAFDepth;
	
	protected LinearFloat showHide = new LinearFloat(0, 0.02f);

	
	protected int colors[] = new int[]{
		ColorUtil.colorFromHex("#FF0000"),
		ColorUtil.colorFromHex("#FF4A00"),
		ColorUtil.colorFromHex("#FFFF08"),
		ColorUtil.colorFromHex("#006F08"),
		ColorUtil.colorFromHex("#0000FB"),
		ColorUtil.colorFromHex("#350074"),
		ColorUtil.colorFromHex("#B217FE")
	};


	protected void overridePropsFile() {
		int FRAMES = 200;
		p.appConfig.setProperty(AppSettings.WIDTH, 1080);
		p.appConfig.setProperty(AppSettings.HEIGHT, 1920);
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 2);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 3);
	}
	
	protected void setupFirstFrame() {
		// create rainbow buffer source
		rainbowBuffer = p.createGraphics(2048, 2048, P.P2D);
		meshTextureBuffer = p.createGraphics(2048, 2048, P.P2D);
		textTextureBuffer = p.createGraphics(2048, 2048, P.P2D);
		
		tiledRainbow = new TiledTexture(rainbowBuffer);
		drawRainbowBuffer();
		p.debugView.setTexture(rainbowBuffer);
		p.debugView.setTexture(meshTextureBuffer);
		p.debugView.setTexture(textTextureBuffer);
		
		// make 3d text
		textToPShape = new TextToPShape(TextToPShape.QUALITY_HIGH);
		String fontFile = FileUtil.getFile("fonts/_sketch/GeometricBlack.ttf");
		float textDepth = 80;
		float textHeight = p.height * 0.09f;
		
		wordGay = textToPShape.stringToShape3d("BE", textDepth, fontFile);
		PShapeUtil.scaleShapeToHeight(wordGay, textHeight);
		PShapeUtil.addTextureUVToShape(wordGay, textTextureBuffer);
		wordGayDepth = PShapeUtil.getDepth(wordGay);
		
		wordAF = textToPShape.stringToShape3d("PROUD", textDepth, fontFile);
		PShapeUtil.scaleShapeToHeight(wordAF, textHeight);
		PShapeUtil.addTextureUVToShape(wordAF, textTextureBuffer);
		wordAFDepth = PShapeUtil.getDepth(wordAF);
		
		// build shape and assign texture
		shape = Shapes.createSheet(10, meshTextureBuffer);
//		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleShapeToHeight(shape, p.height * 10f);
		
		// log mesh size
		PVector modelSize = new PVector(PShapeUtil.getWidth(shape), PShapeUtil.getHeight(shape), PShapeUtil.getDepth(shape));
		p.debugView.setValue("shape.width", modelSize.x);
		p.debugView.setValue("shape.height", modelSize.y);
		p.debugView.setValue("shape.depth", modelSize.z);
		p.debugView.setValue("shape vertices", PShapeUtil.vertexCount(shape));
	}
	
	protected void drawRainbowBuffer() {
		rainbowBuffer.beginDraw();
		rainbowBuffer.noStroke();
		float colorHeight = P.round(rainbowBuffer.height / colors.length);
		for (int i = 0; i < colors.length; i++) {
			rainbowBuffer.fill(colors[i]);
			rainbowBuffer.rect(0, colorHeight * i, rainbowBuffer.width, colorHeight);
		}
		rainbowBuffer.endDraw();
	}
	
	public void keyPressed() {
		super.keyPressed();
	}
		
	public void drawApp() {
		// set up context
		background(0);
//		CameraUtil.setCameraDistanceGood(p.g, 400, 10000);
		p.noStroke();
//		DrawUtil.setDrawCenter(p.g);
		DrawUtil.setBetterLights(p.g);
//		p.lights();
		
		// update repeating textures
		meshTextureBuffer.beginDraw();
		meshTextureBuffer.translate(meshTextureBuffer.width / 2, meshTextureBuffer.height / 2);
		tiledRainbow.setOffset(0, 0);
		tiledRainbow.setSize(3f + 2f * P.sin(2f * p.loop.progressRads()), 3f + 2f * P.sin(2f * p.loop.progressRads()));
		tiledRainbow.setRotation(P.sin(p.loop.progressRads()));
		tiledRainbow.drawCentered(meshTextureBuffer, meshTextureBuffer.width, meshTextureBuffer.height);
		meshTextureBuffer.endDraw();
		
		textTextureBuffer.beginDraw();
		textTextureBuffer.translate(textTextureBuffer.width / 2, textTextureBuffer.height / 2);
		tiledRainbow.setOffset(0, 0);
		tiledRainbow.setSize(2f + 1f * P.sin(2f * p.loop.progressRads()), 2f + 1f * P.sin(2f * p.loop.progressRads()));
		tiledRainbow.setRotation(P.PI - P.sin(p.loop.progressRads()));
		tiledRainbow.drawCentered(textTextureBuffer, textTextureBuffer.width, textTextureBuffer.height);
		textTextureBuffer.endDraw();
		
		// easing
		showHide.setInc(0.015f);
		float showHideTarget = (p.loop.progress() > 0.5f) ? 0 : 1;
		showHide.setTarget(showHideTarget);
		showHide.update();
		float easedSlide = Penner.easeInOutQuad(showHide.value(), 0, 1, 1);
		
		// draw shape
		DrawUtil.setCenterScreen(p.g);
		p.rotateX(0.5f * P.sin(P.HALF_PI + easedSlide * P.PI));
//		p.rotateX(0.4f * P.sin(P.HALF_PI + p.loop.progressRads()));
		// draw sheet
		
		p.pushMatrix();
		p.scale(1.7f);
		Shapes.drawTexturedRect(p.g, meshTextureBuffer);
		p.popMatrix();
		p.translate(0, p.g.height * -0.5f);	// reset vertical to top
		
		// move text
//		p.translate(0, (-0.5f + easedSlide) * p.height);
		
		// gay text 
		p.pushMatrix();
//		p.translate(0, 0, wordGayDepth/2 + 1);
		p.translate(0, p.height * 0.333f);
		p.shape(wordGay);
		p.popMatrix();
		
		// af text
		p.pushMatrix();
//		p.translate(0, 0, -wordAFDepth/2 - 1);
		p.translate(0, p.height * 0.666f);
//		p.rotateX(P.PI);
		p.shape(wordAF);
		p.popMatrix();

		// post processing
		BrightnessFilter.instance(p).setBrightness(1.35f);
		BrightnessFilter.instance(p).applyTo(p);
		VignetteFilter.instance(p).setDarkness(0.5f);
		VignetteFilter.instance(p).applyTo(p);
	}
	
}