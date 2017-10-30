package com.haxademic.sketch.render.avloops;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.OpenGLUtil;

import processing.core.PVector;

public class AVLoop01
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }	
	
	protected float _frames = 232;
	protected float _ticks = 16;
	protected float _boxSize = 200;
	protected float _bg = 0;
	protected PVector _boxRot = new PVector(0,0,0);
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "640" );
		
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "true" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, Math.round(_frames + _frames*4) );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "1" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, ""+ Math.round(_frames) );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames + _frames*4) );
	}

	public void setup() {
		super.setup();	
		
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		_boxSize = 200;
		_boxRot.set(0,0,0);
	}

	public void drawApp() {
		// rendering
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float curTick = P.floor(p.frameCount % (_frames/_ticks));
		P.println(curTick);
		
		_bg *= 0.8f;
		if(curTick == 14) {
			_bg = 70;
		}
		
		p.background(_bg);
		p.fill(20,20,20);
		p.stroke(255);

		if(curTick == 4) {
			_boxSize = 200;
		}
		_boxRot.set(percentComplete * P.TWO_PI, percentComplete * P.TWO_PI, percentComplete * P.TWO_PI);
		
		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
		p.rotateX(_boxRot.x);
		p.rotateY(_boxRot.y);
		p.rotateZ(_boxRot.z);
		p.box(_boxSize);
		p.popMatrix();
		
		_boxSize *= 0.9f;
		
		
		if( p.frameCount == _frames + 1 ) {
			if(p.appConfig.getBoolean("rendering", false) ==  true) {				
				_renderer.stop();
				P.println("render done!");
			}
		}

	}
}
