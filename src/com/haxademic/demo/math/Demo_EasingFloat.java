package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.math.easing.EasingFloat;

import controlP5.ControlP5;

public class Demo_EasingFloat
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public float easeFactor = 6f;
	protected EasingFloat easingX = new EasingFloat(0, 6f);
	protected EasingFloat easingY = new EasingFloat(0, 6f);
	protected EasingFloat easingRotation = new EasingFloat(0, 6f);
	protected EasingFloat easingBottom = new EasingFloat(0, 16f);
	protected ControlP5 cp5;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.APP_NAME, "Demo_EasingFloat" );
		p.appConfig.setProperty( AppSettings.FPS, "60" );
	}


	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

		cp5 = new ControlP5(this);
		cp5.addSlider("easeFactor").setPosition(20,60).setWidth(200).setRange(0,30);
	}

	public void drawApp() {
		background(0);
		
		easingX.setEaseFactor(easeFactor);
		easingY.setEaseFactor(easeFactor);
		easingBottom.setEaseFactor(easeFactor);
		
		easingX.setTarget(p.mouseX);
		easingY.setTarget(p.mouseY);
		easingRotation.setTarget(p.mousePercentX() * P.TWO_PI);
		int bottomVal = P.round(p.frameCount * 0.01f) % 2;
		easingBottom.setTarget((bottomVal % 2) * p.width);

		easingX.update();
		easingY.update();
		easingRotation.update(true);
		easingBottom.update(true);
		
		DrawUtil.setDrawCenter(p);
		p.fill(255);
		p.pushMatrix();
		p.translate(easingX.value(), easingY.value());
		p.rotate(easingRotation.value());
		p.rect(0, 0, 40, 40);
		p.popMatrix();
		p.ellipse(easingBottom.value(), p.height - 20, 40, 40);
	}

}
