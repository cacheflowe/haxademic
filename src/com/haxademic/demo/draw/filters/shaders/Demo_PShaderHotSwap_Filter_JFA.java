package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

import processing.core.PFont;
import processing.core.PGraphics;

public class Demo_PShaderHotSwap_Filter_JFA
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShaderHotSwap shader;
	protected PGraphics imgJFA;
	protected PGraphics imgLast;
	protected int jfaStep = 0;
	protected boolean jfaDirty = true;
	
	protected void config() {
		Config.setAppSize(1600, 800);
		Config.setProperty(AppSettings.SHOW_UI, true);
		Config.setProperty(AppSettings.PG_32_BIT, true);
	}
	
	protected void firstFrame() {
		imgJFA = PG.newDataPG(p.width, p.height);
		imgLast = PG.newDataPG(p.width, p.height);
		
		shader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/jump-flood.glsl"));
//		shader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/color-rotate.glsl"));
		UI.addTitle("Shader Uniforms");
//		UI.addSlider("crossfade", 1f, 0.0f, 1f, 0.01f, false);
//		UI.addSlider("imageBrightness", 9f, 0f, 10f, 0.1f, false);
//		UI.addSlider("flareBrightness", 9f, 0f, 10f, 0.1f, false);
//		UI.addSlider("iters", 100f, 0f, 5000f, 10f, false);
//		shader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/radial-flare.glsl"));
//		UI.addTitle("Shader Uniforms");
//		UI.addSlider("radialLength", 0.95f, 0.5f, 1f, 0.01f, false);
//		UI.addSlider("imageBrightness", 9f, 0f, 10f, 0.1f, false);
//		UI.addSlider("flareBrightness", 9f, 0f, 10f, 0.1f, false);
//		UI.addSlider("iters", 100f, 0f, 5000f, 10f, false);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') reset();
		if(p.key == '1') step();
	}
	
	protected void reset() {
		jfaStep = 0;
		jfaDirty = true;
	}
	
	protected void step() {
		jfaStep++;
		jfaDirty = true;
	}
	
	protected void drawShape() {
		imgJFA.beginDraw();
		imgJFA.background(0);
		PG.setDrawCenter(imgJFA);
		PG.setCenterScreen(imgJFA);

		imgJFA.fill(255);
		imgJFA.circle(0, 0, 100);
		imgJFA.fill(255);
		imgJFA.rect(-300, -200, 100, 40);
		imgJFA.rect(-300, 200, 100, 40);
		imgJFA.rect(300, 200, 500, 20);

		imgJFA.endDraw();
	}
	
	protected void runJfaStep() {
		shader.update();
		shader.shader().set("iter", (float) jfaStep);
		shader.shader().set("texture0", imgJFA);
		imgJFA.filter(shader.shader());
	}
	
	
	protected void drawApp() {
		if(p.frameCount == 1) PG.setTextureRepeat(p.g, true);
		p.background(0);
		shader.update();
		if(jfaDirty == true) {
			if(jfaStep == 0) {
				drawShape();
			}
			runJfaStep();
			jfaDirty = false;
		}
		
		// draw buffer to screen
		p.image(imgJFA, 0, 0);

		// show step
		String fontFile = DemoAssets.fontOpenSansPath;
		PFont font = FontCacher.getFont(fontFile, 24);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		p.text("JFA Step: " + jfaStep, 40, p.height - font.getSize() - 40);
		
		// show shader compilation
		shader.showShaderStatus(p.g);
		
		
	}

}
