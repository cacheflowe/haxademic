package com.haxademic.demo.draw.particle;

import java.util.ArrayList;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.Movie;

public class Demo_ParticleSystem_FromMap 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Movie video;
	protected ParticleSystem particles;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
	}

	public void firstFrame() {
		// create map
		video = DemoAssets.movieKinectSilhouette();
		video.loop();
		video.speed(0.4f);
		
		// create particle system
		PImage[] particleImages = new PImage[] { DemoAssets.particle() };
		boolean loadParticlesDir = true;
		if(loadParticlesDir) {
			ArrayList<PImage> particles = FileUtil.loadImagesFromDir(FileUtil.getPath("haxademic/images/particles/"), "png");
			particleImages = new PImage[particles.size()];
			for (int i = 0; i < particles.size(); i++) {
				particleImages[i] = particles.get(i);
			}
		}

		particles = new ParticleSystem(particleImages);
		particles.enableUI("PARTY_1", false);	// add sliders
	}
	
	public void drawApp() {
		background(0);
		
		// draw image/map base
		pg.beginDraw();
		drawBaseImage(pg);
		particles.launchParticles(pg, 1); // 2f * Mouse.xNorm);	// <- playing with scale here 
		particles.drawParticles(pg);
		pg.endDraw();
		
		p.image(pg, 0, 0);
	}
	
	protected void drawBaseImage(PGraphics pg) {
		pg.background(0);
		if(video.width > 100) ImageUtil.cropFillCopyImage(video, pg, false);
		pg.loadPixels();
	}
			
}