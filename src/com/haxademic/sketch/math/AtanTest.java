package com.haxademic.sketch.math;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class AtanTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	PImage img;
	PGraphics pg;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
		p.appConfig.setProperty( AppSettings.SHOW_STATS, true );
	}

	public void setup() {
		super.setup();
		img = p.loadImage(FileUtil.getFile("images/bread-large.png"));
		pg = ImageUtil.imageToGraphics(img);
	}

	public void drawApp() {
		background(100,100,255);
		p.pushMatrix();
		DrawUtil.setDrawCenter(p);
		p.translate(p.width/2, p.height/2);
		float radsFromCenter = MathUtil.getRadiansToTarget(p.width/2, p.height/2, p.mouseX, p.mouseY);
		P.println("cos (x): ",P.cos(radsFromCenter), "sin (y)",P.sin(radsFromCenter), "angle: ", MathUtil.radiansToDegrees(radsFromCenter));

		float radsFromMouse = MathUtil.getRadiansToTarget(p.mouseX, p.mouseY, p.width/2, p.height/2);
		p.rotate(-radsFromCenter);
		p.image(pg, 0, 0);
		p.popMatrix();
	}

}

