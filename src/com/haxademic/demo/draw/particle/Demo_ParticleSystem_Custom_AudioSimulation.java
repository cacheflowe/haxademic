package com.haxademic.demo.draw.particle;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.particle.Particle;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;

public class Demo_ParticleSystem_Custom_AudioSimulation 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/*
	 * Get audio rendering demo working on PC
		Get entire setup working on mac with VSCode
		Add blue noise shader & texture from Patricio - this will be important for color banding correction
		Work on new particles systems for audio
		Use AudioData (FFT, Waveform) to create rings/animations.
		Work on gradients - lerpColor() between ColorsHax

	 */

	@SuppressWarnings("rawtypes")
	protected ParticleSystem<ParticleCustom> particles;
	
	protected void config() {
		Config.setAppSize(512, 512);
		Config.setProperty(AppSettings.LOOP_FRAMES, 300);
	}

	@SuppressWarnings("rawtypes")
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
		PG.setDrawCenter(pg);
		PG.setCenterScreen(pg);
		launchParticles();
		particles.updateAndDrawParticles(pg, PBlendModes.BLEND);
		pg.endDraw();
		
		// draw to screen
		p.image(pg, 0, 0);
		
		// debug info
		DebugView.setValue("particles.poolSize()", particles.poolSize());
		DebugView.setValue("particles.poolActiveSize()", particles.poolActiveSize());
	}
	
	@SuppressWarnings("rawtypes")
	protected void launchParticles() {
		for(int i=0; i < 1; i++) {
			// calc particle props
			float curNoise = FrameLoop.noiseLoop(2, 0);
			int curColor = p.lerpColor(ColorsHax.colorFromGroupAt(0, 0), ColorsHax.colorFromGroupAt(0, 3), curNoise);
			
			// launch particle
			ParticleCustom particle = (ParticleCustom) particles.launchParticle(0, 0, 0);
			particle
				.setSpeed(0, 0, -0.001f)
				.setAcceleration(1)
				.setGravity(0, 0, 0)
				.setRotation(0, 0, 0, 0, 0, 0)
				.setLifespan(240)
				.setColor(curColor);
		}
	}
	
	
	public static class ParticleCustom<T>
	extends Particle {
		
		public ParticleCustom() {
			super();
		}
		
		protected void drawParticle(PGraphics pg) {
			float curAge = this.ageProgress();
			float endSize = pg.width * 2;
			float curSize = curAge * endSize;
						
			// draw shape
			pg.noStroke();
			pg.fill(color);
			pg.ellipse(0, 0, curSize, curSize);
		}		
		
	}
}