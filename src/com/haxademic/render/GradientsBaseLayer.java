package com.haxademic.render;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.render.FrameLoop;

public class GradientsBaseLayer
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected EasingColor _colorStart;
	protected EasingColor _colorStop;
	protected int FRAMES = 400;
	
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "1280" );
		Config.setProperty( AppSettings.HEIGHT, "720" );
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
	}

	protected void firstFrame() {
	
		_colorStart = new EasingColor("#ffBB0022", 50f);
		_colorStop = new EasingColor("#0B1630", 50f);
		
		// Team 1: #BB0022 / #0B1630
		// Team 2: #F5AB2C / #471D6C
	}

	protected void drawApp() {
		p.background(0);

//		_colorStart.setTargetColorInt( p.color(255f * P.sin(p.frameCount/50f), 255f * P.sin(p.frameCount/45f), 255f * P.sin(p.frameCount/60f)) );
//		_colorStop.setTargetColorInt( p.color(255f * P.cos(p.frameCount/50f), 255f * P.cos(p.frameCount/45f), 255f * P.cos(p.frameCount/60f)) );
		_colorStart.update();
		_colorStop.update();
		
		// both teams
//		if(loop.loopFrames() == 1) {
//			_colorStart.setTargetHex("#F5AB2C");
//			_colorStop.setTargetHex("#471D6C");
//		}
//		if(loop.loopFrames() == 100) {
//			_colorStart.setTargetHex("#471D6C");
//			_colorStop.setTargetHex("#BB0022");
//		}
//		if(loop.loopFrames() == 200) {
//			_colorStart.setTargetHex("#BB0022");
//			_colorStop.setTargetHex("#0B1630");
//		}
//		if(loop.loopFrames() == 300) {
//			_colorStart.setTargetHex("#0B1630");
//			_colorStop.setTargetHex("#F5AB2C");
//		}
		
		// single team
		if(FrameLoop.loopCurFrame() == 1) {
			_colorStart.setTargetHex("#BB0022");
			_colorStop.setTargetHex("#0B1630");
		}
		if(FrameLoop.loopCurFrame() == 200) {
			_colorStart.setTargetHex("#0B1630");
			_colorStop.setTargetHex("#BB0022");
		}

		
		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
		Gradients.linear(p, p.width, p.height, _colorStart.colorInt(), _colorStop.colorInt());
		p.popMatrix();
	}
}
