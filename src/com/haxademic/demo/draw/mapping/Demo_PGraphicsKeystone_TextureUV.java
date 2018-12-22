package com.haxademic.demo.draw.mapping;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class Demo_PGraphicsKeystone_TextureUV
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics externalBuffer;
	protected PGraphicsKeystone keystonePG;
	protected boolean testPattern = true;
	protected PShader shaderPattern;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 700 );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, false );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
	}

	public void setupFirstFrame() {
		buildCanvas();
	}

	protected void buildCanvas() {
		externalBuffer = p.createGraphics( p.width / 2, p.height / 2, P.P3D );
		shaderPattern = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/cacheflowe-op-wavy-rotate.glsl"));
		keystonePG = new PGraphicsKeystone( p, externalBuffer, 12, null );
	}

	public void drawApp() {
		p.background(0);
		// update texture
		shaderPattern.set("time", p.frameCount * 0.01f);
		externalBuffer.filter(shaderPattern);
		// draw pinned pgraphics
		if(testPattern == true) keystonePG.drawTestPattern();
		// map a custom portion of the source
		keystonePG.update(p.g, true, externalBuffer, 
				externalBuffer.width * 0.3f, externalBuffer.height * 0.3f, externalBuffer.width * 0.4f, externalBuffer.height * 0.4f);
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == 't') testPattern = !testPattern;
		if(p.key == 'r') keystonePG.resetCorners();
	}

}
