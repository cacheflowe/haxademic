package com.haxademic.demo.draw.context;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.SphereDistortionFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;

import processing.core.PGraphics;

public class Demo_PG_feedback
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PGraphics _texture;
	protected float _frames = 360;
	
	protected void firstFrame() {

		p.background(0);
		
		_texture = p.createGraphics( p.width, p.height, P.P3D );
		_texture.smooth(OpenGLUtil.SMOOTH_HIGH);
		_texture.beginDraw();
		_texture.background(0);
		_texture.endDraw();
	}
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 1280 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, P.round(_frames * 2) );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, P.round(_frames * 3) );
	}
		
	protected void drawApp() {
		// loop
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float percentRads = percentComplete * P.TWO_PI;
		
		// run feedback
		PG.feedback(_texture, p.color(0, 40), 0.1f, p.height * 0.01f);
		
		// draw new shape
		_texture.beginDraw();
		PG.setDrawCenter(_texture);
		_texture.noFill();
		_texture.stroke( 200 + 55f * P.sin(percentRads), 100 + 55f * P.cos(percentRads), 155 + 100f * P.cos(percentRads) );
		_texture.translate(_texture.width/2, _texture.height/2);
		_texture.rotate(0.8f * P.sin(2f * percentRads));
		
		_texture.rect(0, 0, 50 + 25f * P.sin(percentRads), 50 + 25f * P.sin(percentRads));
		_texture.stroke(0);
		_texture.rect(0, 0, 100 + 35f * P.cos(percentRads), 100 + 35f * P.cos(percentRads));
		_texture.stroke(255);
		_texture.rect(0, 0, 150 + 45f * P.sin(percentRads), 150 + 45f * P.sin(percentRads));
		
		_texture.endDraw();
			
		// draw buffer to screen
		p.image(_texture, 0, 0);
		
		// post effects
		SphereDistortionFilter.instance().setAmplitude(1.f);
		SphereDistortionFilter.instance().applyTo(p);
		VignetteFilter.instance().setDarkness(0.2f);
		VignetteFilter.instance().applyTo(p);
	}
}
