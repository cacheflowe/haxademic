package com.haxademic.demo.render;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.render.Renderer;

import processing.core.PVector;

public class Demo_FrameLoop
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }	
	
	protected int _frames = 232;
	protected int _lastTick = -1;
	protected float _boxSize = 200;
	protected float _bg = 0;
	protected PVector _boxRot = new PVector(0,0,0);
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 640 );
		Config.setProperty( AppSettings.LOOP_FRAMES, _frames );
		Config.setProperty( AppSettings.LOOP_TICKS, 16 );
		
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, Math.round(_frames + _frames*4) );
	}

	protected void firstFrame() {
		if(Renderer.instance().videoRenderer != null) Renderer.instance().videoRenderer.setPG(p.g); 
		_boxSize = 200;
		_boxRot.set(0,0,0);
	}

	protected void drawApp() {
		// make changes on tick
		if(FrameLoop.isTick()) {
			if(FrameLoop.curTick() == 14) {
				_bg = 70;
			}
			if(FrameLoop.curTick() % 2 == 0) {
				_boxSize = 200;
			}
		}
		
		// background
		p.background(_bg);
		p.fill(20,20,20);
		p.stroke(255);

		// box
		p.pushMatrix();
		PG.setCenterScreen(p.g);
		_boxRot.set(FrameLoop.progressRads(), FrameLoop.progressRads(), FrameLoop.progressRads());
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
