package com.haxademic.demo.draw.image;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.image.GridHelper;

import processing.core.PGraphics;

public class Demo_GridHelper 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected GridHelper gridHelper;
	protected GridHelper gridHelper2;
	protected PGraphics fluidBuff;

	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);
	}

	protected void firstFrame() {
		gridHelper = new GridHelper("one");
		gridHelper2 = new GridHelper("two");
	}

	protected void drawApp() {
		// set up context
		p.background(0);
		p.noStroke();

		gridHelper.draw(p.g);
		gridHelper2.draw(p.g);
	}
}
