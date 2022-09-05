package com.haxademic.demo.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeOpaquePixelsFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.particle.IParticleFactory;
import com.haxademic.core.draw.particle.Particle;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.TextToPShape;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PShape;

public class Demo_ParticleSystem3dShadow 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// particle system
	protected ParticleFactoryBasic3d particleFactory;
	protected ParticleSystem particles;
	protected PGraphics shadowBuffer;
	protected PGraphics groundPlane;
	
	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setProperty(AppSettings.SHOW_UI, true);
	}
	
	protected void firstFrame() {
		// particle system init
		particleFactory = new ParticleFactoryBasic3d();
		particles = new ParticleSystem(particleFactory);
		particles.enableUI("Parti3d", false);
		UI.addWebInterface(false);
		
		// extra environment elements
		shadowBuffer = PG.newPG(p.width, p.height);
		groundPlane = PG.newPG(p.width, p.height);
		DebugView.setTexture("shadowBuffer", shadowBuffer);
		DebugView.setTexture("groundPlane", groundPlane);
	}
	
	protected void launchParticles() {
		int launchesPerFrame = 1;
		for (int i = 0; i < launchesPerFrame; i++) {
			// launch w/default UI controls
			particles.launchParticle(0, 0, 0);
		}
	}
	
	protected void drawApp() {
		// update particles
		launchParticles();
		particles.updateParticles();
		
		// draw shadow buffer
		shadowBuffer.beginDraw();
		shadowBuffer.background(0,0);
		shadowBuffer.noFill();
		shadowBuffer.ortho();
		PG.setCenterScreen(shadowBuffer);
		PG.setDrawCorner(shadowBuffer);
		shadowBuffer.translate(0, 0, -p.height * 0.5f);
		shadowBuffer.rotateX(-P.HALF_PI);
		particles.drawParticles(shadowBuffer, PBlendModes.BLEND);
		shadowBuffer.endDraw();
		// shadow buffer post fx
		ColorizeOpaquePixelsFilter.instance(p).setColor(0, 0, 0);
		ColorizeOpaquePixelsFilter.instance(p).applyTo(shadowBuffer);
		BlurHFilter.instance(p).setBlurByPercent(2f, shadowBuffer.width);
		BlurVFilter.instance(p).setBlurByPercent(2f, shadowBuffer.height);
		BlurHFilter.instance(p).applyTo(shadowBuffer);
		BlurVFilter.instance(p).applyTo(shadowBuffer);
		BlurHFilter.instance(p).applyTo(shadowBuffer);
		BlurVFilter.instance(p).applyTo(shadowBuffer);
		BlurHFilter.instance(p).applyTo(shadowBuffer);
		BlurVFilter.instance(p).applyTo(shadowBuffer);
		BlurHFilter.instance(p).applyTo(shadowBuffer);
		BlurVFilter.instance(p).applyTo(shadowBuffer);
		BlurHFilter.instance(p).applyTo(shadowBuffer);
		BlurVFilter.instance(p).applyTo(shadowBuffer);
		BlurHFilter.instance(p).applyTo(shadowBuffer);
		BlurVFilter.instance(p).applyTo(shadowBuffer);
		BlurHFilter.instance(p).applyTo(shadowBuffer);
		BlurVFilter.instance(p).applyTo(shadowBuffer);
		BlurHFilter.instance(p).applyTo(shadowBuffer);
		BlurVFilter.instance(p).applyTo(shadowBuffer);
		
		// draw ground plane
		float radialGradScale = 1.4f;
		groundPlane.beginDraw();
		groundPlane.background(255);
		PG.setCenterScreen(groundPlane);
		Gradients.radial(groundPlane, groundPlane.width * radialGradScale, groundPlane.height * radialGradScale, 0xffffffff, 0xff999999, 100);
		groundPlane.endDraw();
		VignetteFilter.instance(p).setDarkness(0.85f).setSpread(0.15f).applyTo(groundPlane);
		VignetteFilter.instance(p).setDarkness(0.85f).setSpread(0.15f).applyTo(groundPlane);
		VignetteFilter.instance(p).setDarkness(0.85f).setSpread(0.15f).applyTo(groundPlane);
		
		// set context
		pg.beginDraw();
		pg.background(255);
		pg.noFill();
		PG.setCenterScreen(pg);
		PG.setDrawCorner(pg);
//		PG.basicCameraFromMouse(pg, 0.1f, 0);
		
		// ground plane 
		float groundScale = 3f;
		float groundW = groundPlane.width * groundScale;
		float groundH = groundPlane.height * groundScale;
		pg.push();
		PG.setDrawCenter(pg);
		pg.translate(0, p.height * 0.26f, 0);
		pg.rotateX(P.HALF_PI);
		pg.fill(255, 127, 255);
		pg.image(groundPlane, 0, 0, groundW, groundH);
//		pg.rect(0, 0, shadowBuffer.width * groundScale, shadowBuffer.height * groundScale);
		pg.pop();
		
		// background gradient
		float bgH = shadowBuffer.height * groundScale;
		pg.push();
		PG.setDrawCenter(pg);
		pg.translate(0, groundH * -0.4f, groundH * -0.5f);
		pg.rotateZ(P.HALF_PI);
		Gradients.linear(pg, shadowBuffer.width * groundScale, bgH, 0xffffffff, 0xff000000);
		pg.pop();
		
		// draw particles
		PG.basicCameraFromMouse(pg, 0.4f, 0);
		PG.setBetterLights(pg);
		particles.drawParticles(pg, PBlendModes.BLEND);
		pg.noLights();
		
		// draw shadow
		// doing this after particle prevents alpha occlusion
		float shadowScale = 1f;
		pg.push();
		PG.setDrawCenter(pg);
		pg.translate(0, p.height * 0.25f, 0);
		
//		// ground plane 
//		pg.push();
//		pg.rotateX(P.HALF_PI);
//		pg.fill(255, 127, 255);
//		pg.rect(0, 0, shadowBuffer.width * shadowScale, shadowBuffer.height * shadowScale);
//		pg.pop();
		
		// shadow
		pg.push();
		pg.translate(0, p.height * -0.001f, 0);
		pg.rotateX(P.HALF_PI);
		PG.setPImageAlpha(pg, 0.25f);
		pg.image(shadowBuffer, 0, 0, shadowBuffer.width * shadowScale, shadowBuffer.height * shadowScale);
		pg.pop();
		
		pg.pop();
		

		
		pg.endDraw();
		
		// draw to screen
		p.background(0);
		p.image(pg, 0, 0);
		
		// debug
		DebugView.setValue("particles.poolSize()", particles.poolSize());
		DebugView.setValue("particles.poolActiveSize()", particles.poolActiveSize());
	}
	
	////////////////////////////////////
	// Custom particle system
	////////////////////////////////////
	
	public class ParticleFactoryBasic3d
	implements IParticleFactory {
		
		protected TextToPShape textToPShape;
		
		public ParticleFactoryBasic3d() {
			textToPShape = new TextToPShape(TextToPShape.QUALITY_MEDIUM);
		}
		
		public Particle randomize(Particle particle) {
			setColor(particle, ColorUtil.randomColor());
			return particle;
		}

		public Particle setColor(Particle particle, int color) {
			PShapeUtil.setBasicShapeStyles(particle.shape(), color, 0, 0);
			return particle;
		}
		
		public Particle initNewParticle() {
			PShape newShape = null;
			newShape = PShapeUtil.createBox(1, 1, 1, p.color(180, 180, 0));
			return new Particle(newShape);
		}

	}

}