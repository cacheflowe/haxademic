package com.haxademic.sketch.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PBlendModes;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.math.easing.LinearFloat;

public class CircleSphere 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int FRAMES = 100;
	protected float curRotY = 0;
	protected LinearFloat cameraProgress = new LinearFloat(0, 0.001f);

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1000);
		p.appConfig.setProperty(AppSettings.HEIGHT, 1000);
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES);
	}
	
	protected void setupFirstFrame() {
		
	}

	public void drawApp() {
		// set context
		pg.beginDraw();
		pg.blendMode(PBlendModes.BLEND);
		pg.background(0);
		pg.stroke(255);
		pg.noFill();
		pg.fill(0, 0);
		pg.ortho();
//		pg.perspective();
		DrawUtil.setCenterScreen(pg);
		DrawUtil.setDrawCenter(pg);
		DrawUtil.setBetterLights(pg);
		DrawUtil.basicCameraFromMouse(pg);
		
		// config
		float numCircles = 20;
		float segmentRads = P.PI / numCircles;
		float circleDiam = p.height * 0.6f;
		float circleRadius = circleDiam/2f;
		float spacingProgess = 1f / numCircles;
		
		// rotate, scale and draw
		for (float i = -2f; i < numCircles + 2f; i++) {
			float layoutYProgress = i / numCircles;
			float yRads = segmentRads * i;
			
			float y = (-1f + 2f * layoutYProgress) + (spacingProgess * p.loop.progress() * 6f);
			float x = P.cos(P.asin(y));

			pg.pushMatrix();
			pg.translate(0, y * circleRadius, 0);
			pg.rotateX(P.HALF_PI);
			pg.ellipse(0, 0, x * circleDiam, x * circleDiam);
			pg.popMatrix();
		}
		
		// context end
		pg.endDraw();
		
		// post process
		GrainFilter.instance(p).setTime(p.frameCount * 0.01f);
		GrainFilter.instance(p).setCrossfade(0.12f);
		GrainFilter.instance(p).applyTo(pg);
		
		BloomFilter.instance(p).setStrength(1f);
		BloomFilter.instance(p).setBlurIterations(5);
		BloomFilter.instance(p).setBlendMode(BloomFilter.BLEND_SCREEN);
		BloomFilter.instance(p).applyTo(pg);
		
		VignetteFilter.instance(p).setDarkness(0.4f);
		VignetteFilter.instance(p).applyTo(pg);

		// draw to screen
		p.image(pg, 0, 0);
	}
		
	public void keyPressed() {
		super.keyPressed();
//		if(p.key == ' ') 
	}

}