package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.particle.ParticleLauncherGPU;
import com.haxademic.core.render.FrameLoop;

public class Demo_VertexShader_GPUParticlesLauncher_CurlNoise 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int FRAMES = 60 * 4;
	protected ParticleLauncherGPU gpuParticles;

	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setProperty(AppSettings.SHOW_DEBUG, false);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 3);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 4);
	}
	
	protected void firstFrame() {
		// build particles launcher
		gpuParticles = new ParticleLauncherGPU(256);
		DebugView.setValue("totalVertices", gpuParticles.vertices());
	}
	
	protected void drawApp() {
		// clear the screen
		p.background(0);

		// set color map
		ImageUtil.copyImage(ImageGradient.RAINBOWISH(), gpuParticles.colorBuffer());
		
		// launch! need to open & close the position buffer where we're writing new launch pixels
		int startLaunchTime = p.millis();
		int launchesPerFrame = 150;
		gpuParticles.beginLaunch();
//		for (int j = 0; j < launchesPerFrame; j++) gpuParticles.launch(pg, Mouse.xEased, Mouse.yEased);
//		for (int j = 0; j < launchesPerFrame; j++) gpuParticles.launch(pg, p.width/2 + p.width/4 * P.sin(p.frameCount/40f), p.height/2 + p.height/6 * P.sin(p.frameCount/20f));
		for (int j = 0; j < launchesPerFrame; j++) gpuParticles.launch(pg, p.width/2 + p.width/4 * P.sin(FrameLoop.progressRads() * 1f), p.height/2 + p.height/6 * P.sin(FrameLoop.progressRads() * 2f));
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
		PG.basicCameraFromMouse(pg, 0.5f);
//		pg.fill(255);
		pg.blendMode(PBlendModes.ADD);
		gpuParticles.pointSize(4);
		gpuParticles.renderTo(pg);
		pg.endDraw();
		DebugView.setValue("renderTime", p.millis() - startRenderTime);

		// draw buffer to screen
		p.image(pg, 0, 0);
	}
	
}