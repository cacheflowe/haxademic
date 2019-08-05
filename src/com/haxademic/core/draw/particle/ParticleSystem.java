package com.haxademic.core.draw.particle;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;

public class ParticleSystem {
	
	protected ArrayList<Particle> particles = new ArrayList<Particle>();
	protected PImage[] particleImages;
	protected int MAX_ATTEMPTS = 2000;
	protected int MAX_LAUNCHES_PER_FRAME = 10;
	protected int PARTICLE_POOL_MAX_SIZE = 10000;
	protected boolean useMultipleParticles = true;
	
	public ParticleSystem() {
		// load particle source textures
		if(useMultipleParticles) {
			ArrayList<PImage> particles = FileUtil.loadImagesFromDir(FileUtil.getFile("haxademic/images/particles/"), "png");
			particleImages = new PImage[particles.size()];
			for (int i = 0; i < particles.size(); i++) {
				particleImages[i] = particles.get(i);
			}
		} else {
			particleImages = new PImage[] {
					DemoAssets.particle(),
			};
		}
	}
	
	public void drawParticles(PGraphics pg) {
		PG.setDrawCenter(pg);
		if(useMultipleParticles) pg.blendMode(PBlendModes.SCREEN);
		else pg.blendMode(PBlendModes.ADD);
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).update(pg);
		}
		PG.setDrawCorner(pg);
		pg.blendMode(PBlendModes.BLEND);	
	}
	
	public void launchParticles(PGraphics pg) {
		int numLaunched = 0;
		for (int i = 0; i < MAX_ATTEMPTS; i++) {
			int checkX = MathUtil.randRange(0, pg.width);
			int checkY = MathUtil.randRange(0, pg.height);
			int pixelColor = ImageUtil.getPixelColor(pg, checkX, checkY);
			float redColor = (float) ColorUtil.redFromColorInt(pixelColor) / 255f;
			if(redColor > 0.5f && numLaunched < MAX_LAUNCHES_PER_FRAME) {
				launchParticle(pg, checkX, checkY);
				numLaunched++;
			}
		}
	}
	
	protected void launchParticle(PGraphics pg, float x, float y) {
		// look for an available shape
		for (int i = 0; i < particles.size(); i++) {
			if(particles.get(i).available(pg)) {
				launch(particles.get(i), x, y);
				return;
			}
		}
		// didn't find one
		if(particles.size() < PARTICLE_POOL_MAX_SIZE) {
			Particle newShape = new Particle();
			launch(newShape, x, y);
			particles.add(newShape);
		}
	}
	
	protected void launch(Particle shape, float x, float y) {
		shape
			.setSpeed(-1f, 1f, -1f, 0.5f)
			.setGravity(0, 0, 0f, -0.05f)
			.setLifespan(10, 50)
			.setRotation(-0.1f, 0.1f)
			.setSize(10, 40)
			.setColor(P.p.color(P.p.random(200, 255), P.p.random(200, 255), P.p.random(200, 255)))
			.launch(x, y, randomImg());
	}
	
	protected PImage randomImg() {
		return particleImages[MathUtil.randRange(0, particleImages.length - 1)];
	}

}
