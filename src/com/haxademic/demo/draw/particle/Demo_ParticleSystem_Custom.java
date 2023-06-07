package com.haxademic.demo.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.particle.Particle;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class Demo_ParticleSystem_Custom 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/*
	 * ParticleSystem notes:
	 * - @SuppressWarnings("rawtypes") used above because ParticleSystem is a
	 *   generic class
	 * - @SuppressWarnings("unchecked") used below
	 * - This demo overrides ParticleSystem and Particle
	 *   Note the following generic syntax:
	 * - public static class ParticleCustom<T>
	 *     extends Particle {
	 * - public class ParticleSystemCustom<T extends Particle>
	 *     extends ParticleSystem<T>
	 * - @SuppressWarnings("unchecked")
	 *   public ParticleSystemCustom() {
	 *     super((Class<T>) ParticleCustom.class);
	 *   }
	 */

	protected ParticleSystemCustom<Particle> particles;
	
	protected void config() {
		Config.setAppSize(1024, 1024);
	}

	protected void firstFrame() {
		particles = new ParticleSystemCustom<Particle>();
		particles.enableUI("PARTICLES_", false);
	}
	
	protected void drawApp() {
		background(0);
		
		// allow a reset
		if(KeyboardState.keyTriggered(' ')) particles.killAll();
		
		// draw image/map base
		pg.beginDraw();
		pg.background(0);
		PG.setDrawFlat2d(pg, true);
		PG.setDrawCenter(pg);
		launchFromMouse();
		// launchOnOscillation();
		particles.updateAndDrawParticles(pg, PBlendModes.ADD);
		pg.endDraw();
		
		// draw to screen
		p.image(pg, 0, 0);
		
		// debug info
		DebugView.setValue("particles.poolSize()", particles.poolSize());
		DebugView.setValue("particles.poolActiveSize()", particles.poolActiveSize());
	}
	
	protected void launchOnOscillation() {
		particles.launchParticle(FrameLoop.osc(0.05f, pg.width * 0.3f, pg.width * 0.7f), pg.height * 0.7f, 0);
	}

	@SuppressWarnings("rawtypes")
	protected void launchFromMouse() {
		if(P.abs(Mouse.xSpeed) > 0 && P.abs(Mouse.ySpeed) > 0) {
			for(int i=0; i < 3; i++) {
				ParticleCustom particle = (ParticleCustom) particles.launchParticle(Mouse.x, Mouse.y, 0);
				float speedDivisor = 2f + 4f * particle.sizeNorm();
				if(particle != null) 
					particle
						.setSpeed(Mouse.xSpeed / speedDivisor, Mouse.ySpeed / speedDivisor, 0)
						.setGravity(0, 0, 0)
						.setAcceleration(0.97f);
			}
		}
	}
	
	//////////////////////////////////////
	// Custom particle system
	//////////////////////////////////////

	public class ParticleSystemCustom<T extends Particle>
	extends ParticleSystem<T> {

		@SuppressWarnings("unchecked")
		public ParticleSystemCustom() {
			super((Class<T>) ParticleCustom.class);
		}
		
		public void updateRandomRanges(Particle particle) {
			particle.setImage(DemoAssets.particle());
			if(usingUI) {
				particle
					.setSpeedRange(UI.valueX(SPEED_MIN), UI.valueX(SPEED_MAX), UI.valueY(SPEED_MIN), UI.valueY(SPEED_MAX), UI.valueZ(SPEED_MIN), UI.valueZ(SPEED_MAX))
					.setAcceleration(UI.valueX(ACCELERATION), UI.valueY(ACCELERATION), UI.valueZ(ACCELERATION))
					.setGravityRange(UI.valueX(GRAVITY_MIN), UI.valueX(GRAVITY_MAX), UI.valueY(GRAVITY_MIN), UI.valueY(GRAVITY_MAX), UI.valueZ(GRAVITY_MIN), UI.valueZ(GRAVITY_MAX))
					.setRotationRange(UI.valueX(ROTATION_MIN), UI.valueX(ROTATION_MAX), UI.valueY(ROTATION_MIN), UI.valueY(ROTATION_MAX), UI.valueZ(ROTATION_MIN), UI.valueZ(ROTATION_MAX))
					.setLifespanRange(UI.value(LIFESPAN_MIN), UI.value(LIFESPAN_MAX))
					.setSizeRange(UI.value(SIZE_MIN), UI.value(SIZE_MAX))
					.setColor(P.p.color(P.p.random(200, 255), P.p.random(200, 255), P.p.random(200, 255)));
			} else {
				particle
					.setSpeed(0, 0, 0)
					.setAcceleration(1)
					.setGravity(0, -0.1f, 0)
					.setRotationRange(0, 0, 0, 0, -1, 1)
					.setLifespanRange(30, 70)
					.setSizeRange(20, 60)
					.setColor(P.p.color(P.p.random(0, 255), P.p.random(0, 255), P.p.random(0, 255)));
			}
		}
		
	}
	
	//////////////////////////////////////
	// Custom particle
	// Constructor can't be passed any params, for generic instantiation.
	// NEEDS TO BE A STATIC CLASS if nested in another class, 
	// because inner classes don't work with generic instantiation: 
	// https://stackoverflow.com/a/17485341
	//////////////////////////////////////
	
	public static class ParticleCustom<T>
	extends Particle {
		
		public ParticleCustom() {
			super();
		}
		
		public float sizeNorm() {
			return P.map(size, sizeMin, sizeMax, 0, 1);
		}
		
		protected void drawParticle(PGraphics pg) {
			// size tweaks based on lifespan progress...
			// scale up, but alpha fade out instead of scale down
			boolean scalingUp = (lifespanProgress.target() == 1);
			float curSize = (scalingUp) ?
				size * Penner.easeOutExpo(lifespanProgress.value()) :
				size;
			curSize = size * Penner.easeOutCirc(lifespanProgress.value());
			float alpha = (lifespanProgress.target() == 1) ? 255 : 255 * lifespanProgress.value();
			float sizeNorm = sizeNorm();
			
			// draw different types of shapes
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
		}		
		
	}
}