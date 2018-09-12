package com.haxademic.demo.hardware.webcam;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PBlendModes;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.image.BufferMotionDetectionMap;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.particle.ParticleLauncher;
import com.haxademic.core.hardware.webcam.IWebCamCallback;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_WebcamGPUParticles
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics flippedCamera;
	
	protected BufferMotionDetectionMap motionDetectionMap;
	protected PGraphics motionBuffer;

	protected PGraphics renderedParticles;
	protected ArrayList<ParticleLauncher> particleLaunchers;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1600 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 600 );
		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 5 ); // 18
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
	}
		
	public void setupFirstFrame () {
		// capture webcam frames
		p.webCamWrapper.setDelegate(this);
		
		// build final draw buffer
		renderedParticles = p.createGraphics(800, 600, PRenderers.P3D);
		renderedParticles.smooth(8);
		p.debugView.setTexture(renderedParticles);
		
		// build multiple particles launchers
		particleLaunchers = new ArrayList<ParticleLauncher>();
		int totalVertices = 0;
		for (int i = 0; i < 40; i++) {
			ParticleLauncher particles = new ParticleLauncher();
			particleLaunchers.add(particles);
			totalVertices += particles.vertices();
		}
		p.debugView.setValue("totalVertices", totalVertices);
		p.debugView.setTexture(particleLaunchers.get(0).progressBuffer());
	}

	
	@Override
	public void newFrame(PImage frame) {
		// lazy-init flipped camera buffer
		if(flippedCamera == null) {
			int cameraW = 800;	// frame.width (these are jacked up on OS X)
			int cameraH = 600;	// frame.height
			flippedCamera = p.createGraphics(cameraW, cameraH, PRenderers.P2D);
			motionBuffer = p.createGraphics(cameraW, cameraH, PRenderers.P2D);
		}
		// copy flipped
		flippedCamera.copy(frame, 0, 0, frame.width, frame.height, flippedCamera.width, 0, -flippedCamera.width, flippedCamera.height);
		
		// lazy-init motion detection to pass Kinect into
		if(motionDetectionMap == null) {
			motionDetectionMap = new BufferMotionDetectionMap(flippedCamera, 0.05f);
		}
		ImageUtil.cropFillCopyImage(flippedCamera, motionBuffer, true);
		BlurHFilter.instance(P.p).setBlurByPercent(0.5f, motionBuffer.width);
		BlurHFilter.instance(P.p).applyTo(motionBuffer);
		BlurVFilter.instance(P.p).setBlurByPercent(0.5f, motionBuffer.height);
		BlurVFilter.instance(P.p).applyTo(motionBuffer);
		
		motionDetectionMap.setBlendLerp(0.25f);
		motionDetectionMap.setDiffThresh(0.025f);
		motionDetectionMap.setFalloffBW(0.25f);
		motionDetectionMap.setThresholdCutoff(0.5f);
		motionDetectionMap.setBlur(1f);
		motionDetectionMap.updateSource(motionBuffer);
		
		p.debugView.setTexture(flippedCamera);
		p.debugView.setTexture(motionDetectionMap.backplate());
		p.debugView.setTexture(motionDetectionMap.differenceBuffer());
		p.debugView.setTexture(motionDetectionMap.bwBuffer());
	}

	public void drawApp() {
		// clear the screen
		background(0);
		
		if(motionDetectionMap == null) return;
		
		// launch! 
		motionDetectionMap.loadPixels();

		int particleLauncherIndex = p.frameCount % particleLaunchers.size();
		particleLaunchers.get(particleLauncherIndex).beginLaunch();
		
		int FRAME_LAUNCH_INTERVAL = 1;
		int MAX_LAUNCHED_PER_FRAME = 300;
		int LAUNCH_ATTEMPTS = 1500;
		if(p.frameCount % FRAME_LAUNCH_INTERVAL == 0) {
			int numLaunched = 0;
			for (int i = 0; i < LAUNCH_ATTEMPTS; i++) {
				if(numLaunched < MAX_LAUNCHED_PER_FRAME) {
					int checkX = MathUtil.randRange(0, motionBuffer.width);
					int checkY = MathUtil.randRange(0, motionBuffer.height);
					if(motionDetectionMap.pixelActive(checkX, checkY)) {
						particleLaunchers.get(particleLauncherIndex).launch(checkX, checkY);
						numLaunched++;
					}
				}
			}
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
		renderedParticles.blendMode(PBlendModes.BLEND);
		DrawUtil.setPImageAlpha(p, 0.9f);
		p.image(flippedCamera, 0, 0);
		DrawUtil.setPImageAlpha(p, 0.4f);
		p.image(motionDetectionMap.bwBuffer(), flippedCamera.width, 0, flippedCamera.width, flippedCamera.height);
		DrawUtil.setPImageAlpha(p, 1f);
		renderedParticles.blendMode(PBlendModes.ADD);
		p.image(renderedParticles, flippedCamera.width, 0);
		
		// desaturate
		SaturationFilter.instance(p).setSaturation(0);
		SaturationFilter.instance(p).applyTo(p);
	}
	
	public void keyPressed() {
		super.keyPressed();
	}
	
}
