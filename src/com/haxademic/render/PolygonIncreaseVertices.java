package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.ui.UI;

public class PolygonIncreaseVertices 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected String numPolys = "numPolys";
	protected String strokeWeight = "strokeWeight";
	protected String startRadius = "startRadius";
	protected String spacing = "spacing";
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERER, PRenderers.P2D );
//		p.appConfig.setProperty( AppSettings.RENDERER, P.PDF );
//		p.appConfig.setProperty( AppSettings.PDF_RENDERER_OUTPUT_FILE, FileUtil.getHaxademicOutputPath() + "/pdf/345-01.pdf" );

		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1000 );
	}

	public void setupFirstFrame() {
		UI.addSlider(numPolys, 3, 1, 20, 1);
		UI.addSlider(strokeWeight, 1, 0.2f, 100f, 0.1f);
		UI.addSlider(startRadius, 10, 1, 300, 1);
		UI.addSlider(spacing, 10, 1, 200, 1);
	}

	public void drawPolygon(float vertices, float radius) {
		float segmentRads = P.TWO_PI / vertices;
		p.beginShape();
		p.strokeJoin(P.MITER);
		for (float i = 0; i < vertices; i++) {
			float curRads = -P.HALF_PI + segmentRads/2f + i * segmentRads;
			float nextRads = curRads + segmentRads;
			p.vertex(circlePointX(curRads,  radius), circlePointY(curRads,  radius));
//			p.vertex(circlePointX(nextRads, radius), circlePointY(nextRads, radius));
		}
		p.endShape(P.CLOSE);
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
		
		// set context
		p.translate( p.width/2, p.height/2);
		p.rotate(P.PI);
		p.noFill();
		p.stroke(255);
		float strokeW = UI.value(strokeWeight);
		p.strokeWeight(strokeW);
		
//		p.strokeWeight(1);
//		p.noStroke();
//		p.fill(255);
		
		for (float i = 0; i < UI.valueInt(numPolys); i++) {
			float vertices = i + 3;
			float segmentRads = P.TWO_PI / vertices;
			float curShapeRadius = UI.value(startRadius) + UI.value(spacing) * i;
			// v1
			drawPolygon(vertices, curShapeRadius);
			// v2
//			p.pushMatrix();
//			float offsetRads = -P.HALF_PI + segmentRads/2f + i * segmentRads;
//			p.rotate(offsetRads);
//			Shapes.drawDisc(p, curShapeRadius - strokeW/2f, curShapeRadius + strokeW/2f, (int) vertices);
//			p.popMatrix();
			// move to keep centered
			float polySegmentRads = P.TWO_PI / vertices;
			float closestRadius = midpointDist(circlePointX(0,  curShapeRadius), circlePointY(0,  curShapeRadius), circlePointX(polySegmentRads, curShapeRadius), circlePointY(polySegmentRads, curShapeRadius));
			p.translate(0, -0.5f * (closestRadius - curShapeRadius));
		}
	}
}
