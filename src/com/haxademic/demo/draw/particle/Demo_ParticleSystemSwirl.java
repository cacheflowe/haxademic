package com.haxademic.demo.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.particle.Particle;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_ParticleSystemSwirl 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ParticleSystemSwirl<Particle> particles;
	
	protected void config() {
		Config.setAppSize(1024, 1024);
	}

	protected void firstFrame() {
		particles = new ParticleSystemSwirl<Particle>();
		particles.enableUI("SWIRL_1_", false);
	}
	
	protected void drawApp() {
		background(0);
		
		// draw particles
		pg.beginDraw();
		pg.background(0);
		PG.setDrawFlat2d(pg, true);
		PG.setDrawCenter(pg);
		particles.launchParticles(pg);
		particles.updateAndDrawParticles(pg, PBlendModes.ADD);
		pg.endDraw();
		
		p.image(pg, 0, 0);
	}
	
	//////////////////////////////////////
	// Custom particle system w/overrides
	//////////////////////////////////////
	
	@SuppressWarnings("unchecked")
	public class ParticleSystemSwirl<T extends Particle>
	extends ParticleSystem<T> {

		protected PImage[] particleImages;

		public ParticleSystemSwirl() {
			super((Class<T>) ParticleSwirl.class);
			particleImages = FileUtil.loadImagesArrFromDir(FileUtil.getPath("haxademic/images/particles/"), "png");
		}
		
		public PImage randomImg() {
			return particleImages[MathUtil.randRange(0, particleImages.length - 1)];
		}

		// This should probably be in main class - 
		// doesn't need to be in a ParticleSystem subclass.
		// But it's still an example of how to override the default behavior
		public void launchParticles(PGraphics pg) {
			int numLaunched = 0;
			int maxAttempts = (usingUI) ? UI.valueInt(MAX_ATTEMPTS) : MAX_MAP_ATTEMPTS_PER_FRAME;
			int maxLaunches = (usingUI) ? UI.valueInt(MAX_LAUNCHES) : MAX_LAUNCHES_PER_FRAME;
			for (int i = 0; i < maxAttempts; i++) {
				float radius = MathUtil.randRangeDecimal(pg.height * 0.15f, pg.height * 0.35f);
				float rads = MathUtil.randRangeDecimal(0, P.TWO_PI);
				if(numLaunched < maxLaunches) {
					launchParticle(radius, rads, 0).setImage(randomImg());
					numLaunched++;
				}
			}
		}
		
	}
	
	//////////////////////////////////////
	// Custom particle object w/overrides
	//////////////////////////////////////
	
	public static class ParticleSwirl<T>
	extends Particle {
		
		protected float radius;
		protected float radians;
		
		public ParticleSwirl() {
			super();
		}
		
		// launch override for polar motion behavior
		
		@Override
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
		
		// animate with polar motion - override the default particle movement

		@Override
		protected void updatePosition() {
			super.updatePosition();
			// update position
			radians += speed.x;
			radius += speed.y;
			speed.add(gravity);
			pos.set(
				P.p.pg.width/2 + P.cos(radians) * radius, 
				P.p.pg.height/2 + P.sin(radians) * radius, 
				pos.z + speed.z
			);
			
			rotation.add(rotationSpeed);
		}
		
	}
}