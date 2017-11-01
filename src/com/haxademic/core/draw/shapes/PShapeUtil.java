package com.haxademic.core.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class PShapeUtil {
	
	/**
	 * Clone based on a pshape.getTessellation() for a flattened copy of an svg
	 * @param p
	 * @param tesselation
	 * @param texture
	 * @return
	 */
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
	
	public static void addUVsToPShape(PShape s, float outerExtent) {
		s.setStroke(false);
		s.setTextureMode(P.NORMAL);
		for (int i = 0; i < s.getVertexCount(); i++) {
			PVector v = s.getVertex(i);
			s.setTextureUV(
					i, 
					P.map(v.x, -outerExtent, outerExtent, 0, 1f), 
					P.map(v.y, outerExtent, -outerExtent, 0, 1f)
			);
		}
	}

	/**
	 * Adds UV coordinates to an .obj PShape based on a texture and outer extent
	 * @param s
	 * @param img
	 * @param outerExtent
	 */
	public static void addTextureUVToShape(PShape s, PImage img) {
		addTextureUVToShape(s, img, PShapeUtil.getShapeMaxExtent(s), true);
	}
	public static void addTextureUVToShape(PShape s, PImage img, float outerExtent) {
		addTextureUVToShape(s, img, outerExtent, true);
	}
	public static void addTextureUVToShape(PShape shape, PImage img, float outerExtent, boolean xyMapping) {
		shape.setStroke(false);
		shape.setFill(255);
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
		shape.setFill(255);
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
	
	/**
	 * Finds the maximum size in any given direction. A basic but crappy way to figure out PShape size
	 * @param s
	 * @return
	 */
	public static void scaleSvgToExtent_DEPRECATE(PShape s, float newExtent) {
		float modelExtent = getSvgMaxExtent_DEPRECATE(s.getTessellation());
		s.scale(newExtent/modelExtent);
	}
	
	/**
	 * Finds the maximum vertex extent to translate and center the children
	 * @param shape
	 * @return
	 */
	
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
	
	/**
	 * Finds the maximum size in any given direction. A basic but crappy way to figure out PShape size
	 * @param s
	 * @return
	 */
	public static float scaleObjToExtent_DEPRECATE(PShape s, float newExtent) {
		float modelExtent = getShapeMaxExtent(s);
		float newScale = newExtent/modelExtent;
		s.scale(newScale);
		return newScale;
	}
	
	
	// re-scale by displacing actual vertices
	public static void scaleShapeToExtent(PShape s, float newExtent) {
		centerShape(s);
		float modelExtent = getShapeMaxExtent(s);
		float newScale = newExtent/modelExtent;
		adjustVertices(s, newScale);
	}
	
	public static void scaleShapeToHeight(PShape s, float newExtent) {
		centerShape(s);
		float modelHeight = getObjHeight(s);
		float newScale = newExtent/modelHeight;
		adjustVertices(s, newScale);
	}
	
	public static void adjustVertices(PShape s, float scale) {
		for (int i = 0; i < s.getVertexCount(); i++) {
			PVector curVertex = s.getVertex(i);
			s.setVertex(i, curVertex.x * scale, curVertex.y * scale, curVertex.z * scale);
		}
		
		for (int j = 0; j < s.getChildCount(); j++) {
			PShape subShape = s.getChild(j);
			adjustVertices(subShape, scale);
		}
	}
	
	public static float scaleObjToExtentReturnScale_DEPRECATE(PShape s, float newExtent) {
		float modelExtent = getShapeMaxExtent(s);
		float newScale = newExtent / modelExtent;
		s.scale(newScale);
		return newScale;
	}
	
	/**
	 * Finds the maximum size in any given direction. A basic but crappy way to figure out PShape size
	 * @param s
	 * @return
	 */
	public static float getShapeMaxExtent(PShape shape) {
		return getShapeMaxExtent(shape, 0);
	}
	
	public static float getShapeMaxExtent(PShape shape, float outermostVertex) {
		// find mesh size extent to responsively scale the mesh
		for (int i = 0; i < shape.getVertexCount(); i++) {
			PVector vertex = shape.getVertex(i);
			if(P.abs(vertex.x) > outermostVertex) outermostVertex = P.abs(vertex.x);
			if(P.abs(vertex.y) > outermostVertex) outermostVertex = P.abs(vertex.y);
			if(P.abs(vertex.z) > outermostVertex) outermostVertex = P.abs(vertex.z);
		}
	
		for (int j = 0; j < shape.getChildCount(); j++) {
			PShape subShape = shape.getChild(j);
			outermostVertex = getShapeMaxExtent(subShape, outermostVertex);
		}

		return outermostVertex;
	}
	
	public static float getObjHeight(PShape shape) {
		return getObjHeight(shape, 0);
	}

	public static float getObjHeight(PShape shape, float maxAbsYVertex) {
		// find mesh size height. this should only be used after centering the mesh
		for (int i = 0; i < shape.getVertexCount(); i++) {
			if(P.abs(shape.getVertex(i).y) > maxAbsYVertex) maxAbsYVertex = P.abs(shape.getVertex(i).y);
		}
		for (int j = 0; j < shape.getChildCount(); j++) {
			maxAbsYVertex = getObjHeight(shape.getChild(j), maxAbsYVertex);
		}
		return maxAbsYVertex;
	}
	
	/**
	 * Finds the maximum size in any given direction. A basic but crappy way to figure out PShape size
	 * @param s
	 * @return
	 */
	public static float getSvgMaxExtent_DEPRECATE(PShape s) {
		// find mesh size extent to responsively scale the mesh
		// THIS DOESN"T RECURESE - Should be replaced with getObjMaxExtent()
		float outermostVertex = 0;
		for (int i = 0; i < s.getVertexCount(); i++) {
			PVector vertex = s.getVertex(i);
			if(vertex.x > outermostVertex) outermostVertex = vertex.x;
			if(vertex.y > outermostVertex) outermostVertex = vertex.y;
			if(vertex.z > outermostVertex) outermostVertex = vertex.z;
		}
		return outermostVertex;
	}
	
	/**
	 * Draws triangles instead of native draw calls
	 * @param shape
	 * @return
	 */
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
	
	// Same as above, but uses PApplet with no UV coords, to make Joons happy
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
	
	public static void drawTrianglesGrouped(PGraphics p, PShape s, float scale) {
		p.beginShape(PConstants.TRIANGLES);
		for (int j = 0; j < s.getChildCount(); j++) {
			for (int i = 0; i < s.getChild(j).getVertexCount(); i++) {
				if(i+2 < s.getChild(j).getVertexCount()) {	// protect against rogue vertices?
					PVector vertex = s.getChild(j).getVertex(i);
					PVector vertex2 = s.getChild(j).getVertex(i+1);
					PVector vertex3 = s.getChild(j).getVertex(i+2);
					vertex.mult(scale);
					vertex2.mult(scale);
					vertex3.mult(scale);
					p.vertex(vertex.x, vertex.y, vertex.z, s.getChild(j).getTextureU(i), s.getChild(j).getTextureV(i));
					p.vertex(vertex2.x, vertex2.y, vertex2.z, s.getChild(j).getTextureU(i+1), s.getChild(j).getTextureV(i+1));
					p.vertex(vertex3.x, vertex3.y, vertex3.z, s.getChild(j).getTextureU(i+2), s.getChild(j).getTextureV(i+2));
				}
			}
		}
		p.endShape();
	}

	// from @hamoid: https://twitter.com/hamoid/status/816682493793472512
	// not tested yet
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
