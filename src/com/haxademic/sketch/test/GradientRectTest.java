package com.haxademic.sketch.test;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ColorHaxEasing;
import com.haxademic.core.draw.shapes.Gradients;

@SuppressWarnings("serial")
public class GradientRectTest
extends PAppletHax {

	protected ColorHaxEasing _colorStart;
	protected ColorHaxEasing _colorStop;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "600" );
		_appConfig.setProperty( "height", "600" );
	}

	public void setup() {
		super.setup();	
		_colorStart = new ColorHaxEasing("#000000", 20f);
		_colorStop = new ColorHaxEasing("#000000", 20f);
	}

	public void drawApp() {
		p.background(0);

		_colorStart.setTargetColorInt( p.color(255f * P.sin(p.frameCount/20f), 255f * P.sin(p.frameCount/25f), 255f * P.sin(p.frameCount/30f)) );
		_colorStart.update();
		_colorStop.setTargetColorInt( p.color(255f * P.sin(p.frameCount/40f), 255f * P.sin(p.frameCount/45f), 255f * P.sin(p.frameCount/50f)) );
		_colorStop.update();
		
		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
		Gradients.linear(p, p.width, p.height, _colorStart.colorInt(), _colorStop.colorInt());
		p.popMatrix();
	}
}
