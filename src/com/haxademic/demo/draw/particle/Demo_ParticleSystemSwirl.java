package com.haxademic.demo.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.particle.Particle;
import com.haxademic.core.draw.particle.ParticleFactory;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_ParticleSystemSwirl 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ParticleSystemSwirl particles;
	
	protected void config() {
		Config.setAppSize(1024, 1024);
	}

	protected void firstFrame() {
		particles = new ParticleSystemSwirl();
		particles.enableUI("SWIRL_1_", false);
	}
	
	protected void drawApp() {
		background(0);
		
		// draw image/map base
		pg.beginDraw();
		pg.background(0);
		PG.setDrawFlat2d(pg, true);
		PG.setDrawCenter(pg);
		particles.launchParticles(pg);
		particles.drawParticles(pg);
		pg.endDraw();
		
		p.image(pg, 0, 0);
	}
	
	//////////////////////////////////////
	// Custom particle system w/overrides
	//////////////////////////////////////
	
	public class ParticleFactorySwirl
	extends ParticleFactory {
		
		public Particle initNewParticle() {
			return new ParticleSwirl(randomImg());
		}
		
	}
	
	public class ParticleSystemSwirl
	extends ParticleSystem {

		public ParticleSystemSwirl() {
			super(new ParticleFactorySwirl());
		}
		
		public void enableUI(String prefix, boolean saves) {
			usingUI = true;
			super.enableUI(prefix, saves);
		}

		public void launchParticles(PGraphics pg) {
			int numLaunched = 0;
			int maxAttempts = (usingUI) ? UI.valueInt(MAX_ATTEMPTS) : MAX_MAP_ATTEMPTS_PER_FRAME;
			int maxLaunches = (usingUI) ? UI.valueInt(MAX_LAUNCHES) : MAX_LAUNCHES_PER_FRAME;
			for (int i = 0; i < maxAttempts; i++) {
				float radius = MathUtil.randRangeDecimal(pg.height * 0.15f, pg.height * 0.35f);
				float rads = MathUtil.randRangeDecimal(0, P.TWO_PI);
				if(numLaunched < maxLaunches) {
					PImage randImg = ((ParticleFactory) particleFactory).randomImg();
					launchParticle(radius, rads, 0).setImage(randImg);
					numLaunched++;
				}
			}
		}
		
	}
	
	//////////////////////////////////////
	// Custom particle object w/overrides
	//////////////////////////////////////
	
	public class ParticleSwirl
	extends Particle {
		
		protected float radius;
		protected float radians;
		
		public ParticleSwirl(PImage image) {
			super(image);
		}
		
		// launch
		
		public Particle launch(float x, float y, float z) {
			super.launch(x, y, z);
			
			// repurpose x/y as radius/rads
			radius = x;
			radians = y;
			
			// chill out the repurposed values
			gravity.div(100f);
			speed.x = speed.x / 300f;
			speed.y = speed.y;
			
			return this;
		}
		
		// animate

		@Override
		protected void updatePosition() {
			// update position
			radians += speed.x;
			radius += speed.y;
			speed.add(gravity);
			pos.set(
					pg.width/2 + P.cos(radians) * radius, 
					pg.height/2 + P.sin(radians) * radius, 
					pos.z + speed.z
					);
			
			rotation.add(rotationSpeed);
		}
		
		protected void drawParticle(PGraphics pg) {
			float curSize = size * Penner.easeOutExpo(lifespanProgress.value());
			pg.tint(color);
			pg.image(image, 0, 0, curSize, curSize);
			pg.tint(255);
		}
		
	}
}