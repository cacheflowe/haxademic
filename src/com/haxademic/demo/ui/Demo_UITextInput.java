package com.haxademic.demo.ui;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UITextInput;

public class Demo_UITextInput 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected boolean debugMode = true;
	protected UITextInput textInput;
	
	public void setupFirstFrame () {
		int sliderX = 10;
		int sliderY = 10;
		int sliderW = 300;
		int sliderH = 20;
//		new UISlider("b", 255, 0, 255, 0.5f, sliderX, sliderY += 30, sliderW, sliderH),
		textInput = new UITextInput("demo", 20, DemoAssets.fontOpenSansPath, ColorsHax.WHITE, 10, PTextAlign.LEFT, 100, 100, 300, 60);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'd') debugMode = !debugMode;
	}
	
	public void drawApp() {
		p.background(0);
		textInput.update(p.g);
	}
	
}
