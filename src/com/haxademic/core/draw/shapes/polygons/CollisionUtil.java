package com.haxademic.core.draw.shapes.polygons;

import java.util.ArrayList;

import com.haxademic.core.app.P;

import processing.core.PVector;

public class CollisionUtil {

	// from: https://stackoverflow.com/a/9755252/352456
	public static boolean pointInsideTriangle(PVector point, PVector v1, PVector v2, PVector v3) {
		float as_x = point.x - v1.x;
		float as_y = point.y - v1.y;
		boolean s_ab = (v2.x-v1.x)*as_y-(v2.y-v1.y)*as_x > 0;
		if((v3.x-v1.x)*as_y-(v3.y-v1.y)*as_x > 0 == s_ab) return false;
		if((v3.x-v2.x)*(point.y-v2.y)-(v3.y-v2.y)*(point.x-v2.x) > 0 != s_ab) return false;
		return true;
	}

	// from: https://stackoverflow.com/a/8721483/352456
	public static boolean polygonContainsPoint(PVector point, ArrayList<PVector> vertices) {
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
	
	// https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
	// Given three colinear points p, q, r, the function checks if 
	// point q lies on line segment 'pr' 
	public static boolean onSegment(PVector p, PVector q, PVector r) { 
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
	public static int orientation(PVector p, PVector q, PVector r) { 
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
	    int o1 = orientation(line1Start, line1End, line2Start); 
	    int o2 = orientation(line1Start, line1End, line2End); 
	    int o3 = orientation(line2Start, line2End, line1Start); 
	    int o4 = orientation(line2Start, line2End, line1End); 
	  
	    // General case 
	    if (o1 != o2 && o3 != o4) 
	        return true; 
	  
	    // Special Cases 
	    // p1, q1 and p2 are colinear and p2 lies on segment p1q1 
	    if (o1 == 0 && onSegment(line1Start, line2Start, line1End)) return true; 
	    // p1, q1 and q2 are colinear and q2 lies on segment p1q1 
	    if (o2 == 0 && onSegment(line1Start, line2End, line1End)) return true; 
	    // p2, q2 and p1 are colinear and p1 lies on segment p2q2 
	    if (o3 == 0 && onSegment(line2Start, line1Start, line2End)) return true; 
	     // p2, q2 and q1 are colinear and q1 lies on segment p2q2 
	    if (o4 == 0 && onSegment(line2Start, line1End, line2End)) return true; 
	  
	    return false; // Doesn't fall in any of the above cases 
	} 
}
