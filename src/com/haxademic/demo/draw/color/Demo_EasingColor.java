package com.haxademic.demo.draw.color;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.color.EasingColor;

public class Demo_EasingColor
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected EasingColor colorHaxEasing;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
	}

	public void setupFirstFrame() {
		colorHaxEasing = new EasingColor("#00ff00", 10);
		colorHaxEasing = new EasingColor(0x00ff00, 10);
		colorHaxEasing = new EasingColor("#ff00ff00", 10);
//		colorHaxEasing = new EasingColor(0, 255, 0, 255);
		colorHaxEasing = new EasingColor(0, 255, 0);
	}
	
	public void drawApp() {
		colorHaxEasing.setEaseFactor(p.mousePercentX());
		colorHaxEasing.update();
		p.background(0);
		p.fill(colorHaxEasing.colorInt());
		p.rect(0, 0, p.width, p.height);
	}	

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			int newColor = ColorUtil.colorFromHex(ColorUtil.randomHex());
			p.debugView.setValue("newHex", newColor);
			colorHaxEasing.setCurrentInt(0xffffffff);
			colorHaxEasing.setTargetInt(newColor);
		}
	}
}

