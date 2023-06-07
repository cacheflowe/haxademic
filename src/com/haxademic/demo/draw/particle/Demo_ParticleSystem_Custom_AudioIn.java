package com.haxademic.demo.draw.particle;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.particle.Particle;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioIn.AudioInputLibrary;

import processing.core.PGraphics;

public class Demo_ParticleSystem_Custom_AudioIn
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	@SuppressWarnings("rawtypes")
    protected ParticleSystem<ParticleCustom> particles;
	
	protected void config() {
		Config.setAppSize(1024, 1024);
	}

	@SuppressWarnings("rawtypes")
    protected void firstFrame() {
		AudioUtil.setPrimaryMixer();
		AudioIn.instance(AudioInputLibrary.Beads);

		particles = new ParticleSystem<ParticleCustom>(ParticleCustom.class);
//		particles.enableUI("PARTICLES_", false);
	}
	
	protected void drawApp() {
		background(255);
		
		// allow a reset
		if(KeyboardState.keyTriggered(' ')) particles.killAll();
		
		// draw image/map base
		pg.beginDraw();
		pg.background(255);
		PG.setDrawFlat2d(pg, true);
		PG.setDrawCenter(pg);
		launchFromMouse();
		preProcess();
		particles.updateAndDrawParticles(pg, PBlendModes.BLEND);
		pg.endDraw();
		
		// draw to screen
		p.image(pg, 0, 0);
		
		// debug info
		DebugView.setValue("particles.poolSize()", particles.poolSize());
		DebugView.setValue("particles.poolActiveSize()", particles.poolActiveSize());
	}
	
	protected void preProcess() {
	    BlurProcessingFilter.instance().setSigma(10);
	    BlurProcessingFilter.instance().setBlurSize(10);
	    BlurProcessingFilter.instance().applyTo(pg);
	}
	
	@SuppressWarnings("rawtypes")
	protected void launchFromMouse() {
		int audioAmp = (int) (AudioIn.amplitude() * 100);
		// if(P.abs(Mouse.xSpeed) > 0 && P.abs(Mouse.ySpeed) > 0) {
			for(int i=0; i < audioAmp; i++) {
				ParticleCustom particle = (ParticleCustom) particles.launchParticle(Mouse.x, Mouse.y, 0);
				particle
					.setSpeedRange(-2, 2, -2, 2, 0, 0)
					.setAcceleration(0.97f, 0.97f, 1)
					.setGravityRange(0, 0, 0, 0, 0, 0)
					.setRotationRange(0, 0, 0, 0, 0, 0)
					.setLifespanRange(50, 200)
	                .setLifespanSustain(0)
					.setSizeRange(100, 200)
					.setColor(0xff00ff00)
					.setImage(DemoAssets.particle())
				    .randomize();
			}
		// }
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
		
		protected void drawParticle(PGraphics pg) {
			// size tweaks based on lifespan progress...
			// scale up, but alpha fade out instead of scale down
			boolean scalingUp = (lifespanProgress.target() == 1);
			float curSize = (scalingUp) ?
				size * Penner.easeOutExpo(lifespanProgress.value()) :
				size;
			// curSize = size * Penner.easeOutCirc(lifespanProgress.value());
			float alpha = (lifespanProgress.target() == 1) ? 255 : 255 * lifespanProgress.value();
			
			// draw different types of shapes
			pg.tint(color, alpha);
			pg.image(image, 0, 0, curSize, curSize);
			pg.tint(255);
		}		
		
	}
}