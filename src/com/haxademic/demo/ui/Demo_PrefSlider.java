package com.haxademic.demo.ui;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.ui.PrefSlider;

public class Demo_PrefSlider 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected boolean debugMode = true;
	protected PrefSlider[] prefSliders;
	
	public void setupFirstFrame () {
		int sliderX = 10;
		int sliderY = 10;
		int sliderW = 300;
		int sliderH = 20;
		prefSliders = new PrefSlider[] {
				new PrefSlider("r", 255, 0, 255, 0.5f, sliderX, sliderY      , sliderW, sliderH),
				new PrefSlider("g", 255, 0, 255, 0.5f, sliderX, sliderY += 30, sliderW, sliderH),
				new PrefSlider("b", 255, 0, 255, 0.5f, sliderX, sliderY += 30, sliderW, sliderH),
		};
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'd') debugMode = !debugMode;
	}
	
	public void drawApp() {
		// use prefs values
		p.background(prefSliders[0].value(), prefSliders[1].value(), prefSliders[2].value());
		// show sliders in debug mode
		if(debugMode) {
			for (PrefSlider prefSlider : prefSliders) {
				prefSlider.update(p.g);
			}
		}
	}
	
}
