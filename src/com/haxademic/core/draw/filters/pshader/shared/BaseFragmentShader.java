package com.haxademic.core.draw.filters.pshader.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.file.FileUtil;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;

public class BaseFragmentShader {

	protected String shaderFilePath;
	protected PShader shader;
	protected PShaderHotSwap shaderHotSwap;
	protected float time;

	public BaseFragmentShader(String shaderFilePath) {
		if(shaderFilePath != null) {
			this.shaderFilePath = FileUtil.getPath(shaderFilePath);
			shader = P.p.loadShader(this.shaderFilePath);
		}
		setTime(0);
	}

	public PShaderHotSwap updateHotSwap() {
		if(shaderHotSwap == null) {
			shaderHotSwap = new PShaderHotSwap(this.shaderFilePath);
		}
		shaderHotSwap.update();
		DebugView.setValue(FileUtil.fileNameFromPath(shaderFilePath), shaderHotSwap.isValid());
		shader = shaderHotSwap.shader(); 
		return shaderHotSwap;
	}
	
	public PShader shader() {
		return (shaderHotSwap == null) ? 
				shader : 
				shaderHotSwap.shader();
	}
	
	public void applyTo(PGraphics pg) {
		pg.filter(shader());
	}
	
	public void applyTo(PApplet p) {
		p.filter(shader());
	}
	
	public void setOnContext(PGraphics pg) {
		pg.shader(shader());
	}
	
	public void setOnContext(PApplet p) {
		p.shader(shader());
	}
	
	public void resetContext(PGraphics pg) {
		pg.resetShader();
	}
	
	public void resetContext(PApplet p) {
		p.resetShader();
	}
	
	public void setTime(float time) {
		this.time = time;
		if(shader() != null) shader().set("time", time);
	}
	
	public float getTime() {
		return time;
	}
	
}
