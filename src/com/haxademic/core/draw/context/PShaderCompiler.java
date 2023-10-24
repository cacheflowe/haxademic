package com.haxademic.core.draw.context;

import java.io.IOException;
import java.net.URL;

import com.haxademic.core.app.P;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PGL;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;

public class PShaderCompiler
extends PShader {

	// Extends PShader and changes the default behavior of crashing, 
	// to forcing a shader compile, and storing the potential error message to display on-screen
	// https://github.com/processing/processing/blob/master/core/src/processing/opengl/PShader.java

	protected boolean valid = true;
	protected String compileMessage = "";


	public PShaderCompiler(PApplet parent) {
		super(parent);
		forceCompile();
	}

	public PShaderCompiler(PApplet parent, String fragSource) {
		this(parent, PShaderCompiler.defaultVertexShader(), fragSource.split(com.haxademic.core.file.FileUtil.NEWLINE));
	}

	public PShaderCompiler(PApplet parent, String[] fragSource) {
		this(parent, PShaderCompiler.defaultVertexShader(), fragSource);
	}
	
	public PShaderCompiler(PApplet parent, String[] vertSource, String[] fragSource) {
		super(parent, vertSource, fragSource);
		forceCompile();
	}
	
	// helpers to compile & retrieve result

	public boolean isValid() {
		return valid;
	}
	
	public void forceCompile() {
		P.p.filter(this);
	}

	public String compileMessage() {
		return compileMessage;
	}

	protected void setCompileMessage(String message) {
		compileMessage = message;
		P.fail(compileMessage);
	}

	/////////////////////////////////
	// Override PShader methods that throw uncatchable exceptions
	/////////////////////////////////

	public static String[] defaultVertexShader() {
    return loadVertexShader(defTextureShaderVertURL);
	}

	protected boolean compile() {
		boolean vertRes = true;
		if (hasVertexShader()) {
			vertRes = compileVertexShader();
		} else {
			this.valid = false;
			setCompileMessage("Doesn't have a vertex shader");
		}

		boolean fragRes = true;
		if (hasFragmentShader()) {
			fragRes = compileFragmentShader();
		} else {
			this.valid = false;
			setCompileMessage("Doesn't have a fragment shader");
		}

		// addition by @cacheflowe to reload if shader is valid
		return vertRes && fragRes;
	}


	protected void validate() {
		pgl.getProgramiv(glProgram, PGL.LINK_STATUS, intBuffer);
		boolean linked = intBuffer.get(0) == 0 ? false : true;
		if (!linked) {
			this.valid = false;
			setCompileMessage("Cannot link shader program:\n" +
					pgl.getProgramInfoLog(glProgram));
		}

		pgl.validateProgram(glProgram);
		pgl.getProgramiv(glProgram, PGL.VALIDATE_STATUS, intBuffer);
		boolean validated = intBuffer.get(0) == 0 ? false : true;
		if (!validated) {
			this.valid = false;
			setCompileMessage("Cannot validate shader program:\n" +
					pgl.getProgramInfoLog(glProgram));
		} 
	}

	/**
	 * @param shaderSource a string containing the shader's code
	 */
	protected boolean compileVertexShader() {
		pgl.shaderSource(glVertex, PApplet.join(vertexShaderSource, "\n"));
		pgl.compileShader(glVertex);

		pgl.getShaderiv(glVertex, PGL.COMPILE_STATUS, intBuffer);
		boolean compiled = intBuffer.get(0) == 0 ? false : true;
		if (!compiled) {
			this.valid = false;
			setCompileMessage("Cannot compile vertex shader:\n" +
					pgl.getShaderInfoLog(glVertex));
			return false;
		} else {
			return true;
		}
	}


	/**
	 * @param shaderSource a string containing the shader's code
	 */
	protected boolean compileFragmentShader() {
		pgl.shaderSource(glFragment, PApplet.join(fragmentShaderSource, "\n"));
		pgl.compileShader(glFragment);

		pgl.getShaderiv(glFragment, PGL.COMPILE_STATUS, intBuffer);
		boolean compiled = intBuffer.get(0) == 0 ? false : true;
		if (!compiled) {
			this.valid = false;
			setCompileMessage("Cannot compile fragment shader:\n" +
					pgl.getShaderInfoLog(glFragment));
			return false;
		} else {
			return true;
		}
	}

	// Fragment-only helpers borrowed from PGraphicsOpenGL.
	// It would be nice to integrate this into the PShader class
	// https://github.com/processing/processing/blob/master/core/src/processing/opengl/PGraphicsOpenGL.java#L6946

	static protected URL defColorShaderVertURL =    PGraphicsOpenGL.class.getResource("/processing/opengl/shaders/ColorVert.glsl");
	static protected URL defTextureShaderVertURL =  PGraphicsOpenGL.class.getResource("/processing/opengl/shaders/TexVert.glsl");
	static protected URL defLightShaderVertURL =    PGraphicsOpenGL.class.getResource("/processing/opengl/shaders/LightVert.glsl");
	static protected URL defTexlightShaderVertURL = PGraphicsOpenGL.class.getResource("/processing/opengl/shaders/TexLightVert.glsl");
	static protected URL defColorShaderFragURL =    PGraphicsOpenGL.class.getResource("/processing/opengl/shaders/ColorFrag.glsl");
	static protected URL defTextureShaderFragURL =  PGraphicsOpenGL.class.getResource("/processing/opengl/shaders/TexFrag.glsl");
	static protected URL defLightShaderFragURL =    PGraphicsOpenGL.class.getResource("/processing/opengl/shaders/LightFrag.glsl");
	static protected URL defTexlightShaderFragURL = PGraphicsOpenGL.class.getResource("/processing/opengl/shaders/TexLightFrag.glsl");
	static protected URL defLineShaderVertURL =     PGraphicsOpenGL.class.getResource("/processing/opengl/shaders/LineVert.glsl");
	static protected URL defLineShaderFragURL =     PGraphicsOpenGL.class.getResource("/processing/opengl/shaders/LineFrag.glsl");
	static protected URL defPointShaderVertURL =    PGraphicsOpenGL.class.getResource("/processing/opengl/shaders/PointVert.glsl");
	static protected URL defPointShaderFragURL =    PGraphicsOpenGL.class.getResource("/processing/opengl/shaders/PointFrag.glsl");
	static protected URL maskShaderFragURL =        PGraphicsOpenGL.class.getResource("/processing/opengl/shaders/MaskFrag.glsl");


	public static PShaderCompiler loadShader(String fragFilename) {
		if (fragFilename == null || fragFilename.equals("")) {
			PGraphics.showWarning("The fragment shader is missing, cannot create shader object");
			return null;
		}

		int type = PShader.getShaderType(P.p.loadStrings(fragFilename), PShader.POLY);
		PShaderCompiler shader = new PShaderCompiler(P.p);
		shader.setType(type);
		shader.setFragmentShader(fragFilename);
		if (type == PShader.POINT) {
			String[] vertSource = loadVertexShader(defPointShaderVertURL);
			shader.setVertexShader(vertSource);
		} else if (type == PShader.LINE) {
			String[] vertSource = loadVertexShader(defLineShaderVertURL);
			shader.setVertexShader(vertSource);
		} else if (type == PShader.TEXLIGHT) {
			String[] vertSource = loadVertexShader(defTexlightShaderVertURL);
			shader.setVertexShader(vertSource);
		} else if (type == PShader.LIGHT) {
			String[] vertSource = loadVertexShader(defLightShaderVertURL);
			shader.setVertexShader(vertSource);
		} else if (type == PShader.TEXTURE) {
			String[] vertSource = loadVertexShader(defTextureShaderVertURL);
			shader.setVertexShader(vertSource);
		} else if (type == PShader.COLOR) {
			String[] vertSource = loadVertexShader(defColorShaderVertURL);
			shader.setVertexShader(vertSource);
		} else {
			String[] vertSource = loadVertexShader(defTextureShaderVertURL);
			shader.setVertexShader(vertSource);
		}
		
		// force compile, since shader wasn't actually constructed in this function's `new PShaderCompiler` call 
		shader.forceCompile();
		return shader;
	}

	protected static String[] loadVertexShader(URL url) {
		try {
			return PApplet.loadStrings(url.openStream());
		} catch (IOException e) {
			PGraphics.showException("Cannot load vertex shader " + url.getFile());
		}
		return null;
	}

}
