package com.haxademic.core.draw.shapes.polygons;

import java.util.ArrayList;

import com.haxademic.core.app.P;

import processing.core.PVector;

public class CollisionUtil {

	//////////////////////////
	// TRIANGLE CONTAINS POINT
	//////////////////////////
	
	// from: https://stackoverflow.com/a/9755252/352456
	public static boolean pointInsideTriangle(PVector point, PVector v1, PVector v2, PVector v3) {
		float as_x = point.x - v1.x;
		float as_y = point.y - v1.y;
		boolean s_ab = (v2.x-v1.x)*as_y-(v2.y-v1.y)*as_x > 0;
		if((v3.x-v1.x)*as_y-(v3.y-v1.y)*as_x > 0 == s_ab) return false;
		if((v3.x-v2.x)*(point.y-v2.y)-(v3.y-v2.y)*(point.x-v2.x) > 0 != s_ab) return false;
		return true;
	}

	//////////////////////////
	// POLYGON CONTAINS POINT
	//////////////////////////
	
	// from: https://stackoverflow.com/a/8721483/352456
	public static boolean polygonContainsPoint(Polygon polygon, PVector point) {
		return polygonContainsPoint(polygon.vertices(), point);
	}
	
	public static boolean polygonContainsPoint(ArrayList<PVector> vertices, PVector point) {
		int i;
		int j;
		boolean result = false;
		for (i = 0, j = vertices.size() - 1; i < vertices.size(); j = i++) {
			if ((vertices.get(i).y > point.y) != (vertices.get(j).y > point.y) &&
				(point.x < (vertices.get(j).x - vertices.get(i).x) * (point.y - vertices.get(i).y) / (vertices.get(j).y-vertices.get(i).y) + vertices.get(i).x)) {
				result = !result;
			}
		}
		return result;
	}
	
	protected static Rectangle rect = new Rectangle();
	public static boolean rectangleContainsPoint(float checkX, float checkY, int rectX, int rectY, int rectW, int rectH) {
		rect.setRect(rectX, rectY, rectW, rectH);
		return rect.contains(checkX, checkY, 1, 1);
	}
	
	//////////////////////////
	// LINE SEGMENTS INTERSECT
	//////////////////////////
	
	// https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
	// Given three colinear points p, q, r, the function checks if 
	// point q lies on line segment 'pr' 
	public static boolean onLineSegment(PVector p, PVector q, PVector r) { 
	    if (q.x <= P.max(p.x, r.x) && q.x >= P.min(p.x, r.x) && 
	        q.y <= P.max(p.y, r.y) && q.y >= P.min(p.y, r.y)) 
	       return true; 
	    return false; 
	} 
	  
	// To find orientation of ordered triplet (p, q, r). 
	// The function returns following values 
	// 0 --> p, q and r are colinear 
	// 1 --> Clockwise 
	// 2 --> Counterclockwise 
	public static int lineToPointOrientation(PVector p, PVector q, PVector r) { 
	    // See https://www.geeksforgeeks.org/orientation-3-ordered-points/ 
	    // for details of below formula. 
	    float val = (q.y - p.y) * (r.x - q.x) - 
	                (q.x - p.x) * (r.y - q.y); 
	    if (val == 0) return 0;  // colinear 
	    return (val > 0)? 1: 2; // clock or counterclock wise 
	} 
	  
	// The main function that returns true if line segment 'p1q1' 
	// and 'p2q2' intersect. 
	public static boolean linesIntersect(PVector line1Start, PVector line1End, PVector line2Start, PVector line2End) { 
	    // Find the four orientations needed for general and 
	    // special cases 
	    int o1 = lineToPointOrientation(line1Start, line1End, line2Start); 
	    int o2 = lineToPointOrientation(line1Start, line1End, line2End); 
	    int o3 = lineToPointOrientation(line2Start, line2End, line1Start); 
	    int o4 = lineToPointOrientation(line2Start, line2End, line1End); 
	  
	    // General case 
	    if (o1 != o2 && o3 != o4) 
	        return true; 
	  
	    // Special Cases 
	    // p1, q1 and p2 are colinear and p2 lies on segment p1q1 
	    if (o1 == 0 && onLineSegment(line1Start, line2Start, line1End)) return true; 
	    // p1, q1 and q2 are colinear and q2 lies on segment p1q1 
	    if (o2 == 0 && onLineSegment(line1Start, line2End, line1End)) return true; 
	    // p2, q2 and p1 are colinear and p1 lies on segment p2q2 
	    if (o3 == 0 && onLineSegment(line2Start, line1Start, line2End)) return true; 
	     // p2, q2 and q1 are colinear and q1 lies on segment p2q2 
	    if (o4 == 0 && onLineSegment(line2Start, line1End, line2End)) return true; 
	  
	    return false; // Doesn't fall in any of the above cases 
	}
	
	//////////////////////////
	// POLYGONS INTERSECT
	//////////////////////////
	
	public static boolean polygonsIntersect(Polygon poly1, Polygon poly2) {
		// do bounding box check first, for quick distance check
		// TODO: implement AABB
		
		// check vertices inside the other shape, in case a shape is completely inside the other
		for (int i = 0; i < poly1.vertices().size(); i++) {
			if(polygonContainsPoint(poly2, poly1.vertices.get(i))) return true;
		}
		for (int i = 0; i < poly2.vertices().size(); i++) {
			if(polygonContainsPoint(poly1, poly2.vertices.get(i))) return true;
		}
		
		// check edge intersections in case of partial overlap
		for (int i = 0; i < poly1.edges().size(); i++) {
			for (int j = 0; j < poly2.edges().size(); j++) {
				Edge edge1 = poly1.edges().get(i);
				Edge edge2 = poly2.edges().get(j);
				if(linesIntersect(edge1.v1(), edge1.v2(), edge2.v1(), edge2.v2())) return true;
			}
		}
		return false;
	}
	
	//////////////////////////
	// LINE SEGMENT INTERSECTS POLYGON
	//////////////////////////
	
	public static boolean lineIntersectsPolygon(PVector lineStart, PVector lineEnd, Polygon poly) {
		// check for point in polygon
		if(polygonContainsPoint(poly, lineStart)) return true;
		if(polygonContainsPoint(poly, lineEnd)) return true;
		
		// check edge intersections with line segment
		for (int i = 0; i < poly.edges().size(); i++) {
			Edge edge1 = poly.edges().get(i);
			if(linesIntersect(edge1.v1(), edge1.v2(), lineStart, lineEnd)) return true;
		}
		return false;
	}
	
	//////////////////////////
	// IRREGULAR POLYGON AREA
	//////////////////////////
	
	// from: https://www.mathopenref.com/coordpolygonarea2.html
	public static float polygonArea(Polygon poly) {
		ArrayList<PVector> vertices = poly.vertices();
		int numVertices = vertices.size();
		float area = 0;
		int j = numVertices - 1; // previous vertex

		for (int i=0; i < numVertices; i++) {
			PVector v = vertices.get(i);
			PVector vPrev = vertices.get(j);
			area = area + (vPrev.x + v.x) * (vPrev.y - v.y); 
			j = i;  // j is previous vertex to i
		}
		return P.abs(area/2f);
	}
}
