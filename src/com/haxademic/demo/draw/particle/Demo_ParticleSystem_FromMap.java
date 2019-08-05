package com.haxademic.demo.draw.particle;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.video.Movie;

public class Demo_ParticleSystem_FromMap 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Movie video;
	protected ParticleSystem particles;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
	}

	public void setupFirstFrame() {
		// create map
		video = DemoAssets.movieKinectSilhouette();
		video.loop();
		
		particles = new ParticleSystem();
	}
	
	public void drawApp() {
		background(0);
		
		// draw image/map base
		pg.beginDraw();
		drawBaseImage(pg);
		particles.launchParticles(pg);
		particles.drawParticles(pg);
		pg.endDraw();
		
//		p.debugView.setValue("shapes.size()", particles.size());
		p.image(pg, 0, 0);
	}
	
	protected void drawBaseImage(PGraphics pg) {
		pg.background(0);
		if(video.width > 100) ImageUtil.cropFillCopyImage(video, pg, false);
		pg.loadPixels();
	}
			
}