package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.system.AppUtil;
import com.haxademic.core.ui.UI;

import processing.core.PImage;

public class Demo_PShaderHotSwap_SkySegmentation
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShaderHotSwap shader;
	protected PImage imgFg;
	protected PImage imgBg;
	protected PImage imgUi;
	
	protected void config() {
		Config.setAppSize(1889/1.5f, 2048/1.5f);
		Config.setProperty(AppSettings.SHOW_UI, true);
		Config.setProperty(AppSettings.FILLS_SCREEN, false);
	}
	
	protected void firstFrame() {
		AppUtil.setLocation(this, 100, 100);
		shader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/demo-sky-segmentation.glsl"));
		
		UI.addTitle("Shader Uniforms");
		UI.addSlider("uSmoothLow", 0, 0, 1, 0.01f, false);
		UI.addSlider("uSmoothHigh", 1, 0f, 1, 0.01f, false);
		UI.addSlider("uAlphaMapLow", 0, -1, 1f, 0.01f, false);
		UI.addSlider("uAlphaMapHigh", 2, 0, 3, 0.01f, false);

		imgFg = p.loadImage("D:\\workspace\\nike-wwc-sendoff-2023\\shader-test\\fg.png");
		imgBg = p.loadImage("D:\\workspace\\nike-wwc-sendoff-2023\\shader-test\\bg.png");
		imgUi = p.loadImage("D:\\workspace\\nike-wwc-sendoff-2023\\shader-test\\ui.png");
	}
	
	public void keyPressed() {
		super.keyPressed();
//		if(p.key == ' ') reset();
	}
	
	protected void drawApp() {
	    // clear screen, set repeat
		if(p.frameCount == 1) PG.setTextureRepeat(p.g, true);
		p.background(20);
		
		// update uniforms
		shader.shader().set("texture", imgFg);
		shader.shader().set("uMaskTexture", imgBg);
		shader.shader().set("uTime", p.millis() / 1000f);
		shader.shader().set("uSmoothLow", UI.value("uSmoothLow"));
		shader.shader().set("uSmoothHigh", UI.value("uSmoothHigh"));
		shader.shader().set("uAlphaMapLow", UI.value("uAlphaMapLow"));
		shader.shader().set("uAlphaMapHigh", UI.value("uAlphaMapHigh"));
		shader.update();

		// draw camera into buffer
		pg.beginDraw();
		pg.background(255, 0, 0);
		ImageUtil.drawImageCropFill(DemoAssets.textureNebula(), pg, true);
		pg.shader(shader.shader());
		ImageUtil.drawImageCropFill(imgFg, pg, false);
		pg.resetShader();
		ImageUtil.drawImageCropFill(imgUi, pg, false);
		pg.endDraw();

		// run filter on buffer

		// draw buffer to screen
		ImageUtil.cropFillCopyImage(pg, p.g, false);
				
		// show shader compilation
		shader.showShaderStatus(p.g);
		DebugView.setValue("isValid()", shader.isValid());
	}

}
