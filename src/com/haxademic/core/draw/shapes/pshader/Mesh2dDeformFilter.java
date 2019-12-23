package com.haxademic.core.draw.shapes.pshader;

import com.haxademic.core.draw.shapes.pshader.shared.BaseVertexShader;
import com.haxademic.core.media.DemoAssets;

import processing.core.PApplet;
import processing.core.PImage;

public class Mesh2dDeformFilter
extends BaseVertexShader {

	public static Mesh2dDeformFilter instance;
		
	public Mesh2dDeformFilter(PApplet p) {
		super(p, 
			"haxademic/shaders/vertex/mesh-2d-deform-frag.glsl", 
			"haxademic/shaders/vertex/mesh-2d-deform-vert.glsl"
		);
		setDisplacementMap(DemoAssets.justin());
		setDisplaceAmp(1f);
		setSheetMode(true);
	}
	
	public static Mesh2dDeformFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new Mesh2dDeformFilter(p);
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
