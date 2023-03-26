package com.haxademic.core.draw.shapes.pshader.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;

public class BaseVertexShader {

	protected PShader shader;
	protected float time;

	public BaseVertexShader(String shaderFragPath, String shaderVertPath) {
		shader = P.p.loadShader(FileUtil.getPath(shaderFragPath), FileUtil.getPath(shaderVertPath));
		setTime(0);
	}
	
	public PShader shader() {
		return shader;
	}
	
	public void setOnContext(PGraphics pg) {
		pg.shader(shader);
	}
	
	public void setOnContext(PApplet p) {
		p.shader(shader);
	}
	
	public void resetContext(PGraphics pg) {
	    pg.resetShader();
	}

	public void resetContext(PApplet p) {
	    p.resetShader();
	}

	public void setTime(float time) {
		this.time = time;
		shader.set("time", time);
	}
	
	public float getTime() {
		return time;
	}
	
}
