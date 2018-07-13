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

	public BaseFilter(PApplet p, String shaderFragPath, String shaderVertPath) {
		shader = p.loadShader(FileUtil.getFile(shaderFragPath), FileUtil.getFile(shaderVertPath));
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
	
	public void applyVertexShader(PGraphics pg) {
		pg.shader(shader);
	}
	
	public void applyVertexShader(PApplet p) {
		p.shader(shader);
	}
	
	public void setTime(float time) {
		this.time = time;
		shader.set("time", time);
	}
	
	public float getTime() {
		return time;
	}
	
}
