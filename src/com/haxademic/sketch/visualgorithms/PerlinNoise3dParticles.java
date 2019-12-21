package com.haxademic.sketch.visualgorithms;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.ui.UI;

import processing.core.PVector;

public class PerlinNoise3dParticles
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// noise
	protected String noiseScale = "noiseScale";
	protected String octaves = "octaves";
	protected String noiseSpeed = "noiseSpeed";
	protected String falloff = "falloff";
	protected String spacing = "spacing";

	float noiseFactor = 0.8f;
	// spacing
	float halfSize;
	ArrayList<PVector> fieldVecs;
	ArrayList<PVector> fieldVecRots;
	int numParticles = 4000;
	ArrayList<Particle> particles;
	PVector center = new PVector();
	// animation
	protected float frames = 6000;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 1000 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false);
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, Math.round(frames) );
		Config.setProperty(AppSettings.SHOW_UI, true);
	}

	public void firstFrame() {
		AudioIn.instance();
		halfSize = p.width * 0.5f;

		UI.addSlider(noiseScale, 0.93f, 0.0001f, 5f, 0.0001f, false);
		UI.addSlider(octaves, 2f, 1, 8, 1, false);
		UI.addSlider(noiseSpeed, 0.025f, 0, 1f, 0.001f, false);
		UI.addSlider(falloff, 0.6f, 0, 1f, 0.001f, false);
		UI.addSlider(spacing, p.width * 0.2f, 5, 150f, 1f, false);

		buildFieldVectors();
		buildParticles();
	}
	
	protected void buildParticles() {
		particles = new ArrayList<Particle>();
		for (int i = 0; i < numParticles; i++) {
			particles.add(new Particle(i));
		}
	}
	
	protected void buildFieldVectors() {
		fieldVecs = new ArrayList<PVector>();
		fieldVecRots = new ArrayList<PVector>();
		
		float spacingg = UI.value(spacing);
		for (float x = -halfSize; x <= halfSize; x += spacingg) {
			for (float y = -halfSize; y <= halfSize; y += spacingg) {
				for (float z = -halfSize; z <= halfSize; z += spacingg) {
					fieldVecs.add(new PVector(x, y, z));
					fieldVecRots.add(new PVector(0, 0, 0));
				}
			}
		}
	}

	public void drawApp() {
		p.background(0);
		PG.setBetterLights(p);
		p.pushMatrix();
		p.blendMode(PBlendModes.ADD);
		
		// check progress
		float progress = (p.frameCount % frames) / frames;
		
		// update noise 
//		float autoFalloff = (progress < 0.5f) ? P.map(progress, 0, 0.5f, 0, 1) : P.map(progress, 0.5f, 1f, 1, 0);
		p.noiseDetail(UI.valueInt(octaves), 1); // falloff

		// set size & center
		p.translate(p.width/2, p.height/2, 0);
//		p.rotateY(P.map(p.mouseX, 0, width, 0, 1) * P.TWO_PI);
//		p.rotateX(P.map(p.mouseY, 0, width, 0, 1) * P.TWO_PI);
		p.rotateY(progress * P.TWO_PI);
//		p.rotateX(progress * P.TWO_PI);
		
		updateField();
		drawField();
		updateParticles();
		
		p.popMatrix();
	}
	
	protected void updateField() {
		float noiseInc = p.frameCount * UI.value(noiseSpeed);
		
		// update field rotation
		for (int i = 0; i < fieldVecRots.size(); i++) {
			PVector pos = fieldVecs.get(i);
			PVector rot = fieldVecRots.get(i);
			rot.lerp(
					-0.5f + p.noise(noiseInc + (pos.x) * noiseFactor), 
					-0.5f + p.noise(noiseInc + (pos.y) * noiseFactor),
					-0.5f + p.noise(noiseInc + (pos.z) * noiseFactor),
					0.15f
					);
		}

	}
	
	protected void drawField() {
		p.stroke(95);
		p.strokeWeight(0.8f);
		p.noFill();
				
		// draw boxes
		for (int i = 0; i < fieldVecs.size(); i++) {
			p.pushMatrix();
			PVector pos = fieldVecs.get(i);
			PVector rot = fieldVecRots.get(i);
			p.translate(pos.x, pos.y, pos.z);
			p.rotateZ(rot.x);
			p.rotateY(rot.y);
			p.rotateZ(rot.z);
			p.box(10, 10, 10);
			p.popMatrix();
		}
	}
	
	protected void updateParticles() {
		p.stroke(160);
		p.noFill();

		for (int i = 0; i < numParticles; i++) {
			particles.get(i).update();
		}
	}
	
	protected float getNoise(float x, float y, float z ) {
		return p.noise(
				p.frameCount * UI.value(noiseSpeed) + x * UI.value(noiseScale), 
				p.frameCount * UI.value(noiseSpeed) + y * UI.value(noiseScale), 
				p.frameCount * UI.value(noiseSpeed) + z * UI.value(noiseScale)
		);
	}
	
	
	public class Particle {
		
		protected int index;
		protected float amp = 0;
		protected float influenceDist = UI.value(spacing) * 2.5f;
		protected float influenceAmp = 0.05f;
		protected PVector pos = new PVector();
		protected PVector[] trail = new PVector[] {new PVector(), new PVector(), new PVector(), new PVector(), new PVector(), new PVector(), new PVector(), new PVector(), new PVector(), new PVector()};
		protected PVector dir = new PVector();
		protected float speed = P.p.random(2f, 15f);
		
		public Particle(int index) {
			this.index = index;
			reset();
		}
		
		protected void reset() {
			pos.set(
					random(-halfSize,  halfSize), 
					random(-halfSize,  halfSize),
					random(-halfSize,  halfSize)
					);
			
			for (PVector trailPos : trail) {
				trailPos.set(pos);
			}
		}
		
		protected void update() {
			// set trail
			for (int i = trail.length - 2; i >= 0; i--) {
				trail[i+1].set(trail[i]);
			}
			
			// add direction
			for (int i = 0; i < fieldVecs.size(); i++) {
				if(PVector.dist(pos, fieldVecs.get(i)) < influenceDist) {
					dir.lerp(fieldVecRots.get(i), influenceAmp);
				}
			}
			
			dir.normalize();
			dir.mult(speed * amp);
			pos.add(dir);
			trail[0].set(pos);
			
			// draw
			p.pushMatrix();
			p.translate(pos.x, pos.y, pos.z);
//			p.point(0, 0, 0);
			p.popMatrix();
			
			// draw trail
			float newAmp = 0.3f + AudioIn.audioFreq(index);
			if(newAmp > amp) amp = newAmp;
			else amp *= 0.8f;
			
			float startSize = amp * 10f;
			float shrinkInc = startSize / (float)trail.length;
			
			for (int i = 0; i < trail.length - 1; i++) {
				p.strokeWeight(startSize - shrinkInc * (float)i);
//				BoxBetween.draw(p, trail[i], trail[i+1], 4f);
//				P.println(i, trail[i].x, trail[i+1].x);
				p.line(trail[i].x, trail[i].y, trail[i].z, trail[i+1].x, trail[i+1].y, trail[i+1].z);
			}
			
			// reset is needed
			if(pos.dist(center) > halfSize * 4f) reset();
		}
	}
}
