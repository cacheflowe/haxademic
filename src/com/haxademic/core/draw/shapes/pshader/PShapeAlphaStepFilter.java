package com.haxademic.core.draw.shapes.pshader;

import com.haxademic.core.draw.shapes.pshader.shared.BaseVertexShader;

public class PShapeAlphaStepFilter
extends BaseVertexShader {

	public static PShapeAlphaStepFilter instance;
		
	public PShapeAlphaStepFilter() {
		super(
			"haxademic/shaders/vertex/pshape-alpha-step-frag.glsl",
			"haxademic/shaders/vertex/pshape-alpha-step-vert.glsl"
		);
		setAlphaStep(0);
	}
	
	public static PShapeAlphaStepFilter instance() {
		if(instance != null) return instance;
		instance = new PShapeAlphaStepFilter();
		return instance;
	}
		
	public void setAlphaStep(float alphaStep) {
		shader.set("alphaStep", alphaStep);
	}
	
}	
