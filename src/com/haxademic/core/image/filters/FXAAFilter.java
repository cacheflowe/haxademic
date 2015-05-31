package com.haxademic.core.image.filters;

import com.haxademic.core.system.FileUtil;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;

public class FXAAFilter {

	protected static PShader shader;
	
	protected static PShader getShader(PApplet p) {
		if(shader != null) return shader;
		shader = p.loadShader(FileUtil.getHaxademicDataPath() + "shaders/filters/fxaa.glsl");
		return shader;
	}
	
	public static void applyTo(PApplet p, PGraphics pg) {
		pg.filter(getShader(p));
	}
	
	public static void applyTo(PApplet p) {
		p.filter(getShader(p));
	}
}
