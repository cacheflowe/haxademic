package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

public class PolygonIncrementVertices 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int FRAMES = 600;
	protected EasingFloat verticesEased = new EasingFloat(3, 8);

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 960);
		p.appConfig.setProperty(AppSettings.HEIGHT, 960);
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 2);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 3);
	}
	
	protected void setupFirstFrame() {
	}
	
	protected void drawApp() {
		// context & camera
		pg.beginDraw();		
		
		// draw into new buffer
		BrightnessStepFilter.instance(p).setBrightnessStep(-3f/255f);
		BrightnessStepFilter.instance(p).applyTo(pg);
		
		// set line size
		pg.stroke(255);
		pg.strokeWeight(2f);
		pg.noFill();
		
		// context & camera & feedback
		pg.beginDraw();
//		pg.copy(0, 0, pg.width, pg.height, 2, 2, pg.width-4, pg.height-4);
		int feedbackDist = 2;
		pg.copy(0, 0, pg.width, pg.height, -feedbackDist, -feedbackDist, pg.width+feedbackDist*2, pg.height+feedbackDist*2);
		PG.setCenterScreen(pg);
		
		// draw into new buffer
		BrightnessStepFilter.instance(p).setBrightnessStep(-3f/255f);
		BrightnessStepFilter.instance(p).applyTo(pg);
		
		// set line size
		pg.fill(255);
//		pg.strokeWeight(2.5f);
		pg.noStroke();
//		pg.stroke(150f + 55f * P.sin(p.frameCount * 0.075f));

		float polySize = pg.width * 0.3f * (1f + 0.05f * P.sin(p.frameCount * 0.075f));
		float vertices = 5.5f + 2.5f * MathUtil.saw(p.frameCount * 0.0092f);
		verticesEased.setTarget(P.round(vertices));
		verticesEased.update(true);
		float segmentRads = P.TWO_PI / verticesEased.value();
		float rotOffset = P.HALF_PI + segmentRads/2f;
		pg.rotate(P.PI - rotOffset);
		
		pg.beginShape();
		for (float i = 0; i < verticesEased.value(); i++) {
			float curRads = i * segmentRads;
			float nextRads = curRads + segmentRads;
			if(nextRads > P.TWO_PI) nextRads = P.TWO_PI;
			float curX = P.cos(curRads) * polySize; 
			float curY = P.sin(curRads) * polySize; 
			float nextX = P.cos(nextRads) * polySize; 
			float nextY = P.sin(nextRads) * polySize; 
			pg.vertex(curX, curY);
			pg.vertex(nextX, nextY);
		}
		pg.endShape();
//		pg.pushMatrix();
//
//		pg.popMatrix();
		
		pg.endDraw();

		// post process
//		BloomFilter.instance(p).setStrength(0.1f);
//		BloomFilter.instance(p).setBlurIterations(6);
//		BloomFilter.instance(p).setBlendMode(BloomFilter.BLEND_SCREEN);
//		BloomFilter.instance(p).applyTo(pg);
////		BloomFilter.instance(p).applyTo(pg);
		
		VignetteFilter.instance(p).setDarkness(0.99f);
		VignetteFilter.instance(p).applyTo(pg);

		GrainFilter.instance(p).setTime(p.frameCount * 0.01f);
		GrainFilter.instance(p).setCrossfade(0.08f);
		GrainFilter.instance(p).applyTo(pg);
		
		// context end
		ImageUtil.cropFillCopyImage(pg, p.g, true);
	}
	
	public void keyPressed() {
		super.keyPressed();
//		if(p.key == ' ') printDirect.printImage(pg);
//		if(p.key == 'r') frameRand = P.round(p.random(9999999));
	}

	
}