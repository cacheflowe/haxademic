package com.haxademic.core.draw.shapes.pshader;

import com.haxademic.core.draw.shapes.pshader.shared.BaseVertexShader;
import com.haxademic.core.file.DemoAssets;

import processing.core.PApplet;
import processing.core.PImage;

public class MeshDeformAndTextureFilter
extends BaseVertexShader {

	public static MeshDeformAndTextureFilter instance;
	
	// prerequisites:
	// Set UV coords on mesh (you can use PShapeUtil methods or do it manually)
	// set p.stroke(255) before calling p.shape(obj)
	// NO LIGHTS: p.noLights();
	// pshape.setTexture() needs to be used, unlike LINES and POINTS
	
	public MeshDeformAndTextureFilter(PApplet p) {
		super(p, 
			"haxademic/shaders/vertex/mesh-deform-frag.glsl", 
			"haxademic/shaders/vertex/mesh-deform-vert.glsl"
		);
		setDisplacementMap(DemoAssets.justin());
		setDisplaceAmp(1f);
		setSheetMode(true);
	}
	
	public static MeshDeformAndTextureFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new MeshDeformAndTextureFilter(p);
		return instance;
	}
	
	public void setDisplacementMap(PImage displacementMap) {
		shader.set("displacementMap", displacementMap);
	}
	
	public void setDisplaceAmp(float displaceAmp) {
		shader.set("displaceAmp", displaceAmp);
	}
	
	public void setSheetMode(boolean sheet) {
		shader.set("sheet", (sheet) ? 1 : 0);
	}
	
}	
