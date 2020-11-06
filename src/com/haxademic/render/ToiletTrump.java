package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.camera.CameraUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.render.FrameLoop;

import processing.core.PImage;
import processing.core.PShape;

public class ToiletTrump 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape objEarth;
	protected PShape objToilet;
	protected PShape objTP;
	protected PShape objTrump;
	protected PImage bgStars;
	protected int FRAMES = 60*13;
	
	protected void config() {
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.WIDTH, 1024 );
		Config.setProperty( AppSettings.HEIGHT, 1024 );
		Config.setProperty( AppSettings.RENDERING_MOVIE, true );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, P.round(1 + FRAMES) );
	}

	protected void firstFrame() {
		bgStars = P.getImage("images/textures/space/star_field_full_gal.jpg");
		
		// earth
		objEarth = p.loadShape( FileUtil.getPath("models/poly/earth/Earth.obj"));
		PShapeUtil.centerShape(objEarth);
		PShapeUtil.meshFlipOnAxis(objEarth, P.Y);
		PShapeUtil.scaleShapeToHeight(objEarth, p.height * 0.5f);

		// trump
		objTrump = p.loadShape( FileUtil.getPath("models/Trump_lowPoly_updated.obj"));
		PShapeUtil.scaleShapeToHeight(objTrump, p.height * 0.5f);
		PShapeUtil.meshFlipOnAxis(objTrump, P.Y);
		PShapeUtil.centerShape(objTrump);
//		PShapeUtil.meshFlipOnAxis(objTrump, P.Y);
//		PShapeUtil.scaleShapeToHeight(objTrump, p.height * 0.5f);

		// toilet
		objToilet = p.loadShape( FileUtil.getPath("models/poly/toilet/Toilet_01.obj"));
		PShapeUtil.centerShape(objToilet);
		PShapeUtil.meshFlipOnAxis(objToilet, P.Y);
		PShapeUtil.scaleShapeToHeight(objToilet, p.height * 1.3f);
		PShapeUtil.meshRotateOnAxis(objToilet, -P.HALF_PI, P.Y);

		// tp
		objTP = p.loadShape( FileUtil.getPath("models/poly/toilet-paper/toilet-paper.obj"));
		PShapeUtil.centerShape(objTP);
		PShapeUtil.scaleShapeToHeight(objTP, p.height * 0.1f);
	}

	protected void drawApp() {
		p.background(0, 0, 0);
		p.noStroke();
		CameraUtil.setCameraDistance(p.g, 100, 50000);

		// camera position (via scale)
		float curScale = 0.5f + 0.5f * P.sin(1 + FrameLoop.progressRads());
		curScale = Penner.easeInOutQuint(curScale);
		
		// background image
		p.push();
		PG.setCenterScreen(p);
		PG.setDrawCenter(p);
		p.translate(0, 0, -15000);
		p.scale(14 + 1f * curScale);
		p.image(bgStars, 0, 0);
		p.pop();
		
//		p.ortho();
		p.perspective();

		// setup lights
		PG.setDrawCorner(p);
		PG.setCenterScreen(p);
		p.translate(0, 0, -1000);
//		PG.setBetterLights(p);
		PG.setBasicLights(p);
//		p.ambient(255);
		float amb = 10;
		p.ambientLight(amb, amb, amb);
		float dir = 40;
		p.directionalLight(dir, dir, dir, 1, 1, 1); 

		// scale for camera
		p.scale(0.15f + 0.85f * curScale);
		
		// camera
		p.noStroke();
//		p.rotateX(P.map(Mouse.xNorm, 0, 1, -2, 2));
		p.rotateX(-0.5f + 0.05f * P.sin(FrameLoop.progressRads()));
//		p.rotateY(-0.75f + 0.1f * P.sin(FrameLoop.progressRads()));
		p.rotateY(FrameLoop.progressRads());
//		p.scale(0.5f);
		
		// world
		float earthY = 50000 * Mouse.yEasedNorm;
		float earthScale = 50 * Mouse.xEasedNorm;
		float earthRotX = -Mouse.yNorm * 2f;
		float earthRotZ = Mouse.xNorm * 2f;
		DebugView.setValue("earthY", earthY);
		DebugView.setValue("earthScale", earthScale);
		DebugView.setValue("earthRotX", earthRotX);
		DebugView.setValue("earthRotZ", earthRotZ);
		earthY = 8040;
		earthScale = 29;
		earthRotX = -0.917f;
		earthRotZ = 0.197f;
		p.push();
		p.translate(0, earthY, 0);
		p.rotateX(earthRotX);
		p.rotateZ(earthRotZ);
		p.scale(earthScale);
		p.shape(objEarth);
		p.pop();
		
		// trump
		p.push();
		p.translate(0, -p.width * 0.05f + P.sin(FrameLoop.progressRads() * 12f) * p.width * 0.02f, p.width * 0.08f);
		p.rotateY(-FrameLoop.progressRads() * 8f);
		p.shape(objTrump);
		p.pop();
		
		// toilet
		p.push();
		p.rotateY(P.PI);
		p.shape(objToilet);	
		p.pop();
		
		// tp
		p.push();
//		p.rotateX(P.HALF_PI);
		p.translate(p.width * 0.25f, -p.height * 0.7f, -p.width * 0.42f);
		p.rotateY(P.HALF_PI);
		p.shape(objTP);
		p.pop();
		
		
		// water
		PG.setDrawCenter(p);
		p.push();
		p.rotateX(P.HALF_PI);
		p.fill(97, 149, 185);
		p.rect(0, 0, p.width * 0.6f, p.width * 0.75f);
		p.pop();
	}

}