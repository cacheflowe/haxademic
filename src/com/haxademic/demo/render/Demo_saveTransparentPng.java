package com.haxademic.demo.render;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.render.Renderer;

public class Demo_saveTransparentPng
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }	
	
	protected void drawApp() {
		// draw box in transparent buffer
		// by default, PAppletHax's `pg` is created with transparency enabled
		DebugView.setTexture("pg", pg);
		pg.beginDraw();
		pg.clear();
		pg.lights();
		PG.setCenterScreen(pg);
		pg.rotateX(-0.3f);
		pg.rotateY(FrameLoop.count(0.01f));
		pg.fill(50, 50, 127);
		pg.box(200);
		pg.endDraw();
		
		// draw buffer to screen on top of oscillating app background color
		p.background(FrameLoop.osc(0.04f, 0, 255));
		p.image(pg, 0, 0);
		
		// save if keypressed
		if(KeyboardState.keyTriggered(' ')) {
			Renderer.saveBufferToDisk(pg);
		}
	}
}
