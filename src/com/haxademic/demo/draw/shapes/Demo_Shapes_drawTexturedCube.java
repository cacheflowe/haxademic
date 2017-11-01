package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PImage;

public class Demo_Shapes_drawTexturedCube 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int _frames = 60;
	PImage img;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
	}

	public void setup() {
		super.setup();
		img = DemoAssets.squareTexture();
		noStroke();
	}

	public void drawApp() {
		background(255);
		lights();
		translate(width/2, height/2, -200);
		
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutQuart(percentComplete, 0, 1, 1);
		float radsComplete = percentComplete * P.TWO_PI;
		float radsCompleteEased = easedPercent * P.TWO_PI;

		rotateX(P.PI + 0.2f * P.sin(radsComplete)); 
		rotateY(radsCompleteEased * 0.25f); 
		
		Shapes.drawTexturedCube(p.g, 200, img);
	}
}
