package com.haxademic.core.draw.shapes;

import java.util.HashMap;

import com.haxademic.core.app.P;

import geomerative.RCommand;
import geomerative.RFont;
import geomerative.RG;
import geomerative.RGroup;
import geomerative.RMesh;
import geomerative.RPoint;
import processing.core.PShape;

public class TextToPShape {

	protected HashMap<String, RFont> fontsCache;
	protected HashMap<String, PShape> shapeCache2d;
	protected HashMap<String, PShape> shapeCache3d;

	public TextToPShape() {
		initGeomerative();
		initCache();
	}
	
	protected void initCache() {
		fontsCache = new HashMap<String, RFont>();
		shapeCache2d = new HashMap<String, PShape>();	
		shapeCache3d = new HashMap<String, PShape>();	
	}
	
	protected void initGeomerative() {
		RG.init(P.p);
		RCommand.setSegmentator(RCommand.ADAPTATIVE);
		RCommand.setSegmentAngle(0.2f);
	}

	public PShape stringToShape2d(String text, String fontFile) {
		// if letter is in the cache, just send it back
		if(shapeCache2d.get(text) != null) return shapeCache2d.get(text);

		// geomerative builds a mesh from text & cached font
		if(fontsCache.get(fontFile) == null) fontsCache.put(fontFile, new RFont(fontFile, 100, RFont.CENTER)); // 
		RFont font = fontsCache.get(fontFile);
		RGroup grp = font.toGroup(text);
		RMesh rMesh = grp.toMesh();
		
		// convert to triangle strips from geomerative strips
		PShape newShape = P.p.createShape(P.GROUP);
		newShape.setName(text);
		for ( int i = 0; i < rMesh.strips.length; i++ ) {
			
			RPoint[] meshPoints = rMesh.strips[i].getPoints();
			PShape triangle = P.p.createShape();
			triangle.beginShape(P.TRIANGLE_STRIP);
			triangle.fill(255);
			triangle.noStroke();
			for ( int ii = 0; ii < meshPoints.length; ii++ ) {
				triangle.vertex(meshPoints[ii].x, meshPoints[ii].y, 0);
			}
			triangle.endShape();
			newShape.addChild(triangle);
		}
		
		// center it
		PShapeUtil.centerSvg(newShape);
		
		// cache & return
		shapeCache2d.put(text, newShape);
		return newShape;
	}
	
	public PShape stringToShape3d(String text, float depth, String fontFile) {
		// if letter is in the cache, just send it back
		if(shapeCache3d.get(text) != null) return shapeCache3d.get(text);

		// geomerative builds a mesh from text & cached font
		if(fontsCache.get(fontFile) == null) fontsCache.put(fontFile, new RFont(fontFile, 100, RFont.CENTER)); // 
		RFont font = fontsCache.get(fontFile);
		RGroup grp = font.toGroup(text);
		RMesh rMesh = grp.toMesh();
		
		// 3d measurements
		float halfDepth = depth / 2f;
		
		// convert to triangle strips from geomerative strips
		PShape newShape = P.p.createShape(P.GROUP);
		newShape.setName(text);
		for ( int i = 0; i < rMesh.strips.length; i++ ) {
			RPoint[] meshPoints = rMesh.strips[i].getPoints();
			PShape triangle = P.p.createShape();
			
			// back
			triangle.beginShape(P.TRIANGLE_STRIP);
			triangle.fill(255);
			triangle.noStroke();
			for ( int ii = 0; ii < meshPoints.length; ii++ ) {
				triangle.vertex(meshPoints[ii].x, meshPoints[ii].y, -halfDepth);
			}
			triangle.endShape();
			newShape.addChild(triangle);

			// front
			triangle = P.p.createShape();
			triangle.beginShape(P.TRIANGLE_STRIP);
			triangle.fill(255);
			triangle.noStroke();
			for ( int ii = 0; ii < meshPoints.length; ii++ ) {
				triangle.vertex(meshPoints[ii].x, meshPoints[ii].y, halfDepth);
			}
			triangle.endShape();
			newShape.addChild(triangle);
			
			// wall (drawing quads across strip points, which is weird)
			triangle = P.p.createShape();
			triangle.beginShape(P.QUADS);
			triangle.fill(255);
			triangle.noStroke();
			for ( int ii = 0; ii < meshPoints.length - 2; ii++ ) {
				triangle.vertex(meshPoints[ii].x, meshPoints[ii].y, -halfDepth);
				triangle.vertex(meshPoints[ii].x, meshPoints[ii].y, halfDepth);
				triangle.vertex(meshPoints[ii+1].x, meshPoints[ii+1].y, halfDepth);
				triangle.vertex(meshPoints[ii+1].x, meshPoints[ii+1].y, -halfDepth);

				triangle.vertex(meshPoints[ii+2].x, meshPoints[ii+2].y, -halfDepth);
				triangle.vertex(meshPoints[ii+2].x, meshPoints[ii+2].y, halfDepth);
				triangle.vertex(meshPoints[ii+1].x, meshPoints[ii+1].y, halfDepth);
				triangle.vertex(meshPoints[ii+1].x, meshPoints[ii+1].y, -halfDepth);
				
				triangle.vertex(meshPoints[ii+2].x, meshPoints[ii+2].y, -halfDepth);
				triangle.vertex(meshPoints[ii+2].x, meshPoints[ii+2].y, halfDepth);
				triangle.vertex(meshPoints[ii].x, meshPoints[ii].y, halfDepth);
				triangle.vertex(meshPoints[ii].x, meshPoints[ii].y, -halfDepth);
			}
			triangle.endShape();
			newShape.addChild(triangle);
		}
		
		// center it
		PShapeUtil.centerSvg(newShape);
		
		// cache & return
		shapeCache3d.put(text, newShape);
		return newShape;	}
}
