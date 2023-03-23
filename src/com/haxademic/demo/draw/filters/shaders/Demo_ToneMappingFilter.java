package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.filters.pshader.ToneMappingFilter;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.ui.UI;

import processing.core.PImage;

public class Demo_ToneMappingFilter
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
		
		// add tonemapping UI
		UI.addTitle("Tonemapping");
		UI.addToggle(ACTIVE, true, false);
		UI.addSlider(MODE, 1, 0, 9, 1, false);
		UI.addSlider(GAMMA, 2.2f, 0, 10, 0.01f, false);
		UI.addSlider(CROSSFADE, 1, 0, 1, 0.01f, false);
		
		// extra controls
		UI.addTitle("Postprocessing");
		UI.addSlider(CONTRAST, 1, 0, 3, 0.01f, false);
		UI.addSlider(SATURATION, 1, 0, 3, 0.01f, false);
	}
	
	protected void drawApp() {
		drawWebCam();
		doToneMapping();
	}
	
	protected void drawWebCam() {
		p.push();
		PImage webcamImg = WebCam.instance().image();
		boolean webcamIsGood = (webcamImg.width > 400);
		
		p.background((webcamIsGood) ? 50 : p.color(255,0,0));
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
		p.image(webcamImg, 0, 0);
		p.pop();
	}
	
	protected void doToneMapping() {
		if(UI.valueToggle(ACTIVE)) {
			ToneMappingFilter.instance().setMode(UI.valueInt(MODE));
			ToneMappingFilter.instance().setGamma(UI.value(GAMMA));
			ToneMappingFilter.instance().setCrossfade(UI.value(CROSSFADE));
			ToneMappingFilter.instance().applyTo(p.g);
			
			// add some saturation back in
			SaturationFilter.instance().setSaturation(UI.value(SATURATION));
			SaturationFilter.instance().applyTo(p.g);
			ContrastFilter.instance().setContrast(UI.value(CONTRAST));
			ContrastFilter.instance().applyTo(p.g);
		}
	}
	
	@Override
	public void newFrame(PImage frame) {
		DebugView.setValue("Last WebCam frame", p.frameCount);
	}
}
