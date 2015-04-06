package com.haxademic.sketch.test;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ColorHaxEasing;
import com.haxademic.core.draw.shapes.Gradients;

@SuppressWarnings("serial")
public class GradientRadialTest
extends PAppletHax {

	protected ColorHaxEasing _colorGradientCenter;
	protected ColorHaxEasing _colorGradientOuter;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "600" );
		_appConfig.setProperty( "height", "600" );
	}

	public void setup() {
		super.setup();	
		_colorGradientCenter = new ColorHaxEasing("#000000", 20f);
		_colorGradientOuter = new ColorHaxEasing("#000000", 20f);
	}

	public void drawApp() {
		p.background(0);

		_colorGradientCenter.setTargetColorInt( p.color(255f * P.sin(p.frameCount/20f), 255f * P.sin(p.frameCount/25f), 255f * P.sin(p.frameCount/30f)) );
		_colorGradientCenter.update();
		_colorGradientOuter.setTargetColorInt( p.color(255f * P.sin(p.frameCount/40f), 255f * P.sin(p.frameCount/45f), 255f * P.sin(p.frameCount/50f)) );
		_colorGradientOuter.update();
		
		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
		Gradients.radial(p, p.width, p.height, _colorGradientCenter.colorInt(), _colorGradientOuter.colorInt(), 100);
		p.popMatrix();
	}
}
