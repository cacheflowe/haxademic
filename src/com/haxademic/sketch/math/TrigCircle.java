package com.haxademic.sketch.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;

import controlP5.ControlP5;

public class TrigCircle
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public float radians = 0;
	public float radius = 0;
	protected ControlP5 _cp5;
	
	protected float _x = 0;
	protected float _y = 0;

	protected void overridePropsFile() {
	}

	public void setup() {
		super.setup();	

		_cp5 = new ControlP5(this);
		_cp5.addSlider("radians").setPosition(20,60).setWidth(200).setRange(0,P.TWO_PI);
		_cp5.addSlider("radius").setPosition(20,100).setWidth(200).setRange(0,300);
	}

	public void drawApp() {
		background(0);
		DrawUtil.setDrawCenter(p);

		_x = p.width / 2 + P.cos(radians) * radius;
		_y = p.height / 2 + P.sin(radians) * radius;
		
		p.fill(255);
		p.ellipse(_x, _y, 40, 40);
	}

}
