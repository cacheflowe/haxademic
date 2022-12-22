package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.ui.UI;

import processing.core.PImage;

public class Demo_PShaderHotSwap_Denoise
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShaderHotSwap shader;
	
	protected void config() {
		Config.setAppSize(1600, 800);
		Config.setProperty(AppSettings.SHOW_UI, true);
		Config.setProperty(AppSettings.FILLS_SCREEN, false);
	}
	
	protected void firstFrame() {
		shader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/denoise-smart.glsl"));
		UI.addTitle("Shader Uniforms");
		UI.addSlider("uSigma", 5.0f, 0f, 20f, 0.01f, false);
		UI.addSlider("uKSigma", 2.0f, 0f, 10f, 0.01f, false);
		UI.addSlider("uThreshold", 0.100f, 0f, 1f, 0.001f, false);
	}
	
	public void keyPressed() {
		super.keyPressed();
//		if(p.key == ' ') reset();
	}
	
	protected void drawApp() {
	    // clear screen, set repeat
		if(p.frameCount == 1) PG.setTextureRepeat(p.g, true);
		p.background(0);
		
		// draw camera into buffer
		pg.beginDraw();
		PImage camImg = DepthCamera.instance(DepthCameraType.Realsense).camera.getRgbImage();
		ImageUtil.cropFillCopyImage(camImg, pg, true);
		pg.endDraw();

		// run filter on buffer
		shader.update();
		shader.shader().set("uSigma", UI.value("uSigma"));
		shader.shader().set("uKSigma", UI.value("uKSigma"));
		shader.shader().set("uThreshold", UI.value("uThreshold"));
		pg.filter(shader.shader());

		// draw buffer to screen
		p.image(pg, 0, 0);
		
		// draw midpoint
		p.rect(p.width/2, 0, 2, p.height);
		
		// show shader compilation
		shader.showShaderStatus(p.g);
		DebugView.setValue("isValid()", shader.isValid());
	}

}
