package com.haxademic.core.draw.util;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

import processing.core.PGraphics;
import processing.opengl.PGL;
import processing.opengl.PJOGL;
import processing.opengl.Texture;

public class OpenGLUtil {
	
	public static final int SMOOTH_NONE = 0;
	public static final int SMOOTH_LOW = 2;
	public static final int SMOOTH_DEFAULT = 3;
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
		
	public static GL2 getGL2(PGraphics pg) {
		// only for use with Processing 2. for Processing 3, use GL3
		return ((PJOGL)pg.beginPGL()).gl.getGL2();
	}

	public static GL3 getGL3(PGraphics pg) {
		return (GL3) ((PJOGL) pg.beginPGL()).gl.getGL2GL3();
	}
	public static void closeGL3(PGraphics pg) {
		pg.endPGL(); 
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
		GL3 gl = getGL3(pg);
		if(isWireframe == true) {
			gl.glPolygonMode( GL2.GL_FRONT_AND_BACK, GL2.GL_LINE );
		} else {
			gl.glPolygonMode( GL2.GL_FRONT_AND_BACK, GL2.GL_FILL );
		}
		closeGL3(pg);
	}
	
	public static void setFog(PGraphics pg, boolean isEnabled) {
		GL2 gl = ((PJOGL)pg.beginPGL()).gl.getGL2();
		// Turn On Fog
//		float[] FogCol = {0.0f,0.8f,0.8f};  // define a nice light grey

		if(isEnabled)
			gl.glEnable(GL2.GL_FOG);
		else
			gl.glDisable(GL2.GL_FOG);
		
//		gl.glEnable(GL2.GL_FOG);
//		gl.glFogfv(GL2.GL_FOG_COLOR, FogCol, 1); // Set the fog color
//		gl.glFogf(GL2.GL_FOG_DENSITY,0.9f);  // Thin the fog out a little
//		gl.glFogf(GL2.GL_FOG_MODE, GL2.GL_LINEAR); // GL_LINEAR, GL_EXP, or GL_EXP2
//		gl.glFogf(GL2.GL_FOG_START, 0); 
//		gl.glFogf(GL2.GL_FOG_END, 1000);
		
		
		

	    float[] Fog_colour = {0,0,1f,0};


	    gl.glHint(GL2.GL_FOG_HINT, GL2.GL_NICEST);

	    //gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_EXP);
	    gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_EXP2);
	    //gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_LINEAR);

	    gl.glFogf(GL2.GL_FOG_DENSITY, 0.005f);
	    gl.glFogfv(GL2.GL_FOG_COLOR, Fog_colour, 0);
	    gl.glFogf(GL2.GL_FOG_START, 300 - 30);
	    gl.glFogf(GL2.GL_FOG_END, 300);


		
	}
}
