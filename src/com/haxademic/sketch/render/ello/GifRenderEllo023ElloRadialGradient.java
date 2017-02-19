package com.haxademic.sketch.render.ello;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ColorHaxEasing;
import com.haxademic.core.draw.shapes.Gradients;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PShape;

public class GifRenderEllo023ElloRadialGradient
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ColorHaxEasing _colorGradientCenter;
	protected ColorHaxEasing _colorGradientOuter;
	protected PShape _logo;
	
	
	protected float _frames = 40;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "94" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "94" );
		
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "1" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, ""+ Math.round(_frames) );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames + _frames*4) );
	}

	public void setup() {
		super.setup();	
		
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		_colorGradientCenter = new ColorHaxEasing("#000000", 9f);
		_colorGradientOuter = new ColorHaxEasing("#000000", 9f);
		
		_logo = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello-mouth-only.svg");
	}

	public void drawApp() {
		p.background(255);

		// rendering
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		
		_colorGradientCenter.setTargetColorInt( p.color(127f + 127f * P.sin(P.TWO_PI * percentComplete), 127f + 127f * P.sin(P.TWO_PI * percentComplete * 2f), 127f + 127f * P.sin(P.TWO_PI * percentComplete * 4f)) );
		_colorGradientCenter.update();
		_colorGradientOuter.setTargetColorInt( p.color(127f + 127f * P.sin(P.TWO_PI * percentComplete * 2f), 127f + 127f * P.sin(P.TWO_PI * percentComplete * 4f), 127f + 127f * P.sin(P.TWO_PI * percentComplete)) );
		_colorGradientOuter.update();

		
		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
		Gradients.radial(p, p.width * 0.95f, p.height * 0.95f, _colorGradientCenter.colorInt(), _colorGradientOuter.colorInt(), 100);
		p.popMatrix();
		
		
		p.shape(_logo,p.width * 0.05f,p.height * 0.05f,p.width - p.width * 0.1f,p.height - p.height * 0.1f);
	}
}
