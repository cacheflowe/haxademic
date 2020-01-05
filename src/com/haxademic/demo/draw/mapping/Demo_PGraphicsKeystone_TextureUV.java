package com.haxademic.demo.draw.mapping;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class Demo_PGraphicsKeystone_TextureUV
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics externalBuffer;
	protected PGraphicsKeystone keystonePG;
	protected boolean testPattern = true;
	protected PShader shaderPattern;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 700 );
		Config.setProperty( AppSettings.FILLS_SCREEN, false );
		Config.setProperty( AppSettings.FULLSCREEN, false );
	}

	protected void firstFrame() {
		buildCanvas();
	}

	protected void buildCanvas() {
		externalBuffer = p.createGraphics( p.width / 2, p.height / 2, P.P3D );
		shaderPattern = p.loadShader(FileUtil.getPath("haxademic/shaders/textures/cacheflowe-op-wavy-rotate.glsl"));
		keystonePG = new PGraphicsKeystone( p, externalBuffer, 12, null );
	}

	protected void drawApp() {
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
