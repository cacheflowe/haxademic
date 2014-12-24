package com.haxademic.sketch.test;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.image.ScreenUtil;

import controlP5.ControlP5;

@SuppressWarnings("serial")
public class ScreenGrabTest
extends PAppletHax {
	
	protected ControlP5 _cp5;
	
	protected int _x = 0;
	protected int _y = 0;

	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "800" );
		_appConfig.setProperty( "height", "600" );
		_appConfig.setProperty( "fps", "60" );
	}

	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

		_cp5 = new ControlP5(this);
		_cp5.addSlider("_x").setPosition(20,60).setWidth(200).setRange(0,p.displayWidth - p.width);
		_cp5.addSlider("_y").setPosition(20,100).setWidth(200).setRange(0,p.displayHeight - p.height);
	}

	public void drawApp() {
		background(0);
		DrawUtil.setDrawCorner(p);
		p.image( ScreenUtil.getScreen(_x, _y, p.width, p.height), 0, 0 );
	}

}
