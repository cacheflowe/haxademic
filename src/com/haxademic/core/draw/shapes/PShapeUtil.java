package com.haxademic.core.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.SystemUtil;
import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class PShapeUtil {
	
	///////////////////////////
	// CLONE A FLATTENED COPY
	///////////////////////////

	public static PShape clonePShape(PApplet p, PShape tesselation) {
		PShape newShape = p.createShape();
		newShape.beginShape(P.TRIANGLES);
		for (int i = 0; i < tesselation.getVertexCount(); i++) {
			PVector v = tesselation.getVertex(i);
			newShape.vertex(v.x, v.y);
		}
		newShape.endShape(P.CLOSE);
		newShape.setStroke(false);
		return newShape;
	}
	
	///////////////////////////
	// ADD UV COORDINATES
	///////////////////////////

	public static void addTextureUVToShape(PShape s, PImage img) {
		addTextureUVToShape(s, img, PShapeUtil.getMaxExtent(s), true);
	}
	public static void addTextureUVToShape(PShape s, PImage img, float outerExtent) {
		addTextureUVToShape(s, img, outerExtent, true);
	}
	public static void addTextureUVToShape(PShape shape, PImage img, float outerExtent, boolean xyMapping) {
		shape.setStroke(false);
		// shape.setFill(255);	// This seems to jack up vertex shaders
		shape.setTextureMode(P.NORMAL);
		
		for (int i = 0; i < shape.getVertexCount(); i++) {
			PVector v = shape.getVertex(i);
			float uX = (xyMapping == true) ? v.x : v.z;
			shape.setTextureUV(
					i, 
					P.map(uX, -outerExtent, outerExtent, 0, 1f), 
					P.map(v.y, -outerExtent, outerExtent, 0, 1f)
					);
		}
	
		for (int j = 0; j < shape.getChildCount(); j++) {
			PShape subShape = shape.getChild(j);
			addTextureUVToShape(subShape, img, outerExtent, xyMapping);
		}

		if(img != null) shape.setTexture(img);
	}
	
	public static void addTextureUVSpherical(PShape shape, PImage img) {
		shape.setStroke(false);
		// shape.setFill(255);	// This seems to jack up vertex shaders
		shape.setTextureMode(P.NORMAL);
		PVector util = new PVector();
		
		for (int i = 0; i < shape.getVertexCount(); i++) {
			PVector p = shape.getVertex(i);
			// map spherical coordinate to uv coordinate :: https://stackoverflow.com/questions/19357290/convert-3d-point-on-sphere-to-uv-coordinate
			util.set(p.normalize()); 
			float u = P.atan2(util.x, util.z) / P.TWO_PI + 0.5f; 
			float v = P.asin(util.y) / P.PI + .5f;
			shape.setTextureUV(i, u, v);
		}
			
		for (int j = 0; j < shape.getChildCount(); j++) {
			PShape subShape = shape.getChild(j);
			addTextureUVToShape(subShape, img);
		}
		
		if(img != null) shape.setTexture(img);
	}
	
	///////////////////////////
	// FIND A COLOR & ADD TRANSPARENCY
	// transparent face must be drawn last, and so might need to be rearranged in the obj file. 
	// ...Move said faces to the end of the file:
	// usemtl Light Cast
	// f 4/4/1 5/3/2 2/2/2 1/1/1
	///////////////////////////
	
	public static void setShapeMaterialTransparent(PShape shape, float searchR, float searchG, float searchB, float alphaReplace) {
		float thresh = 0.02f;
		for (int i = 0; i < shape.getVertexCount(); i++) {
			// get normalized material components
			float red = P.p.red(shape.getFill(i));
			float green = P.p.green(shape.getFill(i));
			float blue = P.p.blue(shape.getFill(i));
			// check distance from supplied color
			if(P.abs(red / 255f - searchR) < thresh && P.abs(green / 255f - searchG) < thresh && P.abs(blue / 255f - searchB) < thresh) {
				shape.setFill(i, P.p.color(red, green, blue, alphaReplace * 255f));
			}
		}
		for (int j = 0; j < shape.getChildCount(); j++) {
			setShapeMaterialTransparent(shape.getChild(j), searchR, searchG, searchB, alphaReplace);
		}
	}
	
	// overwrite all colors
	
	public static void setMaterialColor(PShape shape, int newColor) {
		for (int i = 0; i < shape.getVertexCount(); i++) {
			shape.setFill(i, newColor);
			shape.setStroke(false);
		}
		for (int j = 0; j < shape.getChildCount(); j++) {
			setMaterialColor(shape.getChild(j), newColor);
		}
	}

	public static void setWireframeColor(PShape shape, int faceColor, int lineColor) {
		for (int i = 0; i < shape.getVertexCount(); i++) {
			shape.setFill(true);
			shape.setFill(i, faceColor);
			shape.setStroke(true);
			shape.setStroke(i, lineColor);
			shape.setStrokeWeight(1.2f);
		}
		for (int j = 0; j < shape.getChildCount(); j++) {
			setWireframeColor(shape.getChild(j), faceColor, lineColor);
		}
	}
	
	///////////////////////////
	// SVG getTesselation() fix
	///////////////////////////
	
	public static void repairMissingSVGVertex(PShape shape) {
		PVector v1 = shape.getVertex(0);
		PVector v2 = shape.getVertex(0);
		PVector v3 = shape.getVertex(shape.getVertexCount() - 1);
		
		shape.beginShape();
		shape.fill(255, 255, 255);
		shape.noStroke();
		shape.vertex(v1.x, v1.y, v1.z);
		shape.vertex(v2.x, v2.y, v2.z);
		shape.vertex(v3.x, v3.y, v3.z);
		shape.endShape();
	}
	
	///////////////////////////
	// EXTRUDE
	///////////////////////////
	
	public static PShape createExtrudedShape(PShape shape, float depth) {
		return createExtrudedShape(shape, depth, null);
	}
	public static PShape createExtrudedShape(PShape shape, float depth, PShape newShape) {
		if(newShape == null) newShape = P.p.createShape();

		newShape.beginShape(P.TRIANGLES);
		// top
		for (int i = 0; i < shape.getVertexCount() - 3; i+=3) {
			PVector v1 = shape.getVertex(i);
			PVector v2 = shape.getVertex(i+1);
			PVector v3 = shape.getVertex(i+2);
			float texU1 = shape.getTextureU(i);
			float texV1 = shape.getTextureU(i);
			float texU2 = shape.getTextureU(i+1);
			float texV2 = shape.getTextureU(i+1);
			float texU3 = shape.getTextureU(i+2);
			float texV3 = shape.getTextureU(i+2);
			
			// top
			newShape.vertex(v1.x, v1.y, depth/2f, texU1, texV1);
			newShape.vertex(v2.x, v2.y, depth/2f, texU2, texV2);
			newShape.vertex(v3.x, v3.y, depth/2f, texU3, texV3);
			
			// bottom
			newShape.vertex(v1.x, v1.y, -depth/2f, texU1, texV1);
			newShape.vertex(v2.x, v2.y, -depth/2f, texU2, texV2);
			newShape.vertex(v3.x, v3.y, -depth/2f, texU3, texV3);
			
			// walls
			// wall 1
			newShape.vertex(v1.x, v1.y,  depth/2f, texU1, texV1);
			newShape.vertex(v1.x, v1.y, -depth/2f, texU1, texV1);
			newShape.vertex(v2.x, v2.y,  depth/2f, texU2, texV2);

			newShape.vertex(v1.x, v1.y, -depth/2f, texU1, texV1);
			newShape.vertex(v2.x, v2.y,  -depth/2f, texU2, texV2);
			newShape.vertex(v2.x, v2.y,  depth/2f, texU2, texV2);

			// wall 2
			newShape.vertex(v2.x, v2.y,  depth/2f, texU2, texV2);
			newShape.vertex(v2.x, v2.y, -depth/2f, texU2, texV2);
			newShape.vertex(v3.x, v3.y,  depth/2f, texU3, texV3);
			
			newShape.vertex(v2.x, v2.y, -depth/2f, texU2, texV2);
			newShape.vertex(v3.x, v3.y, -depth/2f, texU3, texV3);
			newShape.vertex(v3.x, v3.y,  depth/2f, texU3, texV3);
			
			// wall 3
			newShape.vertex(v3.x, v3.y,  depth/2f, texU3, texV3);
			newShape.vertex(v3.x, v3.y, -depth/2f, texU3, texV3);
			newShape.vertex(v1.x, v1.y,  depth/2f, texU1, texV1);
			
			newShape.vertex(v3.x, v3.y, -depth/2f, texU3, texV3);
			newShape.vertex(v1.x, v1.y, -depth/2f, texU1, texV1);
			newShape.vertex(v1.x, v1.y,  depth/2f, texU1, texV1);
		}
		
		newShape.endShape();
		
		for (int j = 0; j < shape.getChildCount(); j++) {
			PShape subShape = shape.getChild(j);
			createExtrudedShape(subShape, depth, newShape);
		}

		return newShape;
	}
	
	///////////////////////////
	// CENTERING
	///////////////////////////
	
	public static void centerShape(PShape shape) {
		float[] extents = {0,0,0,0,0,0};
		getShapeExtents(shape, extents);
		// P.println("extents: ",extents[0],extents[1],extents[2],extents[3],extents[4],extents[5]);
		float offsetX = centerOffsetFromExtents(extents[0], extents[1]);
		float offsetY = centerOffsetFromExtents(extents[2], extents[3]);
		float offsetZ = centerOffsetFromExtents(extents[4], extents[5]);
		offsetShapeVertices(shape, offsetX, offsetY, offsetZ);
//		getShapeExtents(s, extents);
//		P.println("extents: ",extents[0],extents[1],extents[2],extents[3],extents[4],extents[5]);
	}
	
	public static float centerOffsetFromExtents(float min, float max) {
		return -max+(max-min)/2f;
	}
	
	public static void getShapeExtents(PShape shape, float[] extents) {
		for (int i = 0; i < shape.getVertexCount(); i++) {
			PVector vertex = shape.getVertex(i);
			if(extents[0] == 0 || vertex.x < extents[0]) extents[0] = vertex.x;
			if(extents[1] == 0 || vertex.x > extents[1]) extents[1] = vertex.x;
			if(extents[2] == 0 || vertex.y < extents[2]) extents[2] = vertex.y;
			if(extents[3] == 0 || vertex.y > extents[3]) extents[3] = vertex.y;
			if(extents[4] == 0 || vertex.z < extents[4]) extents[4] = vertex.z;
			if(extents[5] == 0 || vertex.z > extents[5]) extents[5] = vertex.z;
		}
		for (int i = 0; i < shape.getChildCount(); i++) {
			PShape subShape = shape.getChild(i);
			getShapeExtents(subShape, extents);
		}
	}
	
	///////////////////////////
	// CHANGE REGISTRATION
	///////////////////////////
	
	public static void setRegistrationOffset(PShape shape, float offsetX, float offsetY, float offsetZ) {
		float w = getWidth(shape);
		float h = getHeight(shape);
		float d = getDepth(shape);
		offsetShapeVertices(shape, w * offsetX, h * offsetY, d * offsetZ);
	}
	
	public static void setOnGround(PShape shape) {
		// required to center model before this call
		setRegistrationOffset(shape, 0, -0.5f, 0);
	}
	
	public static void offsetShapeVertices(PShape s, float xOffset, float yOffset, float zOffset) {
		for (int i = 0; i < s.getVertexCount(); i++) {
			PVector vertex = s.getVertex(i);
			s.setVertex(i, vertex.x + xOffset, vertex.y + yOffset, vertex.z + zOffset);
		}
		for (int i = 0; i < s.getChildCount(); i++) {
			PShape subShape = s.getChild(i);
			offsetShapeVertices(subShape, xOffset, yOffset, zOffset);
		}
	}
	
	///////////////////////////
	// RE-SCALE
	///////////////////////////
	
	public static void scaleShapeToExtent(PShape s, float newExtent) {
		centerShape(s);
		float modelExtent = getMaxExtent(s);
		float newScale = newExtent/modelExtent;
		scaleVertices(s, newScale);
	}
	
	public static void scaleShapeToMaxAbsY(PShape s, float newExtent) {
		centerShape(s);
		float modelHeight = getMaxAbsY(s);
		float newScale = newExtent/modelHeight;
		scaleVertices(s, newScale);
	}
	
	public static void scaleShapeToHeight(PShape s, float newHeight) {
		float newScale = newHeight / getHeight(s);
		scaleVertices(s, newScale);
	}
	
	public static void scaleShapeToWidth(PShape s, float newWidth) {
		float newScale = newWidth / getWidth(s);
		scaleVertices(s, newScale);
	}
	
	public static void scaleShapeToDepth(PShape s, float newDepth) {
		float newScale = newDepth / getDepth(s);
		scaleVertices(s, newScale);
	}
	
	public static void scaleVertices(PShape s, float scale) {
		for (int i = 0; i < s.getVertexCount(); i++) {
			PVector curVertex = s.getVertex(i);
			s.setVertex(i, curVertex.x * scale, curVertex.y * scale, curVertex.z * scale);
		}
		
		for (int j = 0; j < s.getChildCount(); j++) {
			PShape subShape = s.getChild(j);
			scaleVertices(subShape, scale);
		}
	}
	
	///////////////////////////
	// GET BOUNDS / DIMENSIONS
	///////////////////////////
	
	public static float getMaxExtent(PShape shape) {
		return getMaxExtent(shape, 0);
	}
	
	public static float getMaxExtent(PShape shape, float outermostVertex) {
		// find mesh size extent to responsively scale the mesh
		for (int i = 0; i < shape.getVertexCount(); i++) {
			PVector vertex = shape.getVertex(i);
			if(P.abs(vertex.x) > outermostVertex) outermostVertex = P.abs(vertex.x);
			if(P.abs(vertex.y) > outermostVertex) outermostVertex = P.abs(vertex.y);
			if(P.abs(vertex.z) > outermostVertex) outermostVertex = P.abs(vertex.z);
		}
	
		for (int j = 0; j < shape.getChildCount(); j++) {
			PShape subShape = shape.getChild(j);
			outermostVertex = getMaxExtent(subShape, outermostVertex);
		}

		return outermostVertex;
	}
	
	public static float getMaxAbsX(PShape shape) {
		return getMaxAbsX(shape, 0);
	}
	
	public static float getMaxAbsX(PShape shape, float maxAbsXVertex) {
		// find mesh size height. this should only be used after centering the mesh
		for (int i = 0; i < shape.getVertexCount(); i++) {
			if(P.abs(shape.getVertex(i).x) > maxAbsXVertex) maxAbsXVertex = P.abs(shape.getVertex(i).x);
		}
		for (int j = 0; j < shape.getChildCount(); j++) {
			maxAbsXVertex = getMaxAbsX(shape.getChild(j), maxAbsXVertex);
		}
		return maxAbsXVertex;
	}
	
	public static float getWidth(PShape shape) {
		float[] minMax = new float[] {9999, -9999};
		getWidth(shape, minMax);
		return minMax[1] - minMax[0]; // max minus min
	}
	
	public static void getWidth(PShape shape, float[] minMax) {
		for (int i = 0; i < shape.getVertexCount(); i++) {
			if(shape.getVertex(i).x < minMax[0]) minMax[0] = shape.getVertex(i).x;
			if(shape.getVertex(i).x > minMax[1]) minMax[1] = shape.getVertex(i).x;
		}
		for (int j = 0; j < shape.getChildCount(); j++) {
			getWidth(shape.getChild(j), minMax);
		}
	}
	
	public static float getMaxAbsY(PShape shape) {
		return getMaxAbsY(shape, 0);
	}

	public static float getMaxAbsY(PShape shape, float maxAbsYVertex) {
		// find mesh size height. this should only be used after centering the mesh
		for (int i = 0; i < shape.getVertexCount(); i++) {
			if(P.abs(shape.getVertex(i).y) > maxAbsYVertex) maxAbsYVertex = P.abs(shape.getVertex(i).y);
		}
		for (int j = 0; j < shape.getChildCount(); j++) {
			maxAbsYVertex = getMaxAbsY(shape.getChild(j), maxAbsYVertex);
		}
		return maxAbsYVertex;
	}
	
	public static float getHeight(PShape shape) {
		float[] minMax = new float[] {9999, -9999};
		getHeight(shape, minMax);
		return minMax[1] - minMax[0]; // max minus min
	}
	
	public static void getHeight(PShape shape, float[] minMax) {
		for (int i = 0; i < shape.getVertexCount(); i++) {
			if(shape.getVertex(i).y < minMax[0]) minMax[0] = shape.getVertex(i).y;
			if(shape.getVertex(i).y > minMax[1]) minMax[1] = shape.getVertex(i).y;
		}
		for (int j = 0; j < shape.getChildCount(); j++) {
			getHeight(shape.getChild(j), minMax);
		}
	}
	
	public static float getMaxAbsZ(PShape shape) {
		return getMaxAbsZ(shape, 0);
	}
	
	public static float getMaxAbsZ(PShape shape, float maxAbsZVertex) {
		// find mesh size height. this should only be used after centering the mesh
		for (int i = 0; i < shape.getVertexCount(); i++) {
			if(P.abs(shape.getVertex(i).z) > maxAbsZVertex) maxAbsZVertex = P.abs(shape.getVertex(i).z);
		}
		for (int j = 0; j < shape.getChildCount(); j++) {
			maxAbsZVertex = getMaxAbsZ(shape.getChild(j), maxAbsZVertex);
		}
		return maxAbsZVertex;
	}
	
	public static float getDepth(PShape shape) {
		float[] minMax = new float[] {9999, -9999};
		getDepth(shape, minMax);
		return minMax[1] - minMax[0]; // max minus min
	}
	
	public static void getDepth(PShape shape, float[] minMax) {
		for (int i = 0; i < shape.getVertexCount(); i++) {
			if(shape.getVertex(i).z < minMax[0]) minMax[0] = shape.getVertex(i).z;
			if(shape.getVertex(i).z > minMax[1]) minMax[1] = shape.getVertex(i).z;
		}
		for (int j = 0; j < shape.getChildCount(); j++) {
			getDepth(shape.getChild(j), minMax);
		}
	}
	
	///////////////////////////
	// MESH VERTICES TO POINTS PSHAPE
	///////////////////////////

	public static PShape meshShapeToPointsShape(PShape origShape) {
		PShape newShape = P.p.createShape();
		newShape.beginShape(PConstants.POINTS);
		newShape.stroke(255);
		newShape.strokeWeight(1);
		newShape.noFill();
		addVerticesToPointShape(origShape, newShape);
		newShape.endShape();
		return newShape;
	}
	
	// Add vertices to a new PShape, while ignoring duplicates. Reduces vertex count significantly
	public static void addVerticesToPointShape(PShape origShape, PShape newShape) {
		for (int i = 0; i < origShape.getVertexCount(); i++) {
			// check to see if vertex has already been added
			PVector v = origShape.getVertex(i);
			boolean foundDuplicateVertex = false;
			for (int j = 0; j < newShape.getVertexCount(); j++) {
				PVector addedVertex = newShape.getVertex(j);
				if(v.x == addedVertex.x && v.y == addedVertex.y && v.z == addedVertex.z) foundDuplicateVertex = true;
			}
			// if not already added, add it
			if(foundDuplicateVertex == false) newShape.vertex(v.x, v.y, v.z);
		}
		// recurse through children
		for (int j = 0; j < origShape.getChildCount(); j++) {
			addVerticesToPointShape(origShape.getChild(j), newShape);
		}
	}
	
	///////////////////////////
	// MESH ROTATION
	///////////////////////////

	public static void meshRotateOnAxis(PShape shape, float radians, int axis) {
		for (int i = 0; i < shape.getVertexCount(); i++) {
			PVector v = shape.getVertex(i);
			if(axis == P.X) {
				float radius = MathUtil.getDistance(v.z, v.y, 0, 0);
				float newRads = MathUtil.getRadiansToTarget(0, 0, v.z, v.y) + radians;
				shape.setVertex(i, v.x, radius * P.sin(-newRads), radius * P.cos(-newRads));
			} else if(axis == P.Y) {
				float radius = MathUtil.getDistance(v.x, v.z, 0, 0);
				float newRads = MathUtil.getRadiansToTarget(0, 0, v.x, v.z) + radians;
				shape.setVertex(i, radius * P.cos(-newRads), v.y, radius * P.sin(-newRads));
			} else if(axis == P.Z) {
				float radius = MathUtil.getDistance(v.x, v.y, 0, 0);
				float newRads = MathUtil.getRadiansToTarget(0, 0, v.x, v.y) + radians;
				shape.setVertex(i, radius * P.cos(-newRads), radius * P.sin(-newRads), v.z);
			}
		}
		for (int j = 0; j < shape.getChildCount(); j++) {
			meshRotateOnAxis(shape.getChild(j), radians, axis);
		}
	}
	
	///////////////////////////
	// MESH FLIP
	///////////////////////////
	
	public static void meshFlipOnAxis(PShape shape, int axis) {
		for (int i = 0; i < shape.getVertexCount(); i++) {
			PVector v = shape.getVertex(i);
			if(axis == P.X) {
				shape.setVertex(i, v.x * -1f, v.y, v.z);
			} else if(axis == P.Y) {
				shape.setVertex(i, v.x, v.y * -1f, v.z);
			} else if(axis == P.Z) {
				shape.setVertex(i, v.x, v.y, v.z * -1f);
			}
		}
		for (int j = 0; j < shape.getChildCount(); j++) {
			meshFlipOnAxis(shape.getChild(j), axis);
		}
	}
	
	///////////////////////////
	// MESH TWIST
	///////////////////////////

	public static void verticalTwistShape(PShape shape, float amp, float freq) {
		float height = PShapeUtil.getMaxAbsY(shape);
		for (int i = 0; i < shape.getVertexCount(); i++) {
			PVector v = shape.getVertex(i);
			float radius = MathUtil.getDistance(v.x, v.z, 0, 0);
			float twistAtY = ((v.y + radius) * freq) * 0.001f * amp;
			float newRads = MathUtil.getRadiansToTarget(0, 0, v.x, v.z) + twistAtY;
			shape.setVertex(i, radius * P.cos(-newRads), v.y, radius * P.sin(-newRads));
		}
		for (int j = 0; j < shape.getChildCount(); j++) {
			verticalTwistShape(shape.getChild(j), amp, freq);
		}
	}
	
	///////////////////////////
	// DRAW MESH WITH RAW TRIANGLES
	///////////////////////////

	public static void drawTriangles(PGraphics pg, PShape shape, PImage img, float scale) {
		if(img != null) pg.textureMode(PConstants.NORMAL);
		if(img != null) pg.fill(255);

		PShape polygon = shape;
		int vertexCount = polygon.getVertexCount();
		if(vertexCount == 3) {
			int i = 0;
			pg.beginShape(PConstants.TRIANGLES);
			if(img != null) pg.texture(img);

			PVector vertex = polygon.getVertex(i);
			PVector vertex2 = polygon.getVertex(i+1);
			PVector vertex3 = polygon.getVertex(i+2);
			vertex.mult(scale);
			vertex2.mult(scale);
			vertex3.mult(scale);
			pg.vertex(vertex.x, vertex.y, vertex.z, polygon.getTextureU(i), polygon.getTextureV(i));
			pg.vertex(vertex2.x, vertex2.y, vertex2.z, polygon.getTextureU(i+1), polygon.getTextureV(i+1));
			pg.vertex(vertex3.x, vertex3.y, vertex3.z, polygon.getTextureU(i+2), polygon.getTextureV(i+2));
			pg.endShape();
		} else if(vertexCount == 4) {
			int i = 0;
			pg.beginShape(PConstants.QUADS);
			if(img != null) pg.texture(img);

			PVector vertex = polygon.getVertex(i);
			PVector vertex2 = polygon.getVertex(i+1);
			PVector vertex3 = polygon.getVertex(i+2);
			PVector vertex4 = polygon.getVertex(i+3);
			vertex.mult(scale);
			vertex2.mult(scale);
			vertex3.mult(scale);
			vertex4.mult(scale);
			pg.vertex(vertex.x, vertex.y, vertex.z, polygon.getTextureU(i), polygon.getTextureV(i));
			pg.vertex(vertex2.x, vertex2.y, vertex2.z, polygon.getTextureU(i+1), polygon.getTextureV(i+1));
			pg.vertex(vertex3.x, vertex3.y, vertex3.z, polygon.getTextureU(i+2), polygon.getTextureV(i+2));
			pg.vertex(vertex4.x, vertex4.y, vertex4.z, polygon.getTextureU(i+3), polygon.getTextureV(i+3));
			pg.endShape();
		} else {
			pg.beginShape(PConstants.TRIANGLES);
			if(img != null) pg.texture(img);

			for (int i = 0; i < vertexCount; i += 3) {
				if(i < vertexCount - 3) {	// protect against rogue vertices?
					PVector vertex = polygon.getVertex(i);
					PVector vertex2 = polygon.getVertex(i+1);
					PVector vertex3 = polygon.getVertex(i+2);
					vertex.mult(scale);
					vertex2.mult(scale);
					vertex3.mult(scale);
					pg.vertex(vertex.x, vertex.y, vertex.z, polygon.getTextureU(i), polygon.getTextureV(i));
					pg.vertex(vertex2.x, vertex2.y, vertex2.z, polygon.getTextureU(i+1), polygon.getTextureV(i+1));
					pg.vertex(vertex3.x, vertex3.y, vertex3.z, polygon.getTextureU(i+2), polygon.getTextureV(i+2));
				}
			}
			pg.endShape();
		}

		for (int j = 0; j < shape.getChildCount(); j++) {
			PShape subShape = shape.getChild(j);
			drawTriangles(pg, subShape, img, scale);
		}
	}
	
	// Same as above, but uses PApplet with no UV coords. This makes Joons happy, but me sad about code duplication :( 
	public static void drawTrianglesJoons(PApplet p, PShape shape, float scale) {
		PShape polygon = shape;
		int vertexCount = polygon.getVertexCount();
		if(vertexCount == 3) {
			int i = 0;
			p.beginShape(PConstants.TRIANGLES);

			PVector vertex = polygon.getVertex(i);
			PVector vertex2 = polygon.getVertex(i+1);
			PVector vertex3 = polygon.getVertex(i+2);
			vertex.mult(scale);
			vertex2.mult(scale);
			vertex3.mult(scale);
			p.vertex(vertex.x, vertex.y, vertex.z);
			p.vertex(vertex2.x, vertex2.y, vertex2.z);
			p.vertex(vertex3.x, vertex3.y, vertex3.z);
			p.endShape();
		} else if(vertexCount == 4) {
			int i = 0;
			p.beginShape(PConstants.QUADS);

			PVector vertex = polygon.getVertex(i);
			PVector vertex2 = polygon.getVertex(i+1);
			PVector vertex3 = polygon.getVertex(i+2);
			PVector vertex4 = polygon.getVertex(i+3);
			vertex.mult(scale);
			vertex2.mult(scale);
			vertex3.mult(scale);
			vertex4.mult(scale);
			p.vertex(vertex.x, vertex.y, vertex.z);
			p.vertex(vertex2.x, vertex2.y, vertex2.z);
			p.vertex(vertex3.x, vertex3.y, vertex3.z);
			p.vertex(vertex4.x, vertex4.y, vertex4.z);
			p.endShape();
		} else {
			p.beginShape(PConstants.TRIANGLES);

			for (int i = 0; i < vertexCount; i += 3) {
				if(i < vertexCount - 3) {	// protect against rogue vertices?
					PVector vertex = polygon.getVertex(i);
					PVector vertex2 = polygon.getVertex(i+1);
					PVector vertex3 = polygon.getVertex(i+2);
					vertex.mult(scale);
					vertex2.mult(scale);
					vertex3.mult(scale);
					p.vertex(vertex.x, vertex.y, vertex.z);
					p.vertex(vertex2.x, vertex2.y, vertex2.z);
					p.vertex(vertex3.x, vertex3.y, vertex3.z);
				}
			}
			p.endShape();
		}

		for (int j = 0; j < shape.getChildCount(); j++) {
			PShape subShape = shape.getChild(j);
			drawTrianglesJoons(p, subShape, scale);
		}
	}
	
	///////////////////////////
	// EXPORT MESH
	// from @hamoid: https://twitter.com/hamoid/status/816682493793472512
	///////////////////////////

	public static void exportMesh(PShape mesh) {
		StringBuilder verts = new StringBuilder();
		StringBuilder faces = new StringBuilder();
		final int vertsNum = mesh.getVertexCount();
		final PVector v = new PVector();
		for(int i=0; i < vertsNum; i+=3) {
			mesh.getVertex(i, v);
			verts.append("v " + v.x + " " + v.y + " " + v.z + "\n");
			mesh.getVertex(i+1, v);
			verts.append("v " + v.x + " " + v.y + " " + v.z + "\n");
			mesh.getVertex(i+2, v);
			verts.append("v " + v.x + " " + v.y + " " + v.z + "\n");
			faces.append("f " + (i+1) + " " + (i+2) + " " + (i+3) + "\n");
		}
		String outputStr = "o Sphere\n";
		outputStr += verts;
		outputStr += faces;
		FileUtil.writeTextToFile(FileUtil.getHaxademicOutputPath() + "text/model-"+SystemUtil.getTimestamp(P.p)+".obj", outputStr);
	}

}
