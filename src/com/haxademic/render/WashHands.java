package com.haxademic.render;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeOpaquePixelsFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.TextToPShape;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;

public class WashHands 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected String BASE_ROT = "BASE_ROT";
	protected String BG_COLOR_BOT = "BG_COLOR_BOT";
	protected String WATER_COLOR = "WATER_COLOR";
	protected String LIGHT_SPECULAR = "LIGHT_SPECULAR";
	
	protected PShape shape;
	protected float shapeDepth;
	protected PGraphics shadowMap;
	protected EasingFloat tickTrigger = new EasingFloat(1f, 0.3f);
	protected ParticleSystem3D streamParticles;
	protected Text3d[] texts;
	
	protected int FRAMES = 240;
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1200 );
		Config.setProperty( AppSettings.HEIGHT, 900 );
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.LOOP_TICKS, 16 );
		Config.setProperty(AppSettings.RENDERING_MOVIE, true);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
	}
	
	protected void firstFrame() {
		buildUI();
		loadCursor();
		buildText();
		shadowMap = PG.newPG(p.width, p.height);
		DebugView.setTexture("shadowMap", shadowMap);
		streamParticles = new ParticleSystem3D();
	}
	
	protected void buildUI() {
		UI.addTitle("Render controls ");
		UI.addSliderVector(BASE_ROT, 0, -P.TWO_PI, P.TWO_PI, 0.001f, false);
		UI.setValue(BASE_ROT+"_X", 3.18f);
		UI.setValue(BASE_ROT+"_Y", 2.34f);
		UI.setValue(BASE_ROT+"_Z", -1.4f);
		UI.addSliderVector(BG_COLOR_BOT, 0, 0, 255, 1, false);
//		UI.setValue(BG_COLOR_BOT+"_X", 0);
//		UI.setValue(BG_COLOR_BOT+"_Y", 50);
//		UI.setValue(BG_COLOR_BOT+"_Z", 80);
		UI.setValue(BG_COLOR_BOT+"_X", 120);
		UI.setValue(BG_COLOR_BOT+"_Y", 30);
		UI.setValue(BG_COLOR_BOT+"_Z", 70);
		UI.addSliderVector(WATER_COLOR, 0, 0, 255, 1, false);
		UI.setValue(WATER_COLOR+"_X", 24);
		UI.setValue(WATER_COLOR+"_Y", 60);
		UI.setValue(WATER_COLOR+"_Z", 255);
		UI.addSlider(LIGHT_SPECULAR, 80, 0, 255, 1, false);
	}
	
	protected void loadCursor() {
		shape = PShapeUtil.shapeFromImage(P.getImage("haxademic/images/cursor-hand.png"));
		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleVertices(shape, 1, 1, 4);
		PShapeUtil.scaleShapeToExtent(shape, p.height * 0.2f);
		shapeDepth = PShapeUtil.getDepth(shape);
	}
	
	protected void buildText() {
		texts = new Text3d[] {
				new Text3d("WASH", 0.9f, -0.25f, -0.34f, -0.2f, 0.4f, 0),
				new Text3d("YOUR", 1f, 0.25f, -0.34f, -0.2f, -0.4f, 1),
				new Text3d("MOTHER", 0.7f, -0.25f, -0.22f, -0.1f, 0.4f, 2),
				new Text3d("FUCKIN", 1f, 0.22f, -0.22f, -0.1f, -0.4f, 3),
				new Text3d("HANDS", 1.1f, 0, 0.23f, 0.1f, 0, 4),
		};
	}
	
	protected void drawBgGradient() {
		p.pushMatrix();
		p.translate(p.width/2, p.height/2, -p.width);
		p.rotate(P.HALF_PI);
		p.scale(5f + 0.5f * P.sin(FrameLoop.progressRads() * 4));
		Gradients.linear(p, p.width, p.height, p.color(255), p.color(UI.valueX(BG_COLOR_BOT), UI.valueY(BG_COLOR_BOT), UI.valueZ(BG_COLOR_BOT)));
		p.popMatrix();
	}
	
	protected void updateProps() {
		if(FrameLoop.isTick()) {
			tickTrigger.setTarget((tickTrigger.target() == 1) ? 0 : 1);
//			tickTrigger.setCurrent(1).setTarget(0);
		}
		tickTrigger.update();
	}
	
	protected void drawApp() {
		// set context
		p.background(0);
		p.noStroke();
		updateProps();
		drawBgGradient();
		drawShadowBuffer();
		drawShadowToStage();
		drawShapeToMainBuffer();
		
		// draw to stage
		p.image(pg, 0, 0);
		postProcess();
	}
	
	protected void drawShadowBuffer() {
		shadowMap.beginDraw();
		shadowMap.clear();
		PG.setCenterScreen(shadowMap);
		shadowMap.rotateX(-P.HALF_PI);
		drawShapes(shadowMap);
		shadowMap.endDraw();
		ColorizeOpaquePixelsFilter.instance(p).applyTo(shadowMap);
		applyBlur(shadowMap);
	}
	
	protected void drawShadowToStage() {
		PG.setDrawCenter(p);
		p.pushMatrix();
		p.translate(p.width/2, p.height/2 + 320);
		p.rotateX(P.HALF_PI);
		p.scale(1.25f);
		PG.setPImageAlpha(p, 0.25f);
		p.image(shadowMap, 0, 0);
		PG.setPImageAlpha(p, 1f);
		p.popMatrix();
		PG.setDrawCorner(p);
	}
	
	protected void applyBlur(PGraphics pg) {
		BlurProcessingFilter.instance(p).setBlurSize(20);
		BlurProcessingFilter.instance(p).setSigma(10);
		for (int i = 0; i < 3; i++) {
			BlurProcessingFilter.instance(p).applyTo(pg);
		}
	}
	
	protected void drawShapeToMainBuffer() {
		pg.beginDraw();
		pg.clear();
		pg.noStroke();
		addLights(pg);
		PG.setCenterScreen(pg);
		drawShapes(pg);
		pg.endDraw();
	}
	
	protected void addLights(PGraphics pg) {
		pg.ambient(127);
		pg.lightSpecular(UI.value(LIGHT_SPECULAR), UI.value(LIGHT_SPECULAR), UI.value(LIGHT_SPECULAR)); 
		pg.specular(p.color(250)); 
		pg.directionalLight(200, 200, 200, -0.2f, 0.3f, 1); 
		pg.directionalLight(200, 200, 200, -0.2f, 0f, -0.8f); 
		pg.shininess(0.4f); 
	}
	
	protected void drawShapes(PGraphics pg) {
		// overall rotation
		pg.push();
		pg.rotateY(0.3f * P.sin(FrameLoop.progressRads()));
		
		//////////////////////////////
		// hands!
		//////////////////////////////
		pg.push();
		pg.rotateX(UI.valueX(BASE_ROT));
		pg.rotateY(UI.valueY(BASE_ROT));
		pg.rotateZ(UI.valueZ(BASE_ROT));
		
		// draw shape
		pg.push();
		pg.rotateZ(0.1f * P.sin(FrameLoop.progressRads() * 4f));
		pg.translate(P.sin(FrameLoop.progressRads() * 3f) * 20f, P.sin(FrameLoop.progressRads() * 8f) * 20f, shapeDepth/2f);
		pg.shape(shape);
		pg.pop();

		pg.push();
		pg.rotateZ(0.1f * P.sin(P.PI + FrameLoop.progressRads() * 4f));
		pg.translate(P.sin(P.PI + FrameLoop.progressRads() * 3f) * 20f, P.sin(P.PI + FrameLoop.progressRads() * 8f) * 20f, -shapeDepth/2f);
		pg.shape(shape);
		pg.pop();

		pg.pop();
		
		// stream
		float streamRadius = 30;
		pg.push();
		pg.fill(27, 70, 255);
		pg.translate(0, -200);
		pg.rotateY(0.2f * P.sin(FrameLoop.progressRads()));
//		Shapes.drawCylinder(pg, 10, streamRadius, streamRadius, 500, false);
		pg.pop();
		
		// stream particles
		for(int i=0; i < 7; i++) {
			float randStreamRot = FrameLoop.progressRads() * 10f * p.noise(i/5f) + p.noise(i*10f) + FrameLoop.loopCurFrame(); // p.noise(i/10f + FrameLoop.loopCurFrame()) * P.TWO_PI * 2f; // p.random(0, P.TWO_PI);
			float randX = P.cos(randStreamRot) * streamRadius;
			float randZ = P.sin(randStreamRot) * streamRadius;
			float randY = -600 - 30 * p.noise(i/5f + FrameLoop.loopCurFrame()); // MathUtil.randRangeDecimal(-300, -600);
			int whiteAdd = P.round(200 * p.noise(i/5f + FrameLoop.loopCurFrame())); // MathUtil.randRange(0, 200);
			int color = p.color(UI.valueX(WATER_COLOR) + whiteAdd, UI.valueY(WATER_COLOR) + whiteAdd, UI.valueZ(WATER_COLOR));
			int lifespan = P.round(100 + 40 * p.noise(i/5f + FrameLoop.loopCurFrame()));
			float speed = 2.8f + 0.6f * p.noise(i/5f + FrameLoop.loopCurFrame());
			streamParticles.launch(pg, randX, randY, randZ, -randStreamRot, color, lifespan, speed);
		}
		for(int i=0; i < 2; i++) {
			float randStreamRot = FrameLoop.progressRads() * 40f * p.noise(i/5f) + p.noise(i*2) + FrameLoop.loopCurFrame(); // p.noise(i/10f + FrameLoop.loopCurFrame()) * P.TWO_PI * 2f; // p.random(0, P.TWO_PI);
			float randX = P.cos(randStreamRot) * streamRadius * 3;
			float randZ = P.sin(randStreamRot) * streamRadius * 3;
			float randY = -70 - 10f * p.noise(i/5f + FrameLoop.loopCurFrame()); // MathUtil.randRangeDecimal(-300, -600);
			int whiteAdd = P.round(200 * p.noise(i/5f + FrameLoop.loopCurFrame())); // MathUtil.randRange(0, 200);
			int color = p.color(UI.valueX(WATER_COLOR) + whiteAdd, UI.valueY(WATER_COLOR) + whiteAdd, UI.valueZ(WATER_COLOR));
			int lifespan = P.round(30 + 30 * p.noise(i/5f + FrameLoop.loopCurFrame()));
			float speed = 2.f + 0.6f * p.noise(i/5f + FrameLoop.loopCurFrame());
			float gravity = 0.1f;
			streamParticles.launchSplash(pg, randX, randY, randZ, -randStreamRot, color, lifespan, speed, gravity);
		}
		for(int i=0; i < 3; i++) {
			float randStreamRot = FrameLoop.progressRads() * 20f * p.noise(i/5f) + p.noise(i + FrameLoop.loopCurFrame()/20f); // p.noise(i/10f + FrameLoop.loopCurFrame()) * P.TWO_PI * 2f; // p.random(0, P.TWO_PI);
			float randX = P.cos(randStreamRot) * streamRadius * 4;
			float randZ = P.sin(randStreamRot) * streamRadius * 5;
			float randY = -150 + 290f * p.noise(i/5f + FrameLoop.loopCurFrame()); // MathUtil.randRangeDecimal(-300, -600);
			int color = p.color(255, 255, 255, 70);
			int lifespan = P.round(50 + 20 * p.noise(i/5f + FrameLoop.loopCurFrame()));
			float speed = 0.01f + 0.02f * p.noise(i/5f + FrameLoop.loopCurFrame());
			float gravity = -0.015f + 0.03f * p.noise(i/5f + FrameLoop.loopCurFrame());
			streamParticles.launchBubbles(pg, randX, randY, randZ, -randStreamRot, color, lifespan, speed, gravity);
		}
		streamParticles.drawParticles(pg);
		
		
		// end overall rotation
		pg.pop();
		
		
		// TEXT!
		for (int i = 0; i < texts.length; i++) {
			texts[i].update(pg);
		}
	}
	
	protected void postProcess() {
		// post process
		BloomFilter.instance(p).setStrength(0.1f);
		BloomFilter.instance(p).setBlurIterations(2);
		BloomFilter.instance(p).setBlendMode(BloomFilter.BLEND_MULTIPLY);
//		BloomFilter.instance(p).applyTo(p.g);
//		BloomFilter.instance(p).applyTo(pg);
		
		VignetteFilter.instance(p).setDarkness(0.4f);
		VignetteFilter.instance(p).setSpread(0.3f);
		VignetteFilter.instance(p).applyTo(p.g);

		GrainFilter.instance(p).setTime(p.frameCount * 0.01f);
		GrainFilter.instance(p).setCrossfade(0.04f);
		GrainFilter.instance(p).applyTo(p.g);

	}
	
	////////////////////////////////////////
	// Text
	////////////////////////////////////////
	
	public class Text3d {
		
		protected PShape wordWash;
		protected LinearFloat wordWashEase = new LinearFloat(0f, 0.02f);

		protected float x;
		protected float y;
		protected float rotX;
		protected float rotY;
		protected int tickShow;
		
		public Text3d(String word, float scale, float x, float y, float rotX, float rotY, int tickShow) {
			this.x = x;
			this.y = y;
			this.rotX = rotX;
			this.rotY = rotY;
			this.tickShow = tickShow;
			
			TextToPShape textToPShape = new TextToPShape(TextToPShape.QUALITY_MEDIUM);
			String fontFile = FileUtil.getPath(DemoAssets.fontBitlowPath);
			wordWash = textToPShape.stringToShape3d(word, 70, fontFile);
			PShapeUtil.scaleShapeToHeight(wordWash, p.height * 0.11f * scale);
			wordWash.disableStyle();
		}
		
		public void update(PGraphics pg) {
			pg.push();
			pg.translate(pg.width * x, pg.height * y, 0);	// -0.25f   -0.33f
			if(FrameLoop.isTick()) {
				if(FrameLoop.curTick() == tickShow) wordWashEase.setInc(0.07f).setTarget(1);
				if(FrameLoop.curTick() == tickShow + 4) wordWashEase.setInc(0.04f).setTarget(0);
				if(tickShow == 4 && FrameLoop.curTick() % 8 == 0)  wordWashEase.setInc(0.04f).setTarget(0);
			}
			wordWashEase.update();
			float easedProgress = Penner.easeInOutExpo(wordWashEase.value());
			pg.rotateY(rotY + FrameLoop.osc(0.125f, -0.2f, 0.2f));  // 0.4f
			if(tickShow != 4) pg.rotateX(rotX + FrameLoop.osc(0.225f, -0.05f, 0.05f));  // -0.1f
			else pg.rotateX(rotX*1.5f);
			pg.scale(easedProgress);
			pg.shape(wordWash);
			pg.pop();
		}
	}
	
	
	////////////////////////////////////////
	// Particles
	////////////////////////////////////////
	
	public class ParticleSystem3D {

		// particles & source textures
		protected ArrayList<Particle3D> particles = new ArrayList<Particle3D>();

		// config
		protected int PARTICLE_POOL_MAX_SIZE = 5000;
		protected boolean screenBlendMode = false;

		public ParticleSystem3D() {
		}

		public void drawParticles(PGraphics pg) {
			PG.setDrawCenter(pg);
//			pg.blendMode(PBlendModes.ADD);
			for (int i = 0; i < particles.size(); i++) {
				particles.get(i).update(pg);
			}
			pg.blendMode(PBlendModes.BLEND);
			PG.setDrawCorner(pg);
		}

		protected Particle3D getParticle() {
			// look for an available shape
			for (int i = 0; i < particles.size(); i++) {
				if(particles.get(i).available()) {
					return particles.get(i);
				}
			}
			// didn't find one
			Particle3D newShape = initNewParticle();
			particles.add(newShape);
			return newShape;
		}
		
		protected Particle3D initNewParticle() {
			return new Particle3D();
		}

		protected void launch(PGraphics pg, float x, float y, float z, float rot, int color, int lifespan, float speed) {
			getParticle()
				.setSpeed(0, 0, speed, speed, 0, 0)
				.setGravity(0, 0, 0, 0, 0, 0)
				.setLifespan(lifespan, lifespan)
				.setRotation(rot, rot)
				.setSize(10, 10)
				.setColor(color)
				.launch(pg, x, y, z);
		}

		protected void launchSplash(PGraphics pg, float x, float y, float z, float rot, int color, int lifespan, float speed, float gravity) {
			float speedX = P.cos(rot) * speed;
			float speedZ = P.sin(rot) * speed;
			getParticle()
				.setSpeed(speedX, speedX, -3f, -3f, speedZ, speedZ)
				.setGravity(0, 0, gravity, gravity, 0, 0)
				.setLifespan(lifespan, lifespan)
				.setRotation(rot, rot)
				.setSize(7, 7)
				.setColor(color)
				.launch(pg, x, y, z);
		}
		
		protected void launchBubbles(PGraphics pg, float x, float y, float z, float rot, int color, int lifespan, float speed, float gravity) {
			float speedX = P.cos(rot) * speed;
			float speedZ = P.sin(rot) * speed;
			getParticle()
			.setSpeed(speedX, speedX, 0, 0, speedZ, speedZ)
			.setGravity(0, 0, gravity, gravity, 0, 0)
			.setLifespan(lifespan, lifespan)
			.setRotation(rot, rot)
			.setSize(24, 24)
			.setColor(color)
			.launch(pg, x, y, z);
		}
		
	}
	
	
	public class Particle3D {
		
		protected PVector pos = new PVector(0, 0, 0);
		protected PVector speed = new PVector(0, 0, 0);
		protected PVector speedMin = new PVector(0, 0);
		protected PVector speedMax = new PVector(0, 0);

		protected PVector gravity = new PVector(0, 0, 0);
		protected PVector gravityMin = new PVector(0, 0);
		protected PVector gravityMax = new PVector(0, 0);
		
		protected float size = 10f;
		protected float sizeMin = 10f;
		protected float sizeMax = 10f;
		
		protected float rotationMin = 0f;
		protected float rotationMax = 0f;
		
		protected float lifespan = 60;
		protected float lifespanMin = 60;
		protected float lifespanMax = 60;
		protected LinearFloat sizeProgress = new LinearFloat(0, 1f/lifespan);
		
		protected int color = 0xffffffff;
		
		public Particle3D() {}
		
		// Random range setters
		
		public Particle3D setSize(float sizeMin, float sizeMax) {
			this.sizeMin = sizeMin;
			this.sizeMax = sizeMax;
			return this;
		}
		
		public Particle3D setLifespan(float lifespanMin, float lifespanMax) {
			this.lifespanMin = lifespanMin; 
			this.lifespanMax = lifespanMax; 
			return this;
		}
		
		public Particle3D setRotation(float rotationMin, float rotationMax) {
			this.rotationMin = rotationMin;
			this.rotationMax = rotationMax;
			return this;
		}
		
		public Particle3D setSpeed(float speedMinX, float speedMaxX, float speedMinY, float speedMaxY, float speedMinZ, float speedMaxZ) {
			this.speedMin.set(speedMinX, speedMinY, speedMinZ);
			this.speedMax.set(speedMaxX, speedMaxY, speedMaxZ);
			return this;
		}
		
		public Particle3D setGravity(float gravityMinX, float gravityMaxX, float gravityMinY, float gravityMaxY, float gravityMinZ, float gravityMaxZ) {
			this.gravityMin.set(gravityMinX, gravityMinY, gravityMinZ);
			this.gravityMax.set(gravityMaxX, gravityMaxY, gravityMaxZ);
			return this;
		}
		
		public Particle3D setColor(int color) {
			this.color = color;
			return this;
		}
		
		// Launch!
		
		public Particle3D launch(PGraphics pg, float x, float y, float z) {
			// random params
			sizeProgress.setInc(1f/MathUtil.randRange(lifespanMin, lifespanMax));
			sizeProgress.setCurrent(0);
			sizeProgress.setTarget(1);
			size = MathUtil.randRangeDecimal(sizeMin, sizeMax);
			
			// set motion properties
			pos.set(x, y, z);
			speed.set(
					MathUtil.randRangeDecimal(speedMin.x, speedMax.x), 
					MathUtil.randRangeDecimal(speedMin.y, speedMax.y), 
					MathUtil.randRangeDecimal(speedMin.z, speedMax.z));
			gravity.set(
					MathUtil.randRangeDecimal(gravityMin.x, gravityMax.x), 
					MathUtil.randRangeDecimal(gravityMin.y, gravityMax.y),
					MathUtil.randRangeDecimal(gravityMin.z, gravityMax.z));
			
			return this;
		}
		
		// animate
		
		public void update(PGraphics pg) {
			if(available()) return;
			
			pg.sphereDetail(6);
			
			// update position
			speed.add(gravity);
			pos.add(speed);
			
			// update size
			sizeProgress.update();
			float curSize = size * Penner.easeOutExpo(sizeProgress.value());
			if(sizeProgress.value() == 1) sizeProgress.setTarget(0);
			
			// draw image
			pg.pushMatrix();
			pg.translate(pos.x, pos.y, pos.z);
			pg.rotateY(rotationMin);
			pg.fill(color);
			pg.box(curSize, curSize, curSize);
//			pg.sphere(curSize);
			pg.fill(255);
			pg.popMatrix();
		}
		
		public boolean available() {
			boolean finished = (sizeProgress.value() == 0 && sizeProgress.target() == 0);
			return finished; //  || pos.y < -100 || pos.x < -100 || pos.y > pg.height + 100 || pos.x > pg.height + 100;
		}
	}


}