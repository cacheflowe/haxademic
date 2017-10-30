package com.haxademic.sketch.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;

import controlP5.ControlP5;

public class TrigDriveTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public float radians = 0;
	public float speed = 0;
	protected ControlP5 _cp5;
	
	protected float _x = 0;
	protected float _y = 0;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FPS, "60" );
	}

	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

		_cp5 = new ControlP5(this);
		_cp5.addSlider("radians").setPosition(20,60).setWidth(200).setRange(0,P.TWO_PI);
		_cp5.addSlider("speed").setPosition(20,100).setWidth(200).setRange(0,10);
		
		_x = p.width / 2;
		_y = p.height / 2;
	}

	public void drawApp() {
		background(0);
		DrawUtil.setDrawCenter(p);

		_x += P.sin(radians) * speed;
		_y += P.cos(radians) * speed;
		
		if( _x > p.width ) _x = 0;
		if( _x < 0 ) _x = p.width;
		if( _y > p.height ) _y = 0;
		if( _y < 0 ) _y = p.height;

		p.pushMatrix();
		p.fill(255);
		
		p.translate(_x, _y);
		p.rotate(-radians);
		p.rect(0, 0, 20, 40);
		
		p.popMatrix();
	}

}
