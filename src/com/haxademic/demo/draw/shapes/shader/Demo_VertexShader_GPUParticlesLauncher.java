package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.particle.ParticleLauncherGPU;
import com.haxademic.core.hardware.mouse.Mouse;

public class Demo_VertexShader_GPUParticlesLauncher 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ParticleLauncherGPU gpuParticles;

	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 960);
		Config.setProperty(AppSettings.HEIGHT, 960);
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}
	
	protected void firstFrame() {
		// build particles launcher
		gpuParticles = new ParticleLauncherGPU(512);
		DebugView.setValue("totalVertices", gpuParticles.vertices());
	}
	
	protected void drawApp() {
		// clear the screen
		background(0);
		
		// launch! need to open & close the position buffer where we're writing new launch pixels
		int startLaunchTime = p.millis();
		int launchesPerFrame = 2000;
		gpuParticles.beginLaunch();
		for (int j = 0; j < launchesPerFrame; j++) gpuParticles.launch(pg, Mouse.xEased, Mouse.yEased);
		gpuParticles.endLaunch();
		DebugView.setValue("launchTime", p.millis() - startLaunchTime);

		// update particles buffers
		int startUpdateTime = p.millis();
		gpuParticles.update();
		DebugView.setValue("updateTime", p.millis() - startUpdateTime);

		// render!
		int startRenderTime = p.millis();
		pg.beginDraw();
		pg.background(0);
		PG.setCenterScreen(pg);
//		PG.basicCameraFromMouse(pg, 0.5f);
		pg.fill(255);
		pg.blendMode(PBlendModes.ADD);
		gpuParticles.renderTo(pg);
		pg.endDraw();
		DebugView.setValue("renderTime", p.millis() - startRenderTime);

		// draw buffer to screen
		p.image(pg, 0, 0);
	}
	
}