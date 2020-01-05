package com.haxademic.demo.ui;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.ui.UISlider;

public class Demo_UISlider 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected boolean debugMode = true;
	protected UISlider[] prefSliders;
	
	protected void firstFrame () {
		int sliderX = 10;
		int sliderY = 10;
		int sliderW = 300;
		int sliderH = 20;
		prefSliders = new UISlider[] {
				new UISlider("r", 255, 0, 255, 0.5f, sliderX, sliderY      , sliderW, sliderH),
				new UISlider("g", 255, 0, 255, 0.5f, sliderX, sliderY += 30, sliderW, sliderH),
				new UISlider("b", 255, 0, 255, 0.5f, sliderX, sliderY += 30, sliderW, sliderH),
		};
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'd') debugMode = !debugMode;
	}
	
	protected void drawApp() {
		// use prefs values
		p.background(prefSliders[0].value(), prefSliders[1].value(), prefSliders[2].value());
		// show sliders in debug mode
		if(debugMode) {
			for (UISlider prefSlider : prefSliders) {
				prefSlider.draw(p.g);
			}
		}
	}
	
}
