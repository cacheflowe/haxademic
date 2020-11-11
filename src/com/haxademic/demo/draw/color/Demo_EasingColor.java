package com.haxademic.demo.draw.color;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

public class Demo_EasingColor
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected EasingColor colorHaxEasing;
	protected String EASING = "EASING";
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 640 );
		Config.setProperty( AppSettings.SHOW_UI, true );
	}

	protected void firstFrame() {
		// init easing color obj in various ways
//		colorHaxEasing = new EasingColor("#00ff00", 10);
//		colorHaxEasing = new EasingColor(0x00ff00, 10);
//		colorHaxEasing = new EasingColor("#ff00ff00", 10);
//		colorHaxEasing = new EasingColor(0, 255, 0, 255);
		colorHaxEasing = new EasingColor(0, 255, 0);
		
		// add UI
		UI.addSlider(EASING, 0.2f, 0.001f, 10, 0.001f, false);
	}
	
	protected void drawApp() {
		// change color on interval
		if(FrameLoop.frameMod(120)) {
			int newColor = ColorUtil.colorFromHex(ColorUtil.randomHex());
			colorHaxEasing.setTargetInt(newColor);
			DebugView.setValue("newHex", newColor);
		}
		
		// update easing from UI & color object
		colorHaxEasing.setEaseFactor(UI.value(EASING));
		colorHaxEasing.update();
		
		// draw color
		float padding = 100;
		p.background(0);
		p.fill(colorHaxEasing.colorInt());
		p.rect(padding, padding, p.width - padding * 2, p.height - padding * 2);
	}	

}
