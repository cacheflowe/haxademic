package com.haxademic.render.ello;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PShape;

public class GifRenderEllo023ElloRadialGradient
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected EasingColor _colorGradientCenter;
	protected EasingColor _colorGradientOuter;
	protected PShape _logo;
	
	
	protected float _frames = 40;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "94" );
		Config.setProperty( AppSettings.HEIGHT, "94" );
		
		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		Config.setProperty( AppSettings.RENDERING_GIF, "false" );
		Config.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		Config.setProperty( AppSettings.RENDERING_GIF_QUALITY, "1" );
		Config.setProperty( AppSettings.RENDERING_GIF_START_FRAME, ""+ Math.round(_frames) );
		Config.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames + _frames*4) );
	}

	protected void firstFrame() {
	
		
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		_colorGradientCenter = new EasingColor("#000000", 9f);
		_colorGradientOuter = new EasingColor("#000000", 9f);
		
		_logo = p.loadShape(FileUtil.haxademicDataPath()+"svg/ello-mouth-only.svg");
	}

	protected void drawApp() {
		p.background(255);

		// rendering
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		
		_colorGradientCenter.setTargetInt( p.color(127f + 127f * P.sin(P.TWO_PI * percentComplete), 127f + 127f * P.sin(P.TWO_PI * percentComplete * 2f), 127f + 127f * P.sin(P.TWO_PI * percentComplete * 4f)) );
		_colorGradientCenter.update();
		_colorGradientOuter.setTargetInt( p.color(127f + 127f * P.sin(P.TWO_PI * percentComplete * 2f), 127f + 127f * P.sin(P.TWO_PI * percentComplete * 4f), 127f + 127f * P.sin(P.TWO_PI * percentComplete)) );
		_colorGradientOuter.update();

		
		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
		Gradients.radial(p, p.width * 0.95f, p.height * 0.95f, _colorGradientCenter.colorInt(), _colorGradientOuter.colorInt(), 100);
		p.popMatrix();
		
		
		p.shape(_logo,p.width * 0.05f,p.height * 0.05f,p.width - p.width * 0.1f,p.height - p.height * 0.1f);
	}
}
