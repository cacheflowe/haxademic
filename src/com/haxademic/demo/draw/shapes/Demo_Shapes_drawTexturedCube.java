package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.math.easing.Penner;

import processing.core.PImage;

public class Demo_Shapes_drawTexturedCube 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage img;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, 160 );
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
	}

	public void setup() {
		super.setup();
		noStroke();
	}

	public void drawApp() {
		background(0);
		lights();
		translate(width/2, height/2, -200);
		
		float easedPercent = Penner.easeInOutQuart(loop.progress(), 0, 1, 1);
		float radsCompleteEased = easedPercent * P.TWO_PI;

		p.rotateY(P.map(p.mouseX, 0, p.width, 0, P.TWO_PI * 2));
		p.rotateX(P.map(p.mouseY, 0, p.height, P.TWO_PI * 2, 0));
		// rotateY(radsCompleteEased); 
		
		Shapes.drawTexturedCube(p.g, 200, DemoAssets.justin());
	}
}
