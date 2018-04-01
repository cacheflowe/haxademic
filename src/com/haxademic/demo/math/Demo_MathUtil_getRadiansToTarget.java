package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;

public class Demo_MathUtil_getRadiansToTarget
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	PGraphics pg;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	public void setup() {
		super.setup();
		pg = ImageUtil.imageToGraphics(DemoAssets.smallTexture());
	}

	public void drawApp() {
		background(100,100,255);
		p.pushMatrix();
		DrawUtil.setDrawCenter(p);
		p.translate(p.width/2, p.height/2);
		float radsFromCenter = MathUtil.getRadiansToTarget(p.width/2, p.height/2, p.mouseX, p.mouseY);
		p.debugView.setHelpLine("cos (x)", ""+P.cos(radsFromCenter));
		p.debugView.setHelpLine("sin (y)", ""+P.sin(radsFromCenter));
		p.debugView.setHelpLine("angle", ""+MathUtil.radiansToDegrees(radsFromCenter));
		p.debugView.setHelpLine("rads", ""+radsFromCenter);

		p.rotate(-radsFromCenter);
		p.image(pg, 0, 0);
		p.popMatrix();
	}

}

