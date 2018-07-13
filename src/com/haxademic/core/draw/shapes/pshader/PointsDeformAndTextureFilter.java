package com.haxademic.core.draw.shapes.pshader;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;
import com.haxademic.core.file.DemoAssets;

import processing.core.PApplet;
import processing.core.PImage;

public class PointsDeformAndTextureFilter
extends BaseFilter {

	public static PointsDeformAndTextureFilter instance;
	
	// prerequisites:
	// Set UV coords on mesh (you can use PShapeUtil methods or do it manually)
	
	public PointsDeformAndTextureFilter(PApplet p) {
		super(p, 
			"haxademic/shaders/point/points-default-frag.glsl", 
			"haxademic/shaders/point/points-deform-and-texture-vert.glsl"
		);
		setColorMap(DemoAssets.justin());
		setDisplacementMap(DemoAssets.justin());
		setDisplaceAmp(1f);
		setMaxPointSize(3f);
		setModelMaxExtent(1000);
		setSheetMode(true);
		setColorPointSizeMode(true);
	}
	
	public static PointsDeformAndTextureFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new PointsDeformAndTextureFilter(p);
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
	
	public void setMaxPointSize(float maxPointSize) {
		shader.set("maxPointSize", maxPointSize);
	}
	
	public void setModelMaxExtent(float modelMaxExtent) {
		shader.set("modelMaxExtent", modelMaxExtent);
	}
	
	public void setSheetMode(boolean sheet) {
		shader.set("sheet", (sheet) ? 1 : 0);
	}
	
	public void setColorPointSizeMode(boolean colorPointSize) {
		shader.set("colorPointSize", (colorPointSize) ? 1 : 0);
	}

}	
