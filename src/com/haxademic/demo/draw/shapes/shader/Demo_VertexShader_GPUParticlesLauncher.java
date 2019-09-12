package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.particle.ParticleLauncherGPU;
import com.haxademic.core.math.MathUtil;

public class Demo_VertexShader_GPUParticlesLauncher 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ParticleLauncherGPU gpuParticles;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 960);
		p.appConfig.setProperty(AppSettings.HEIGHT, 960);
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true);
	}
	
	protected void setupFirstFrame() {
		// build particles launcher
		gpuParticles = new ParticleLauncherGPU(512);
		p.debugView.setValue("totalVertices", gpuParticles.vertices());
	}
	
	public void drawApp() {
		// clear the screen
		background(0);
		
		// launch! need to open & close the position buffer where we're writing new launch pixels
		int launchesPerFrame = 500;
		gpuParticles.beginLaunch();
		for (int j = 0; j < launchesPerFrame; j++) 
			 gpuParticles.launch(pg, p.mouseXEase.value() * pg.width, p.mouseYEase.value() * pg.height);
//			gpuParticles.launch(pg, pg.width/2f, pg.height/2f);
		gpuParticles.endLaunch();

		// update particles buffers
		gpuParticles.update();

		// render!
		pg.beginDraw();
		pg.background(0);
		PG.setCenterScreen(pg);
//		PG.basicCameraFromMouse(pg, 0.5f);
		pg.fill(255);
		pg.blendMode(PBlendModes.ADD);
		gpuParticles.renderTo(pg);
		pg.endDraw();

		// draw buffer to screen
		p.image(pg, 0, 0);
	}
	
}