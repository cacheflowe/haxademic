package com.haxademic.core.draw.context;

public class OpenGL32Util {
	
	// info: https://github.com/diwi/PixelFlow/blob/master/src/com/thomasdiewald/pixelflow/java/dwgl/DwGLTexture.java
	// Processing float 32 support: https://github.com/processing/processing/issues/3321
	
//	public static DwPixelFlow context;
//	public static DwPixelFlow context() {
//		if(context != null) return context;
//		context = new DwPixelFlow(P.p);
//		return context;
//	}
//	
//	public static DwGLTexture newTexture32(int w, int h) {
//		DwGLTexture texture32 = new DwGLTexture();
//	    texture32.resize(context(), GL.GL_RGBA32F, w, h, GL.GL_RGBA, GL.GL_FLOAT, GL.GL_LINEAR, 4, 4);
//		return texture32;
//	}
//	
//	public static DwGLTexture newTexture32Data(int w, int h) {
//		DwGLTexture texture32 = new DwGLTexture();
//	    texture32.resize(context(), GL.GL_RGBA32F, w, h, GL.GL_RGBA, GL.GL_FLOAT, GL.GL_NEAREST, 4, 4);
//		return texture32;
//	}
//	
//	public static DwGLTexture newTexture8(int w, int h) {
//		DwGLTexture texture8 = new DwGLTexture();
//		texture8.resize(context(), GL.GL_RGBA8, w, h, GL.GL_RGBA, GL.GL_FLOAT, GL.GL_NEAREST, GL.GL_REPEAT, 4, 4);
//		return texture8;
//	}
//	
//	public static PGraphics2D newPGraphics2D(int w, int h) {
//	    return (PGraphics2D) P.p.createGraphics(w, h, PConstants.P2D);
//	}
//	
//	public static DwGLSLProgram newShader(String path) {
//		return OpenGL32Util.context().createShader(path);
//	}
//	
//	public static void pGraphics2dToTexture32(PGraphics2D pg, DwGLTexture texture32) {
//	    DwFilter.get(context()).copy.apply(pg, texture32);
//	}
//	
//	public static void texture32ToPGraphics2d(DwGLTexture texture32, PGraphics2D pg) {
//		DwFilter.get(context()).copy.apply(texture32, pg);
//	}
}
