package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.image.ImageFramesHistory;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_ImageSequenceRecorder_Slitscan 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ImageFramesHistory recorder;
	protected PGraphics camBuffer;
	protected PGraphics noiseBuffer;
	protected PGraphics slitscanOutputBuffer;
	protected PGraphics slitscanLerpedBuffer;
	protected PShaderHotSwap slitscanShader;
	protected TextureShader noiseTexture;
	protected int numFrames = 16;
	
	// TODO:
	// - Swap for faster camera (60fps would be nice)
	// - Add more shaders to handle more frames of history
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, false );
		Config.setProperty(AppSettings.FILLS_SCREEN, false );
	}
		
	protected void firstFrame () {
		camBuffer = PG.newPG32(640, 480, false, true);
		noiseBuffer = PG.newPG32(640, 480, false, true);
		slitscanOutputBuffer = PG.newPG32(640, 480, false, true);
		slitscanLerpedBuffer = PG.newPG32(640, 480, false, true);
		DebugView.setTexture("slitscanLerpedBuffer", slitscanLerpedBuffer);
		recorder = new ImageFramesHistory(camBuffer.width, camBuffer.height, numFrames);
		slitscanShader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/slitscan-texture-map.glsl"));	
		noiseTexture = new TextureShader(TextureShader.noise_simplex_2d_iq);
		WebCam.instance().setDelegate(this);
	}

	protected void drawApp() {
		p.background( 0 );
		DebugView.setTexture("camBuffer", camBuffer);

		// update noise
		noiseTexture.updateTime();
		noiseTexture.shader().set("offset", 0f, p.frameCount * 0.007f);
		noiseTexture.shader().set("rotation", 0f, p.frameCount * 0.002f);
		noiseTexture.shader().set("zoom", 2.f);
		noiseBuffer.filter(noiseTexture.shader());
		ContrastFilter.instance().setContrast(1.35f);	// weights the time distortion to newest/oldest
		ContrastFilter.instance().applyTo(noiseBuffer);
		DebugView.setTexture("noiseBuffer", noiseBuffer);
		
		// override map buffer
		// pixel mode
		/*
		noiseBuffer.beginDraw();
		noiseBuffer.noStroke();
		noiseBuffer.background(255);
		int gridSize = 20;
		for (int x = 0; x < noiseBuffer.width; x+=gridSize) {
			for (int y = 0; y < noiseBuffer.height; y+=gridSize) {
				noiseBuffer.fill(p.noise(x, y) * 255f);
				noiseBuffer.rect(x, y, gridSize, noiseBuffer.height);
			}
		}
		noiseBuffer.endDraw();
		*/
		
		// debug draw recorder object frames
		PG.setDrawCorner(p);
		recorder.drawDebug(p.g);	// kills the rest of the drawing
		
		// slitscanShader
		slitscanShader.update();
		slitscanShader.shader().set("texture", noiseBuffer);
		slitscanShader.shader().set("map", noiseBuffer);
		for (int i = 0; i < numFrames; i++) {
			slitscanShader.shader().set("frame_"+i, recorder.getSortedFrame(i));
		}
		slitscanOutputBuffer.filter(slitscanShader.shader());
		DebugView.setTexture("slitscanOutputBuffer", slitscanOutputBuffer);
		
		// lerp the slitscan to next buffer
		BlendTowardsTexture.instance().setBlendLerp(0.45f);
		BlendTowardsTexture.instance().setSourceTexture(slitscanOutputBuffer);
		BlendTowardsTexture.instance().applyTo(slitscanLerpedBuffer);
		
		// draw live webcam
		p.pushMatrix();
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
//		p.image(camBuffer, 0, 0);
		p.image(slitscanOutputBuffer, 0, 0);
//		p.image(slitscanLerpedBuffer, 0, 0);
		p.popMatrix();
		
	}

	@Override
	public void newFrame(PImage frame) {
		// set recorder frame - use buffer as intermediary to fix aspect ratio
		ImageUtil.copyImageFlipH(frame, camBuffer);
		recorder.addFrame(camBuffer);
		// do some post-processing
		SaturationFilter.instance().setSaturation(0);
//		SaturationFilter.instance().applyTo(recorder.getCurFrame());
		// set debug staus
		DebugView.setValue("Last WebCam frame", p.frameCount);
	}

}
