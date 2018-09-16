package com.haxademic.demo.draw.shapes.shader;

import java.util.ArrayList;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PBlendModes;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.particle.ParticleLauncher;

import processing.core.PGraphics;

public class Demo_VertexShader_GPUParticlesLauncher 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics renderedParticles;
	protected ArrayList<ParticleLauncher> particleLaunchers;

	// TODO: optimize launching: beginDraw/endDraw calls are super slow. can both be copied to a single canvas, then copied back after drawn into?


	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1080);
		p.appConfig.setProperty(AppSettings.HEIGHT, 1080);
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true);
	}
	
	protected void setupFirstFrame() {
		// build final draw buffer
		renderedParticles = p.createGraphics(p.width, p.height, PRenderers.P3D);
		renderedParticles.smooth(8);
//		DrawUtil.setDrawFlat2d(renderedParticles, true);
//		p.debugView.setTexture(renderedParticles);
		
		// build multiple particles launchers
		particleLaunchers = new ArrayList<ParticleLauncher>();
		int totalVertices = 0;
		for (int i = 0; i < 40; i++) {
			ParticleLauncher particles = new ParticleLauncher();
			particleLaunchers.add(particles);
			totalVertices += particles.vertices();
		}
		p.debugView.setValue("totalVertices", totalVertices);
	}
	
	public void keyPressed() {
		super.keyPressed();
//		if(p.key == ' ') newPositions();
	}
	
	public void drawApp() {
		// clear the screen
		background(0);
		
		// launch! 
		int particleLauncherIndex = p.frameCount % particleLaunchers.size();
		particleLaunchers.get(particleLauncherIndex).beginLaunch();
		for (int j = 0; j < 420; j++) {		// -2 rows (64)
			particleLaunchers.get(particleLauncherIndex).launch(p.mouseX, p.mouseY);
		}
		particleLaunchers.get(particleLauncherIndex).endLaunch();

		// update particles launcher buffers
		for (int i = 0; i < particleLaunchers.size(); i++) {
			particleLaunchers.get(i).update();
		}

		// render!
		renderedParticles.beginDraw();
		DrawUtil.setDrawFlat2d(renderedParticles, true);
		renderedParticles.background(0);
		renderedParticles.fill(255);
		renderedParticles.blendMode(PBlendModes.ADD);
		for (int i = 0; i < particleLaunchers.size(); i++) {
			particleLaunchers.get(i).renderTo(renderedParticles);
		}
		renderedParticles.endDraw();

		// draw buffer to screen
		p.image(renderedParticles, 0, 0);
	}
	
}