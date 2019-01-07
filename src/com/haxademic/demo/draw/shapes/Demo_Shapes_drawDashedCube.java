package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

public class Demo_Shapes_drawDashedCube 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected LinearFloat tiltProgress = new LinearFloat(0, 0.025f);
	protected float easedProgress = 0;
	
	protected LinearFloat[] easings;
	
	protected void overridePropsFile() {
		int FRAMES = 250;
		boolean rendering = false;
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 ); // 1140
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, rendering );
		p.appConfig.setProperty( AppSettings.SCREEN_X, 0 );
		p.appConfig.setProperty( AppSettings.SCREEN_Y, 0 );
		p.appConfig.setProperty( AppSettings.ALWAYS_ON_TOP, false );
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, rendering );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, (FRAMES * 2) + 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (FRAMES * 3) + 1 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
	}

	protected void setupFirstFrame() {
	}
	
	public void drawApp() {
		// debug input
		p.debugView.setValue("p.mousePercentX()", p.mousePercentX());
		p.debugView.setValue("p.mousePercentY()", p.mousePercentY());

		// context & camera
		p.background(0);
		DrawUtil.setCenterScreen(p);
		DrawUtil.setDrawCenter(p);
		p.lights();
		p.ortho();
//		p.perspective();
//		DrawUtil.basicCameraFromMouse(p.g, 1f);
		
		// progress through loop
		if(p.loop.progress() > 0.5f) tiltProgress.setTarget(1);
		else tiltProgress.setTarget(0);
		tiltProgress.update();
		easedProgress = Penner.easeInOutCubic(tiltProgress.value(), 0, 1, 1);
		
		// rotate for rendering
//		p.rotateY(P.QUARTER_PI * 0.75f);
//		p.rotateX(p.loop.progressRads());

		// almost-hexagon tilt
//		p.rotateY(easedProgress * P.PI * 0.2307f); // p.mousePercentX()
//		p.rotateX(easedProgress * P.PI * 0.34547f); // p.mousePercentY()

		// hexagon
//		p.rotateY(easedProgress * P.PI * 0.1968f); // p.mousePercentX()
//		p.rotateX(easedProgress * P.PI * 0.25f); // p.mousePercentY()

//		easedProgress = 1;

//		p.rotateY(easedProgress * P.QUARTER_PI + easedProgress * P.PI);
//		p.rotateX(easedProgress * P.QUARTER_PI);
		
		
		
		drawInfiniteZoomCubes();
//		drawNestedCubes();
//		drawSingleCube();
//		drawFlatGrid();
	}
	
	protected void drawCubeDashLerp() {
		Shapes.drawDashedCube(p.g, 500, 20f + P.sin(p.loop.progressRads()) * 5f, false);
	}
	
	protected void drawInfiniteZoomCubes() {
		// hexagon tilt
//		p.rotateY(P.PI * 0.1958f); // p.mousePercentX()
//		p.rotateX(P.PI * 0.25f); // p.mousePercentY()

		
		float numCubes = 60;
		float spacing = 40;
		for (int i = 0; i < numCubes; i++) {
			float cubeSize = i * spacing;
			cubeSize += p.loop.progress() * spacing;
//			drawDashedCube(cubeSize, 20f + P.sin(p.loop.progressRads()) * 5f);
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
			if((i*3) == p.loop.loopCurFrame()) easings[i].setTarget(1);
			if((i*3) + P.round(p.loop.frames() / 2) == p.loop.loopCurFrame()) easings[i].setTarget(0);
			easings[i].update();
			float easedFloat = Penner.easeInOutCubic(easings[i].value(), 0, 1, 1);

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
				float curOsc = 0.5f + 0.5f * P.sin(-p.loop.progressRads() + distanceToRadians);
				curOsc = Penner.easeInOutCubic(curOsc, 0, 1, 1);
				float tileOscSize = (tileSize / 2) + (tileSize / 2) * 1f; // curOsc;

				// position and draw
				p.pushMatrix();
				p.translate(tileX, tileY);
				p.rotateY(easedProgress * P.HALF_PI * curOsc); // 
				p.rotateX(easedProgress * P.HALF_PI * curOsc); // p.mousePercentY()
				Shapes.drawDashedCube(p.g, tileSize, tileSize / 10f, true);
				p.popMatrix();
			}
		}
	}
	
	
}