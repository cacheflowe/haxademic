package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;

public class Demo_MathUtil_getRadiansToTarget
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	PGraphics image;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	public void setupFirstFrame() {

		image = ImageUtil.imageToGraphics(DemoAssets.arrow());
	}

	public void drawApp() {
		background(100,100,255);
		p.pushMatrix();
		PG.setDrawCenter(p);
		p.translate(p.width/2, p.height/2);
		float radsFromCenter = MathUtil.getRadiansToTarget(p.width/2, p.height/2, p.mouseX, p.mouseY);
		p.debugView.setHelpLine("cos (x)", ""+P.cos(radsFromCenter));
		p.debugView.setHelpLine("sin (y)", ""+P.sin(radsFromCenter));
		p.debugView.setHelpLine("angle", ""+MathUtil.radiansToDegrees(radsFromCenter));
		p.debugView.setHelpLine("rads", ""+radsFromCenter);

		p.rotate(radsFromCenter);
		p.image(image, 0, 0);
		p.popMatrix();
	}

}

