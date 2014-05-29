package com.haxademic.sketch.test;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.easing.EasingFloat;

import controlP5.ControlP5;

@SuppressWarnings("serial")
public class EasingFloatTest
extends PAppletHax {
	
	public float easeFactor = 6f;
	protected EasingFloat _easingX = new EasingFloat(0, 6f);
	protected EasingFloat _easingY = new EasingFloat(0, 6f);
	protected ControlP5 _cp5;

	protected void overridePropsFile() {
		_appConfig.setProperty( "fps", "60" );
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
		
		_easingX.setTarget(p.mouseX);
		_easingY.setTarget(p.mouseY);
		
		_easingX.update();
		_easingY.update();
		
		DrawUtil.setDrawCenter(p);
		p.fill(255);
		p.ellipse(_easingX.value(), _easingY.value(), 40, 40);
	}

}
