package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.filters.pshader.FeedbackRadialFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_FeedbackRadialToAlpha
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	float frames = 240;
	float progress = 0;
	float progressRads = 0;
	int W = 800;
	int H = 800;
	PGraphics buffer;
	PShape xShape;

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
		
		xShape = DemoAssets.shapeX().getTessellation();
		PShapeUtil.centerShape(xShape);
		PShapeUtil.scaleShapeToMaxAbsY(xShape, p.height * 0.25f);
	}
	
	protected void drawImg(boolean black) {
		buffer.beginDraw();
		xShape.disableStyle();
		buffer.fill(127f + 127f * P.sin(progressRads));
		buffer.translate(buffer.width/2, buffer.height/2);
		buffer.rotate(progressRads * 0.25f);
		buffer.shape(xShape);
		buffer.endDraw();
	}
	
	protected void applyFeedbackToBuffer() {
		FeedbackRadialFilter.instance(P.p).setAmp(P.map(p.mouseY, 0, p.height, 0.85f, 100.15f));
		FeedbackRadialFilter.instance(P.p).setSampleMult(1f / 255f * 1f);
		FeedbackRadialFilter.instance(P.p).setWaveAmp(1f / 255f * 1.2f);
		FeedbackRadialFilter.instance(P.p).setWaveFreq(1f);
		FeedbackRadialFilter.instance(P.p).setAlphaMult(0.96f);
		FeedbackRadialFilter.instance(P.p).applyTo(buffer);
	}

	public void drawApp() {
		progress = (p.frameCount % frames) / frames;
		progressRads = progress * P.TWO_PI;
		p.debugView.setValue("progress", progress);
		
		background(255, 255, 0);
		
		// draw on top of image
		if(p.frameCount % 40 < 20) drawImg(false);

		// apply feedback
		applyFeedbackToBuffer();
		
		// draw again to see the full image on top
//		drawImg(true);

		// draw to screen
		p.image(buffer, 0, 0);
	}
}

