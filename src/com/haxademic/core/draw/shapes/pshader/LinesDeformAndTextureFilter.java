package com.haxademic.core.draw.shapes.pshader;

import com.haxademic.core.draw.shapes.pshader.shared.BaseVertexShader;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;

public class LinesDeformAndTextureFilter
extends BaseVertexShader {

	public static LinesDeformAndTextureFilter instance;
	
	// prerequisites:
	// Set UV coords on mesh (you can use PShapeUtil methods or do it manually)
	// use obj.disableStyle() to let vertex shader re-texture 
	// set p.stroke(255) before calling p.shape(obj)
	
	public LinesDeformAndTextureFilter() {
		super(
			"haxademic/shaders/vertex/lines-deform-frag.glsl", 
			"haxademic/shaders/vertex/lines-deform-vert.glsl"
		);
		setColorMap(DemoAssets.justin());
		setDisplacementMap(DemoAssets.justin());
		setDisplaceAmp(1f);
		setWeight(2f);
		setModelMaxExtent(1000);
		setSheetMode(true);
		setColorThicknessMode(true);
	}
	
	public static LinesDeformAndTextureFilter instance() {
		if(instance != null) return instance;
		instance = new LinesDeformAndTextureFilter();
		return instance;
	}
		
	public void setColorMap(PImage colorMap) {
		shader.set("colorMap", colorMap);
	}
	
	public void setDisplacementMap(PImage displacementMap) {
		shader.set("displacementMap", displacementMap);
	}
	
	public void setDisplaceAmp(float displaceAmp) {
		shader.set("displaceAmp", displaceAmp);
	}
	
	public void setWeight(float weight) {
		shader.set("weight", weight);
	}
	
	public void setModelMaxExtent(float modelMaxExtent) {
		shader.set("modelMaxExtent", modelMaxExtent);
	}
	
	public void setSheetMode(boolean sheet) {
		shader.set("sheet", (sheet) ? 1 : 0);
	}
	
	public void setColorThicknessMode(boolean colorThickness) {
		shader.set("colorThickness", (colorThickness) ? 1 : 0);
	}

}	
