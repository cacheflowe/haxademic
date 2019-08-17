package com.haxademic.demo.draw.particle;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.particle.ParticleSystemSwirl;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;

public class Demo_ParticleSystemSwirl 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ParticleSystemSwirl particles;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1200 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
	}

	public void setupFirstFrame() {
		particles = new ParticleSystemSwirl(new PImage[] { DemoAssets.particle() });
		particles.enableUI(false);
	}
	
	public void drawApp() {
		background(0);
		
		// draw image/map base
		pg.beginDraw();
		pg.background(0);
		particles.launchParticles(pg);
		particles.drawParticles(pg);
		pg.endDraw();
		
		p.image(pg, 0, 0);
	}
}