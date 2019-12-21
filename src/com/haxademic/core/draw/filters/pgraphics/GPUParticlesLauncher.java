package com.haxademic.core.draw.filters.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pgraphics.shared.BaseVideoFilter;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.image.BufferMotionDetectionMap;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.particle.ParticleLauncherGPU;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class GPUParticlesLauncher
extends BaseVideoFilter {
	
	protected PGraphics motionBuffer;
	protected BufferMotionDetectionMap motionDetectionMap;

	protected PGraphics renderedParticles;
	protected ParticleLauncherGPU particleLaunchers;

	public GPUParticlesLauncher(int width, int height) {
		super(width, height);

		// build final draw buffer
		renderedParticles = PG.newPG(width, height);
		motionBuffer = P.p.createGraphics(width / 4, height / 4, PRenderers.P3D);
		
		// build particle launcher
		particleLaunchers = new ParticleLauncherGPU(512);
		int totalVertices = particleLaunchers.vertices();
		
		// debug
		DebugView.setTexture("renderedParticles", renderedParticles);
		DebugView.setValue("totalVertices", totalVertices);
		DebugView.setTexture("progressBuffer (1)", particleLaunchers.positionBuffer());
	}
	
	public void newFrame(PImage frame) {
		// store (and crop fill) frame into `sourceBuffer`
		super.newFrame(frame);
		
		// lazy init and update motion detection buffers/calcs
		if(motionDetectionMap == null) {
			motionDetectionMap = new BufferMotionDetectionMap(sourceBuffer, 0.1f);
			motionDetectionMap.setBlendLerp(0.6f);
			motionDetectionMap.setDiffThresh(0.05f);
			motionDetectionMap.setFalloffBW(0.2f);
			motionDetectionMap.setThresholdCutoff(0.5f);
			motionDetectionMap.setBlur(1f);
			DebugView.setTexture("motionDetectionMap", motionDetectionMap.differenceBuffer());
		}
		
		// pre-process motion buffer for smoother launch blobs
		ImageUtil.copyImage(sourceBuffer, motionBuffer);
		BlurHFilter.instance(P.p).setBlurByPercent(0.5f, motionBuffer.width);
		BlurHFilter.instance(P.p).applyTo(motionBuffer);
		BlurVFilter.instance(P.p).setBlurByPercent(0.5f, motionBuffer.height);
		BlurVFilter.instance(P.p).applyTo(motionBuffer);

		// run motion detection
		motionDetectionMap.updateSource(motionBuffer);
	}
	
	public void update() {
		if(motionDetectionMap == null) return;

		// launch particles from random places within the motion detection zones
		motionDetectionMap.loadPixels();

		int FRAME_LAUNCH_INTERVAL = 1;
		int MAX_LAUNCHED_PER_FRAME = 2500;
		int LAUNCH_ATTEMPTS = 5000;

		particleLaunchers.beginLaunch();
		if(P.p.frameCount % FRAME_LAUNCH_INTERVAL == 0) {
			int numLaunched = 0;
			for (int i = 0; i < LAUNCH_ATTEMPTS; i++) {
				if(numLaunched < MAX_LAUNCHED_PER_FRAME) {
					int checkX = MathUtil.randRange(0, sourceBuffer.width);
					int checkY = MathUtil.randRange(0, sourceBuffer.height);
					if(motionDetectionMap.pixelActive(checkX, checkY)) {
						particleLaunchers.launch(renderedParticles, checkX, checkY);
						numLaunched++;
					}
				} else {
					break;
				}
			}
		}
		particleLaunchers.endLaunch();
		particleLaunchers.update();

		// render!
		renderedParticles.beginDraw();
		PG.setDrawFlat2d(renderedParticles, true);
		renderedParticles.background(0);
		renderedParticles.fill(255);
		PG.setCenterScreen(renderedParticles);
		renderedParticles.blendMode(PBlendModes.ADD);
		particleLaunchers.renderTo(renderedParticles);
		renderedParticles.blendMode(PBlendModes.BLEND);
		renderedParticles.endDraw();

		// draw composite to output buffer
		destBuffer.beginDraw();
		destBuffer.background(0);
		destBuffer.blendMode(PBlendModes.BLEND);
		PG.resetPImageAlpha(destBuffer);
		destBuffer.image(sourceBuffer, 0, 0);
		PG.setPImageAlpha(destBuffer, 0.5f);
		PG.setPImageAlpha(destBuffer, 1f);
		destBuffer.blendMode(PBlendModes.ADD);
		destBuffer.image(renderedParticles, 0, 0);
		destBuffer.blendMode(PBlendModes.BLEND);
		
		destBuffer.endDraw();
		// desaturate
//		SaturationFilter.instance(p).setSaturation(0);
//		SaturationFilter.instance(p).applyTo(p);	}
	}
}
