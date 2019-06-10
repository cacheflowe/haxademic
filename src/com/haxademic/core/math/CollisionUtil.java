package com.haxademic.core.math;

import java.util.ArrayList;

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
}
