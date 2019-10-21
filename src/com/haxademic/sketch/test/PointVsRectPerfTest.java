package com.haxademic.sketch.test;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.math.MathUtil;

public class PointVsRectPerfTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 768 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 256 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
//		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_NONE );
	}

	public void drawApp() {
		p.background(0);
		
		int drawCalls = 64 * 128;
		p.debugView.setValue("drawCalls", drawCalls);
		
		// draw rects
		int startIndex = (p.frameCount * 10) % (256*128);
		int startTimeRect = p.millis();
		p.noStroke();
		for (int i = 0; i < drawCalls; i++) {
			p.fill(startIndex % 255);
			p.rect(512 + MathUtil.gridColFromIndex(startIndex, 256), MathUtil.gridRowFromIndex(startIndex, 256), 1, 1);
			startIndex++;
		}
		p.debugView.setValue("TimeRect", p.millis() - startTimeRect);

		// draw points
		startIndex = (p.frameCount * 10) % (256*128);
		int startTimePoint = p.millis();
		p.noFill();
		for (int i = 0; i < drawCalls; i++) {
			p.stroke(startIndex % 255);
			p.point(256 + MathUtil.gridColFromIndex(startIndex, 256), MathUtil.gridRowFromIndex(startIndex, 256));
			startIndex++;
		}
		p.debugView.setValue("TimePoint", p.millis() - startTimePoint);
		
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
		p.debugView.setValue("TimePixels", p.millis() - startTimePixels);
	}
	
}
