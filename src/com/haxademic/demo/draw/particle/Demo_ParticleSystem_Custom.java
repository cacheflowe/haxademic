package com.haxademic.demo.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.particle.ParticleSystemCustom;
import com.haxademic.core.draw.particle.ParticleSystemCustom.ParticleCustom;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PImage;

public class Demo_ParticleSystem_Custom 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ParticleSystemCustom particles;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1200 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
	}

	protected void firstFrame() {
		particles = new ParticleSystemCustom(new PImage[] { DemoAssets.particle() });
//		particles.enableUI("PARTICLES_", false);
	}
	
	protected void drawApp() {
		background(0);
		
		// draw image/map base
		pg.beginDraw();
//		BrightnessStepFilter.instance(p).setBrightnessStep(-1f/255);
//		BrightnessStepFilter.instance(p).applyTo(pg);
		pg.background(0);
//		launchFromMouse();
		particles.launchParticle(pg, FrameLoop.osc(0.05f, pg.width * 0.3f, pg.width * 0.7f), pg.height * 0.7f);
		particles.drawParticles(pg, PBlendModes.BLEND);
		pg.endDraw();
		
		DebugView.setValue("particles.poolSize()", particles.poolSize());
		
		p.image(pg, 0, 0);
	}
	
	protected void launchFromMouse() {
		if(P.abs(Mouse.xSpeed) > 0 && P.abs(Mouse.ySpeed) > 0) {
			for(int i=0; i < 3; i++) {
				ParticleCustom parti = (ParticleCustom) particles.launchParticle(pg, Mouse.x, Mouse.y);
				float speedDivisor = 2f + 4f * parti.sizeNorm();
				parti	.setSpeed(Mouse.xSpeed / speedDivisor, Mouse.ySpeed / speedDivisor)
						.setGravity(0, 0)
						.setAcceleration(0.97f);
			}
		}
	}
	
}