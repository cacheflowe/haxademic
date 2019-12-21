package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.filters.pshader.FeedbackRadialFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PShape;

public class Demo_FeedbackRadialShader
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float frames = 60;
	protected float progress = 0;
	protected float progressRads = 0;
	protected int W = 1600;
	protected int H = 800;
	protected PGraphics buffer;
	protected PShape xShape;

	protected String feedbackAmp = "feedbackAmp";
	protected String feedbackMultX = "feedbackMultX";
	protected String feedbackMultY = "feedbackMultY";
	protected String feedbackBrightMult = "feedbackBrightMult";
	protected String feedbackAlphaMult = "feedbackAlphaMult";
	protected String feedbackWaveAmp = "feedbackWaveAmp";
	protected String feedbackWaveFreq = "feedbackWaveFreq";
	protected String feedbackWaveStartMult = "feedbackWaveStartMult";

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, W );
		p.appConfig.setProperty( AppSettings.HEIGHT, H );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, P.round(1 + frames * 3) );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, P.round(1 + frames * 4) );
	}

	public void setupFirstFrame() {

		
		buffer = P.p.createGraphics(W, H, PRenderers.P3D); 
		
		xShape = DemoAssets.shapeFractal().getTessellation();
		PShapeUtil.centerShape(xShape);
		PShapeUtil.scaleShapeToMaxAbsY(xShape, p.height * 0.25f);

//		xShape = p.loadShape(FileUtil.getFile("svg/ello-type.svg")).getTessellation();
//		PShapeUtil.centerShape(xShape);
//		PShapeUtil.scaleShapeToExtent(xShape, p.width * 0.4f);
		
		UI.addSlider(feedbackAmp, 0.001f, 0.00001f, 0.005f, 0.00001f, false);
		UI.addSlider(feedbackMultX, 1f, 0f, 1f, 0.001f, false);
		UI.addSlider(feedbackMultY, 1f, 0f, 1f, 0.001f, false);
		UI.addSlider(feedbackBrightMult, 1f, 0.9f, 1.1f, 0.0001f, false);
		UI.addSlider(feedbackAlphaMult, 0.99f, 0.9f, 1f, 0.001f, false);
		UI.addSlider(feedbackWaveAmp, 0.1f, 0f, 1f, 0.001f, false);
		UI.addSlider(feedbackWaveFreq, 10f, 0f, 100f, 0.1f, false);
		UI.addSlider(feedbackWaveStartMult, 0.01f, -0.2f, 0.2f, 0.001f, false);
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
		FeedbackRadialFilter.instance(P.p).setAmp(UI.value(feedbackAmp));
		FeedbackRadialFilter.instance(P.p).setMultX(UI.value(feedbackMultX));
		FeedbackRadialFilter.instance(P.p).setMultY(UI.value(feedbackMultY));
		FeedbackRadialFilter.instance(P.p).setSampleMult(UI.value(feedbackBrightMult));
		FeedbackRadialFilter.instance(P.p).setWaveAmp(UI.value(feedbackWaveAmp));
		FeedbackRadialFilter.instance(P.p).setWaveFreq(UI.value(feedbackWaveFreq));
		FeedbackRadialFilter.instance(P.p).setWaveStart(p.frameCount * UI.value(feedbackWaveStartMult));
		FeedbackRadialFilter.instance(P.p).setAlphaMult(UI.value(feedbackAlphaMult));
		FeedbackRadialFilter.instance(P.p).applyTo(buffer);
	}

	public void drawApp() {
		progress = (p.frameCount % frames) / frames;
		progressRads = progress * P.TWO_PI;
		DebugView.setValue("progress", progress);
		
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

