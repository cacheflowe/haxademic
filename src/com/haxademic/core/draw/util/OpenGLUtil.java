package com.haxademic.core.draw.util;

import javax.media.opengl.GL;

import processing.core.PApplet;

import com.haxademic.core.app.P;

public class OpenGLUtil {

	public static GL getGlFromP( PApplet p ) {
//		PGraphicsOpenGL pgl = (PGraphicsOpenGL) p.g;
//		return pgl.gl;
		return null;
	}
	
	public static final int LOW = 0;
	public static final int MEDIUM = 1;
	public static final int HIGH = 2;

	public static void setQuality( PApplet p, int quality ) {
		//		p.hint(p.DISABLE_DEPTH_SORT);
//		GL gl = ( P.p != null ) ? getGlFromP( P.p ) : getGlFromP( p );
//		switch ( quality ) {
//			case LOW :
//				p.hint(P.DISABLE_OPENGL_2X_SMOOTH);
//				gl.glHint (GL.GL_LINE_SMOOTH_HINT, GL.GL_FASTEST);
//				gl.glHint (GL.GL_POINT_SMOOTH_HINT, GL.GL_FASTEST);
//				gl.glHint (GL.GL_POLYGON_SMOOTH_HINT, GL.GL_FASTEST);
//			case MEDIUM :
//				p.hint(P.ENABLE_OPENGL_2X_SMOOTH);
//				gl.glHint (GL.GL_LINE_SMOOTH_HINT, GL.GL_FASTEST);
//				gl.glHint (GL.GL_POINT_SMOOTH_HINT, GL.GL_FASTEST);
//				gl.glHint (GL.GL_POLYGON_SMOOTH_HINT, GL.GL_FASTEST);
//				break;
//			case HIGH :
//				p.hint(P.ENABLE_OPENGL_4X_SMOOTH);
//				gl.glHint (GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
//				gl.glHint (GL.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
//				gl.glHint (GL.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
//				gl.glEnable (GL.GL_LINE_SMOOTH);
//				break;
//		}
	}

	public static final int NORMAL = 10;
	public static final int ADDITIVE = 11;

	public static void enableBlending( PApplet p, boolean isBlending ) {
		GL gl = ( P.p != null ) ? getGlFromP( P.p ) : getGlFromP( p );
		if( isBlending == true ) 
			gl.glEnable( GL.GL_BLEND );
		else 
			gl.glDisable( GL.GL_BLEND );
	}
	
	public static void setBlendMode( PApplet p, int blendMode ) {
		GL gl = ( P.p != null ) ? getGlFromP( P.p ) : getGlFromP( p );
		switch ( blendMode ) {
			case NORMAL :
				gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				gl.glBlendEquation(GL.GL_FUNC_ADD);
			case ADDITIVE :
				gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
		}
	}

}
