package com.haxademic.core.draw.shapes.pshader;

import com.haxademic.core.app.P;

import processing.core.PApplet;
import processing.opengl.PGL;
import processing.opengl.PShader;

public class PShaderCompiler
extends PShader {

	protected boolean valid = true;
	protected String compileMessage = "";

	public PShaderCompiler(PApplet parent, String[] vertSource, String[] fragSource) {
		super(parent, vertSource, fragSource);
		// force it to load immediately for error-checking!
		P.p.filter(this);
	}

	public boolean isValid() {
		return valid;
	}

	public String compileMessage() {
		return compileMessage;
	}

	protected void setCompileMessage(String message) {
		compileMessage = message;
		P.out(compileMessage);
	}

	/////////////////////////////////
	// Override PShader methods that throw uncatchable exceptions
	/////////////////////////////////

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
}
