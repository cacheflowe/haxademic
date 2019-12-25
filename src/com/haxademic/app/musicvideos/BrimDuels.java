package com.haxademic.app.musicvideos;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.shapes.LineTrail;
import com.haxademic.core.draw.shapes.PShapeSolid;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.shapes.pshader.MeshDeformAndTextureFilter;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class BrimDuels 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float w = 1920;
	protected float h = 1080;
	protected int FRAMES = 1038; // (17.299s)
	protected int TICKS = 64;
	
	protected float planetBounceOsc = 0;
	
	protected PImage backgroundImg;
	protected PImage brimLogo;
	protected PImage waveform;
	
//	protected PShape icosa;
	protected PImage planetTexture;
	protected PShapeSolid planet;
	
	protected PImage floorSrcImg;
	protected PGraphics floorTexture;
	
	protected float cols = 20;
	protected float rows = cols;
	protected PShape planetShape;
	protected PGraphics texture;
	protected PGraphics displaceTexture;

	protected LineTrail[] trails;
	protected PVector trailPos = new PVector();
	protected int numTrails = 100;
	protected PShape moon;
	
	protected LinearFloat beatTime = new LinearFloat(0, 0.025f);
	
	
	protected void config() {
		Config.setProperty(AppSettings.RENDER_AUDIO_FILE, FileUtil.getPath("audio/brim-duels/brim-liski-duels.wav"));
		Config.setProperty(AppSettings.RENDER_AUDIO_SIMULATION, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE, true);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, FRAMES + 1);
		Config.setProperty(AppSettings.WIDTH, (int) w);
		Config.setProperty(AppSettings.HEIGHT, (int) h);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.LOOP_TICKS, TICKS);
	}

	protected void firstFrame() {
		backgroundImg = P.getImage("audio/brim-duels/bg-swirl.jpg");
		brimLogo = P.getImage("audio/brim-duels/brim-liski-logo.png");
		waveform = P.getImage("audio/brim-duels/output-16k-trans.png");
		buildPlanet();
		buildTrails();
		buildFloor();
		buildSheetDisplacer();
		buildMoons();
	}

	public void drawApp() {
		background(0);
		p.noiseSeed(1008);
		
		// draw pre
		updateSheetDisplacer();
		updateFloorTexture();
		
		// update calcs
		planetBounceOsc = P.sin(-P.QUARTER_PI/2f - P.HALF_PI + FrameLoop.progressRads() * 2f);
		
		// draw to main buffer
		pg.beginDraw();
		pg.background(0);
		pg.noStroke();
//		PG.setBetterLights(pg);
//		pg.lights();
		
		drawBackground();
		
		PG.push(pg);
		// set camera
//		pg.rotateX(-0.2f);
//		pg.rotateX(-P.QUARTER_PI * Mouse.xNorm);
//		pg.rotateY(-P.HALF_PI - AnimationLoop.progressRads() * 2f);
		pg.rotateX(-P.QUARTER_PI * 0.25f + P.QUARTER_PI * 0.25f * P.sin(P.QUARTER_PI -FrameLoop.progressRads()));

		// draw components
//		drawWaveform();
		drawSheetDisplacer();
		
		// lights!
		float directionalAmp = 120; // 255 * Mouse.xNorm;
		float specularAmp = 150 * Mouse.xNorm;
		float ambientAmp = 50; // 255 * Mouse.xNorm; // 255 * Mouse.xNorm;
		pg.lights();
		pg.ambient(ambientAmp);
		pg.lightSpecular(specularAmp, specularAmp, specularAmp); 
		pg.directionalLight(directionalAmp, directionalAmp, directionalAmp, -0.0f, -0.0f, 1); 
		pg.directionalLight(directionalAmp, directionalAmp, directionalAmp, 0.0f, 0.0f, -1); 
		pg.specular(p.color(100)); 
		pg.shininess(1000.0f * Mouse.xNorm); 

		drawPlanet();
//		drawTrails();
		
		// update beats
		beatTime.update();
		if(FrameLoop.isTick() && FrameLoop.curTick() < 64 - 8) {
			int eighthTick = FrameLoop.curTick() % 8;
			if(eighthTick == 0 || eighthTick == 3 || eighthTick == 6) {		// 1, 4, 7
				beatTime.setCurrent(1);
				beatTime.setTarget(0);
			}
		}
		PG.pop(pg);

		drawLogo();
		pg.endDraw();
		
		// draw to screen
		p.image(pg, 0, 0);
		
		// debug info
		// p.text(AnimationLoop.curTick(), 20, 20);
	}
	
	//////////////////////////
	// BACKGROUND
	//////////////////////////
	
	protected void drawBackground() {
		PG.setDrawCenter(pg);
		PG.push(pg);
		
		float bgScale = MathUtil.scaleToTarget(backgroundImg.height, pg.height * 1.8f);
		pg.translate(pg.width * 0.5f, pg.height * 0.5f, -pg.height * 1.f);
		pg.image(backgroundImg, 0, 0, backgroundImg.width * bgScale, backgroundImg.height * bgScale);
		
		PG.pop(pg);
		PG.setDrawCorner(pg);
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
		PG.setDrawCenter(floorTexture);
		PG.setCenterScreen(floorTexture);
		floorTexture.rotate(FrameLoop.progressRads());
		floorTexture.image(floorSrcImg, 0, 0, floorSrcImg.width * srcScale, floorSrcImg.height * srcScale);
		floorTexture.endDraw();
	}
	
	protected void drawFloor() {
		PG.setDrawCenter(pg);
		PG.push(pg);
		pg.translate(pg.width * 0.5f, pg.height * 0.6f);
		
		// draw floor - TODO: switch to textured?
//		float floorSize = pg.width * 0.4f;
		pg.rotateY(P.QUARTER_PI);
		pg.rotateX(P.HALF_PI);
//		pg.fill(100, 255, 100);
//		pg.rect(0, 0, floorSize, floorSize);
		Shapes.drawTexturedRect(pg, floorTexture);
		
		PG.pop(pg);
		PG.setDrawCorner(pg);
	}
	
	//////////////////////////
	// PLANET
	//////////////////////////
	
	protected void buildPlanet() {
//		icosa = Icosahedron.createIcosahedron(p.g, 4, planetTexture);
//		PShapeUtil.scaleShapeToHeight(icosa, pg.height * 0.3f);
		
		planetTexture = P.getImage("images/textures/space/saturn-dark.jpg"); // DemoAssets.textureJupiter();
		planet = PShapeSolid.newSolidIcos(pg.height * 0.175f, planetTexture, 5);
	}
	
	protected void drawPlanet() {
		PG.push(pg);
		PG.setDrawCorner(pg);
		pg.translate(pg.width * 0.5f, pg.height * 0.4f);
		
		// draw planet
		pg.translate(0, pg.height * 0.025f * planetBounceOsc);	// bounce up/down
		PG.push(pg);
		pg.rotateY(P.sin(FrameLoop.progressRads()) * 0.5f);
//		pg.shape(planet, 0, 0);
		planet.deformWithAudioByNormals(pg.height * 0.025f);
		PShapeUtil.drawTriangles(pg, planet.shape(), planetTexture, 1);
		PG.pop(pg);
		
		// draw audio ring
		/*
		PG.push(pg);
		pg.rotateY(-P.HALF_PI - AnimationLoop.progressRads() * 2f);
		pg.rotateX(-P.HALF_PI);
		pg.fill(255);
		Shapes.drawDiscAudio(pg, p.height * 0.32f, p.height * 0.34f, AudioIn.waveform.length, 10, false);
		PG.pop(pg);
		*/

		// trails/ticks attached to planet y
		drawTrails();
		drawTicks();
		
		// draw waveform ring
		pg.noLights();
//		pg.lights();
		pg.noStroke();
		PG.push(pg);
		pg.rotateY(-P.HALF_PI - FrameLoop.progressRads());
		pg.rotateX(-P.HALF_PI);
		Shapes.drawDiscTextured(pg, p.height * 0.25f, p.height * 0.33f, 100, waveform);
		PG.pop(pg);
		
		
		PG.pop(pg);
	}
	
	//////////////////////////
	// TICKS	
	//////////////////////////
	
	protected void drawTicks() {
		float inRadius = pg.height * 0.35f;
		float outRadius = pg.height * 0.37f;
		float oneRadius = pg.height * 0.39f;
		float midRadius = pg.height * 0.36f;
		float segmentRads = (1f / (float) TICKS) * P.TWO_PI;
		for (int i = 0; i < TICKS; i++) {
			float rads = P.HALF_PI + i * segmentRads;
			float x = P.cos(rads) * inRadius;
			float y = 0;
			float z = P.sin(rads) * inRadius;
			float xOut = P.cos(rads) * outRadius;
			float yOut = 0;
			float zOut = P.sin(rads) * outRadius;
//			float strokeAmp = 1f - (i % 4) * 0.3f;
			pg.strokeWeight(2);
			pg.stroke(255);//, 255 * strokeAmp);
//			if(i == AnimationLoop.curTick() + 1) {
//				pg.stroke(40, 160, 190);
//				pg.strokeWeight(4);
//			}
			if((i % 4) == 0) {
				xOut = P.cos(rads) * oneRadius;
				yOut = 0;
				zOut = P.sin(rads) * oneRadius;
			}
			pg.line(x, y, z, xOut, yOut, zOut);
			
			// playhead
			if(i == (FrameLoop.curTick() + 1) % TICKS) {
				float xMid = P.cos(rads) * midRadius;
				float yMid = 0;
				float zMid = P.sin(rads) * midRadius;

				pg.fill(40, 160, 190);
				pg.noStroke();
				pg.pushMatrix();
				pg.translate(xMid, yMid, zMid);
				pg.sphere(pg.height * 0.01f);
				pg.popMatrix();
			}
		}
	}
	
	//////////////////////////
	// TRAILS	
	//////////////////////////
	
	protected void buildTrails() {
		trails = new LineTrail[numTrails];
		for (int i = 0; i < trails.length; i++) {
			trails[i] = new LineTrail(10);
		}
	}
	
	protected void drawTrails() {
		PG.push(pg);
//		PG.setCenterScreen(pg);

		for (int i = 0; i < trails.length; i++) {
			float progress = i + FrameLoop.progressRads();
			float eqAmp = AudioIn.audioFreq(P.round(10 + i * 3f)) * 3f;
			if(i % 3 == 0) progress = i + FrameLoop.progressRads() * 2;
			if(i % 5 == 0) progress = i + FrameLoop.progressRads() * 3;
			float minRadius = pg.height * 0.25f;
			float addRadius = p.noise(i * 10f) * pg.height * 0.3f;
			float radius = minRadius + addRadius * (1f + 0.2f * planetBounceOsc);
			float x = P.cos(progress) * radius;
			float y = (-0.5f + p.noise(i)) * pg.height * 0.3f + P.sin(progress + i) * pg.height * 0.02f;
			float z = P.sin(progress) * radius;
			trailPos.set(x, y, z);
			pg.strokeWeight(2f);
			trails[i].update(pg, trailPos, p.color(100 + 155f * eqAmp), p.color(100 + 155f * eqAmp, 0));
			
			// mooon!
			if(i == 1 || i == 4 || i == 8 || i == 12) {
				pg.pushMatrix();
				pg.translate(x, y, z);
				pg.shape(moon);
				pg.popMatrix();
			}
		}
		PG.pop(pg);
	}
	
	//////////////////////////
	// LOGO	
	//////////////////////////
	
	protected void drawLogo() {
		float scale = MathUtil.scaleToTarget(brimLogo.height, pg.height * 0.20f);
		PG.push(pg);
		PG.setDrawCenter(pg);
		pg.translate(pg.width * 0.5f, pg.height * 0.87f);
		PG.setPImageAlpha(pg, 0.5f + 0.5f * beatTime.value());
		pg.image(brimLogo, 0, 0, brimLogo.width * scale, brimLogo.height * scale);
		PG.resetPImageAlpha(pg);
		PG.setDrawCorner(pg);
		PG.pop(pg);
	}
	
	//////////////////////////
	// WAVEFORM	
	//////////////////////////
	
	protected void drawWaveform() {
		float waveformScale = 0.4f;
		float waveW = waveform.width * waveformScale;
		float waveH = waveform.height * waveformScale;
		pg.image(waveform, -waveW * FrameLoop.progress(), 0, waveW, waveH);
		pg.image(waveform, -waveW * FrameLoop.progress() + waveW, 0, waveW, waveH);
	}

	
	//////////////////////////
	// MOONS	
	//////////////////////////
	
	protected void buildMoons() {
		PImage moonTex = P.getImage("images/textures/space/saturn-moon-1.jpg");
		p.sphereDetail(10);
		moon = p.createShape(P.SPHERE, pg.height * 0.012f);
		moon.setTexture(moonTex);
		PShapeUtil.addTextureUVSpherical(moon, moonTex);
	}
	
	//////////////////////////
	// SHEET DISPLACER	
	//////////////////////////
	
	protected void buildSheetDisplacer() {
		int size = P.round(pg.height * 0.65f);
		
		// displace texture
		displaceTexture = p.createGraphics(size, size, PRenderers.P3D);
		
		// create wireframe texture
		texture = p.createGraphics(size, size, P.P3D);
		
		// build sheet mesh
		planetShape = Shapes.createSheet((int) cols, (int) rows, texture);
	}
	
	protected void updateSheetDisplacer() {
		// draw grid
		PG.drawGrid(texture, p.color(255), p.color(255), 25, 25, 4f);
		
		// update displace texture
		displaceTexture.beginDraw();
		displaceTexture.background(0);
		displaceTexture.noStroke();
		displaceTexture.blendMode(PBlendModes.ADD);
		PG.setDrawCenter(displaceTexture);
		PG.setCenterScreen(displaceTexture);
//		float scaleImg = MathUtil.scaleToTarget(DemoAssets.particle().height, displaceTexture.height * 1.4f);
		float iter = 6f;
		for (float i = 0; i < iter; i++) {
			// radial gradient
			Gradients.radial(displaceTexture, displaceTexture.width * 1/iter * i, displaceTexture.height * 1/iter * i, p.color(50), p.color(0), 100);
			BlurVFilter.instance(p).setBlurByPercent(1, displaceTexture.width);
			BlurVFilter.instance(p).applyTo(displaceTexture);
			BlurHFilter.instance(p).setBlurByPercent(1, displaceTexture.width);
			BlurHFilter.instance(p).applyTo(displaceTexture);
		}
		iter = 60f;
		for (float i = 0; i < iter; i++) {
			// ellipse
			displaceTexture.fill(1);
			displaceTexture.ellipse(0, 0, displaceTexture.width * 1/iter * i, displaceTexture.height * 1/iter * i);
		}			

		displaceTexture.blendMode(PBlendModes.BLEND);
		displaceTexture.endDraw();
		
		DebugView.setTexture("displaceTexture", displaceTexture);
	}

	protected void drawSheetDisplacer() {
		PG.setDrawCorner(pg);
		PG.push(pg);
		pg.translate(pg.width * 0.5f, pg.height * 0.64f + -planetBounceOsc * pg.height * 0.02f);
		pg.rotateY(P.QUARTER_PI);
		pg.rotateY(FrameLoop.progressRads() * -0.5f);
		pg.rotateX(-P.HALF_PI);
		
		OpenGLUtil.setWireframe(pg, true);
		
		pg.noLights();
		float displaceDist = 80f;
		MeshDeformAndTextureFilter.instance(p).setDisplacementMap(displaceTexture);
		MeshDeformAndTextureFilter.instance(p).setDisplaceAmp(displaceDist + displaceDist * planetBounceOsc);
		MeshDeformAndTextureFilter.instance(p).setSheetMode(true);
		MeshDeformAndTextureFilter.instance(p).applyTo(pg);

		// draw mesh
		pg.shape(planetShape);
		pg.resetShader();
		
		OpenGLUtil.setWireframe(pg, false);
		PG.pop(pg);
	}
}

