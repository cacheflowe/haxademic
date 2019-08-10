package com.haxademic.demo.draw.image;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import processing.video.Movie;

public class Demo_TexturedStripsPShape
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Movie testMovie;
	protected PImage staticImg;
	protected PImage[] threadTexturesSource;
	protected PGraphics[] threadTextures;
	protected PGraphics bwMap;
	protected boolean videoMap = false;
	protected ArrayList<Particle> particles;
	protected int numParticles = 200;
	protected boolean debug = false;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
	}
	
	public void setupFirstFrame() {
		// load movie
		if(videoMap) {
			testMovie = DemoAssets.movieKinectSilhouette();
			testMovie.jump(0);
			testMovie.loop();
		} else {
			staticImg = DemoAssets.squareTexture();
		}
		
		// load map & build buffer
		bwMap = p.createGraphics(p.width, p.height, PRenderers.P3D);
		
		// load thread textures
		threadTexturesSource = new PImage[] {
				DemoAssets.textureJupiter(),
				DemoAssets.textureNebula(),
				DemoAssets.squareTexture(),
				DemoAssets.justin(),
		};
		threadTextures = new PGraphics[] {
				p.createGraphics(8, 512, P.P2D),
				p.createGraphics(8, 512, P.P2D),
				p.createGraphics(8, 512, P.P2D),
				p.createGraphics(8, 512, P.P2D),
		};
		for (int i = 0; i < threadTextures.length; i++) {
			ImageUtil.cropFillCopyImage(threadTexturesSource[i], threadTextures[i], true);
		}
		
		// build particles
		particles = new ArrayList<Particle>();
		for (int i = 0; i < numParticles; i++) {
			particles.add(new Particle());
		}
		
	}

	
	public void drawApp() {
		background(0);
		
		// update map
		if(videoMap) {
			if(testMovie.width > 10) {
				ImageUtil.cropFillCopyImage(testMovie.get(), bwMap, true);
			}
		} else {
			ImageUtil.cropFillCopyImage(staticImg, bwMap, true);
		}
		bwMap.loadPixels();
		
		// draw debug
		if(debug) {
			p.image(bwMap, 0, 0);
		}
		
		// draw particles
		p.stroke(255);
		p.strokeWeight(1);
		p.noFill();
		for (int i = 0; i < numParticles; i++) {
			particles.get(i).update(p.g);
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'd') debug = !debug;
	}
	
	public class Particle {
		
		protected int TAIL_SEGMENTS = 40;
		protected float BORDER = 10;
		protected float TAIL_W = 2;
		protected float TAIL_LERP_BLACK = 0.9f;
		protected float TAIL_LERP_SLOW = 0.3f;
		protected float TURN_LERP_LOW = 2f;
		protected float TURN_LERP_HIGH = 5f;
		protected float SPEED_LOW = 20f;
		protected float SPEED_HIGH = 35f;
		protected float SPEED_SLOW = 5f;
		protected PVector pos = new PVector();
		protected PVector tailEnd = new PVector();
		protected PVector[] tail = new PVector[TAIL_SEGMENTS];
		protected EasingFloat easedDir = new EasingFloat(0, 3);
		protected EasingFloat easedSpeed = new EasingFloat(0, 3);
		protected EasingFloat tailLerp = new EasingFloat(TAIL_LERP_BLACK, 8);
		protected float activeSpeed;
		protected boolean onBlack;
		protected PImage texture;
		protected PShape shape;

		
		public Particle() {
			buildShape();
			reset();
		}
		
		protected void buildShape() {
			// build pshape strip
			PImage stripTexture = threadTextures[0];
			float segments = TAIL_SEGMENTS;
			float stripW = 10;
			float stripH = 500;
			float textureStepY = (float) stripTexture.height / segments;
			float stepY = stripH / segments;
			float textureUV_Y = 0;
			float curY = 0;
			shape = createShape();
			shape.beginShape(TRIANGLE_STRIP);
			shape.noStroke();
			shape.texture(stripTexture);
			for (int i = 0; i < segments; i++) {
				shape.vertex(0, curY, 0, textureUV_Y);
				shape.vertex(stripW, curY, stripTexture.width, textureUV_Y);
				textureUV_Y += textureStepY;
				curY += stepY;
			}
			shape.endShape();
		}
		
		protected void reset() {
			onBlack = true;
			resetSpeed();
			newStartLocation();
			resetTail();
			resetDirection();
			texture = threadTextures[MathUtil.randRange(0, threadTextures.length - 2)];
			shape.setTexture(texture);
		}
		
		protected void resetSpeed() {
			activeSpeed = MathUtil.randRangeDecimal(SPEED_LOW, SPEED_HIGH);	
			easedSpeed.setCurrent(activeSpeed);
			easedSpeed.setTarget(activeSpeed);
		}
		
		protected void newStartLocation() {
			int randStartWall = MathUtil.randRange(0, 3);
			if(randStartWall == 0) {
				// top
				pos.x = MathUtil.randRangeDecimal(0, p.width);
				pos.y = -BORDER;
			} else if(randStartWall == 1) {
				// right
				pos.x = p.width + BORDER;
				pos.y = MathUtil.randRangeDecimal(0, p.height);
			} else if(randStartWall == 2) {
				// bottom
				pos.x = MathUtil.randRangeDecimal(0, p.width);
				pos.y = p.height + BORDER;
			} else if(randStartWall == 3) {
				// left
				pos.x = -BORDER;
				pos.y = MathUtil.randRangeDecimal(0, p.height);
			} 
		}
		
		protected void resetTail() {
			for (int i = 0; i < tail.length; i++) {
				if(tail[i] == null) tail[i] = new PVector();
				tail[i].set(pos);
			}
			tailEnd = tail[tail.length - 1];
		}
		
		protected void resetDirection() {
			float offsetX = MathUtil.randRangeDecimal(0, p.width/5f);
			if(MathUtil.randBoolean() == true) offsetX *= -1f;
			float offsetY = MathUtil.randRangeDecimal(0, p.width/5f);
			if(MathUtil.randBoolean() == true) offsetY *= -1f;
			
			float dirToCenter = MathUtil.getRadiansToTarget(pos.x, pos.y, p.width/2 + offsetX, p.height/2 + offsetY);
			easedDir.setCurrent(-dirToCenter);
			easedDir.setTarget(-dirToCenter);
			easedDir.setEaseFactor(MathUtil.randRangeDecimal(TURN_LERP_LOW, TURN_LERP_HIGH));
		}
		
		protected void update(PGraphics pg) {
			// update props
			easedSpeed.update(true);
			
			// update location
			pos.add(P.cos(easedDir.value()) * easedSpeed.value(), P.sin(easedDir.value()) * easedSpeed.value());
			tail[0].set(pos); // copy to top tail
			if(tailEnd.x < -BORDER || tailEnd.x > pg.width + BORDER || tailEnd.y < -BORDER || tailEnd.y > pg.height + BORDER) reset();
			
			// get pixel color
			int pixelColor = ImageUtil.getPixelColor(bwMap, (int) pos.x, (int) pos.y);
			float r = ColorUtil.redFromColorInt(pixelColor) / 255f;
			if(r < 0.1f && onBlack == false) {
				// on black!
				onBlack = true;
				float turnRads = P.PI * MathUtil.randRangeDecimal(0.9f, 1.1f); 
				if(MathUtil.randBoolean() == true) turnRads *= -1f;
				easedDir.setTarget(easedDir.target() +turnRads);
				tailLerp.setTarget(TAIL_LERP_BLACK);
				resetSpeed();
			} else if(r >= 0.1f && onBlack == true) {
				// on white!
				onBlack = false;
				easedSpeed.setTarget(SPEED_SLOW);
				tailLerp.setTarget(TAIL_LERP_SLOW);
			}

			// update extra params after updating position
			easedDir.update(true);
			tailLerp.update(true);

			// copy up tail from the end
			for (int i = tail.length - 2; i >= 0; i--) {
				tail[i+1].lerp(tail[i], tailLerp.value());
			}
			
			// update tail vertices
			// start with current direction
			float segmentDir = easedDir.value();

			for (int i = 0; i < TAIL_SEGMENTS; i++) {
				// get direction to prev segment
				if(i > 0) {
					segmentDir = -MathUtil.getRadiansToTarget(tail[i].x, tail[i].y, tail[i-1].x, tail[i-1].y);
				}
				
				// get rect points from tail segment vectors
				float leftXCur = tail[i].x + TAIL_W * P.cos(segmentDir - P.HALF_PI);
				float leftYCur = tail[i].y + TAIL_W * P.sin(segmentDir - P.HALF_PI);
				float rightXCur = tail[i].x + TAIL_W * P.cos(segmentDir + P.HALF_PI);
				float rightYCur = tail[i].y + TAIL_W * P.sin(segmentDir + P.HALF_PI);
								
				// update triangle strip vertices - 2 per tail segment
				int vertIndex = i * 2;
				shape.setVertex(vertIndex,   leftXCur, leftYCur, 0);
				shape.setVertex(vertIndex+1, rightXCur, rightYCur, 0);
			}
			
			// draw PShape
			p.shape(shape);
		}
	}
}
