package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PImage;

public class Demo_Shapes_drawTexturedCube 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage img;
	
	protected void config() {
		Config.setProperty( AppSettings.LOOP_FRAMES, 160 );
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
	}

	public void firstFrame() {

		noStroke();
	}

	public void drawApp() {
		background(0);
		lights();
		translate(width/2, height/2, -200);
		
		float easedPercent = Penner.easeInOutQuart(FrameLoop.progress());
		float radsCompleteEased = easedPercent * P.TWO_PI;

		p.rotateY(P.map(p.mouseX, 0, p.width, 0, P.TWO_PI * 2));
		p.rotateX(P.map(p.mouseY, 0, p.height, P.TWO_PI * 2, 0));
		// rotateY(radsCompleteEased); 
		
		Shapes.drawTexturedCube(p.g, 200, DemoAssets.justin());
	}
}
