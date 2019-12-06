package com.haxademic.core.draw.shapes;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;

import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class PShapeSolid {

	protected PShape shape;
	protected float _spectrumInterval;
	
	protected ArrayList<PVector> vertices;
	protected ArrayList<PVector> normals;
	protected ArrayList<Integer> sharedVertexIndices;
	
	public PShapeSolid(PShape shape) {
		this.shape = shape;
		calculateSharedVertices();
	}
	
	public PShape shape() {
		return shape;
	}
	
	//////////////////////
	// Init: Helper methods
	//////////////////////
	
	public static PShapeSolid newSolidObj(float size, PShape obj, PImage texture) {
		PShapeUtil.scaleShapeToExtent(obj, size);
		PShapeUtil.addTextureUVSpherical(obj, texture);
		return new PShapeSolid(obj);
	}
	
	public static PShapeSolid newSolidSphere(float size, PImage texture) {
		PShape sphere = P.p.createShape(P.SPHERE, size).getTessellation();
		sphere.setTexture(texture);
		PShapeUtil.addTextureUVSpherical(sphere, texture);

		PShape group = P.p.createShape(P.GROUP);
		group.addChild(sphere);
		return new PShapeSolid(group);
	}
	
	public static PShapeSolid newSolidIcos(float size, PImage texture) {
		return newSolidIcos(size, texture, 4);
	}
	public static PShapeSolid newSolidIcos(float size, PImage texture, int resolution) {
		PShape icos = Icosahedron.createIcosahedron(P.p.g, resolution, texture).getTessellation();
		PShapeUtil.repairMissingSVGVertex(icos);
		PShapeUtil.scaleShapeToExtent(icos, size);
		PShapeUtil.addTextureUVSpherical(icos, texture);

		PShape group = P.p.createShape(P.GROUP);
		group.addChild(icos);
		return new PShapeSolid(group);
	}


	
	//////////////////////
	// Init: Find shared vertices between triangles 
	//////////////////////
	
	protected void calculateSharedVertices() {
		// build data of shared vertex connections
		vertices = new ArrayList<PVector>();
		sharedVertexIndices = new ArrayList<Integer>();
		normals = new ArrayList<PVector>();
		
		// loop through model, finding vertices
		for (int j = 0; j < shape.getChildCount(); j++) {
			for (int i = 0; i < shape.getChild(j).getVertexCount(); i++) {
				// store vertex
				PVector vertex = shape.getChild(j).getVertex(i);
				vertices.add(vertex);
				PVector normal = shape.getChild(j).getNormal(i);
				normals.add(normal);
				
				// look for matching vertex, and store vertex to shared vertex match, if we find one.  
				boolean foundMatch = false;
				for(int v=0; v < vertices.size(); v++) {
					if(foundMatch == false && vertices.get(v).dist(vertex) == 0) {
						sharedVertexIndices.add(v);
						foundMatch = true;
					}
				}
				
				// otherwise, add a new vertex match
				if(foundMatch == false) {
					sharedVertexIndices.add(vertices.size() - 1);
				}
			}
		}
		
		// spread spectrum across vertices
		_spectrumInterval = 512f / sharedVertexIndices.size();
		// P.println("vertex count: ", vertices.size());
	}
	
	/////////////////////////////////
	// Deform methods
	/////////////////////////////////
	
	public void deformWithAudio(float ampMultMax) {
		// deform from original copy, using vertexIndex as the key to find the shared index
		int vertexIndex = 0;
//		PVector normalTemp = new PVector();
		for (int j = 0; j < shape.getChildCount(); j++) {
			for (int i = 0; i < shape.getChild(j).getVertexCount(); i++) {
				int sharedVertexIndex = sharedVertexIndices.get(vertexIndex);
				PVector vOrig = vertices.get(vertexIndex);
				// float amp = 1 + 10.5f * P.p._audioInput.getFFT().spectrum[ P.floor(_spectrumInterval * sharedVertexIndex) ]; // get shared vertex deformation
				float amp = 1 + ampMultMax * AudioIn.audioFreq(P.round((float)P.p.frameCount/10f) + P.floor(_spectrumInterval * sharedVertexIndex)); // moving starting vertex
//				float amp = 1 + 0.1f * AudioIn.getEqAvgBand( P.floor(_spectrumInterval * sharedVertexIndex) ); // get shared vertex deformation
				shape.getChild(j).setVertex(i, vOrig.x * amp, vOrig.y * amp, vOrig.z * amp);
				vertexIndex++;
			}
		}
//		P.println("vertexIndex", vertexIndex);
	}
	
	public void deformWithAudioByNormals(float ampMax) {
		// deform from original copy, using vertexIndex as the key to find the shared index
		int vertexIndex = 0;
		PVector normalTemp = new PVector();
		for (int j = 0; j < shape.getChildCount(); j++) {
			for (int i = 0; i < shape.getChild(j).getVertexCount(); i++) {
//				P.println("shape.getChild(j).getVertexCount()", shape.getChild(j).getVertexCount());
				int sharedVertexIndex = sharedVertexIndices.get(vertexIndex);
				PVector vOrig = vertices.get(vertexIndex);
//				float amp = 0 + 200f * P.p._audioInput.getFFT().spectrum[ P.floor(_spectrumInterval * sharedVertexIndex) ]; // get shared vertex deformation
				float amp = 0 + ampMax * AudioIn.audioFreq((P.round((float)P.p.frameCount/100f) + P.floor(_spectrumInterval * sharedVertexIndex))); // get shared vertex deformation
//				float amp = 1 + 0.1f * AudioIn.getEqAvgBand( P.floor(_spectrumInterval * sharedVertexIndex) ); // get shared vertex deformation
				PVector normal = normals.get(vertexIndex);
				normalTemp.set(normal).mult(amp);
//				shape.getChild(j).setVertex(i, vOrig.x * amp, vOrig.y * amp, vOrig.z * amp);
				shape.getChild(j).setVertex(i, vOrig.x + normal.x * amp, vOrig.y + normal.y * amp, vOrig.z + normal.z * amp);
				vertexIndex++;
			}
		}
//		P.println("vertexIndex", vertexIndex);
	}
	
	public void setVertexColorWithAudio(int color) {
		// deform from original copy, using vertexIndex as the key to find the shared index
		int faceIndex = 0;
		for (int j = 0; j < shape.getChildCount(); j++) {
			for (int i = 0; i < shape.getChild(j).getVertexCount() - 2; i+=3) {
//				PVector vertex1 = shape.getChild(j).getVertex(i);
//				PVector vertex2 = shape.getChild(j).getVertex(i+1);
//				PVector vertex3 = shape.getChild(j).getVertex(i+2);
//				pg.fill(color, 255f * eq);
//				pg.beginShape(P.TRIANGLE);
//				pg.vertex(vertex1.x, vertex1.y, vertex1.z);
//				pg.vertex(vertex2.x, vertex2.y, vertex2.z);
//				pg.vertex(vertex3.x, vertex3.y, vertex3.z);
//				pg.endShape();
				float eq = AudioIn.audioFreq(faceIndex) * 1f;
				shape.getChild(j).setFill(P.p.color(color, P.round(255f * eq)));
				faceIndex++;
			}
		}
//		P.println("faceIndex", faceIndex);
//		if(disableStyle == true) shape.disableStyle();
	}
	
	public void updateWithTrig(boolean disableStyle, float time, float ampScale, float spreadMultiplier) {
		// deform from original copy, using vertexIndex as the key to find the shared index
		int vertexIndex = 0;
		for (int j = 0; j < shape.getChildCount(); j++) {
			for (int i = 0; i < shape.getChild(j).getVertexCount(); i++) {
				int sharedVertexIndex = sharedVertexIndices.get(vertexIndex);
				PVector vOrig = vertices.get(vertexIndex);
				float vertexIndexPercent = (float)(sharedVertexIndex + 1) / (float)(sharedVertexIndices.size() + 1);
				float amp = 1.0f + ampScale + ampScale * P.sin((time * P.TWO_PI) + (spreadMultiplier * P.TWO_PI * vertexIndexPercent)); 
				shape.getChild(j).setVertex(i, vOrig.x * amp, vOrig.y * amp, vOrig.z * amp);
				vertexIndex++;
			}
		}
		if(disableStyle == true) shape.disableStyle();
	}
	
	public void updateWithTrigGradient(float time, float ampScale, float spreadMultiplier, PImage texture) {
		// deform from original copy, using vertexIndex as the key to find the shared index
		int vertexIndex = 0;
		texture.loadPixels();
		for (int j = 0; j < shape.getChildCount(); j++) {
			for (int i = 0; i < shape.getChild(j).getVertexCount(); i++) {
				int sharedVertexIndex = sharedVertexIndices.get(vertexIndex);
				PVector vOrig = vertices.get(vertexIndex);
				float vertexIndexPercent = (float)(sharedVertexIndex + 1) / (float)(sharedVertexIndices.size() + 1);
				float oscVal = P.sin((time * P.TWO_PI) + (spreadMultiplier * P.TWO_PI * vertexIndexPercent));
				float amp = 1.0f + ampScale + ampScale * oscVal; 
				shape.getChild(j).setVertex(i, vOrig.x * amp, vOrig.y * amp, vOrig.z * amp);
				if(i < shape.getChild(j).getVertexCount() - 2) { // i % 3 == 0 &&
					int newColor = ImageUtil.getPixelColor(texture, (int)P.map(oscVal, -1f, 1f, 1, texture.width - 2), (int)P.map(oscVal, -1f, 1f, 1, texture.height - 2));
					shape.getChild(j).setFill(newColor);
				}
				vertexIndex++;
			}
		}
	} 
	
	public void updateWithNoise(float time, float freq, float ampScale, int noiseOctaves, float noiseFalloff) {
		// deform from original copy, using vertexIndex as the key to find the shared index
		P.p.noiseDetail(noiseOctaves, noiseFalloff);
		int vertexIndex = 0;
		for (int j = 0; j < shape.getChildCount(); j++) {
			for (int i = 0; i < shape.getChild(j).getVertexCount(); i++) {
				PVector vOrig = vertices.get(vertexIndex);
				float amp = 1.0f + ampScale * P.p.noise(
						vOrig.x * freq + P.cos(time), 
						vOrig.y * freq + P.sin(time), 
						vOrig.z * freq + P.sin(time));
				shape.getChild(j).setVertex(i, vOrig.x * amp, vOrig.y * amp, vOrig.z * amp);
				vertexIndex++;
			}
		}
	}
	
	public void updateWithTrigAndNoiseCombo(float progressRadians, float trigAmpScale, float trigSpreadMultiplier, float trigIndexPercentOffset, float noiseFreq, float noiseAmpScale, int noiseOctaves, float noiseFalloff) {
		// deform from original copy, using vertexIndex as the key to find the shared index
		P.p.noiseDetail(noiseOctaves, noiseFalloff);
		int vertexIndex = 0;
		for (int j = 0; j < shape.getChildCount(); j++) {
			for (int i = 0; i < shape.getChild(j).getVertexCount(); i++) {
				int sharedVertexIndex = sharedVertexIndices.get(vertexIndex);
				PVector vOrig = vertices.get(vertexIndex);
				float vertexIndexPercent = (float)(sharedVertexIndex + 1) / (float)(sharedVertexIndices.size() + 1);
				vertexIndexPercent = (vertexIndexPercent + trigIndexPercentOffset) % 1f; 
				float ampTrig = 1.0f + trigAmpScale + trigAmpScale * P.sin((progressRadians) + (trigSpreadMultiplier * P.TWO_PI * vertexIndexPercent)); 
				float ampNoise = 1.0f + noiseAmpScale * 2f * (-0.5f + P.p.noise(vOrig.x + P.cos(progressRadians) * noiseFreq, vOrig.y + P.sin(progressRadians) * noiseFreq, vOrig.z + P.sin(progressRadians) * noiseFreq));
				// float comboAmp = P.min(ampTrig, ampNoise);
				float comboAmp = (ampTrig + ampNoise) / 2f;
				shape.getChild(j).setVertex(i, vOrig.x * comboAmp, vOrig.y * comboAmp, vOrig.z * comboAmp);
				vertexIndex++;
			}
		}
	}
	

}
