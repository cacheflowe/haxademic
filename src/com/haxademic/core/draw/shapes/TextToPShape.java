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
	protected HashMap<String, PShape> textShape2d;
	protected HashMap<String, PShape> textShape3d;
	
	public static final float QUALITY_HIGH = 0.1f;
	public static final float QUALITY_MEDIUM = 0.2f;
	protected float quality = QUALITY_MEDIUM;

	public TextToPShape() {
		this(QUALITY_MEDIUM);
	}
	
	public TextToPShape(float quality) {
		this.quality = quality;
		initGeomerative();
		initCache();
	}
	
	protected void initCache() {
		fontsCache = new HashMap<String, RFont>();
		textShape2d = new HashMap<String, PShape>();	
		textShape3d = new HashMap<String, PShape>();	
	}
	
	protected void initGeomerative() {
		RG.init(P.p);
		RCommand.setSegmentator(RCommand.ADAPTATIVE);
		RCommand.setSegmentAngle(quality);
	}

	public PShape stringToShape2d(String text, String fontFile) {
		// if letter is in the cache, just send it back
		if(textShape2d.get(text) != null) return textShape2d.get(text);

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
		PShapeUtil.centerShape(newShape);
		
		// cache & return
		textShape2d.put(text, newShape);
		return newShape;
	}
	
	public PShape stringToShape3d(String text, float depth, String fontFile) {
		// if letter is in the cache, just send it back
		if(textShape3d.get(text) != null) return textShape3d.get(text);

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
		PShapeUtil.centerShape(newShape);
		
		// cache & return
		textShape3d.put(text, newShape);
		return newShape;
	}
}
