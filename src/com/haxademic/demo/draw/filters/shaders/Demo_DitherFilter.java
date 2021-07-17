package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.DitherFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.ui.UI;

import processing.core.PImage;

public class Demo_DitherFilter
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// dither UI
	protected String DITHER_MODE = "DITHER_MODE";
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
		UI.addTitle("Dither");
		UI.addSlider(DITHER_MODE, 2, 0, 2, 1, false);
		
		// extra controls
		UI.addTitle("Preprocessing");
		UI.addSlider(CONTRAST, 1, 0, 3, 0.01f, false);
		UI.addSlider(SATURATION, 1, 0, 3, 0.01f, false);
	}
	
	protected void drawApp() {
		drawWebCam();
		doDither();
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
	
	protected void doDither() {
		// pre-process the image
		SaturationFilter.instance(p).setSaturation(UI.value(SATURATION));
		SaturationFilter.instance(p).applyTo(p.g);
		ContrastFilter.instance(p).setContrast(UI.value(CONTRAST));
		ContrastFilter.instance(p).applyTo(p.g);

		// do dithering
		if(UI.valueInt(DITHER_MODE) == 0) DitherFilter.instance(P.p).setDitherMode2x2();
		if(UI.valueInt(DITHER_MODE) == 1) DitherFilter.instance(P.p).setDitherMode4x4();
		if(UI.valueInt(DITHER_MODE) == 2) DitherFilter.instance(P.p).setDitherMode8x8();
		DitherFilter.instance(P.p).applyTo(p.g);
	}
	
	@Override
	public void newFrame(PImage frame) {
		DebugView.setValue("Last WebCam frame", p.frameCount);
	}
}
