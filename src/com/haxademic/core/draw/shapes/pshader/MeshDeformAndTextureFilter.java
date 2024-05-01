package com.haxademic.core.draw.shapes.pshader;

import com.haxademic.core.draw.shapes.pshader.shared.BaseVertexShader;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;

public class MeshDeformAndTextureFilter
extends BaseVertexShader {

	public static MeshDeformAndTextureFilter instance;
	
	// prerequisites:
	// Set UV coords on mesh (you can use PShapeUtil methods or do it manually)
	// set p.stroke(255) before calling p.shape(obj)
	// NO LIGHTS: p.noLights();
	// pshape.setTexture() needs to be used, unlike LINES and POINTS
	
	public MeshDeformAndTextureFilter() {
		super(
			"haxademic/shaders/vertex/mesh-deform-frag.glsl", 
			"haxademic/shaders/vertex/mesh-deform-vert.glsl"
		);
		setDisplacementMap(DemoAssets.justin());
		setDisplaceAmp(1f);
		setSheetMode(true);
	}
	
	public static MeshDeformAndTextureFilter instance() {
		if(instance != null) return instance;
		instance = new MeshDeformAndTextureFilter();
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
	
	public void setYAxisOnly(boolean yAxisOnly) {
		shader.set("yAxisOnly", (yAxisOnly) ? 1 : 0);
	}
	
}	
