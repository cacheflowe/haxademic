package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.math.MathUtil;

public class Demo_Shapes_drawDisc 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ImageGradient imageGradient;
	
	protected void overridePropsFile() {
		int FRAMES = 140;
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES );
	}
	
	public void setupFirstFrame() {
		imageGradient = new ImageGradient(ImageGradient.PASTELS());
	}

	public void drawApp() {
		p.background(0);
		p.noStroke();
		//		DrawUtil.setBetterLights(p);
		p.lights();
		DrawUtil.setCenterScreen(p);
		p.translate(0, p.height * -0.075f);
		rotateX(0.9f + 0.15f * P.sin(loop.progressRads())); 
		
		
		// set up concentric polygon config
		float radius = p.height * 0.39f; 
		float spacing = p.height * 0.01f; 
		float lineWeight = radius * 0.015f;
		float vertices = P.round(5.5f + 2.5f * P.sin(loop.progressRads()));
		vertices = 8f;
		
		// rotate polygon to sit on a flat bottom
		// offset y
		float radiusClosest = MathUtil.polygonClosestPoint(vertices, radius);
		float radiusDiff = radius - radiusClosest;
		p.translate(0, radiusDiff);
		// rotate
		float segmentRads = P.TWO_PI / vertices;
		p.rotate(P.HALF_PI);
		p.rotate(segmentRads / 2f);
		
		// draw concentric shapes
		float index = 0;
		while(radius > 2) {
			p.pushMatrix();
			p.translate(0, 0, p.width * 0.07f * P.sin(index * 0.3f + loop.progressRads()));
//			p.rotateZ(0.2f * P.sin(index * 0.3f + loop.progressRads()));
			p.fill(imageGradient.getColorAtProgress(0.5f + 0.4f * P.sin(index * 0.3f + loop.progressRads())));
//			Shapes.drawDisc(p, radius, radius - lineWeight, (int) vertices);
			float h = 20f + 15f * P.sin(P.PI + index * 0.3f + loop.progressRads());
			Shapes.drawDisc3D(p, radius, radius - lineWeight, h, (int) vertices, 0, 0 );
			radius -= lineWeight + spacing;
			lineWeight *= 0.98f;
			index++;
			p.popMatrix();
		}
	}
}
