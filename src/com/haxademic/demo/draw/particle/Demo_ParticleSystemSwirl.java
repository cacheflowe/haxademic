package com.haxademic.demo.draw.particle;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.particle.ParticleSystemSwirl;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;

public class Demo_ParticleSystemSwirl 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ParticleSystemSwirl particles;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1200 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
	}

	protected void firstFrame() {
		particles = new ParticleSystemSwirl(new PImage[] { DemoAssets.particle() });
		particles.enableUI("SWIRL_1_", false);
	}
	
	protected void drawApp() {
		background(0);
		
		// draw image/map base
		pg.beginDraw();
		pg.background(0);
		particles.launchParticlesFromMap(pg);
		particles.drawParticles(pg);
		pg.endDraw();
		
		p.image(pg, 0, 0);
	}
}