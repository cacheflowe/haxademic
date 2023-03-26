package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.draw.filters.pshader.compound.ReactionDiffusionStepFilter;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class Demo_ReactionDiffusionStepFilter
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected String RD_ITERATIONS = "RD_ITERATIONS";
	protected String RD_BLUR_ITERATIONS = "RD_BLUR_ITERATIONS";
	protected String RD_BLUR_X = "RD_BLUR_X";
	protected String RD_BLUR_Y = "RD_BLUR_Y";
	protected String RD_SHARPEN = "RD_SHARPEN";
	protected String RD_THRESH_ACTIVE = "RD_THRESH_ACTIVE";
	protected String RD_THRESH_CROSSFADE = "RD_THRESH_CROSSFADE";
	protected String RD_THRESH_CUTOFF = "RD_THRESH_CUTOFF";

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 960 );
		Config.setProperty( AppSettings.HEIGHT, 960 );
		Config.setProperty( AppSettings.SHOW_FPS_IN_TITLE, true );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 300 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 2100 );
	}
	
	protected void firstFrame() {
		// set up UI
		UI.addTitle("ReactionDiffusionStepFilter");
		UI.addSlider(RD_ITERATIONS, 2, 0, 20, 1, false);
		UI.addSlider(RD_BLUR_ITERATIONS, 2, 0, 20, 1, false);
		UI.addSlider(RD_BLUR_X, 1f, 0, 1.7f, 0.01f, false);
		UI.addSlider(RD_BLUR_Y, 1f, 0, 1.7f, 0.01f, false);
		UI.addSlider(RD_SHARPEN, 2f, 0, 30, 0.1f, false);
		UI.addToggle(RD_THRESH_ACTIVE, true, false);
		UI.addSlider(RD_THRESH_CROSSFADE, 0.5f, 0, 1, 0.01f, false);
		UI.addSlider(RD_THRESH_CUTOFF, 0.5f, 0, 1, 0.01f, false);
	}

	public void drawOuterEnclosure(PGraphics pg, int color) {
		// outer circle enclosure
		PG.setDrawCenter(pg);
		pg.noFill();
		pg.stroke(color);
		pg.strokeWeight(300);
//		pg.ellipse(pg.width/2, pg.height/2, 900, 900);
		pg.ellipse(pg.width/2, pg.height/2, pg.width, pg.height);
		pg.ellipse(pg.width/2, pg.height/2, pg.width * 1.2f, pg.height * 1.2f);
	}
	
	public void drawSeedCircle(PGraphics pg, int color) {
		// outer circle enclosure
		PG.setDrawCenter(pg);
		pg.noFill();
		pg.stroke(color);
		pg.strokeWeight(pg.width * 0.05f);
//		pg.ellipse(pg.width/2, pg.height/2, 900, 900);
		pg.ellipse(pg.width/2, pg.height/2, pg.width * 0.3f, pg.height * 0.3f);
	}
	
	protected void drawApp() {
		// set up drawing context & border shape
		pg.beginDraw();
		if(p.frameCount == 1) {
			pg.background(0);
			drawSeedCircle(pg, 255);
		}
//		if(p.frameCount % 120 == 1) drawSeedCircle(pg, 255);
		drawOuterEnclosure(pg, 255);
		
		// R/D effect
		ReactionDiffusionStepFilter.applyTo(pg, 
				UI.valueInt(RD_ITERATIONS), 
				UI.valueInt(RD_BLUR_ITERATIONS), 
				UI.valueEased(RD_BLUR_X),
				UI.valueEased(RD_BLUR_Y), 
				UI.valueEased(RD_SHARPEN),
				UI.valueToggle(RD_THRESH_ACTIVE),
				UI.valueEased(RD_THRESH_CROSSFADE),
				UI.valueEased(RD_THRESH_CUTOFF)
		);
		
		// zoom/rotate
		RotateFilter.instance().setZoom(1f);
		RotateFilter.instance().setRotation(0.0001f);
//		RotateFilter.instance().setOffset(0, 0.0002f);
		RotateFilter.instance().applyTo(pg);
		
		pg.endDraw();
		
		// draw to screen
		p.image(pg, 0, 0);
	}

}

