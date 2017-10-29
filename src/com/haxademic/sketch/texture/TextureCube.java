package com.haxademic.sketch.texture;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PImage;

public class TextureCube 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int _frames = 60;
	PImage img;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.APP_VIEWER_WINDOW, true );
		p.appConfig.setProperty( AppSettings.APP_VIEWER_SCALE, 0.75f );
	}

	public void setup() {
		super.setup();
		img = loadImage(FileUtil.getFile("images/smiley-big.png"));
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

		Shapes.drawTexturedBox(p.g, 200, img);
	}
}
