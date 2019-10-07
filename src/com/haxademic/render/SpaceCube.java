package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.camera.CameraUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.math.easing.EasingFloat;

public class SpaceCube 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int FRAMES = 600;
	protected EasingFloat rot1X = new EasingFloat(0, 8);
	protected EasingFloat rot1Y = new EasingFloat(0, 8);
	protected EasingFloat rot1Z = new EasingFloat(0, 8);
	protected EasingFloat rot2X = new EasingFloat(0, 10);
	protected EasingFloat rot2Y = new EasingFloat(0, 10);
	protected EasingFloat rot2Z = new EasingFloat(0, 10);
	protected EasingFloat rot3X = new EasingFloat(0, 12);
	protected EasingFloat rot3Y = new EasingFloat(0, 12);
	protected EasingFloat rot3Z = new EasingFloat(0, 12);

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
	
	public void drawApp() {
		// set context
		pg.beginDraw();
		pg.background(10, 0, 5);
		PG.setCenterScreen(pg);
		PG.setDrawCenter(pg);
		PG.setBetterLights(pg);
		CameraUtil.setCameraDistance(pg, 100, 20000);

		// rotate camera
		float targetX = P.map(P.p.mousePercentY(), 0, 1, P.PI, -P.PI);
		float targetY = P.map(P.p.mousePercentX(), 0, 1, P.PI, -P.PI);
		float targetZ = 0;
		
		// loop override
		if(loop.progress() < 0.2f) {
			targetX = 0;
			targetY = 0;
			targetZ = 0;
		} else if(loop.progress() < 0.4f) {
			targetX = P.PI;
			targetY = P.QUARTER_PI;
			targetZ = 0;
		} else if(loop.progress() < 0.6f) {
			targetX = P.QUARTER_PI - 0.166666f;
			targetY = P.QUARTER_PI;
			targetZ = 0;
		} else if(loop.progress() < 0.8f) {
			targetX = 0;
			targetY = -P.PI;
			targetZ = P.QUARTER_PI;
		} else if(loop.progress() < 1f) {
			targetX = P.QUARTER_PI;
			targetY = -P.PI;
			targetZ = 0;
		}  
		
		rot1X.setTarget(targetX);
		rot1X.update(true);
		rot1Y.setTarget(targetY);
		rot1Y.update(true);
		rot1Z.setTarget(targetZ);
		rot1Z.update(true);
		rot2X.setTarget(targetX);
		rot2X.update(true);
		rot2Y.setTarget(targetY);
		rot2Y.update(true);
		rot2Z.setTarget(targetZ);
		rot2Z.update(true);
		rot3X.setTarget(targetX);
		rot3X.update(true);
		rot3Y.setTarget(targetY);
		rot3Y.update(true);
		rot3Z.setTarget(targetZ);
		rot3Z.update(true);

		
		// draw box
		float boxSize = 120;
		pg.fill(50, 5, 200);
		pg.stroke(255);
		pg.strokeWeight(3f);
		pg.pushMatrix();
		pg.rotateX(rot1X.value());
		pg.rotateY(rot1Y.value());
		pg.rotateZ(rot1Z.value());
		pg.box(boxSize);
		pg.popMatrix();
		
		// draw outer shell
		float cylRadius = 3;
		float outerBox = boxSize * 1f;
		float outerBox2 = outerBox * 2f;
		
		pg.pushMatrix();
		pg.rotateX(rot2X.value());
		pg.rotateY(rot2Y.value());
		pg.rotateZ(rot2Z.value());
		
		pg.noStroke();
		pg.fill(50, 30, 30);
		pg.pushMatrix(); pg.translate( outerBox, 0,  outerBox); Shapes.drawCylinder(pg, 12, cylRadius, cylRadius, outerBox2, false); pg.popMatrix();
		pg.pushMatrix(); pg.translate(-outerBox, 0,  outerBox); Shapes.drawCylinder(pg, 12, cylRadius, cylRadius, outerBox2, false); pg.popMatrix();
		pg.pushMatrix(); pg.translate( outerBox, 0, -outerBox); Shapes.drawCylinder(pg, 12, cylRadius, cylRadius, outerBox2, false); pg.popMatrix();
		pg.pushMatrix(); pg.translate(-outerBox, 0, -outerBox); Shapes.drawCylinder(pg, 12, cylRadius, cylRadius, outerBox2, false); pg.popMatrix();
		
		pg.pushMatrix();
		pg.rotateX(P.HALF_PI);
		pg.pushMatrix(); pg.translate( outerBox, 0,  outerBox); Shapes.drawCylinder(pg, 12, cylRadius, cylRadius, outerBox2, false); pg.popMatrix();
		pg.pushMatrix(); pg.translate(-outerBox, 0,  outerBox); Shapes.drawCylinder(pg, 12, cylRadius, cylRadius, outerBox2, false); pg.popMatrix();
		pg.pushMatrix(); pg.translate( outerBox, 0, -outerBox); Shapes.drawCylinder(pg, 12, cylRadius, cylRadius, outerBox2, false); pg.popMatrix();
		pg.pushMatrix(); pg.translate(-outerBox, 0, -outerBox); Shapes.drawCylinder(pg, 12, cylRadius, cylRadius, outerBox2, false); pg.popMatrix();
		pg.popMatrix();
		
		pg.pushMatrix();
		pg.rotateZ(P.HALF_PI);
		pg.pushMatrix(); pg.translate( outerBox, 0,  outerBox); Shapes.drawCylinder(pg, 12, cylRadius, cylRadius, outerBox2, false); pg.popMatrix();
		pg.pushMatrix(); pg.translate(-outerBox, 0,  outerBox); Shapes.drawCylinder(pg, 12, cylRadius, cylRadius, outerBox2, false); pg.popMatrix();
		pg.pushMatrix(); pg.translate( outerBox, 0, -outerBox); Shapes.drawCylinder(pg, 12, cylRadius, cylRadius, outerBox2, false); pg.popMatrix();
		pg.pushMatrix(); pg.translate(-outerBox, 0, -outerBox); Shapes.drawCylinder(pg, 12, cylRadius, cylRadius, outerBox2, false); pg.popMatrix();
		pg.popMatrix();
		
		// draw outer shell connectors
		float sphereRadius = cylRadius * 1.4f;
//		pg.fill(0, 0, 0);
		pg.pushMatrix(); pg.translate( outerBox, outerBox,  outerBox); pg.sphere(sphereRadius); pg.popMatrix();
		pg.pushMatrix(); pg.translate(-outerBox, outerBox,  outerBox); pg.sphere(sphereRadius); pg.popMatrix();
		pg.pushMatrix(); pg.translate( outerBox, outerBox, -outerBox); pg.sphere(sphereRadius); pg.popMatrix();
		pg.pushMatrix(); pg.translate(-outerBox, outerBox, -outerBox); pg.sphere(sphereRadius); pg.popMatrix();
		pg.pushMatrix(); pg.translate( outerBox, -outerBox,  outerBox); pg.sphere(sphereRadius); pg.popMatrix();
		pg.pushMatrix(); pg.translate(-outerBox, -outerBox,  outerBox); pg.sphere(sphereRadius); pg.popMatrix();
		pg.pushMatrix(); pg.translate( outerBox, -outerBox, -outerBox); pg.sphere(sphereRadius); pg.popMatrix();
		pg.pushMatrix(); pg.translate(-outerBox, -outerBox, -outerBox); pg.sphere(sphereRadius); pg.popMatrix();

		pg.popMatrix();
		
		// outer dashed cube
		pg.pushMatrix();
		pg.rotateX(rot3X.value());
		pg.rotateY(rot3Y.value());
		pg.rotateZ(rot3Z.value());
		
		pg.strokeWeight(2f);
		pg.stroke(200, 100, 200);
		Shapes.drawDashedCube(pg, outerBox2 * 1.5f, outerBox2 / 10f, true);

		pg.popMatrix();
		
		// draw cube grid
		pg.fill(255, 127);
		pg.noStroke();	
//		pg.stroke(255);
//		pg.strokeWeight(1);	
		pg.pushMatrix();
		pg.rotateX(rot1X.value());
		pg.rotateY(rot1Y.value());
		pg.rotateZ(rot1Z.value());
		pg.sphereDetail(6);
		int spacing = P.round(outerBox2 * 0.25f);
		for (float x = -boxSize * 5 - spacing/2f; x < boxSize * 5; x+=spacing) {
			for (float y = -boxSize * 5 - spacing/2f; y < boxSize * 5; y+=spacing) {
				for (float z = -boxSize * 5 - spacing/2f; z < boxSize * 5; z+=spacing) {
					// pg.point(x, y, z);
					pg.pushMatrix();
					pg.translate(x, y, z);
					pg.sphere(1.3f);
					pg.popMatrix();
				}
			}
		}
		pg.popMatrix();
		
		// post process
		BloomFilter.instance(p).setStrength(1f);
		BloomFilter.instance(p).setBlurIterations(6);
		BloomFilter.instance(p).setBlendMode(BloomFilter.BLEND_SCREEN);
		BloomFilter.instance(p).applyTo(pg);
//		BloomFilter.instance(p).applyTo(pg);
		
		VignetteFilter.instance(p).setDarkness(0.99f);
		VignetteFilter.instance(p).applyTo(pg);

		GrainFilter.instance(p).setTime(p.frameCount * 0.01f);
		GrainFilter.instance(p).setCrossfade(0.08f);
		GrainFilter.instance(p).applyTo(pg);
		
		// context end
		pg.endDraw();
		ImageUtil.cropFillCopyImage(pg, p.g, true);
	}
	
	public void keyPressed() {
		super.keyPressed();
//		if(p.key == ' ') printDirect.printImage(pg);
//		if(p.key == 'r') frameRand = P.round(p.random(9999999));
	}

	
}