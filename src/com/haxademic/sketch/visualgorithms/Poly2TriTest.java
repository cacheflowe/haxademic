package com.haxademic.sketch.visualgorithms;

import java.util.ArrayList;
import java.util.List;

import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;

public class Poly2TriTest 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// code from: http://forum.processing.org/one/topic/delauney-diagram-inside-a-blob.html

	ArrayList<PolygonPoint> points = new ArrayList<PolygonPoint>();
	List<DelaunayTriangle> triangles;
	boolean bShowTriangulation;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "800" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "600" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}

	public void setup() {
		super.setup();
	}

	public void drawApp() {
		p.background(100);

		PG.resetGlobalProps(p);
		PG.setDrawCenter(p);

		if (points.size() < 3) {
			for (TriangulationPoint point : points) {
				float x = point.getXf();
				float y = point.getYf();
				ellipse(x, y, 3, 3);
			}
		}
		background(128);
		noFill();
		stroke(0,70,255);
		strokeWeight(4);
		if (points.size() < 3)
		{
			for (TriangulationPoint point : points)
			{
				float x = point.getXf();
				float y = point.getYf();
				ellipse(x, y, 3, 3);
			}
			return;
		}
		beginShape();
		for (PolygonPoint point : points)
		{
			//~ println("ShPt: " + point);
			float x = point.getXf();
			float y = point.getYf();
			vertex(x, y);
		}
		endShape(CLOSE);

		if (bShowTriangulation && triangles != null)
		{
			stroke(255,127,127);
			strokeWeight(1);
			for (DelaunayTriangle triangle : triangles)
			{
				//~ println("Tr: " + triangle);
				float fx = 0, fy = 0;
				float px = 0, py = 0;
				boolean bFirst = true;
				for (TriangulationPoint point : triangle.points) {
					float x = point.getXf();
					float y = point.getYf();
					if (bFirst) {
						fx = x; fy = y;
						bFirst = false;
					} else {
						line(px, py, x, y);
					}
					px = x; py = y;
				}
				line(px, py, fx, fy);
			}
		}
	}

	public void keyReleased() {
		switch (key) {
			case 't':
				DoTriangulation();
				break;
			case 'c':
				points.clear();
				triangles = null;
				bShowTriangulation = false;
				break;
		}
	}

	void DoTriangulation() {
		if (points.size() < 3)
			return;

		Polygon polygon = new Polygon(points);
		Poly2Tri.triangulate(polygon);
		triangles = polygon.getTriangles();
		println("DT: " + triangles);
		bShowTriangulation = true;
	}

	public void mousePressed() {
		PolygonPoint point = new PolygonPoint((float) mouseX, (float) mouseY);
		points.add(point);
	}
}

