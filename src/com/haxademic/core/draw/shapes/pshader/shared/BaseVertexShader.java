package com.haxademic.core.draw.shapes.pshader.shared;

import com.haxademic.core.file.FileUtil;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;

public class BaseVertexShader {

	protected PShader shader;
	protected float time;

	public BaseVertexShader(PApplet p, String shaderFragPath, String shaderVertPath) {
		shader = p.loadShader(FileUtil.getFile(shaderFragPath), FileUtil.getFile(shaderVertPath));
		setTime(0);
	}
	
	public PShader shader() {
		return shader;
	}
	
	public void applyTo(PGraphics pg) {
		pg.shader(shader);
	}
	
	public void applyTo(PApplet p) {
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
