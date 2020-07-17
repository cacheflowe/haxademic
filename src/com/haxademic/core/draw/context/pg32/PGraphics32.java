package com.haxademic.core.draw.context.pg32;

import java.nio.Buffer;

import com.jogamp.opengl.GL;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PGL;
import processing.opengl.PGraphics3D;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PJOGL;

/**
 * OpenGL renderer with 32-bit float texture.
 */
public class PGraphics32 
extends PGraphics3D {
	
	// proposed renderer name
	static final String P32 = "processing.opengl.PGraphics32";

	protected PGL createPGL(PGraphicsOpenGL pg) {
		return new PJOGL32(pg);
	}
	
	// override minimal code necessary to return 32-bit float texture 
	
	public class PJOGL32
	extends PJOGL {

		public PJOGL32(PGraphicsOpenGL pg) {
			super(pg);
		}

		@Override
		public void texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, Buffer data) {
			gl.glTexImage2D(target, level, GL.GL_RGBA32F, width, height, border, format, type, data); 
		}
	}

	////////////////////////////////////////////////////////////////////
	// static factory method

	public static PGraphics createGraphics(PApplet p, int w, int h) {
		if (!p.g.isGL()) {
			throw new RuntimeException("createGraphics() with P32 requires size() to use P2D or P3D");
		}

		PGraphics pg = (PGraphics) (new PGraphics32());
		pg.setParent(p);
		pg.setPrimary(false);
		pg.setSize(w, h);
		return pg;
	}
	
}