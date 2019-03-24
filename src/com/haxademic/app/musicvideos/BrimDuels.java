package com.haxademic.app.musicvideos;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class BrimDuels 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float w = 1280;
	protected float h = 720;
	protected int FRAMES = 1038; // (17.299s)
	
	protected PImage backgroundImg;
	protected PImage waveform;
	
	protected PShape icosa;
	
	protected PImage floorSrcImg;
	protected PGraphics floorTexture;
	
	protected LinearFloat beatTime = new LinearFloat(0, 0.025f);
	
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, FRAMES + 1);
		if(p.appConfig.getBoolean(AppSettings.RENDERING_MOVIE, false) == true) {
			w = 1920;
			h = 1080;
		}
		p.appConfig.setProperty(AppSettings.WIDTH, (int) w);
		p.appConfig.setProperty(AppSettings.HEIGHT, (int) h);
		p.appConfig.setProperty(AppSettings.FPS, 60);
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.LOOP_TICKS, 64);
	}

	protected void setupFirstFrame() {
		backgroundImg = P.getImage("audio/brim-duels/bg-swirl.jpg");
		waveform = P.getImage("audio/brim-duels/output-16k-tran.png");
		buildPlanet();
		buildFloor();
	}

	public void drawApp() {
		background(0);
		
		// draw pre
		updateFloorTexture();
		
		// draw to main buffer
		pg.beginDraw();
		pg.background(0);
		pg.noStroke();
		DrawUtil.setBetterLights(pg);

		drawBackground();

		// set camera
//		pg.rotateX(-0.2f);
		pg.rotateX(-P.QUARTER_PI * p.mousePercentX());
		
		// draw components
//		drawWaveform();
		drawFloor();
		drawPlanet();
		
		beatTime.update();
		if(p.loop.isTick() && p.loop.curTick() < 64 - 8) {
			int eighthTick = p.loop.curTick() % 8;
			if(eighthTick == 0 || eighthTick == 3 || eighthTick == 6) {		// 1, 4, 7
				beatTime.setCurrent(1);
				beatTime.setTarget(0);
			}
		}
		
		pg.endDraw();
		
		// draw to screen
		p.image(pg, 0, 0);
		
		// debug info
		p.text(p.loop.curTick(), 20, 20);
	}
	
	//////////////////////////
	// BACKGROUND
	//////////////////////////
	
	protected void drawBackground() {
		DrawUtil.setDrawCenter(pg);
		DrawUtil.push(pg);
		
		float bgScale = 2f;
		pg.translate(pg.width * 0.5f, pg.height * 0.5f, -pg.width * 0.8f);
		pg.image(backgroundImg, 0, 0, backgroundImg.width * bgScale, backgroundImg.height * bgScale);
		
		DrawUtil.pop(pg);
		DrawUtil.setDrawCorner(pg);
	}
	
	//////////////////////////
	// FLOOR
	//////////////////////////
	
	protected void buildFloor() {
		floorTexture = p.createGraphics(500, 500, PRenderers.P3D);
//		floorSrcImg = P.getImage("images/textures/space/black-holes/BlackHole.jpg");
		floorSrcImg = DemoAssets.squareTexture();
	}
	
	protected void updateFloorTexture() {
		float srcScale = MathUtil.scaleToTarget(floorSrcImg.height, floorTexture.height * 1.5f);
		floorTexture.beginDraw();
		DrawUtil.setDrawCenter(floorTexture);
		DrawUtil.setCenterScreen(floorTexture);
		floorTexture.rotate(p.loop.progressRads());
		floorTexture.image(floorSrcImg, 0, 0, floorSrcImg.width * srcScale, floorSrcImg.height * srcScale);
		floorTexture.endDraw();
	}
	
	protected void drawFloor() {
		DrawUtil.setDrawCenter(pg);
		DrawUtil.push(pg);
		pg.translate(pg.width * 0.5f, pg.height * 0.6f);
		
		// draw floor - TODO: switch to textured?
		float floorSize = pg.width * 0.4f;
		pg.rotateY(P.QUARTER_PI);
		pg.rotateX(P.HALF_PI);
//		pg.fill(100, 255, 100);
//		pg.rect(0, 0, floorSize, floorSize);
		Shapes.drawTexturedRect(pg, floorTexture);
		
		DrawUtil.pop(pg);
		DrawUtil.setDrawCorner(pg);
	}
	
	//////////////////////////
	// PLANET
	//////////////////////////
	
	protected void buildPlanet() {
		PImage planetTexture = P.getImage("images/textures/space/saturn.jpg"); // DemoAssets.textureJupiter();
		
		icosa = Icosahedron.createIcosahedron(p.g, 4, planetTexture);
		PShapeUtil.scaleShapeToHeight(icosa, pg.height * 0.3f);
	}
	
	protected void drawPlanet() {
		DrawUtil.push(pg);
		pg.translate(pg.width * 0.5f, pg.height * 0.4f);
		
		// draw planet
		DrawUtil.push(pg);
		pg.rotateY(p.loop.progressRads());
		pg.shape(icosa, 0, 0);
		DrawUtil.pop(pg);
		
		// draw ring
		DrawUtil.push(pg);
		pg.rotateY(-P.HALF_PI - p.loop.progressRads());
		pg.rotateX(-P.HALF_PI);
		Shapes.drawDiscTextured(pg, p.width * (0.25f + 0.1f * beatTime.value()), p.width * 0.15f, 100, waveform);
		DrawUtil.pop(pg);
		
		DrawUtil.pop(pg);
	}
	
	//////////////////////////
	// WAVEFORM	
	//////////////////////////
	
	protected void drawWaveform() {
		float waveformScale = 0.4f;
		float waveW = waveform.width * waveformScale;
		float waveH = waveform.height * waveformScale;
		pg.image(waveform, -waveW * p.loop.progress(), 0, waveW, waveH);
		pg.image(waveform, -waveW * p.loop.progress() + waveW, 0, waveW, waveH);
	}

}

