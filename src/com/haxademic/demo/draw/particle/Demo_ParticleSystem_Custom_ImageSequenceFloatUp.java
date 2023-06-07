package com.haxademic.demo.draw.particle;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.particle.Particle;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_ParticleSystem_Custom_ImageSequenceFloatUp 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/*
	 * ParticleSystem notes:
	 * - @SuppressWarnings("rawtypes") used above because ParticleSystem is a
	 *   generic class
	 * - @SuppressWarnings("unchecked") used below
	 * - This demo overrides ParticleSystem and Particle
	 * Note the following generic syntax:
	 * - public static class ParticleCustom<T>
	 *   extends Particle {
	 * - public class ParticleSystemCustom<T extends Particle>
	 *   extends ParticleSystem<T>
	 * - @SuppressWarnings("unchecked")
	 *   public ParticleSystemCustom() {
	 *     super((Class<T>) ParticleCustom.class);
	 *   }
	 */

	protected ParticleSystemCustom<Particle> particles;
	
	protected void config() {
		Config.setAppSize(800, 800);
		Config.setProperty(AppSettings.SMOOTHING, AppSettings.SMOOTH_MEDIUM);
	}

	protected void firstFrame() {
		particles = new ParticleSystemCustom<Particle>();
	}
	
	protected void drawApp() {
		background(0);
		
		// allow a reset
		if(KeyboardState.keyTriggered(' ')) particles.killAll();
		
		// draw particles
		launchParticles();
		drawParticles();
		
		// draw to screen
		p.image(pg, 0, 0);
	}
	
	protected void launchParticles() {
		if(FrameLoop.frameModLooped(120)) {
			float randX = p.random(pg.width * 0.25f, pg.width * 0.75f);
			float randY = p.random(pg.width * 0.6f, pg.width * 0.8f);
			particles.launchParticle(randX, randY, 0);
		}
	}
		
	protected void drawParticles() {
		// draw
		pg.beginDraw();
		pg.background(0);
		PG.setDrawCenter(pg);
		particles.updateAndDrawParticles(pg);
		pg.endDraw();
		
		// update debug info to confirm recycling behavior
		DebugView.setValue("particles.poolSize()", particles.poolSize());
		DebugView.setValue("particles.poolActiveSize()", particles.poolActiveSize());
	}
	
	//////////////////////////////////////
	// Custom particle system
	//////////////////////////////////////

	public class ParticleSystemCustom<T extends Particle>
	extends ParticleSystem<T> {
		
		protected int imgIndex = 0;
		protected PImage[] images;

		@SuppressWarnings("unchecked")
		public ParticleSystemCustom() {
			super((Class<T>) ParticleCustom.class);
			
			images = new PImage[] {
				DemoAssets.textureCursor(),
				DemoAssets.smallTexture(),
				DemoAssets.justin(),
			};
		}
		
		protected PImage nextImg() {
			imgIndex++;
			imgIndex = imgIndex % images.length;
			return images[imgIndex];
		}
		
		public void updateRandomRanges(Particle particle) {
			particle
				.setSize(1)
				.setSpeed(0, -2, 0)
				.setAcceleration(1)
				.setGravity(0, 0, 0)
				.setRotation(0, 0, 0, 0, 0, 0)
				.setLifespan(10)
				.setLifespanSustain(70)
				.setColor(0xffffffff)
				.setImage(nextImg());
		}
		
	}
	
	//////////////////////////////////////
	// Custom particle
	//////////////////////////////////////
	
	public static class ParticleCustom
	extends Particle {
		
		public ParticleCustom() {}
		
		protected void drawParticle(PGraphics pg) {
			// size tweaks based on lifespan progress...
			// scale up, but alpha fade out instead of scale down
			float curSize = size * Penner.easeOutCirc(lifespanProgress.value());
//			boolean scalingUp = (lifespanProgress.target() == 1);
//			float alpha = (lifespanProgress.target() == 1) ? 255 : 255 * lifespanProgress.value();
			float alpha = 255;
			
			// draw image
			pg.tint(color, alpha);
			pg.image(image, 0, 0, curSize * image.width, curSize * image.width);
			pg.tint(255);
		}		
		
	}
}