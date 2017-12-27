package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.app.haxmapper.textures.TextureEQGrid;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.BlurProcessingFilter;
import com.haxademic.core.draw.filters.shaders.MirrorFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.PerlinTexture;
import com.haxademic.core.draw.shaders.textures.TextureShader;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.webcam.WebCamWrapper;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_FeedbackMapShader
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	float frames = 80;
	float progress = 0;
	float progressRads = 0;
	int W = 800;
	int H = 800;
	PGraphics buffer;
	PGraphics map;
	PShape xShape;
	PShader feedbackShader;
	int mode = 0;
	protected PerlinTexture perlin;
	protected BaseTexture audioTexture;
	protected TextureShader textureShader;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, W );
		p.appConfig.setProperty( AppSettings.HEIGHT, H );
		p.appConfig.setProperty( AppSettings.HIDE_CURSOR, true);
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, P.round(1 + frames * 3) );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, P.round(1 + frames * 4) );
	}

	public void setup() {
		super.setup();
		
		buffer = P.p.createGraphics(W, H, PRenderers.P3D); 
		map = P.p.createGraphics(W, H, PRenderers.P3D);
		
		xShape = DemoAssets.shapeX().getTessellation();
		PShapeUtil.centerShape(xShape);
		PShapeUtil.scaleShapeToHeight(xShape, p.height * 0.35f);

//		xShape = p.loadShape(FileUtil.getFile("svg/ello-type.svg")).getTessellation();
//		PShapeUtil.centerShape(xShape);
//		PShapeUtil.scaleShapeToExtent(xShape, p.width * 0.4f);
		
		perlin = new PerlinTexture(p, 128, 128);
		perlin.update(0.15f, 0.05f, p.frameCount/ 10f, 0);
		
		audioTexture = new TextureEQGrid(128, 128);
		
		feedbackShader = loadShader(FileUtil.getFile("shaders/filters/feedback-map.glsl"));
		
//		WebCamWrapper.initWebCam(p, 3);
		textureShader = new TextureShader(TextureShader.bw_voronoi);
		textureShader = new TextureShader(TextureShader.bw_clouds);
		textureShader = new TextureShader(TextureShader.BWNoiseInfiniteZoom, 0.005f);
	}
	
	protected void drawImg(PImage img) {
		if(img != null) {
			buffer.beginDraw();
//			DrawUtil.setPImageAlpha(buffer, 0.5f);
			DrawUtil.setDrawCenter(buffer);
			buffer.tint(
					300 + 155 * P.sin(p.frameCount/50f),
					300 + 155 * P.sin(p.frameCount/80f),
					300 + 155 * P.sin(p.frameCount/90f),
					10);
			float scaleHeight = MathUtil.scaleToTarget(img.height, buffer.height);
			buffer.image(img, buffer.width/2, buffer.height/2, img.width * scaleHeight, img.height * scaleHeight);
			buffer.endDraw();
		}
	}
		
	protected void drawXShape(boolean black) {
		buffer.beginDraw();
		xShape.disableStyle();
		buffer.fill(127f + 127f * P.sin(progressRads * 30));
		if(black) buffer.fill(0);
		buffer.translate(buffer.width/2, buffer.height/2);
		buffer.rotate(progressRads * 0.25f);
		buffer.shape(xShape);
		buffer.endDraw();
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(key == ' ') drawXShape(true);
	}
	
	protected void updateMapAudio() {
		audioTexture.update();
		MirrorFilter.instance(p).applyTo(audioTexture.texture());
		map = audioTexture.texture();	
	}
	
	protected void updateMapPerlin() {
		perlin.update(
				P.map(p.mouseY, 0, p.height, 0.0001f, 0.05f), 
				P.map(p.mouseX, 0, p.width, 0.0001f, 0.001f), 
				50f * P.cos(p.frameCount/ 5000f), 
				50f * P.sin(p.frameCount/ 5000f)
				);
		ImageUtil.cropFillCopyImage(perlin.texture(), map, true);
	}
	
	protected void updateMapWebcam() {
		map.beginDraw();
		if(WebCamWrapper.getImage() != null) ImageUtil.cropFillCopyImage(WebCamWrapper.getImage(), map, true);
		map.endDraw();
	}
	
	protected void updateMapShader() {
		if(textureShader.shaderPath().equals(TextureShader.bw_voronoi)) textureShader.setTime(progressRads);
		else textureShader.updateTime();
		map.filter(textureShader.shader());
	}
	
	protected void applyFeedbackToBuffer() {
		feedbackShader.set("map", map);
		// feedbackShader.set("samplemult", P.map(p.mouseY, 0, p.height, 0.85f, 1.15f) );
		feedbackShader.set("amp", P.map(p.mouseX, 0, p.width, 0.0001f, 0.01f) );
		for (int i = 0; i < 1; i++) buffer.filter(feedbackShader); 
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
		
		background(255);
		
		// draw on top of image
		drawXShape(false);
		
		// draw map
//		updateMapPerlin();
//		updateMapAudio();
//		updateMapWebcam();
		updateMapShader();
		
		// blur the map
		blurMap();

		// apply feedback
		applyFeedbackToBuffer();
		
		// draw again to see the full image on top
		drawXShape(true);
//		drawImg(WebCamWrapper.getImage());

		// draw to screen
		p.image(buffer, 0, 0);

		// debug draw
		p.debugView.setTexture(buffer);
		p.debugView.setTexture(map);
	}
}

