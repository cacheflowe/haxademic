package com.haxademic.sketch.test;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.mesh.PGraphicsKeystone;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class PGraphicsKeystoneTextureUV
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics externalBuffer;
	protected PGraphicsKeystone pgPinnable;
	protected boolean testPattern = true;
	protected PShader shaderPattern;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 700 );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
	}

	public void setup() {
		super.setup();	
		buildCanvas();
	}

	protected void buildCanvas() {
		externalBuffer = p.createGraphics( p.width / 2, p.height / 2, P.P3D );
		shaderPattern = p.loadShader(FileUtil.getFile("shaders/textures/cacheflowe-op-wavy-rotate.glsl"));
		pgPinnable = new PGraphicsKeystone( p, externalBuffer, 12, null );
	}

	public void drawApp() {
		p.background(0);
		// update texture
		shaderPattern.set("time", p.frameCount * 0.01f);
		externalBuffer.filter(shaderPattern);
		// draw pinned pgraphics
		if(testPattern == true) pgPinnable.drawTestPattern();
		// map a custom portion of the source
		pgPinnable.update(p.g, true, externalBuffer, 
				externalBuffer.width * 0.3f, externalBuffer.height * 0.3f, externalBuffer.width * 0.4f, externalBuffer.height * 0.4f);
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'd') testPattern = !testPattern;
		if(p.keyCode == 8) pgPinnable.resetCorners(p.g);
	}

}
