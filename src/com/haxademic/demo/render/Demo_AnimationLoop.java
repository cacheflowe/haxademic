package com.haxademic.demo.render;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.OpenGLUtil;

import processing.core.PVector;

public class Demo_AnimationLoop
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }	
	
	protected float _frames = 232;
	protected float _lastTick = -1;
	protected float _boxSize = 200;
	protected float _bg = 0;
	protected PVector _boxRot = new PVector(0,0,0);
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, _frames );
		p.appConfig.setProperty( AppSettings.LOOP_TICKS, 16 );
		
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, Math.round(_frames + _frames*4) );
	}

	public void setup() {
		super.setup();	
		
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		
		_boxSize = 200;
		_boxRot.set(0,0,0);
	}

	public void drawApp() {
		// make changes on tick
		if(p.loop.isTick()) {
			if(p.loop.curTick() == 14) {
				_bg = 70;
			}
			if(p.loop.curTick() % 2 == 0) {
				_boxSize = 200;
			}
		}
		
		// background
		p.background(_bg);
		p.fill(20,20,20);
		p.stroke(255);

		// box
		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
		_boxRot.set(p.loop.progressRads(), p.loop.progressRads(), p.loop.progressRads());
		p.rotateX(_boxRot.x);
		p.rotateY(_boxRot.y);
		p.rotateZ(_boxRot.z);
		p.box(_boxSize);
		p.popMatrix();

		// lerp down
		_bg *= 0.8f;
		_boxSize *= 0.9f;
	}
}
