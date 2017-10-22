package com.haxademic.sketch.buffer;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.filters.shaders.SphereDistortionFilter;
import com.haxademic.core.draw.filters.shaders.VignetteFilter;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;

public class FeedbackBufferTestDrawUtil
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PGraphics _texture;
	protected float _frames = 200;
	
	public void setup() {
		super.setup();
		p.background(0);
		
		_texture = p.createGraphics( p.width, p.height, P.P3D );
		_texture.smooth(OpenGLUtil.SMOOTH_HIGH);
		_texture.beginDraw();
		_texture.background(0);
		_texture.endDraw();

	}
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1280 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, P.round(_frames * 2) );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, P.round(_frames * 3) );
	}
		
	public void drawApp() {
		// loop
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutSine(percentComplete, 0, 1, 1);
		float percentRads = percentComplete * P.TWO_PI;
		
		// run feedback
		DrawUtil.feedback(_texture, p.color(0, 40), 0.1f, p.height * 0.01f);
		
		// draw new shape
		_texture.beginDraw();
		DrawUtil.setDrawCenter(_texture);
		_texture.noFill();
		_texture.stroke( 200 + 55f * P.sin(percentRads), 100 + 55f * P.cos(percentRads), 155 + 100f * P.cos(percentRads) );
		_texture.translate(_texture.width/2, _texture.height/2);
		_texture.rotate(P.sin(2f * percentRads));
		_texture.rect(0, 0, 50 + 5f * P.sin(percentRads), 50 + 5f * P.sin(percentRads));
		_texture.stroke(0);
		_texture.rect(0, 0, 100 + 5f * P.sin(percentRads), 100 + 5f * P.sin(percentRads));
		_texture.endDraw();
			
		// draw buffer to screen
		p.image(_texture, 0, 0);
		
		// post effects
		SphereDistortionFilter.instance(p).setAmplitude(1.f);
		SphereDistortionFilter.instance(p).applyTo(p);
		VignetteFilter.instance(p).setDarkness(0.2f);
		VignetteFilter.instance(p).applyTo(p);
	}
}
