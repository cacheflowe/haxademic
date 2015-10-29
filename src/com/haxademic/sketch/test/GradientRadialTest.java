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
	
	protected float _frames = 120;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "600" );
		_appConfig.setProperty( "height", "600" );
		
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "rendering_gif", "false" );
		_appConfig.setProperty( "rendering_gif_framerate", "40" );
		_appConfig.setProperty( "rendering_gif_quality", "15" );
		_appConfig.setProperty( "rendering_gif_startframe", "2" );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+Math.round(_frames*4 + 1) );
	}

	public void setup() {
		super.setup();	
		_colorGradientCenter = new ColorHaxEasing("#000000", 20f);
		_colorGradientOuter = new ColorHaxEasing("#000000", 20f);
	}

	public void drawApp() {
		p.background(0);

		// rendering
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		
		_colorGradientCenter.setTargetColorInt( p.color(127f + 127f * P.sin(P.TWO_PI/0.25f * percentComplete), 127f + 127f * P.sin(P.TWO_PI/0.75f * percentComplete), 127f + 127f * P.sin(P.TWO_PI/0.5f * percentComplete)) );
		_colorGradientCenter.update();
		_colorGradientOuter.setTargetColorInt( p.color(127f + 127f * P.sin(P.TWO_PI/0.5f * percentComplete), 127f + 127f * P.sin(P.TWO_PI/0.25f * percentComplete), 127f + 127f * P.sin(P.TWO_PI/0.75f * percentComplete)) );
		_colorGradientOuter.update();

		// endless cycle
//		_colorGradientCenter.setTargetColorInt( p.color(255f * P.sin(p.frameCount/20f), 255f * P.sin(p.frameCount/25f), 255f * P.sin(p.frameCount/30f)) );
//		_colorGradientCenter.update();
//		_colorGradientOuter.setTargetColorInt( p.color(255f * P.sin(p.frameCount/40f), 255f * P.sin(p.frameCount/45f), 255f * P.sin(p.frameCount/50f)) );
//		_colorGradientOuter.update();
		
		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
		Gradients.radial(p, p.width * 2, p.height * 2, _colorGradientCenter.colorInt(), _colorGradientOuter.colorInt(), 100);
		p.popMatrix();
	}
}
