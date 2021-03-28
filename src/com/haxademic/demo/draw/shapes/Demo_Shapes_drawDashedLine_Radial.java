package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.render.FrameLoop;

public class Demo_Shapes_drawDashedLine_Radial 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
		int FRAMES = 200;
		Config.setProperty( AppSettings.WIDTH, 1080 );
		Config.setProperty( AppSettings.HEIGHT, 1080 );
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, (FRAMES * 1) + 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (FRAMES * 2) + 1 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
	}
	
	protected void drawApp() {
		p.background(0);
		p.stroke(255);
		p.strokeWeight(8f);
		PG.setCenterScreen(p);
//		PG.basicCameraFromMouse(p.g);
		
		float numLines = 24;
		float segmentRads = P.TWO_PI / numLines;
		float radius = p.width * 0.4f;
		
		for(float i = 0; i < numLines; i++) {
			float rads = segmentRads * i;
			float startX = P.cos(rads) * radius * 0.1f;
			float startY = P.sin(rads) * radius * 0.1f;
			float endX = P.cos(rads) * radius;
			float endY = P.sin(rads) * radius;
			Shapes.drawDashedLine(p.g, startX, startY, 0, endX, endY, 0, 50f + 30f * P.sin(FrameLoop.progressRads() + rads * 3f), false);
		}
		
	}
	
}