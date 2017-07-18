package com.haxademic.sketch.test;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.easing.EasingFloat;

import controlP5.ControlP5;

public class EasingFloatTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public float easeFactor = 6f;
	protected EasingFloat _easingX = new EasingFloat(0, 6f);
	protected EasingFloat _easingY = new EasingFloat(0, 6f);
	protected EasingFloat _easingBottom = new EasingFloat(0, 16f);
	protected ControlP5 _cp5;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FPS, "60" );
	}


	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

		_cp5 = new ControlP5(this);
		_cp5.addSlider("easeFactor").setPosition(20,60).setWidth(200).setRange(0,30);
	}

	public void drawApp() {
		background(0);
		
		_easingX.setEaseFactor(easeFactor);
		_easingY.setEaseFactor(easeFactor);
		_easingBottom.setEaseFactor(easeFactor);
		
		_easingX.setTarget(p.mouseX);
		_easingY.setTarget(p.mouseY);
		int bottomVal = P.round(p.frameCount * 0.01f) % 2;
		_easingBottom.setTarget((bottomVal % 2) * p.width);

		_easingX.update();
		_easingY.update();
		_easingBottom.update(true);
		
		DrawUtil.setDrawCenter(p);
		p.fill(255);
		p.ellipse(_easingX.value(), _easingY.value(), 40, 40);
		p.ellipse(_easingBottom.value(), p.height - 20, 40, 40);
	}

}
