package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;

public class Demo_MathUtil_getRadiansToTarget
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	PGraphics image;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 640 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {

		image = ImageUtil.imageToGraphics(DemoAssets.arrow());
	}

	protected void drawApp() {
		background(100,100,255);
		p.pushMatrix();
		PG.setDrawCenter(p);
		p.translate(p.width/2, p.height/2);
		float radsFromCenter = MathUtil.getRadiansToTarget(p.width/2, p.height/2, p.mouseX, p.mouseY);
		DebugView.setHelpLine("cos (x)", ""+P.cos(radsFromCenter));
		DebugView.setHelpLine("sin (y)", ""+P.sin(radsFromCenter));
		DebugView.setHelpLine("angle", ""+MathUtil.radiansToDegrees(radsFromCenter));
		DebugView.setHelpLine("rads", ""+radsFromCenter);

		p.rotate(radsFromCenter);
		p.image(image, 0, 0);
		p.popMatrix();
	}

}

