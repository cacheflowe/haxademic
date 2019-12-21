package com.haxademic.demo.ui;

import java.util.HashMap;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.ui.UISlider;

public class Demo_UISlider_Map 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected boolean debugMode = true;
	protected HashMap<String, UISlider> sliders;
	
	public void setupFirstFrame () {
		int sliderX = 10;
		int sliderY = 10;
		int sliderW = 300;
		int sliderH = 20;
		sliders = new HashMap<String, UISlider>();
		sliders.put("r", new UISlider("r", 255, 0, 255, 0.5f, sliderX, sliderY      , sliderW, sliderH));
		sliders.put("g", new UISlider("g", 255, 0, 255, 0.5f, sliderX, sliderY += 30, sliderW, sliderH));
		sliders.put("b", new UISlider("b", 255, 0, 255, 0.5f, sliderX, sliderY += 30, sliderW, sliderH));
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'd') debugMode = !debugMode;
	}
	
	public void drawApp() {
		// use prefs values
		p.background(sliders.get("r").value(), sliders.get("g").value(), sliders.get("b").value());
		// show sliders in debug mode
		if(debugMode) {
			for (UISlider prefSlider : sliders.values()) {
				prefSlider.draw(p.g);
			}
		}
	}
	
}
