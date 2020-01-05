package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;

public class Demo_EasingFloat_updateRadians
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics image;
	protected EasingFloat rotation = new EasingFloat(0, 8f);

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
		PG.setCenterScreen(p);
		
		float radsFromCenter = MathUtil.getRadiansToTarget(p.width/2f, p.height/2f, p.mouseX, p.mouseY);
		rotation.setTarget(radsFromCenter);
		rotation.updateRadians();
		
		DebugView.setValue("radsFromCenter", radsFromCenter);
		DebugView.setValue("rotation.value()", rotation.value());
		DebugView.setHelpLine("cos (x)", ""+P.cos(rotation.value()));
		DebugView.setHelpLine("sin (y)", ""+P.sin(rotation.value()));
		DebugView.setHelpLine("angle", ""+MathUtil.radiansToDegrees(rotation.value()));
		DebugView.setHelpLine("rads", ""+rotation.value());

		p.rotate(rotation.value());
		p.image(image, 0, 0);
		p.popMatrix();
	}

}

