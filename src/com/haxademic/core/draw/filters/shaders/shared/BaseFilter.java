package com.haxademic.core.draw.filters.shaders.shared;

import com.haxademic.core.file.FileUtil;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;

public class BaseFilter {

	protected PShader shader;
	protected float time;

	public BaseFilter(PApplet p, String shaderFilePath) {
		shader = p.loadShader(FileUtil.getFile(shaderFilePath));
		setTime(0);
	}

	public PShader shader() {
		return shader;
	}
	
	public void applyTo(PGraphics pg) {
		pg.filter(shader);
	}
	
	public void applyTo(PApplet p) {
		p.filter(shader);
	}
	
	public void setTime(float time) {
		this.time = time;
		shader.set("time", time);
	}
	
	public float getTime() {
		return time;
	}
	
}
