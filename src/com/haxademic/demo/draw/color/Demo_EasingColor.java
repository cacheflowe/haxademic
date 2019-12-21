package com.haxademic.demo.draw.color;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.hardware.mouse.Mouse;

public class Demo_EasingColor
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected EasingColor colorHaxEasing;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 640 );
	}

	public void firstFrame() {
		colorHaxEasing = new EasingColor("#00ff00", 10);
		colorHaxEasing = new EasingColor(0x00ff00, 10);
		colorHaxEasing = new EasingColor("#ff00ff00", 10);
//		colorHaxEasing = new EasingColor(0, 255, 0, 255);
		colorHaxEasing = new EasingColor(0, 255, 0);
	}
	
	public void drawApp() {
		colorHaxEasing.setEaseFactor(Mouse.xNorm);
		colorHaxEasing.update();
		p.background(0);
		p.fill(colorHaxEasing.colorInt());
		p.rect(0, 0, p.width, p.height);
	}	

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			int newColor = ColorUtil.colorFromHex(ColorUtil.randomHex());
			DebugView.setValue("newHex", newColor);
			colorHaxEasing.setCurrentInt(0xffffffff);
			colorHaxEasing.setTargetInt(newColor);
		}
	}
}

