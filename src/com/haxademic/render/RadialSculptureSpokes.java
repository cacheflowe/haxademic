package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.GodRays;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.render.FrameLoop;

import processing.core.PShape;

public class RadialSculptureSpokes 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape s;
	protected int FRAMES = 60*8;
	protected LinearFloat rotX = new LinearFloat(0, 0.025f);

	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setPgSize(1024*2, 1024*2);
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2 );
	}

	protected void firstFrame() {
		s = createShape(P.GROUP);
		
		float numRows = 12;
		float numCols = 36;
		float rowSpacing = pg.height * 0.02f;
		float staticLightsH = numRows * rowSpacing;
		float startY = -staticLightsH / 2f;
		float segRads = P.TWO_PI / numCols;
		float staticRadius = pg.width * 0.3f;
		float totemRadius = pg.width * 0.15f;
		
		for (int i = 0; i < numCols; i++) {
			for (int j = 0; j < numRows; j++) {
				float colRads = segRads * i;
				float lightX = P.cos(colRads) * staticRadius;
				float lightZ = P.sin(colRads) * staticRadius;
				float lightY = startY + j * rowSpacing;
				float lightFloorX = P.cos(colRads) * totemRadius;
				float lightFloorZ = P.sin(colRads) * totemRadius;
				
				PShape subShape = createShape();
				subShape.beginShape();
				subShape.strokeWeight(4);
				subShape.stroke(255, 127);
				subShape.noFill();
				subShape.vertex(lightX, lightY, lightZ);
				subShape.quadraticVertex(lightFloorX, lightY, lightFloorZ, lightFloorX, staticLightsH, lightFloorZ);
				subShape.endShape();
				
				s.addChild(subShape);
				
				PShape subPoint = createShape();
				subPoint.beginShape(P.POINTS);
				subPoint.strokeWeight(8);
				subPoint.stroke(255);
				subPoint.fill(255);
				subPoint.vertex(lightX, lightY, lightZ);
				subPoint.endShape();
				
				s.addChild(subPoint);
			}
		}
	}

	protected void drawApp() {
		background(0);
		
		// set context
		// we're drawing retina style to double-sized pg
		pg.beginDraw();
		pg.background(0);
		pg.ortho();
//		pg.perspective();
		PG.setCenterScreen(pg);
		PG.setDrawFlat2d(pg, true);	// solves stroke transparency issues/artifacts
//		PG.basicCameraFromMouse(p.g, 1.3f);
		
		// update eased rotation
		if(FrameLoop.loopCurFrame() == FRAMES / 2) rotX.setTarget(0);
		if(FrameLoop.loopCurFrame() == 1) rotX.setTarget(1);
		rotX.setInc(0.01f);
		rotX.update();
		float easingRot = Penner.easeInOutExpo(rotX.value());
		
		// set rotation on context, add lights & draw cached shape
		pg.rotateX(-P.HALF_PI + P.HALF_PI * 0.88f * easingRot);
		pg.rotateY(FrameLoop.progressRads() * 0.25f);
		PG.setBetterLights(pg);
		pg.shape(s);
		pg.endDraw();
		
		// postprocess
		// post process
		BloomFilter.instance(p).setStrength(0.2f);
		BloomFilter.instance(p).setBlurIterations(5);
		BloomFilter.instance(p).setBlendMode(BloomFilter.BLEND_SCREEN);
//		BloomFilter.instance(p).applyTo(pg);
		BloomFilter.instance(p).applyTo(pg);
		
		GodRays.instance(p).setDecay(0.2f);
		GodRays.instance(p).setWeight(0.7f);
		GodRays.instance(p).setRotation(1.9f + 0.4f * P.sin(FrameLoop.progressRads() * 3f));
		GodRays.instance(p).setAmp(0.8f);
//		GodRays.instance(p).applyTo(pg);
		
		VignetteFilter.instance(p).setDarkness(0.9f);
		VignetteFilter.instance(p).applyTo(pg);

		GrainFilter.instance(p).setTime(p.frameCount * 0.02f);
		GrainFilter.instance(p).setCrossfade(0.02f);
		GrainFilter.instance(p).applyTo(pg);
		

		
		// draw to screen
		ImageUtil.drawImageCropFill(pg, p.g, true, false);
	}

}