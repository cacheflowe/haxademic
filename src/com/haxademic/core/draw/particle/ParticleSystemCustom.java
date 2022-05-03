package com.haxademic.core.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class ParticleSystemCustom
extends ParticleSystem {

	public ParticleSystemCustom(PImage[] particleImages) {
		super(particleImages);
	}
	
	public void enableUI(String prefix, boolean saves) {
		usingUI = true;
		super.enableUI(prefix, saves);
		P.out("ENABLEUI YO", MAX_ATTEMPTS);
	}

	protected void launch(PGraphics pg, Particle2d shape, float x, float y) {
		if(usingUI) {
			shape
				.setSpeedRange(UI.value(SPEED_X_MIN), UI.value(SPEED_X_MAX), UI.value(SPEED_Y_MIN), UI.value(SPEED_Y_MAX))
				.setGravityRange(UI.value(GRAVITY_X_MIN), UI.value(GRAVITY_X_MAX), UI.value(GRAVITY_Y_MIN), UI.value(GRAVITY_Y_MAX))
				.setLifespanRange(UI.value(LIFESPAN_MIN), UI.value(LIFESPAN_MAX))
				.setRotationRange(UI.value(ROTATION_MIN), UI.value(ROTATION_MAX))
				.setSizeRange(UI.value(SIZE_MIN), UI.value(SIZE_MAX))
				.setColor(P.p.color(P.p.random(200, 255), P.p.random(200, 255), P.p.random(200, 255)))
				.launch(pg, x, y, randomImg());
		} else {
			shape
				.setSpeedRange(0, 0, 0, 3.5f)
				.setGravityRange(0, 0, -0.025f, -0.1f)
				.setLifespanRange(30, 70)
				.setRotationRange(-0.05f, 0.05f)
				.setSizeRange(20, 60)
				.setColor(P.p.color(P.p.random(0, 255), P.p.random(0, 255), P.p.random(0, 255)))
				.launch(pg, x, y, randomImg());
		}
	}

	
	protected Particle2d initNewParticle() {
		// override with custom particle subclass
		return new ParticleCustom();
	}

	//////////////////////////////////////
	// Custom particle
	//////////////////////////////////////
	
	public class ParticleCustom
	extends Particle2d {
		
		public ParticleCustom() {}
		
		public float sizeNorm() {
			return P.map(size, sizeMin, sizeMax, 0, 1);
		}
		
		public void update(PGraphics pg) {
			if(available(pg)) return;
			
			// update position
			speed.add(gravity);
			speed.x = speed.x * acceleration;
			speed.y = speed.y * acceleration;	// leave z alone
			pos.add(speed);
			
			// update size
			boolean scalingUp = (sizeProgress.target() == 1);
			sizeProgress.update();
			float curSize = (scalingUp) ?
					size * Penner.easeOutExpo(sizeProgress.value()) :
					size;
			curSize = size * Penner.easeOutCirc(sizeProgress.value());
			if(sizeProgress.value() == 1) sizeProgress.setTarget(0);
			
			// draw image
			float alpha = (sizeProgress.target() == 1) ? 255 : 255 * sizeProgress.value();
			float sizeNorm = sizeNorm();
			pg.pushMatrix();
			pg.translate(pos.x, pos.y);
			pg.rotate(pos.z);
			if(sizeNorm > 0.7f) {
				pg.tint(color, alpha);
				pg.image(image, 0, 0, curSize, curSize);
				pg.tint(255);
			} else if(sizeNorm > 0.3f) {
				pg.noFill();
				pg.stroke(color, alpha);
				pg.strokeWeight(size * 0.1f);
				pg.ellipse(0, 0, curSize, curSize);
			} else {
				pg.noStroke();
				pg.fill(color, alpha);
				pg.strokeWeight(size * 0.1f);
				pg.rect(0, 0, curSize, curSize);
			}
			pg.popMatrix();
		}
		
	}

}
