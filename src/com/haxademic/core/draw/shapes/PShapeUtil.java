package com.haxademic.core.draw.shapes;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.polygons.Triangle3d;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.render.Renderer;
import com.haxademic.core.system.SystemUtil;

import geomerative.RG;
import geomerative.RPoint;
import geomerative.RShape;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class PShapeUtil {
	
	///////////////////////////
	// VERTEX COUNT
	///////////////////////////
	
	public static int vertexCount(PShape shape) {
		int numVertices = shape.getVertexCount();
		for (int j = 0; j < shape.getChildCount(); j++) {
			numVertices += vertexCount(shape.getChild(j));
		}
		return numVertices;
	}
	
	///////////////////////////
	// DEBUG VERTICES
	///////////////////////////
	
	public static void debugVertices(PGraphics pg, PShape shape) {
		pg.push();
		int numVertices = shape.getVertexCount();
		for (int i = 0; i < numVertices; i++) {
			float x = shape.getVertex(i).x;
			float y = shape.getVertex(i).y;
			pg.fill(255);
			pg.stroke(255);
			pg.ellipse(x, y, 5, 5);
			
			String fontFile = DemoAssets.fontOpenSansPath;
			PFont font = FontCacher.getFont(fontFile, 12);
			FontCacher.setFontOnContext(pg, font, 0xffffffff, 1f, PTextAlign.LEFT, PTextAlign.TOP);
			pg.text("["+i+"]" + P.round(x) + "," + P.round(y), x + 10, y - 5);

		}
		pg.pop();
	}
	
	///////////////////////////
	// CREATE
	///////////////////////////
	
	public static PShape shapeFromImage(PImage img) {
		img.loadPixels();
		PShape newShape = P.p.createShape(P.GROUP);
		newShape.setStroke(false);
		
		for( int x=0; x < img.width; x++ ){
			for(int y=0; y < img.height; y++){
				int pixelColor = ImageUtil.getPixelColor( img, x, y );
//				float pixelBrightness = P.p.brightness( pixelColor );
				if(pixelColor != ImageUtil.TRANSPARENT_PNG && pixelColor != ImageUtil.EMPTY_INT) {
//				if( pixelColor != ImageUtil.EMPTY_WHITE_INT && pixelColor != ImageUtil.WHITE_INT ) {
					P.p.fill(EasingColor.redFromColorInt(pixelColor), EasingColor.greenFromColorInt(pixelColor), EasingColor.blueFromColorInt(pixelColor), 255);
					P.p.noStroke();
					
					PShape sh = P.p.createShape();
					sh.beginShape(P.TRIANGLES);
					sh.fill( EasingColor.redFromColorInt(pixelColor), EasingColor.greenFromColorInt(pixelColor), EasingColor.blueFromColorInt(pixelColor), 255 );
					
					// BL, BR, TR, TL
					float size = 0.5f;

					// front
					sh.vertex(x - size, y + size,  size);
					sh.vertex(x + size, y + size,  size);
					sh.vertex(x + size, y - size,  size);
					
					sh.vertex(x - size, y + size,  size);
					sh.vertex(x + size, y - size,  size);
					sh.vertex(x - size, y - size,  size);

					// back
					sh.vertex(x - size, y + size,  -size);
					sh.vertex(x + size, y + size,  -size);
					sh.vertex(x + size, y - size,  -size);
					
					sh.vertex(x - size, y + size,  -size);
					sh.vertex(x + size, y - size,  -size);
					sh.vertex(x - size, y - size,  -size);

					// left
					sh.vertex(x - size, y + size, -size);
					sh.vertex(x - size, y + size,  size);
					sh.vertex(x - size, y - size,  size);

					sh.vertex(x - size, y + size, -size);
					sh.vertex(x - size, y - size,  size);
					sh.vertex(x - size, y - size, -size);

					// right
					sh.vertex(x + size, y + size, -size);
					sh.vertex(x + size, y + size,  size);
					sh.vertex(x + size, y - size,  size);

					sh.vertex(x + size, y + size, -size);
					sh.vertex(x + size, y - size,  size);
					sh.vertex(x + size, y - size, -size);
					
					// floor
					sh.vertex(x - size, y + size, -size);
					sh.vertex(x + size, y + size, -size);
					sh.vertex(x + size, y + size,  size);

					sh.vertex(x + size, y + size,  size);
					sh.vertex(x - size, y + size,  size);
					sh.vertex(x - size, y + size, -size);

					// ceiling
					sh.vertex(x - size, y - size, -size);
					sh.vertex(x + size, y - size, -size);
					sh.vertex(x + size, y - size,  size);

					sh.vertex(x + size, y - size,  size);
					sh.vertex(x - size, y - size,  size);
					sh.vertex(x - size, y - size, -size);

					sh.endShape();

					newShape.addChild(sh);
				}
			}
		}
		centerShape(newShape);
		
		return newShape;
	}
	
	/////////////////////////////////
	// Load Models w/higher performance 
	// by loading the texture separately. 
	// Need to file a bug w/Processing 
	/////////////////////////////////
	
	public static PShape loadModelAndTexture(String modelPath, String texturePath, float height) {
		return loadModelAndTexture(P.p.g, modelPath, texturePath, height);
	}
	
	public static PShape loadModelAndTexture(PGraphics pg, String modelPath, String texturePath, float height) {
		P.outInit("---- Loading .obj -------------");
		int processTime = P.p.millis();
		// load .obj to graphics context
		PShape obj = pg.loadShape(FileUtil.getPath(modelPath));
		
		// manually load texture
		if(texturePath != null) {
			PImage tex = ImageCacher.get(texturePath);
			P.p.textureMode(P.NORMAL);
			obj = (PShapeOpenGL) obj.getTessellation();
			obj.setTextureMode(P.NORMAL);
			obj.setTexture(tex);
		}
		PShapeUtil.scaleShapeToHeight(obj, height);
		
		// log load time
		P.out("-- texture load", texturePath);
		P.out("-- .obj load", modelPath, P.p.millis() - processTime, "ms");
		P.out("-- Vertices: ", PShapeUtil.vertexCount(obj));
		P.out("-- Children: ", obj.getChildCount());

		return obj;
	}
	
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
	
	public static void addTextureUVExactWidthHeight(PShape shape, PImage img, float width, float height) {
		shape.setStroke(false);
		// shape.setFill(255);	// This seems to jack up vertex shaders
		shape.setTextureMode(P.NORMAL);
		
		for (int i = 0; i < shape.getVertexCount(); i++) {
			PVector v = shape.getVertex(i);
			shape.setTextureUV(
					i, 
					P.map(v.x, -width/2f, width/2f, 0, 1f), 
					P.map(v.y, -height/2f, height/2f, 0, 1f)
					);
		}
		
		for (int j = 0; j < shape.getChildCount(); j++) {
			PShape subShape = shape.getChild(j);
			addTextureUVExactWidthHeight(subShape, img, width, height);
		}
		
		if(img != null) shape.setTexture(img);
	}
	
	public static PVector util = new PVector();
	public static void addTextureUVSpherical(PShape shape, PImage img) {
		shape.setStroke(false);
		// shape.setFill(255);	// This seems to jack up vertex shaders
		shape.setTextureMode(P.NORMAL);
		
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
	
	public static boolean colorMatchNormalized(float searchRNorm, float searchGNorm, float searchBNorm, int pColor, float closenessThreshold) {
		float thresh = 0.02f;
		// get normalized material components
		float redNorm = P.p.red(pColor) / 255f;
		float greenNorm = P.p.green(pColor) / 255f;
		float blueNorm = P.p.blue(pColor) / 255f;
		// check distance from supplied color
		if(P.abs(redNorm - searchRNorm) < thresh && P.abs(greenNorm - searchGNorm) < thresh && P.abs(blueNorm - searchBNorm) < thresh) {
			return true;
		} else {
			return false;
		}

	}
	
	public static void replaceShapeMaterial(PShape shape, float searchR, float searchG, float searchB, int fillReplace, int strokeReplace, float closenessThreshold) {
//		shape.setStrokeWeight(4);
//		shape.setStroke(strokeReplace);
		for (int i = 0; i < shape.getVertexCount(); i++) {
			if(colorMatchNormalized(searchR, searchG, searchB, shape.getFill(i), closenessThreshold)) {
				shape.setFill(i, fillReplace);
				shape.setStrokeWeight(i, 4);
				shape.setStroke(strokeReplace);
			}
		}
		for (int j = 0; j < shape.getChildCount(); j++) {
			replaceShapeMaterial(shape.getChild(j), searchR, searchG, searchB, fillReplace, strokeReplace, closenessThreshold);
		}
	}

	public static PShape addShapesByColor(PShape shape, float searchR, float searchG, float searchB, PShape container, float closenessThreshold) {
		for (int j = 0; j < shape.getChildCount(); j++) {
			PShape subShape = shape.getChild(j);
			if(colorMatchNormalized(searchR, searchG, searchB, subShape.getFill(0), closenessThreshold)) {
				container.addChild(subShape);
			}
//			getShapeFromColor(shape.getChild(j), searchR, searchG, searchB);
		}
		return container;
	}
	
	
	///////////////////////////
	// Apply noise color for testing
	///////////////////////////
	
	public static void addTestFillToShape(PShape shape, float oscMult) {
		for (int i = 0; i < shape.getVertexCount(); i++) {
			PVector vertex = shape.getVertex(i);
			int fillReplace = P.p.color(
				127 + 127f * P.sin(vertex.x * oscMult),
				127 + 127f * P.sin(vertex.y * oscMult),
				127 + 127f * P.sin(vertex.z * oscMult)
			);
			shape.setFill(i, fillReplace);
			shape.noStroke();
		}
		for (int j = 0; j < shape.getChildCount(); j++) {
			addTestFillToShape(shape.getChild(j), oscMult);
		}
	}

	public static void addTestStrokeToShape(PShape shape, float strokeWeight, float oscMult) {
		for (int i = 0; i < shape.getVertexCount(); i++) {
			PVector vertex = shape.getVertex(i);
			int strokeReplace = P.p.color(
					127 + 127f * P.sin(vertex.x * oscMult),
					127 + 127f * P.sin(vertex.y * oscMult),
					127 + 127f * P.sin(vertex.z * oscMult)
					);
			shape.noFill();
			shape.setStrokeWeight(i, 4);
			shape.setStroke(strokeReplace);
		}
		for (int j = 0; j < shape.getChildCount(); j++) {
			addTestStrokeToShape(shape.getChild(j), strokeWeight, oscMult);
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
			// copy triangle vertices & UV coords
			PVector v1 = shape.getVertex(i);
			PVector v2 = shape.getVertex(i+1);
			PVector v3 = shape.getVertex(i+2);
			float texU1 = shape.getTextureU(i);
			float texV1 = shape.getTextureU(i);
			float texU2 = shape.getTextureU(i+1);
			float texV2 = shape.getTextureU(i+1);
			float texU3 = shape.getTextureU(i+2);
			float texV3 = shape.getTextureU(i+2);
			
			// half depth to keep new model centered on z-axis
			float halfDepth = depth / 2f;
			
			// top
			newShape.vertex(v1.x, v1.y, halfDepth, texU1, texV1);
			newShape.vertex(v2.x, v2.y, halfDepth, texU2, texV2);
			newShape.vertex(v3.x, v3.y, halfDepth, texU3, texV3);
			
			// bottom
			newShape.vertex(v1.x, v1.y, -halfDepth, texU1, texV1);
			newShape.vertex(v2.x, v2.y, -halfDepth, texU2, texV2);
			newShape.vertex(v3.x, v3.y, -halfDepth, texU3, texV3);
			
			// walls
			// wall 1
			newShape.vertex(v1.x, v1.y,  halfDepth, texU1, texV1);
			newShape.vertex(v1.x, v1.y, -halfDepth, texU1, texV1);
			newShape.vertex(v2.x, v2.y,  halfDepth, texU2, texV2);

			newShape.vertex(v1.x, v1.y, -halfDepth, texU1, texV1);
			newShape.vertex(v2.x, v2.y, -halfDepth, texU2, texV2);
			newShape.vertex(v2.x, v2.y,  halfDepth, texU2, texV2);

			// wall 2
			newShape.vertex(v2.x, v2.y,  halfDepth, texU2, texV2);
			newShape.vertex(v2.x, v2.y, -halfDepth, texU2, texV2);
			newShape.vertex(v3.x, v3.y,  halfDepth, texU3, texV3);
			
			newShape.vertex(v2.x, v2.y, -halfDepth, texU2, texV2);
			newShape.vertex(v3.x, v3.y, -halfDepth, texU3, texV3);
			newShape.vertex(v3.x, v3.y,  halfDepth, texU3, texV3);
			
			// wall 3
			newShape.vertex(v3.x, v3.y,  halfDepth, texU3, texV3);
			newShape.vertex(v3.x, v3.y, -halfDepth, texU3, texV3);
			newShape.vertex(v1.x, v1.y,  halfDepth, texU1, texV1);
			
			newShape.vertex(v3.x, v3.y, -halfDepth, texU3, texV3);
			newShape.vertex(v1.x, v1.y, -halfDepth, texU1, texV1);
			newShape.vertex(v1.x, v1.y,  halfDepth, texU1, texV1);
		}
		
		newShape.endShape();
		
		// recurse through original shape children if nested shapes
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
	
	public static void scaleVertices(PShape s, float x, float y, float z) {
		for (int i = 0; i < s.getVertexCount(); i++) {
			PVector curVertex = s.getVertex(i);
			s.setVertex(i, curVertex.x * x, curVertex.y * y, curVertex.z * z);
		}
		
		for (int j = 0; j < s.getChildCount(); j++) {
			PShape subShape = s.getChild(j);
			scaleVertices(subShape, x, y, z);
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

	public static PVector getBounds(PShape shape) {
		return new PVector(
			PShapeUtil.getWidth(shape), 
			PShapeUtil.getHeight(shape), 
			PShapeUtil.getDepth(shape)
		);
	}
	
	
	///////////////////////////
	// COLLISIONS
	///////////////////////////

	// https://en.wikipedia.org/wiki/M%C3%B6ller%E2%80%93Trumbore_intersection_algorithm
	public static final float EPSILON = 0.0000001f;
	protected static PVector edge1 = new PVector();
	protected static PVector edge2 = new PVector();
	protected static PVector h = new PVector();
	protected static PVector s = new PVector();
	protected static PVector q = new PVector();
	public static boolean rayIntersectsTriangle(PVector rayOrigin, PVector rayVector, PVector vertex0, PVector vertex1, PVector vertex2) {
		edge1 = PVector.sub(vertex1, vertex0);
		edge2 = PVector.sub(vertex2, vertex0);
		h = rayVector.cross(edge2);
		float a = edge1.dot(h);
		if (a > -EPSILON && a < EPSILON) return false;    // This ray is parallel to this triangle.
		float f = 1.0f / a;
		s = PVector.sub(rayOrigin, vertex0);
		float u = f * (s.dot(h));
		if (u < 0f || u > 1f) return false;
		q = s.cross(edge1);
		float v = f * rayVector.dot(q);
		if (v < 0f || u + v > 1f) return false;
		// At this stage we can compute t to find out where the intersection point is on the line.
		float t = f * edge2.dot(q);
		return (t > EPSILON); // ray intersection
	}

	///////////////////////////
	// GET FACES
	///////////////////////////
	
	public static Triangle3d[] getTesselatedFaces(PShape shape) {
		// only use with a PShape that's had `getTesselation()` called on it
		int vertexCount = shape.getVertexCount();
		int numFaces = vertexCount/3;
//		P.out("getFaces vertexCount", vertexCount);
//		P.out("getFaces numFaces", numFaces);
		Triangle3d[] triangles = new Triangle3d[numFaces];
		int triIndex = 0;
		for (int j = 0; j < shape.getVertexCount(); j+=3) {
			triangles[triIndex] = new Triangle3d(
				shape.getVertex(j+0), 
				shape.getVertex(j+1), 
				shape.getVertex(j+2)
			);
			triIndex++;
		}
		return triangles;
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
	
	public static PShape svgToUniformPointsShape(String fileName, float spacing) {
		// load svg and polygonize with Geomerative
		if(!RG.initialized()) RG.init(P.p);
		RShape rShape = RG.loadShape(fileName);
		rShape = RG.centerIn(rShape, P.p.g);

		RG.setPolygonizer(RG.UNIFORMLENGTH);
		RG.setPolygonizerLength(spacing);
		RPoint[] points = rShape.getPoints();

		// create PShape
		PShape svg = P.p.createShape();
		svg.beginShape(PConstants.POINTS);
		svg.stroke(255);
		svg.strokeWeight(1);
		svg.noFill();

		for(int i=0; i < points.length; i++){
			svg.vertex(points[i].x, points[i].y);
		}
		svg.endShape(P.CLOSE);

		return svg;
	}
	
	public static PShape pointsShapeFromRPoints(RPoint[] points) {
		if(!RG.initialized()) RG.init(P.p);

		// create PShape
		PShape svg = P.p.createShape();
		svg.beginShape(PConstants.POINTS);
		svg.stroke(255);
		svg.strokeWeight(1);
		svg.noFill();
		for(int i=0; i < points.length; i++){
			svg.vertex(points[i].x, points[i].y, 0);
		}
		svg.endShape(P.CLOSE);
		
		return svg;
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
	
	protected static boolean hasVertex(ArrayList<PVector> array, PVector p) {
		for (int i = 0; i < array.size(); i++) {
			if(p.dist(array.get(i)) == 0) return true;
		}
		return false;
	}
	
	// recurse through mesh points
	public static ArrayList<PVector> getUniqueVertices(PShape shape) {
		return getUniqueVertices(shape, null);
	}
	
	public static ArrayList<PVector> getUniqueVertices(PShape shape, ArrayList<PVector> uniqueVertices) {
		if(uniqueVertices == null) uniqueVertices = new ArrayList<PVector>();
		for (int i = 0; i < shape.getVertexCount(); i++) {
			PVector point = shape.getVertex(i);
			if(hasVertex(uniqueVertices, point) == false) {
				uniqueVertices.add( point ); 
			}
		}
			
		for (int j = 0; j < shape.getChildCount(); j++) {
			PShape subShape = shape.getChild(j);
			getUniqueVertices(subShape, uniqueVertices);
		}
		return uniqueVertices;
	}

	
	///////////////////////////
	// POINTS FOR GPU DATA
	///////////////////////////
	
	public static PShape pointsShapeForGPUData(int size) {
		return pointsShapeForGPUData(size, size);
	}
	
	public static PShape pointsShapeForGPUData(int w, int h) {
		int vertices = w * h;
		PShape shape = P.p.createShape();
		shape.beginShape(PConstants.POINTS);
		for (int i = 0; i < vertices; i++) {
			float x = i % w;
			float y = P.floor(i / w);
			shape.vertex(x/(w-1f), y/(h-1f), 0); // x/y coords are used as UV coords for position map (0-1)
		}
		shape.endShape();
		return shape;
	}

	public static PShape texturedParticlesShapeForGPUData(float cols, float rows, float shapeSize, PImage texture) {
		// make a perfect grid
		float shapeSpacing = shapeSize * 1f;
		float shapeSpacingHalf = shapeSpacing / 2f;

		// create PShapes inside a group
		int startBuildTime = P.p.millis();
		startBuildTime = P.p.millis();
		int numVerts = 0;
		PShape group = P.p.createShape(P.GROUP);
		for (int x = 0; x < cols; x++) {
			for (int y = 0; y < rows; y++) {
				float gridX = shapeSpacingHalf + -(shapeSpacing * cols/2) + (x * shapeSpacing);
				float gridY = shapeSpacingHalf + -(shapeSpacing * rows/2) + (y * shapeSpacing);
				float gridZ = 0;
				
				PShape shape = PShapeUtil.createTexturedRect(shapeSize, shapeSize, gridX, gridY, 0, texture);
				numVerts += 6; // shape.getVertexCount(); // calling this caused tessellation calculations that triggered an error
				
				// give the shape attributes for the shader to pick out their UV coord from grid index
				shape.attrib("x", (float) x);
				shape.attrib("y", (float) y);
				shape.attrib("shapeCenterX", gridX);
				shape.attrib("shapeCenterY", gridY);
				shape.attrib("shapeCenterZ", gridZ);
				group.addChild(shape);
			}
		}
		DebugView.setValue("Group PShape time", P.p.millis() - startBuildTime + "ms");
		DebugView.setValue("Num shapes", cols * rows);
		DebugView.setValue("Num verts", numVerts);
		return group;
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
				shape.setVertex(i, v.x, radius * P.sin(newRads), radius * P.cos(newRads));
			} else if(axis == P.Y) {
				float radius = MathUtil.getDistance(v.x, v.z, 0, 0);
				float newRads = MathUtil.getRadiansToTarget(0, 0, v.x, v.z) + radians;
				shape.setVertex(i, radius * P.cos(newRads), v.y, radius * P.sin(newRads));
			} else if(axis == P.Z) {
				float radius = MathUtil.getDistance(v.x, v.y, 0, 0);
				float newRads = MathUtil.getRadiansToTarget(0, 0, v.x, v.y) + radians;
				shape.setVertex(i, radius * P.cos(newRads), radius * P.sin(newRads), v.z);
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
		// float height = PShapeUtil.getMaxAbsY(shape);
		for (int i = 0; i < shape.getVertexCount(); i++) {
			PVector v = shape.getVertex(i);
			float radius = MathUtil.getDistance(v.x, v.z, 0, 0);
			float twistAtY = ((v.y + radius) * freq) * 0.001f * amp;
			float newRads = MathUtil.getRadiansToTarget(0, 0, v.x, v.z) + twistAtY;
			shape.setVertex(i, radius * P.cos(newRads), v.y, radius * P.sin(newRads));
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
				if(i < vertexCount - 2) {	// protect against rogue vertices?
					PVector vertex = polygon.getVertex(i);
					PVector vertex2 = polygon.getVertex(i+1);
					PVector vertex3 = polygon.getVertex(i+2);
					if(scale != 1) {
						vertex.mult(scale);
						vertex2.mult(scale);
						vertex3.mult(scale);
					}
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
	
	public static void drawTrianglesAudio(PGraphics pg, PShape shape, float scale, int color) {
		drawTrianglesAudio(pg, shape, scale, color, 0);	
	}
	
	public static void drawTrianglesAudio(PGraphics pg, PShape shape, float scale, int color, int faceIndex) {
		PShape polygon = shape;
		int vertexCount = polygon.getVertexCount();
		if(vertexCount == 3) {
			int i = 0;
			pg.beginShape(PConstants.TRIANGLES);
			pg.fill(color, 255f * AudioIn.audioFreq(faceIndex));
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
			faceIndex++;
		} else if(vertexCount == 4) {
			int i = 0;
			pg.beginShape(PConstants.QUADS);
			pg.fill(color, 255f * AudioIn.audioFreq(faceIndex));
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
			faceIndex++;
		} else {
			pg.beginShape(PConstants.TRIANGLES);
			
			for (int i = 0; i < vertexCount; i += 3) {
				if(i < vertexCount - 3) {	// protect against rogue vertices?
					pg.fill(color, 255f * AudioIn.audioFreq(faceIndex));
					PVector vertex = polygon.getVertex(i);
					PVector vertex2 = polygon.getVertex(i+1);
					PVector vertex3 = polygon.getVertex(i+2);
					if(scale != 1) {
						vertex.mult(scale);
						vertex2.mult(scale);
						vertex3.mult(scale);
					}
					pg.vertex(vertex.x, vertex.y, vertex.z, polygon.getTextureU(i), polygon.getTextureV(i));
					pg.vertex(vertex2.x, vertex2.y, vertex2.z, polygon.getTextureU(i+1), polygon.getTextureV(i+1));
					pg.vertex(vertex3.x, vertex3.y, vertex3.z, polygon.getTextureU(i+2), polygon.getTextureV(i+2));
					faceIndex++;
				}
			}
			pg.endShape();
		}
		
		for (int j = 0; j < shape.getChildCount(); j++) {
			PShape subShape = shape.getChild(j);
			drawTrianglesAudio(pg, subShape, scale, faceIndex);
		}
	}
	
	// Same as above, but uses PApplet with no UV coords. This makes Joons happy, but me sad about code duplication :( 
	public static void drawTrianglesJoons(PApplet p, PShape shape, float scale, String material) {
		PShape polygon = shape;
		int vertexCount = polygon.getVertexCount();
		if(vertexCount == 3) {
			int i = 0;
			setColorForJoons(material, polygon.getFill(i));
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
			setColorForJoons(material, polygon.getFill(i));
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
				if(i < vertexCount - 2) {	// protect against rogue vertices?
					setColorForJoons(material, polygon.getFill(i));
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
			drawTrianglesJoons(p, subShape, scale, material);
		}
	}
	
	public static void setColorForJoons(String material, int color) {
		float r = P.p.red(color);
		float g = P.p.green(color);
		float b = P.p.blue(color);
		if(Renderer.instance().joons != null) Renderer.instance().joons.jr.fill(material, r, g, b);		
		P.p.fill(r, g, b);
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
		FileUtil.writeTextToFile(FileUtil.haxademicOutputPath() + "text/model-"+SystemUtil.getTimestamp()+".obj", outputStr);
	}
	
	///////////////////////////
	// Create basic PShapes
	// from: https://processing.org/reference/createShape_.html
	///////////////////////////
	
	// shared helpers
	
	public static PShape setBasicShapeStyles(PShape shape, int color, int stroke, float strokeWeight) {
		shape.setFill(color != 0);		// boolean turn on/off
		shape.setFill(color);
		shape.setStroke(stroke != 0);	// boolean turn on/off
		shape.setStroke(stroke);
		shape.setStrokeWeight(strokeWeight);
		return shape;
	}	
	
	public static PShape translateShape(PShape shape, float x, float y, float z) {
		if(x != 0 || y != 0 || z != 0) {
			shape.translate(x, y, z);
		}
		return shape;
	}	
	
	// individual shapes - could add more here, but these are some common basics!
	
	public static PShape createRect(float width, float height, int color) {
		return createRect(width, height, 0,	0, 0, color, 0, 0);
	}
	
	public static PShape createRect(float width, float height, float x, float y, float z, int color, int stroke, float strokeWeight) {
		PShape shape = P.p.createShape(P.RECT, -width/2f, -height/2f, width, height);
		translateShape(shape, x, y, z);
		setBasicShapeStyles(shape, color, stroke, strokeWeight);
		return shape;
	}

	public static PShape createTexturedRect(float width, float height, float x, float y, float z, PImage texture) {
//		{
//			P.p.push();
//			P.p.textureMode(P.NORMAL);
//			PShape shape = P.p.createShape();
//			shape.beginShape();
//			shape.textureMode(P.NORMAL);
//			shape.texture(texture);
//			shape.vertex(x + -width/2, y + -height/2, z,			0, 0);
//			shape.vertex(x +  width/2, y + -height/2, z,			1, 0);
//			shape.vertex(x +  width/2, y +  height/2, z,			1, 1);
//			shape.vertex(x + -width/2, y +  height/2, z,			0, 1);
//			shape.endShape();
//			shape = shape.getTessellation();
//			shape.setTexture(texture);
//			P.p.pop();
//			return shape;
//		}
		
		{
			PShape shape = P.p.createShape(P.RECT, -width/2f, -height/2f, width, height);
			shape.setStroke(false);
			shape.textureMode(P.NORMAL);
			translateShape(shape, x, y, z);
			shape.setTexture(texture);
			return shape;
		}
	}
	
	public static PShape createEllipse(float width, float height, int color) {
		return createEllipse(width, height, 0, 0, 0, color, 0, 0);
	}
	
	public static PShape createEllipse(float width, float height, float x, float y, float z, int color, int stroke, float strokeWeight) {
		PShape shape = P.p.createShape(P.ELLIPSE, 0, 0, width, height);
		translateShape(shape, x, y, z);
		setBasicShapeStyles(shape, color, stroke, strokeWeight);
		return shape;
	}
	
	public static PShape createBox(float width, float height, float depth, int color) {
		return createBox(width, height, depth, 0, 0, 0, color, 0, 0);
	}
	
	public static PShape createBox(float width, float height, float depth, float x, float y, float z, int color, int stroke, float strokeWeight) {
		PShape shape = P.p.createShape(P.BOX, width, height, depth);
		translateShape(shape, x, y, z);
		setBasicShapeStyles(shape, color, stroke, strokeWeight);
		return shape;
	}
	
	public static PShape createSphere(float size, int color) {
		return createSphere(size, 0, 0, 0, color, 0, 0);
	}
	
	public static PShape createSphere(float size, float x, float y, float z, int color, int stroke, float strokeWeight) {
		PShape shape = P.p.createShape(P.SPHERE, size/2f);
		translateShape(shape, x, y, z);
		setBasicShapeStyles(shape, color, stroke, strokeWeight);
		return shape;
	}
	
	///////////////////////////
	// COPY MESH (only works on basic shapes, it seems)
	// from: https://discourse.processing.org/t/copying-a-pshape/1081/8
	///////////////////////////

	public static class PShapeCopy extends PShape {
		public static PShape copyShape(PShape shape) {
			return createShape(P.p, shape);
		}
	}
}
