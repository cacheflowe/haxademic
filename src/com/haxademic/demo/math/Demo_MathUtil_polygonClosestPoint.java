package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.MathUtil;

public class Demo_MathUtil_polygonClosestPoint 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void firstFrame() {
		OpenGLUtil.setTextureQualityHigh(p.g);
	}
	
	protected void drawApp() {
		p.background(0);
		PG.setCenterScreen(p);
		PG.setDrawCenter(p);
		
		float vertices = (float) P.round(7f + 4f * P.sin(p.frameCount * 0.01f));
		float radius = p.height * 0.3f;
		float segmentRads = P.TWO_PI / vertices;
		float radiusClosest = MathUtil.polygonClosestPoint(vertices, radius);
		
		// draw vertices
		p.stroke(255);
		for (int i = 0; i < vertices; i++) {
			p.strokeWeight(10);
			float x = radius * P.cos(i * segmentRads);
			float y = radius * P.sin(i * segmentRads);
			p.point(x, y);
			
			p.strokeWeight(1);
			float xNext = radius * P.cos((i+1) * segmentRads);
			float yNext = radius * P.sin((i+1) * segmentRads);
			p.line(x, y, xNext, yNext);
		}
		
		// show closest points
		p.stroke(0,255,0);
		p.strokeWeight(10);
		float offsetRads = segmentRads / 2f;
		for (int i = 0; i < vertices; i++) {
			float x = radiusClosest * P.cos(offsetRads + i * segmentRads);
			float y = radiusClosest * P.sin(offsetRads + i * segmentRads);
			p.point(x, y);
		}
	}
}
