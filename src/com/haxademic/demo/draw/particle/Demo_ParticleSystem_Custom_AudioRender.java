package com.haxademic.demo.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.particle.Particle;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.render.Renderer;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_ParticleSystem_Custom_AudioRender
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// TODO
	// - Build gradient image w/3 colors, and slide between them while drawing particles
	// - Tweak particle falloff and pre-effects
	
	@SuppressWarnings("rawtypes")
    protected ParticleSystem<ParticleCustom> particles;
	
	protected void config() {
		Config.setAppSize(1280, 1024);
        Config.setProperty( AppSettings.RENDERING_MOVIE, true );
        Config.setProperty( AppSettings.RENDER_AUDIO_SIMULATION, true );
        Config.setProperty( AppSettings.RENDER_AUDIO_FILE, P.path("haxademic/audio/cacheflowe_bigger_loop.wav") );
        Config.setProperty( AppSettings.RENDER_AUDIO_FILE, P.path("audio/nike-women.wav") );
    }
    
	@SuppressWarnings("rawtypes")
    protected void firstFrame() {
		AudioUtil.setPrimaryMixer();
//		AudioIn.instance(AudioInputLibrary.Beads); // for real-time mic input?
		AudioIn.instance();

		particles = new ParticleSystem<ParticleCustom>(ParticleCustom.class);
//		particles.enableUI("PARTICLES_", false);

		Renderer.instance().videoRenderer.setPG(pg);
	}
	
	protected void drawApp() {
		background(0);
		
		// allow a reset
		if(KeyboardState.keyTriggered(' ')) particles.killAll();
		
		// draw image/map base
		pg.beginDraw();
		if(p.frameCount < 10) pg.background(0);
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
	    BlurProcessingFilter.instance().setSigma(20);
	    BlurProcessingFilter.instance().setBlurSize(20);
	    BlurProcessingFilter.instance().applyTo(pg);
	    BrightnessStepFilter.instance().setBrightnessStep(-30/255f);
	    BrightnessStepFilter.instance().applyTo(pg);
	}
	
	@SuppressWarnings("rawtypes")
	protected void launchFromMouse() {
		float launchX = pg.width / 2 + P.cos(FrameLoop.count(0.02f)) * 200;
		float launchY = pg.height / 2 + P.sin(FrameLoop.count(0.02f)) * 200;

		int audioAmp = (int) (AudioIn.amplitude() * 300);
		if(audioAmp > 0) {
    		for(int i=0; i < audioAmp * 3; i++) {
    		    int[] colorss = new int[] {
    		            0xff00FC00,
    		            0xff00C457,
    		            0xff95FF61,
    		    };
    		    
    		    PImage[] particlesTex = new PImage[] {
//    		            DemoAssets.particleLight(),
    		            DemoAssets.particleMedium(),
    		            DemoAssets.particleHeavy(),
    		    };
    		    
    			ParticleCustom particle = (ParticleCustom) particles.launchParticle(launchX, launchY, 0);
    			particle
    				.setSpeedRange(-2, 2, -2, 2, 0, 0)
    				.setAcceleration(0.97f, 0.97f, 1)
    				.setGravityRange(0, 0, 0, 0, 0, 0)
    				.setRotationRange(0, 0, 0, 0, 0, 0)
    				.setLifespanRange(100, 200)
                    .setLifespanSustain(0)
    				.setSizeRange(100, 300)
    				.setColor(colorss[MathUtil.randIndex(colorss.length)])
    				.setImage(particlesTex[MathUtil.randIndex(particlesTex.length)])
    			    .randomize();
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