package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.FeedbackRadialFilter;
import com.haxademic.core.draw.filters.pshader.RepeatFilter;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

public class Demo_TrigInfinity
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected int FRAMES = 60 * 5;
	
	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}
	
	protected void firstFrame() {
		pg = PG.newPG32(pg.width, pg.height, true, false);
	}

	protected void drawApp() {
		p.background(0);
		 
		// begin draw & preprocess fx
		pg.beginDraw();
		if(p.frameCount == 1) pg.background(0);
		BrightnessStepFilter.instance().setBrightnessStep(-1f/255f);
		BrightnessStepFilter.instance().applyTo(pg);
//		RepeatFilter.instance().setOffset(0, -0.001f);
		RepeatFilter.instance().setZoom(1.03f);
		RepeatFilter.instance().setOffset(0f, 0f);
		RepeatFilter.instance().applyTo(pg);
		FeedbackRadialFilter.instance().setAmp(0.009f);
		FeedbackRadialFilter.instance().setWaveAmp(1f);
		FeedbackRadialFilter.instance().setWaveFreq(1f);
		FeedbackRadialFilter.instance().applyTo(pg);
		PG.setDrawCenter(pg);
		PG.setCenterScreen(pg);
		pg.noStroke();
		
		// draw infinity shape
		float radius = pg.height * 0.3f;
		float particleSize = pg.height * 0.1f;
		float x = P.cos(FrameLoop.progressRads()) * radius;
		float y = P.sin(FrameLoop.progressRads() * 2f) * radius / 3f;
		pg.blendMode(PBlendModes.ADD);
		pg.fill(255);
//		pg.ellipse(x, y, 40, 40);
		pg.image(DemoAssets.particle(), x, y, particleSize, particleSize);
		pg.blendMode(PBlendModes.BLEND);
		pg.endDraw();
		
		// postprocess
//		Pixelate2Filter.instance().applyTo(pg);
//		ThresholdFilter.instance().applyTo(pg);
		
		// draw to screen
		p.image(pg, 0, 0);
		
//		InvertFilter.instance().applyTo(p);
	}

}
