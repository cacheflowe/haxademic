package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.render.FrameLoop;

public class Demo_Shapes_drawDashedCube 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected LinearFloat tiltProgress = new LinearFloat(0, 0.025f);
	protected float easedProgress = 0;
	
	protected LinearFloat[] easings;
	
	protected void config() {
		int FRAMES = 250;
		boolean rendering = false;
		Config.setProperty( AppSettings.WIDTH, 1280 ); // 1140
		Config.setProperty( AppSettings.HEIGHT, 720 );
		Config.setProperty( AppSettings.FULLSCREEN, rendering );
		Config.setProperty( AppSettings.SCREEN_X, 0 );
		Config.setProperty( AppSettings.SCREEN_Y, 0 );
		Config.setProperty( AppSettings.ALWAYS_ON_TOP, false );
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE, rendering );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, (FRAMES * 2) + 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (FRAMES * 3) + 1 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
	}

	protected void firstFrame() {
	}
	
	protected void drawApp() {
		// debug input
		DebugView.setValue("Mouse.xNorm", Mouse.xNorm);
		DebugView.setValue("Mouse.yNorm", Mouse.yNorm);

		// context & camera
		p.background(0);
		PG.setCenterScreen(p);
		PG.setDrawCenter(p);
		p.lights();
		p.ortho();
//		p.perspective();
//		PG.basicCameraFromMouse(p.g, 1f);
		
		// progress through loop
		if(FrameLoop.progress() > 0.5f) tiltProgress.setTarget(1);
		else tiltProgress.setTarget(0);
		tiltProgress.update();
		easedProgress = Penner.easeInOutCubic(tiltProgress.value());
		
		// rotate for rendering
//		p.rotateY(P.QUARTER_PI * 0.75f);
//		p.rotateX(AnimationLoop.progressRads());

		// almost-hexagon tilt
//		p.rotateY(easedProgress * P.PI * 0.2307f); // Mouse.xNorm
//		p.rotateX(easedProgress * P.PI * 0.34547f); // Mouse.yNorm

		// hexagon
//		p.rotateY(easedProgress * P.PI * 0.1968f); // Mouse.xNorm
//		p.rotateX(easedProgress * P.PI * 0.25f); // Mouse.yNorm

//		easedProgress = 1;

//		p.rotateY(easedProgress * P.QUARTER_PI + easedProgress * P.PI);
//		p.rotateX(easedProgress * P.QUARTER_PI);
		
		
		
		drawInfiniteZoomCubes();
//		drawNestedCubes();
//		drawSingleCube();
//		drawFlatGrid();
	}
	
	protected void drawCubeDashLerp() {
		Shapes.drawDashedCube(p.g, 500, 20f + P.sin(FrameLoop.progressRads()) * 5f, false);
	}
	
	protected void drawInfiniteZoomCubes() {
		// hexagon tilt
//		p.rotateY(P.PI * 0.1958f); // Mouse.xNorm
//		p.rotateX(P.PI * 0.25f); // Mouse.yNorm

		
		float numCubes = 60;
		float spacing = 40;
		for (int i = 0; i < numCubes; i++) {
			float cubeSize = i * spacing;
			cubeSize += FrameLoop.progress() * spacing;
//			drawDashedCube(cubeSize, 20f + P.sin(AnimationLoop.progressRads()) * 5f);
			p.pushMatrix();
//			p.rotateZ(0.1f * P.sin(cubeSize * 0.01f));
			Shapes.drawDashedCube(p.g, cubeSize, 2f + (cubeSize * 0.08f), false);
			p.popMatrix();
		}
	}
	
	protected void drawNestedCubes() {
		float cubeSize = 2000;
		float numCubes = 40;

		// lazy init easings
		if(easings == null) {
			easings = new LinearFloat[(int) numCubes];
			for (int i = 0; i < easings.length; i++) {
				easings[i] = new LinearFloat(0, 0.025f);
			}
		}
		
		// draw and trigger easing value targets on interval
		for (int i = 0; i < numCubes; i++) {
			// animate with offset
			if((i*3) == FrameLoop.loopCurFrame()) easings[i].setTarget(1);
			if((i*3) + P.round(FrameLoop.loopFrames() / 2) == FrameLoop.loopCurFrame()) easings[i].setTarget(0);
			easings[i].update();
			float easedFloat = Penner.easeInOutCubic(easings[i].value());

			float dir = (i % 2 == 0) ? -0.25f : 0.25f;
			// draw!
			p.pushMatrix();
//			p.rotateY(easedFloat * P.HALF_PI);
			float cubeSizeDiff = cubeSize / numCubes;
			p.translate(cubeSizeDiff * easedFloat * dir, cubeSizeDiff * easedFloat * dir);
			float curCubeSize = cubeSize - i * (cubeSizeDiff);
			Shapes.drawDashedCube(p.g, curCubeSize, curCubeSize / (numCubes - i), true);
			p.popMatrix();
		}
	}
	
	protected void drawSingleCube() {
		// draw single dashed cube
		float cubeSize = 500;
		Shapes.drawDashedCube(p.g, cubeSize, cubeSize / 10f, true);
	}
	
	protected void drawFlatGrid() {
		// draw dashed cube grid
		// calc grid values
		float tileSize = 100f;
		float cols = p.width / tileSize;
		float rows = p.height / tileSize;
		float startX = -p.width / 2 + tileSize / 2;
		float startY = -p.height / 2 + tileSize / 2;
		float centerX = 0;
		float centerY = 0;
		
		// draw grid of tiles
		for (int x = 0; x < cols; x++) {
			for (int y = 0; y < rows; y++) {
				float tileX = startX + x * tileSize;
				float tileY = startY + y * tileSize;
				
				// map distance to sin offset
				float distanceFromCenter = MathUtil.getDistance(tileX, tileY, centerX, centerY);
				float distanceToRadians = P.map(distanceFromCenter, 0, 1000, 0, P.TWO_PI * 1f);
				
				// calc wave
				float curOsc = 0.5f + 0.5f * P.sin(-FrameLoop.progressRads() + distanceToRadians);
				curOsc = Penner.easeInOutCubic(curOsc);
				// float tileOscSize = (tileSize / 2) + (tileSize / 2) * 1f; // curOsc;

				// position and draw
				p.pushMatrix();
				p.translate(tileX, tileY);
				p.rotateY(easedProgress * P.HALF_PI * curOsc); // 
				p.rotateX(easedProgress * P.HALF_PI * curOsc); // Mouse.yNorm
				Shapes.drawDashedCube(p.g, tileSize, tileSize / 10f, true);
				p.popMatrix();
			}
		}
	}
	
	
}