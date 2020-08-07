package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.ui.UI;

import processing.core.PImage;

public class Demo_PShaderHotSwap_WebcamFilter
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShaderHotSwap shader;
	// tonemapping UI
	protected String ACTIVE = "ACTIVE";
	protected String MODE = "MODE";
	protected String GAMMA = "GAMMA";
	protected String CROSSFADE = "CROSSFADE";
	// extra effects UI
	protected String SATURATION = "SATURATION";
	protected String CONTRAST = "CONTRAST";

	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);	
	}
	
	protected void firstFrame() {
		// add webcam
		WebCam.instance().setDelegate(this);
		
		// add hot swap shader
		shader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/tone-mapping.glsl"));
		
		// add UI
		UI.addTitle("Tonemapping");
		UI.addToggle(ACTIVE, true, false);
		UI.addSlider(MODE, 0, 0, 9, 1, false);
		UI.addSlider(GAMMA, 2.2f, 0, 10, 0.01f, false);
		UI.addSlider(CROSSFADE, 1, 0, 1, 0.01f, false);
		UI.addSlider(CONTRAST, 1, 0, 3, 0.01f, false);
		UI.addSlider(SATURATION, 1, 0, 3, 0.01f, false);
	}
	
	protected void drawApp() {
		if(p.frameCount == 1) PG.setTextureRepeat(p.g, true);

		// draw webcam
		p.push();
		PImage webcamImg = WebCam.instance().image();
		boolean webcamIsGood = (webcamImg.width > 400);
		
		p.background((webcamIsGood) ? 50 : p.color(255,0,0));
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
		p.image(webcamImg, 0, 0);
		p.pop();
		
		// update & apply shader
		shader.update();
		shader.shader().set("time", p.frameCount * 0.01f);
		shader.shader().set("mode", UI.valueInt(MODE));
		shader.shader().set("gamma", UI.value(GAMMA));
		shader.shader().set("crossfade", UI.value(CROSSFADE));
		if(UI.valueToggle(ACTIVE)) p.filter(shader.shader());
		shader.showShaderStatus(p.g);
		
		// add some saturation back in
		SaturationFilter.instance(p).setSaturation(UI.value(SATURATION));
		if(UI.valueToggle(ACTIVE)) SaturationFilter.instance(p).applyTo(p.g);
		ContrastFilter.instance(p).setContrast(UI.value(CONTRAST));
		if(UI.valueToggle(ACTIVE)) ContrastFilter.instance(p).applyTo(p.g);
	}
	
	@Override
	public void newFrame(PImage frame) {
		DebugView.setValue("Last WebCam frame", p.frameCount);
	}
}
