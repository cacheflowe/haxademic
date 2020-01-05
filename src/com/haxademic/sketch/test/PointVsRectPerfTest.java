package com.haxademic.sketch.test;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.math.MathUtil;

public class PointVsRectPerfTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 768 );
		Config.setProperty( AppSettings.HEIGHT, 256 );
		Config.setProperty( AppSettings.FULLSCREEN, false );
//		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_NONE );
	}

	protected void drawApp() {
		p.background(0);
		
		int drawCalls = 64 * 128;
		DebugView.setValue("drawCalls", drawCalls);
		
		// draw rects
		int startIndex = (p.frameCount * 10) % (256*128);
		int startTimeRect = p.millis();
		p.noStroke();
		for (int i = 0; i < drawCalls; i++) {
			p.fill(startIndex % 255);
			p.rect(512 + MathUtil.gridColFromIndex(startIndex, 256), MathUtil.gridRowFromIndex(startIndex, 256), 1, 1);
			startIndex++;
		}
		DebugView.setValue("TimeRect", p.millis() - startTimeRect);

		// draw points
		startIndex = (p.frameCount * 10) % (256*128);
		int startTimePoint = p.millis();
		p.noFill();
		for (int i = 0; i < drawCalls; i++) {
			p.stroke(startIndex % 255);
			p.point(256 + MathUtil.gridColFromIndex(startIndex, 256), MathUtil.gridRowFromIndex(startIndex, 256));
			startIndex++;
		}
		DebugView.setValue("TimePoint", p.millis() - startTimePoint);
		
		// set pixels
		startIndex = (p.frameCount * 10) % (256*128);
		int startTimePixels = p.millis();
		p.loadPixels();
		for (int i = 0; i < drawCalls; i++) {
			int col = p.color(startIndex % 255);
			int x = MathUtil.gridColFromIndex(startIndex, 256);
			int y = MathUtil.gridRowFromIndex(startIndex, 256);
//			p.set(512 + MathUtil.gridColFromIndex(startIndex, 256), MathUtil.gridRowFromIndex(startIndex, 256), col);
			pixels[MathUtil.gridIndexFromXY(x, y, 256)] = col;
			startIndex++;
		}
		p.updatePixels();
		DebugView.setValue("TimePixels", p.millis() - startTimePixels);
	}
	
}
