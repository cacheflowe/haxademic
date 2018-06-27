package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.filters.shaders.BlendTowardsTexture;
import com.haxademic.core.draw.image.BufferMotionDetectionMap;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.webcam.IWebCamCallback;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;
import processing.video.Movie;

public class Demo_VertexShader_GPUParticlesWebcamDeform 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics webcamBuffer;
	protected PGraphics webcamMirror;
	protected PGraphics displacementMap;
	protected BufferMotionDetectionMap motionDetectionMap;
	protected Movie movie;

	protected PShape shape;
	protected PImage colorMap;
	protected PShader pointsTexturedShader;
	protected PGraphics pg;
	protected float w = 1024;
	protected float h = 512;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, (int) w);
		p.appConfig.setProperty(AppSettings.HEIGHT, (int) h);
		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 5);
	}

	protected void setupFirstFrame() {
		// set up webcam
		p.webCamWrapper.setDelegate(this);
		webcamBuffer = p.createGraphics((int) w, (int) h, PRenderers.P2D);
		webcamMirror = p.createGraphics((int) w, (int) h, PRenderers.P2D);

		
		// load & set texture
		displacementMap = p.createGraphics((int) w, (int) h, PRenderers.P2D);
		colorMap = p.loadImage(FileUtil.getFile("images/_sketch/reebok-delta.png"));// DemoAssets.textureNebula();
//		colorMap = DemoAssets.textureNebula();

		// build offsecreen buffer (things don't work the same on the main drawing surface)
		pg = p.createGraphics((int) w, (int) h, PRenderers.P3D);

		// count vertices for debugView
		int vertices = P.round(w * h); 
		p.debugView.setValue("Vertices", vertices);
		
		// Build points vertices
		shape = P.p.createShape();
		shape.beginShape(PConstants.POINTS);
		shape.stroke(255);
		shape.strokeWeight(1);
		shape.noFill();
		for (int i = 0; i < vertices; i++) {
			float x = i % w;
			float y = P.floor(i / w);
			if(y % 2 == 1) x = w - x - 1;
			shape.vertex(x/w, y/h, 0); // x/y coords are used as UV coords (0-1), and multplied by `spread` uniform
		}
		shape.endShape();
		shape.setTexture(colorMap);
		
		// load shader
		pointsTexturedShader = loadShader(
			FileUtil.getFile("haxademic/shaders/point/displacement-frag.glsl"), 
			FileUtil.getFile("haxademic/shaders/point/displacement-vert.glsl")
		);

		// clear the screen
		background(0);
	}

	public void drawApp() {
		// update displacement texture
		// perlin.update(0.15f, 0.05f, p.frameCount/ 10f, 0);
//		audioTexture.update();
//		MirrorFilter.instance(p).applyTo(audioTexture.texture());
		
//		texture = p.webCamWrapper.getImage();
		if(motionDetectionMap != null) {
//			displacementMap = motionDetectionMap.differenceBuffer();
			BlendTowardsTexture.instance(p).setBlendLerp(0.1f);
			BlendTowardsTexture.instance(p).setSourceTexture(motionDetectionMap.bwBuffer());
			BlendTowardsTexture.instance(p).applyTo(displacementMap);
			p.debugView.setTexture(displacementMap);
		}
		
		// fade background
		pg.beginDraw();
		pg.clear();
//		DrawUtil.fadeToBlack(pg, 60);
		
		// move to screen center
		pg.translate(p.width/2f, p.height/2f, 0);
		
		// draw vertex points. strokeWeight w/disableStyle works here for point size
		shape.disableStyle();
		pg.strokeWeight(1.0f);
		pointsTexturedShader.set("displacementMap", displacementMap);
		pointsTexturedShader.set("colorMap", colorMap);
//		if(movie != null && movie.width > 20) pointsTexturedShader.set("colorMap", movie);
		pointsTexturedShader.set("pointSize", 0.5f + p.mousePercentX() * 2f); // 2.5f + 1.5f * P.sin(P.TWO_PI * percentComplete));
		pointsTexturedShader.set("width", w);
		pointsTexturedShader.set("height", h);
		pointsTexturedShader.set("spread", 0.5f + p.mousePercentY() * 2f);//2.5f + 0.5f * P.sin(P.PI + 2f * loop.progressRads()));
		pointsTexturedShader.set("displaceStrength", 150f);//130f + 130f * P.sin(P.PI + P.TWO_PI * percentComplete));
//		pg.rotateX(P.sin(p.frameCount * 0.01f) * 0.2f);
		pg.shader(pointsTexturedShader);  
		pg.shape(shape);
		pg.resetShader();
		pg.endDraw();
		
		// draw buffer to screen
		p.image(webcamMirror, 0, 0);
		p.image(pg, 0, 0);
	}

	@Override
	public void newFrame(PImage frame) {
		// copy webcam and create motion detection at size of cropped webcam (and downscaling)
		ImageUtil.cropFillCopyImage(frame, webcamBuffer, true);
		ImageUtil.flipH(webcamBuffer);
		ImageUtil.flipV(webcamBuffer);
		ImageUtil.cropFillCopyImage(frame, webcamMirror, true);
		ImageUtil.flipH(webcamMirror);
		
		// lazy init and update motion detection buffers/calcs
		if(motionDetectionMap == null) {
			motionDetectionMap = new BufferMotionDetectionMap(webcamBuffer, 1f);
		}
		// float mouseX = p.mousePercentX();
		// p.debugView.setValue("mouseX", mouseX);
		motionDetectionMap.setBlendLerp(0.05f);
		motionDetectionMap.setDiffThresh(0.03f);
		motionDetectionMap.setFalloffBW(0.2f);
		motionDetectionMap.setThresholdCutoff(0.5f);
		motionDetectionMap.setBlur(1f);
		motionDetectionMap.updateSource(webcamBuffer);
		
		// set textures for debug view
		p.debugView.setTexture(frame);
//		p.debugView.setTexture(motionDetectionMap.backplate());
//		p.debugView.setTexture(motionDetectionMap.differenceBuffer());
//		p.debugView.setTexture(motionDetectionMap.bwBuffer());
		
//		if(movie == null) {
//			movie = new Movie(p, FileUtil.getFile("images/_sketch/attract-loop.mp4"));
//			movie.play();
//			movie.loop();
//		}
	}

		
}



































