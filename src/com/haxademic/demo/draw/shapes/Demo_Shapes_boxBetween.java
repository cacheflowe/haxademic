package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.math.easing.ElasticFloat;

import processing.core.PVector;

public class Demo_BoxBetween
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PVector point1 = new PVector();
	protected PVector point2 = new PVector();
	protected ElasticFloat polySides = new ElasticFloat(3, 0.5f, 0.5f);


	protected void overridePropsFile() {
		int FRAMES = 360;
		p.appConfig.setProperty(AppSettings.WIDTH, 1000);
		p.appConfig.setProperty(AppSettings.HEIGHT, 1000);
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES);
	}

	public void drawApp() {
		// context
		p.background(0);
		p.noStroke();
		p.perspective();
		DrawUtil.setDrawCenter( p );
		DrawUtil.setBetterLights( p );
		p.translate(p.width/2, p.height/2, -p.width);
		p.noStroke();
//		p.rotateX(P.HALF_PI * p.mousePercentY());
		p.rotateY(p.loop.progressRads());
		
		// spiral
		float yInc = p.height * 0.11f;
		float pSides = 3 + 39f * p.mousePercentX();
		float segmentRads = P.TWO_PI / pSides; // polySides.value();
		float radius = (p.width * 0.2f) + (p.width * 0.2f) + (p.width * 0.2f) * P.sin(p.loop.progressRads()); // p.width * 0.087f + 
		float thickness = p.width * 0.075f;

		float radiusInner = p.width * 0.08f + (p.width * 0.08f) * P.sin(p.loop.progressRads());
		
//		drawTwistTube(segmentRads, radius, yInc, thickness, 0.2f);
//		p.rotateY(P.TWO_PI/3);
		drawTwistTube(segmentRads, radius, yInc, thickness, 1f);
//		p.rotateY(P.TWO_PI/3);
//		drawTwistTube(segmentRads, radius, yInc, thickness, 2.8f);
	}
	
	protected void drawTwistTube(float segmentRads, float radius, float yInc, float thickness, float colorAmp) {
		int index = 0;
		float y = p.height * 2;
		for( float yy=y; yy > -p.height * 2; yy -= yInc ) {
			float x = P.cos(index * segmentRads) * radius;
			float z = P.sin(index * segmentRads) * radius;
			
				// color cycle
				p.fill(
						colorAmp * (127f + 127f * P.sin(1 + index * 0.1f + p.loop.progressRads())),
						colorAmp * (127f + 127f * P.sin(2 + index * 0.1f + p.loop.progressRads())),
						colorAmp * (127f + 127f * P.sin(0 + index * 0.1f + p.loop.progressRads()))
						);
			
//			p.fill(127f + 127f * P.sin(0 + index * 0.1f + p.loop.progressRads()));
			
//			p.fill(255f);
			p.pushMatrix();
			p.translate(x, yy, z);
			p.sphere(thickness * 1.0f);
			p.popMatrix();
			
			if(index > 0) {
				point1.set(x, yy, z);
				Shapes.boxBetween(p.g, point1, point2, thickness );
			}
			
			point2.set(x, yy, z);
			index++;
		}

	}

}
