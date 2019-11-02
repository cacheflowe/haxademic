package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_FeedbackRadialShader
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	float frames = 60;
	float progress = 0;
	float progressRads = 0;
	int W = 1600;
	int H = 800;
	PGraphics buffer;
	PShape xShape;
	PShader feedbackShader;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, W );
		p.appConfig.setProperty( AppSettings.HEIGHT, H );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, P.round(1 + frames * 3) );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, P.round(1 + frames * 4) );
	}

	public void setup() {
		super.setup();
		
		buffer = P.p.createGraphics(W, H, PRenderers.P3D); 
		
		xShape = DemoAssets.shapeFractal().getTessellation();
		PShapeUtil.centerShape(xShape);
		PShapeUtil.scaleShapeToMaxAbsY(xShape, p.height * 0.25f);

//		xShape = p.loadShape(FileUtil.getFile("svg/ello-type.svg")).getTessellation();
//		PShapeUtil.centerShape(xShape);
//		PShapeUtil.scaleShapeToExtent(xShape, p.width * 0.4f);
		
		feedbackShader = loadShader(FileUtil.getFile("haxademic/shaders/filters/feedback-radial.glsl"));
	}
	
	protected void drawImg(boolean black) {
		buffer.beginDraw();
		xShape.disableStyle();
		buffer.fill(127f + 127f * P.sin(progressRads));
		if(black) buffer.fill(0);
		buffer.translate(buffer.width/2, buffer.height/2);
//		buffer.rotate(progressRads * 0.25f);
		buffer.shape(xShape);
		buffer.endDraw();
	}
	
	protected void applyFeedbackToBuffer() {
//		feedbackShader.set("samplemult", P.map(p.mouseY, 0, p.height, 0.85f, 1.15f) );
		feedbackShader.set("amp", P.map(p.mouseX, 0, p.width, 0f, 0.004f) );
//		feedbackShader.set("amp", 0.0001f);
		feedbackShader.set("waveAmp", P.map(p.mouseX, 0, p.width, 0f, 0.005f) );
		feedbackShader.set("waveFreq", P.map(p.mouseY, 0, p.height, 0f, 10f) );
		for (int i = 0; i < 1; i++) buffer.filter(feedbackShader); 
	}

	public void drawApp() {
		progress = (p.frameCount % frames) / frames;
		progressRads = progress * P.TWO_PI;
		p.debugView.setValue("progress", progress);
		
		background(255);
		
		// draw on top of image
		drawImg(false);

		// apply feedback
		applyFeedbackToBuffer();
		
		// draw again to see the full image on top
		drawImg(true);

		// draw to screen
		p.image(buffer, 0, 0);
	}
}

