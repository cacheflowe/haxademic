package com.haxademic.core.draw.context;

import com.haxademic.core.app.P;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLProfile;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PJOGL;
import processing.opengl.Texture;

public class OpenGLUtil {
	
	public static final int SMOOTH_NONE = 0;
	public static final int SMOOTH_LOW = 2;
	public static final int SMOOTH_DEFAULT = 3;
	public static final int SMOOTH_MEDIUM = 4;
	public static final int SMOOTH_HIGH = 8;

	public static final int GL_QUALITY_LOW = 0;
	public static final int GL_QUALITY_MEDIUM = 1;
	public static final int GL_QUALITY_HIGH = 2;

	public static void setTextureRepeat(PGraphics pg) {
		pg.textureWrap(Texture.REPEAT);
	}
	
	public static void setQuality(PGraphics pg, int quality) {
		//		pg.hint(p.DISABLE_DEPTH_SORT);
		GL4 gl = getGL4(pg);
//		GL gl = ((PJOGL)pg.beginPGL()).gl.getGL();
		switch ( quality ) {
			case GL_QUALITY_LOW :
//				p.hint(P.DISABLE_OPENGL_2X_SMOOTH);
				gl.glHint (GL4.GL_LINE_SMOOTH_HINT, GL4.GL_FASTEST);
				gl.glHint (GL4.GL_POINT, GL.GL_FASTEST);
				gl.glHint (GL4.GL_POLYGON_SMOOTH_HINT, GL.GL_FASTEST);
//				gl.glHint (GL.GL_POLYGON_SMOOTH_HINT, GL.GL_FASTEST);
				gl.glDisable(GL4.GL_LINE_SMOOTH);
				break;
			case GL_QUALITY_MEDIUM :
//				p.hint(P.ENABLE_OPENGL_2X_SMOOTH);
				gl.glHint (GL4.GL_LINE_SMOOTH_HINT, GL4.GL_FASTEST);
//				gl.glHint (GL4.GL_POINT_SMOOTH_HINT, GL4.GL_FASTEST);
				gl.glHint (GL4.GL_POLYGON_SMOOTH_HINT, GL4.GL_FASTEST);
				gl.glDisable(GL4.GL_LINE_SMOOTH);
				break;
			case GL_QUALITY_HIGH :
				gl.glHint (GL4.GL_LINE_SMOOTH_HINT, GL4.GL_NICEST);
//				gl.glHint (GL4.GL_POINT_SMOOTH_HINT, GL4.GL_NICEST);
				gl.glHint (GL4.GL_POLYGON_SMOOTH, GL4.GL_NICEST);
				gl.glEnable (GL4.GL_LINE_SMOOTH);
				break;
		}
	}
	
	public static void listAvailableProfiles() {
		String blah[] = GLProfile.GL_PROFILE_LIST_ALL;
		for (int i = 0; i < blah.length; i++) {
			P.println("Profile ", i, blah[i], GLProfile.isAvailable(blah[i]));
		}
	}
	
	public static void printProfile(PGraphics pg) {
		P.println("GLProfile = ", getGlVersion(pg));
	}
	
	public static String getGlVersion(PGraphics pg) {
		PJOGL pjogl = (PJOGL) pg.beginPGL();
		return pjogl.gl.getGLProfile().getName();
	}
		
	public static GL3 getGL3(PGraphics pg) {
		return (GL3) ((PJOGL) pg.beginPGL()).gl.getGL3();
	}
	
	public static GL4 getGL4(PGraphics pg) {
		return (GL4) ((PJOGL) pg.beginPGL()).gl.getGL4();
	}
	
	// from: https://github.com/processing/processing/wiki/Advanced-OpenGL
	
	public static void setTextureQualityLow(PGraphics pg) {
	    pg.hint(P.DISABLE_TEXTURE_MIPMAPS);
	    ((PGraphicsOpenGL)pg).textureSampling(2);
	}
	
	public static void setTextureQualityHigh(PGraphics pg) {
		pg.hint(P.ENABLE_TEXTURE_MIPMAPS);
		((PGraphicsOpenGL)pg).textureSampling(5);
	}
	
	public static void optimize2D(PGraphics pg) {
		pg.hint(PConstants.DISABLE_DEPTH_SORT);
		pg.hint(PConstants.DISABLE_DEPTH_TEST);
		pg.hint(PConstants.DISABLE_DEPTH_MASK);
		pg.hint(PConstants.DISABLE_OPTIMIZED_STROKE);
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
	// Also: https://threejs.org/examples/webgl_materials_blending_custom.html#
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
				break;
			case SATURATE :
				gl.glBlendFunc(GL.GL_SRC_COLOR, GL.GL_DST_ALPHA);
				gl.glBlendEquation(GL.GL_FUNC_ADD);
				break;
		}
	}
	
	public static void setBlendModeCustom(PGraphics pg, int blendSrc, int blendDest, int blendEquation) {
		GL gl = ((PJOGL)pg.beginPGL()).gl.getGL();
		gl.glBlendFunc(blendSrc, blendDest);
		gl.glBlendEquation(blendEquation);
	}

	public static void setWireframe(PGraphics pg, boolean isWireframe) {  
		GL4 gl = getGL4(pg).getGL4();
		if(isWireframe == true) {
			gl.glPolygonMode( GL4.GL_FRONT_AND_BACK, GL4.GL_LINE );
		} else {
			gl.glPolygonMode( GL4.GL_FRONT_AND_BACK, GL4.GL_FILL );
		}
//		closeGL3(pg);
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
