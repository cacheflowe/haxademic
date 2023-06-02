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
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;

public class Demo_ParticleSystem_Custom_InterphaseSimulation 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int[][] sequence = new int[][] {
		new int[] {1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0},
		new int[] {0,0,0,0,1,0,0,0,0,1,0,0,1,0,0,0},
		new int[] {0,1,0,0,0,1,0,1,0,1,0,0,0,0,1,1},
		new int[] {0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0},
		new int[] {0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1},
		new int[] {0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0},
		new int[] {0,0,0,0,0,1,0,0,1,0,1,0,0,0,1,0},
		new int[] {0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,1},
	};
	protected int curStep = 0;


	@SuppressWarnings("rawtypes")
	protected ParticleSystem<ParticleCustom> particles;
	
	protected void config() {
		Config.setAppSize(512, 512);
		Config.setProperty(AppSettings.LOOP_FRAMES, 300);
		Config.setProperty(AppSettings.LOOP_TICKS, 16);
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
		if(FrameLoop.isTick()) launchParticles();
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
		// loop through channels, looking for active sequencer steps
		for(int i=0; i < sequence.length; i++) {
			if(sequence[i][FrameLoop.curTick()] == 1) {
				// calc particle props
				int curColor = ColorsHax.colorFromGroupAt(0, i);
				
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
	}
	
	
	public static class ParticleCustom<T>
	extends Particle {
		
		public ParticleCustom() {
			super();
		}
		
		protected void drawParticle(PGraphics pg) {
			float curAge = this.ageProgress();
			float curAgeEased = Penner.easeOutExpo(curAge);
			float endSize = pg.width * 2;
			float curSize = curAgeEased * endSize;
						
			// draw shape
			pg.noStroke();
			pg.fill(color);
			pg.ellipse(0, 0, curSize, curSize);
		}		
		
	}
}