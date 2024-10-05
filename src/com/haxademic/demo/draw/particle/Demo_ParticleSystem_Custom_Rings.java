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
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;

public class Demo_ParticleSystem_Custom_Rings
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ParticleSystem<ParticleCustom> particles;
	
	protected void config() {
		Config.setAppSize(1024, 1024);
	}

	protected void firstFrame() {
		particles = new ParticleSystem<ParticleCustom>(ParticleCustom.class);
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
		if(FrameLoop.frameModLooped(10)) launchParticle();
		particles.updateAndDrawParticles(pg, PBlendModes.BLEND);
		pg.endDraw();
		
		// draw to screen
		p.image(pg, 0, 0);
		
		// debug info
		DebugView.setValue("particles.poolSize()", particles.poolSize());
		DebugView.setValue("particles.poolActiveSize()", particles.poolActiveSize());
	}

	protected void launchParticle() {
		float xPos = Mouse.x;
		float yPos = Mouse.y;
		float zPos = 0;

		// launch!
		ParticleCustom particle = (ParticleCustom) particles.launchParticle(xPos, yPos, zPos);
		particle
				.setSpeed(0, 0, -0.001f)
				.setAcceleration(1)
				.setGravity(0, 0, 0)
				.setRotation(0, 0, 0, 0, 0, 0)
				.setRotationSpeed(0, 0, 0)
				.setLifespan(120)
				.setColor(255);
	}

	/////////////////////////////
	// custom particle class
	/////////////////////////////

	public static class ParticleCustom<T>
			extends Particle {

		public ParticleCustom() {
			super();
		}

		protected void drawParticle(PGraphics pg) {
			float curAge = this.ageProgress();
			float curAgeEased = Penner.easeInExpo(curAge);
			float alpha = 1f - curAgeEased;
			float curSize = P.map(curAge, 0, 1, 200, 600);

			// draw shape
			pg.push();
			pg.noFill();
			pg.stroke(color, alpha * 255);
			pg.strokeWeight(1.6f);
			pg.circle(0, 0, curSize);
			pg.pop();
		}

	}

}