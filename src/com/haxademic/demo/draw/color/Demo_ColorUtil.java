package com.haxademic.demo.draw.color;

import java.awt.Color;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.image.TickerScroller;

public class Demo_ColorUtil
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected TickerScroller ticker;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 182 );
	}

	public void setup() {
		super.setup();
	}
	
	public void drawApp() {
		if(p.frameCount % 200 < 100) {
			p.background(Integer.decode("0x" + "f6eb0f"));
			p.background(Color.decode("#f6eb0f").getRGB());
			p.background(0xf6eb0f);
		} else {
			p.background(ColorUtil.colorFromHex("#EB0029"));
		}
	}	

}

