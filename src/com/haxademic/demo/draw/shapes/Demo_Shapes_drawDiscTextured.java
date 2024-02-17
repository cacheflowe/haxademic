package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PImage;

public class Demo_Shapes_drawDiscTextured 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
		Config.setProperty( AppSettings.LOOP_FRAMES, 160 );
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
	}

	protected void firstFrame() {
		noStroke();
	}

	protected void drawApp() {
		background(0);
		lights();
		PG.setCenterScreen(p.g);
		PG.basicCameraFromMouse(p.g);

		float easedPercent = Penner.easeInOutQuart(FrameLoop.progress());
		float radsCompleteEased = easedPercent * P.TWO_PI;
		
		Shapes.drawDiscTextured(p.g, 300, 250, 100, DemoAssets.justin());
	}
}
