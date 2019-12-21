package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.FeedbackRadialFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

import processing.core.PShape;

public class Demo_FeedbackRadialToAlpha
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float frames = 240;
	protected float progress = 0;
	protected float progressRads = 0;
	protected int W = 800;
	protected int H = 800;
	protected PShape xShape;
	
	protected String feedbackAmp = "feedbackAmp";
	protected String feedbackMultX = "feedbackMultX";
	protected String feedbackMultY = "feedbackMultY";
	protected String feedbackBrightMult = "feedbackBrightMult";
	protected String feedbackAlphaMult = "feedbackAlphaMult";
	protected String feedbackWaveAmp = "feedbackWaveAmp";
	protected String feedbackWaveFreq = "feedbackWaveFreq";
	protected String feedbackWaveStartMult = "feedbackWaveStartMult";
	protected String shapeSpinMult = "shapeSpinMult";


	protected void config() {
		Config.setProperty( AppSettings.WIDTH, W );
		Config.setProperty( AppSettings.HEIGHT, H );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, P.round(1 + frames * 3) );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, P.round(1 + frames * 4) );
	}

	public void firstFrame() {
		pg = PG.newPG(W, H); 
		
		xShape = DemoAssets.shapeX().getTessellation();
		PShapeUtil.centerShape(xShape);
		PShapeUtil.scaleShapeToMaxAbsY(xShape, p.height * 0.25f);
		
		UI.addSlider(feedbackAmp, 0.001f, 0.00001f, 0.005f, 0.00001f, false);
		UI.addSlider(feedbackMultX, 1f, 0f, 1f, 0.001f, false);
		UI.addSlider(feedbackMultY, 1f, 0f, 1f, 0.001f, false);
		UI.addSlider(feedbackBrightMult, 1f, 0.9f, 1.1f, 0.0001f, false);
		UI.addSlider(feedbackAlphaMult, 0.99f, 0.9f, 1f, 0.001f, false);
		UI.addSlider(feedbackWaveAmp, 0.1f, 0f, 1f, 0.001f, false);
		UI.addSlider(feedbackWaveFreq, 10f, 0f, 100f, 0.1f, false);
		UI.addSlider(feedbackWaveStartMult, 0.01f, -0.2f, 0.2f, 0.001f, false);
		
		UI.addSlider(shapeSpinMult, 0.01f, -0.05f, 0.05f, 0.001f, false);
	}
	
	protected void drawImg(boolean black) {
		pg.beginDraw();
		xShape.disableStyle();
		pg.fill(127f + 127f * P.sin(progressRads));
		pg.translate(pg.width/2, pg.height/2);
		pg.rotate(p.frameCount * UI.value(shapeSpinMult));
		pg.shape(xShape);
		pg.endDraw();
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
		FeedbackRadialFilter.instance(P.p).applyTo(pg);
	}

	public void drawApp() {
		progress = (p.frameCount % frames) / frames;
		progressRads = progress * P.TWO_PI;
		DebugView.setValue("progress", progress);
		
		background(255, 255, 0);
		
		// draw on top of image
		drawImg(false);

		// apply feedback
		applyFeedbackToBuffer();

		// draw to screen
		p.image(pg, 0, 0);
	}
}

