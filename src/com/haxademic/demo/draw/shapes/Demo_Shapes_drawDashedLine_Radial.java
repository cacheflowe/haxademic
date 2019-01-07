package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Shapes;

public class Demo_Shapes_drawDashedLine_Radial 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	
	protected void overridePropsFile() {
		int FRAMES = 200;
		p.appConfig.setProperty( AppSettings.WIDTH, 1080 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1080 );
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, (FRAMES * 1) + 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (FRAMES * 2) + 1 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );

	}
	
	public void drawApp() {
		p.background(0);
		p.stroke(255);
		p.strokeWeight(8f);
		DrawUtil.setCenterScreen(p);
//		DrawUtil.basicCameraFromMouse(p.g);
		
		float numLines = 24;
		float segmentRads = P.TWO_PI / numLines;
		float radius = p.width * 0.4f;
		
		for(float i = 0; i < numLines; i++) {
			float rads = segmentRads * i;
			float startX = P.cos(rads) * radius * 0.1f;
			float startY = P.sin(rads) * radius * 0.1f;
			float endX = P.cos(rads) * radius;
			float endY = P.sin(rads) * radius;
			Shapes.drawDashedLine(p.g, startX, startY, 0, endX, endY, 0, 50f + 30f * P.sin(p.loop.progressRads() + rads * 3f), false);
		}
		
	}
	
}