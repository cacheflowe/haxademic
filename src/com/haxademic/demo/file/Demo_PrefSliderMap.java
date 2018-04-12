package com.haxademic.demo.file;

import java.util.HashMap;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.ui.PrefSlider;

public class Demo_PrefSliderMap 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected boolean debugMode = true;
	protected HashMap<String, PrefSlider> prefSliders;
	
	public void setupFirstFrame () {
		int sliderX = 10;
		int sliderY = 10;
		int sliderW = 300;
		int sliderH = 20;
		prefSliders = new HashMap<String, PrefSlider>();
		prefSliders.put("r", new PrefSlider("r", 255, 0, 255, 0.5f, sliderX, sliderY      , sliderW, sliderH));
		prefSliders.put("g", new PrefSlider("g", 255, 0, 255, 0.5f, sliderX, sliderY += 30, sliderW, sliderH));
		prefSliders.put("b", new PrefSlider("b", 255, 0, 255, 0.5f, sliderX, sliderY += 30, sliderW, sliderH));
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'd') debugMode = !debugMode;
	}
	
	public void drawApp() {
		// use prefs values
		p.background(prefSliders.get("r").value(), prefSliders.get("g").value(), prefSliders.get("b").value());
		// show sliders in debug mode
		if(debugMode) {
			for (PrefSlider prefSlider : prefSliders.values()) {
				prefSlider.update(p.g);
			}
		}
	}
	
}
