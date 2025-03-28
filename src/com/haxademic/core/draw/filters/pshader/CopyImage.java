package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PImage;

public class CopyImage
extends BaseFragmentShader {

	public static CopyImage instance;
	
	public CopyImage() {
		super("haxademic/shaders/filters/copy-image.glsl");
		setFlipX(false);
		setFlipY(false);
	}
	
	public static CopyImage instance() {
		if(instance != null) return instance;
		instance = new CopyImage();
		return instance;
	}
	
	public void setSourceTexture(PImage source) {
		shader.set("sourceTexture", source);
	}
	
	public void setFlipX(boolean flipX) {
		shader.set("flipX", flipX);
	}
	
	public void setFlipY(boolean flipY) {
		// needed if source is a PImage instead of PGraphics
		shader.set("flipY", flipY);
	}
	
}
