package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.math.MathUtil;

public class PolygonIncreseVertices 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int _frames = 160;

	protected void overridePropsFile() {
//		p.appConfig.setProperty( AppSettings.RENDERER, P.PDF );
//		p.appConfig.setProperty( AppSettings.PDF_RENDERER_OUTPUT_FILE, FileUtil.getHaxademicOutputPath() + "/pdf/iterations-02.pdf" );

		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1000 );
	}

	public void setup() {
		super.setup();
	}

	public void drawPolygon(float vertices, float radius) {
		float segmentRads = P.TWO_PI / vertices;
		p.beginShape();
		for (float i = 0; i < vertices; i++) {
			float curRads = -P.HALF_PI + segmentRads/2f + i * segmentRads;
			float nextRads = curRads + segmentRads;
			p.vertex(circlePointX(curRads,  radius), circlePointY(curRads,  radius));
			p.vertex(circlePointX(nextRads, radius), circlePointY(nextRads, radius));
		}
		p.endShape();
	}
	
	protected float circlePointX(float rads, float radius) {
		return radius * P.cos(rads);
	}
	
	protected float circlePointY(float rads, float radius) {
		return radius * P.sin(rads);
	}
	
	protected float midpointDist(float x1, float y1, float x2, float y2) {
		float midpointX = (x1 + x2) / 2f;
		float midpointY = (y1 + y2) / 2f;
		return MathUtil.getDistance(0, 0, midpointX, midpointY);
	}
	
	public void drawApp() {
		background(0);
		p.translate(p.width / 2, p.height / 2);
		p.rotate(P.PI);
		
		float startRadius = 10;
		p.noStroke();
		p.noFill();
		
		int polyCount = 0;
		for (float i = 19; i >= 3; i--) {
//			if(polyCount % 2f == 0) p.fill(0);
//			else p.fill(255);
			p.stroke(255);
			float vertices = i;
			float curShapeRadius = startRadius + startRadius * vertices;
			drawPolygon(vertices, curShapeRadius);
			float polySegmentRads = P.TWO_PI / vertices;
			float closestRadius = midpointDist(circlePointX(0,  curShapeRadius), circlePointY(0,  curShapeRadius), circlePointX(polySegmentRads, curShapeRadius), circlePointY(polySegmentRads, curShapeRadius));
			p.translate(0, 0.5f * (closestRadius - curShapeRadius));
			polyCount++;
		}
	}
}
