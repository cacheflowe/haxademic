package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;

public class Demo_EasingFloat_updateRadians
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics image;
	protected EasingFloat rotation = new EasingFloat(0, 8f);

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	public void setup() {
		super.setup();
		image = ImageUtil.imageToGraphics(DemoAssets.arrow());
	}

	public void drawApp() {
		background(100,100,255);
		p.pushMatrix();
		DrawUtil.setDrawCenter(p);
		DrawUtil.setCenterScreen(p);
		
		float radsFromCenter = MathUtil.getRadiansToTarget(p.width/2f, p.height/2f, p.mouseX, p.mouseY);
		P.out(radsFromCenter);
		p.debugView.setValue("radsFromCenter", radsFromCenter);
		rotation.setTarget(radsFromCenter);
		rotation.updateRadians();
		p.debugView.setValue("rotation.value()", rotation.value());
		
		p.debugView.setHelpLine("cos (x)", ""+P.cos(rotation.value()));
		p.debugView.setHelpLine("sin (y)", ""+P.sin(rotation.value()));
		p.debugView.setHelpLine("angle", ""+MathUtil.radiansToDegrees(rotation.value()));
		p.debugView.setHelpLine("rads", ""+rotation.value());

		p.rotate(-rotation.value());
		p.image(image, 0, 0);
		p.popMatrix();
	}

}

