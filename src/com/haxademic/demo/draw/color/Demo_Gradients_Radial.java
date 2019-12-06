package com.haxademic.demo.draw.color;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.color.Gradients;

public class Demo_Gradients_Radial
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected EasingColor _colorGradientCenter;
	protected EasingColor _colorGradientOuter;
	
	protected float _frames = 120;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERER, P.P3D );
		p.appConfig.setProperty( AppSettings.WIDTH, "600" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "600" );
		
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "2" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames*4 + 1) );
	}

	public void setupFirstFrame() {
	
		_colorGradientCenter = new EasingColor("#000000", 20f);
		_colorGradientOuter = new EasingColor("#000000", 20f);
	}

	public void drawApp() {
		p.background(0);

		// rendering
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		
		_colorGradientCenter.setTargetInt( p.color(127f + 127f * P.sin(P.TWO_PI/0.25f * percentComplete), 127f + 127f * P.sin(P.TWO_PI/0.75f * percentComplete), 127f + 127f * P.sin(P.TWO_PI/0.5f * percentComplete)) );
		_colorGradientCenter.update();
		_colorGradientOuter.setTargetInt( p.color(127f + 127f * P.sin(P.TWO_PI/0.5f * percentComplete), 127f + 127f * P.sin(P.TWO_PI/0.25f * percentComplete), 127f + 127f * P.sin(P.TWO_PI/0.75f * percentComplete)) );
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
