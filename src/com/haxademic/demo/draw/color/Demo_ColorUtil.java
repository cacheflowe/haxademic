package com.haxademic.demo.draw.color;

import java.awt.Color;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.color.ColorUtil;

public class Demo_ColorUtil
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 182 );
	}

	protected void firstFrame() {

	}
	
	protected void drawApp() {
		if(p.frameCount % 200 < 100) {
			p.background(Integer.decode("0x" + "f6eb0f"));
			p.background(Color.decode("#f6eb0f").getRGB());
			p.background(0xf6eb0f);
		} else {
			p.background(ColorUtil.colorFromHex("#EB0029"));
		}
	}	

}

