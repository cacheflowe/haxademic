package com.haxademic.demo.draw.particle;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.particle.ParticlesGPU;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_ParticlesGPU 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// chaldni pattern
	protected PGraphics pgMap;

	// particles
	protected ParticlesGPU particles;
	
	protected void config() {
		Config.setAppSize(1024, 1024);
	}

	protected void firstFrame() {
		pgMap = PG.newPG(pg.width, pg.height);
		particles = new ParticlesGPU();
	}

	protected void updatePattern() {
		// draw map ---------------------------------
		// pgMap.filter();
		ImageUtil.cropFillCopyImage(DemoAssets.justin(), pgMap, true);
		ImageUtil.cropFillCopyImage(DemoAssets.textureNebula(), pgMap, true);
		PImage webcamImg = WebCam.instance().image();
		if(webcamImg != null) {
			ImageUtil.cropFillCopyImage(webcamImg, pgMap, true);
			ImageUtil.flipV(pgMap);
		}
		ImageUtil.flipH(pgMap);
		DebugView.setTexture("pgMap", pgMap);

		// draw generative shapes
		// pgMap.beginDraw();
		// pgMap.background(0);
		// for (int i = 0; i < 7; i++) {
		// 	pgMap.stroke(255);
		// 	pgMap.strokeWeight(15 - i);
		// 	pgMap.noFill();
		// 	pgMap.ellipse(pg.width/2, pg.height/2, i * 100, i * 100);
		// }
		// pgMap.endDraw();

		// blur map ---------------------------------
		BlurProcessingFilter.instance().setBlurSize(10);
		BlurProcessingFilter.instance().setSigma(10);
		// BlurProcessingFilter.instance().applyTo(pgMap);
		// BlurProcessingFilter.instance().applyTo(pgMap);
	}

	protected void updateParticles() {
		particles.setBaseParticleSize(1f);
		particles.setBaseParticleSpeed(0.5f);
		particles.setMapDecelCurve(0.f);
		particles.updateParticles(pg, pgMap);
		if(KeyboardState.keyTriggered(' ')) particles.resetRandomPositions();
	}
	
	public void drawApp() {
		// drawPre
		updatePattern();
		updateParticles();

		// draw particles
		p.background(0);
		particles.drawParticles(p.g, pgMap);
	}


}