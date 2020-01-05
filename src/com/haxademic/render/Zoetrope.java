package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

public class Zoetrope 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int FRAMES = 1600;
	protected float curRotY = 0;
	protected LinearFloat cameraProgress = new LinearFloat(0, 0.001f);

	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1000);
		Config.setProperty(AppSettings.HEIGHT, 1000);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES);
	}
	
	protected void firstFrame() {
		
	}

	protected void drawApp() {
		// set context
		pg.beginDraw();
		pg.blendMode(PBlendModes.BLEND);
		pg.background(0);
		pg.stroke(255);
		pg.fill(0);
		PG.setCenterScreen(pg);
		PG.setDrawCenter(pg);
		PG.setBetterLights(pg);
		
		// cofig
		float numFrames = 50;
		float segmentRads = P.TWO_PI / numFrames;
		float radius = p.width * 1.4f;

		// update spin speed
		cameraProgress.setInc(0.003f);
		cameraProgress.update();
		float speed = cameraProgress.value() * segmentRads;
		speed = P.constrain(speed, 0.01f, segmentRads);

		// move camera
		float easeCamera = P.map(speed, 0, segmentRads, 0, 1);
		float easedCamersVal = Penner.easeInOutExpo(easeCamera, 0, 1, 1);
		pg.translate(0, 0, P.map(easedCamersVal, 0, 1, 0, -p.width * 3f));
		pg.rotateX(P.map(easedCamersVal, 0, 1, 0, -P.HALF_PI));

		// spin zoetrope
		curRotY += speed;
		pg.rotateY(curRotY);
		
		// draw floor
		pg.pushMatrix();
		pg.translate(0, p.height * 0.6f);
		pg.rotateX(P.HALF_PI);
		pg.ellipse(0, 0, radius * 1.5f, radius * 1.5f);
		pg.popMatrix();
		
		// rotate, scale and draw
		for (float i = 0; i < numFrames; i++) {
			float rot = segmentRads * i;
			float size = p.width * 0.4f + (p.width * 0.3f) * P.sin(rot);
			float x = P.cos(rot) * radius;
			float z = P.sin(rot) * radius;
			pg.pushMatrix();
			pg.translate(x,  0, z);
			pg.rotateY(-rot + P.HALF_PI);
			pg.rotateX(P.sin(rot) * 0.75f);
//			pg.rect(0, 0, size/3f, size * (1f + speed * 3f));
			pg.box(size/6f, size * (1f + speed * 1f), size/2f);
			pg.popMatrix();
		}
		
		// context end
		pg.endDraw();
		
		// post process
		GrainFilter.instance(p).setTime(p.frameCount * 0.01f);
		GrainFilter.instance(p).setCrossfade(0.12f);
		GrainFilter.instance(p).applyTo(pg);
		
		BloomFilter.instance(p).setStrength(3f);
		BloomFilter.instance(p).setBlurIterations(5);
		BloomFilter.instance(p).setBlendMode(BloomFilter.BLEND_SCREEN);
		BloomFilter.instance(p).applyTo(pg);
		
		VignetteFilter.instance(p).setDarkness(0.4f);
		VignetteFilter.instance(p).applyTo(pg);

		// draw to screen
		p.image(pg, 0, 0);
	}
		
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			if(cameraProgress.target() == 1) cameraProgress.setTarget(0);
			else if(cameraProgress.target() == 0) cameraProgress.setTarget(1);
		}
	}

}