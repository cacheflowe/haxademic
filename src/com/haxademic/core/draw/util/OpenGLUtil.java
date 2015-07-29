package com.haxademic.core.draw.util;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import processing.core.PGraphics;
import processing.opengl.PJOGL;
import processing.opengl.Texture;

public class OpenGLUtil {
	
	public static final int SMOOTH_LOW = 2;
	public static final int SMOOTH_MEDIUM = 4;
	public static final int SMOOTH_HIGH = 8;

	public static final int LOW = 0;
	public static final int MEDIUM = 1;
	public static final int HIGH = 2;

	public static void setTextureRepeat(PGraphics pg) {
		pg.textureWrap(Texture.REPEAT);
	}
	
	public static void setQuality(PGraphics pg, int quality) {
		//		p.hint(p.DISABLE_DEPTH_SORT);
		GL gl = ((PJOGL)pg.beginPGL()).gl.getGL();
		switch ( quality ) {
			case LOW :
//				p.hint(P.DISABLE_OPENGL_2X_SMOOTH);
				gl.glHint (GL.GL_LINE_SMOOTH_HINT, GL.GL_FASTEST);
//				gl.glHint (GL.GL_POINT_SMOOTH_HINT, GL.GL_FASTEST);
//				gl.glHint (GL.GL_POLYGON_SMOOTH_HINT, GL.GL_FASTEST);
				gl.glDisable(GL.GL_LINE_SMOOTH);
				break;
			case MEDIUM :
//				p.hint(P.ENABLE_OPENGL_2X_SMOOTH);
				gl.glHint (GL.GL_LINE_SMOOTH_HINT, GL.GL_FASTEST);
//				gl.glHint (GL.GL_POINT_SMOOTH_HINT, GL.GL_FASTEST);
//				gl.glHint (GL.GL_POLYGON_SMOOTH_HINT, GL.GL_FASTEST);
				gl.glDisable(GL.GL_LINE_SMOOTH);
				break;
			case HIGH :
//				p.hint(P.ENABLE_OPENGL_4X_SMOOTH);
				gl.glHint (GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
//				gl.glHint (GL.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
//				gl.glHint (GL.GL_POLYGON_SMOOTH, GL.GL_NICEST);
				gl.glEnable (GL.GL_LINE_SMOOTH);
				break;
		}
	}

	public enum Blend {
		DEFAULT,
		ADDITIVE,
		ADD_SATURATE,
		ALPHA_REVEAL,
		DARK_INVERSE,
		LIGHT_ADD,
		SATURATE
	}

	public static void setBlending(PGraphics pg, boolean isBlending) {
		GL gl = ((PJOGL)pg.beginPGL()).gl.getGL();
		if( isBlending == true ) 
			gl.glEnable( GL.GL_BLEND );
		else 
			gl.glDisable( GL.GL_BLEND );
	}
	
	// See: http://www.andersriggelsen.dk/glblendfunc.php
	public static void setBlendMode(PGraphics pg, Blend blendMode) {
		GL gl = ((PJOGL)pg.beginPGL()).gl.getGL();
		switch ( blendMode ) {
			case DEFAULT :
				gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				gl.glBlendEquation(GL.GL_FUNC_ADD);
				break;
			case ADD_SATURATE :
				gl.glBlendFunc(GL.GL_SRC_COLOR, GL.GL_SRC_COLOR);
				gl.glBlendEquation(GL.GL_FUNC_ADD);
				break;
			case ADDITIVE :
				gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
				gl.glBlendEquation(GL.GL_FUNC_ADD);
				break;
			case ALPHA_REVEAL :
				gl.glBlendFunc(GL.GL_SRC_COLOR, GL.GL_ONE_MINUS_SRC_COLOR);
				gl.glBlendEquation(GL.GL_FUNC_ADD);
				break;
			case DARK_INVERSE :
				gl.glBlendFunc(GL.GL_ONE_MINUS_DST_COLOR, GL.GL_ONE_MINUS_DST_COLOR);
				gl.glBlendEquation(GL.GL_FUNC_SUBTRACT);
				break;
			case LIGHT_ADD :
				gl.glBlendFunc(GL.GL_SRC_COLOR, GL.GL_SRC_ALPHA);
				gl.glBlendEquation(GL.GL_FUNC_ADD);
			case SATURATE :
				gl.glBlendFunc(GL.GL_SRC_COLOR, GL.GL_SRC_ALPHA_SATURATE);
				gl.glBlendEquation(GL.GL_FUNC_ADD);
		}
	}

	public static void setWireframe(PGraphics pg, boolean isWireframe) {
		GL2 gl = ((PJOGL)pg.beginPGL()).gl.getGL2();
		if(isWireframe == true) {
			gl.glPolygonMode( GL2.GL_FRONT_AND_BACK, GL2.GL_LINE );
		} else {
			gl.glPolygonMode( GL2.GL_FRONT_AND_BACK, GL2.GL_FILL );
		}
	}
}
