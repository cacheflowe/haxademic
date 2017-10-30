package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class MappedShader
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	protected PGraphics buffer;
	protected PGraphicsKeystone pgKeystone;
	protected boolean DEBUG_MODE = false;
	protected PShader textureShader;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, true );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
	}

	public void setup() {
		super.setup();
		buildDrawingSurface();
	}
	
	protected void buildDrawingSurface() {
		// set applet drawing quality
		P.println(OpenGLUtil.getGlVersion(p.g));
		OpenGLUtil.setTextureQualityHigh(p.g);

		// init mappable main drawing canvas
		buffer = p.createGraphics(p.width, p.height, P.P3D);
		buffer.smooth(AppSettings.SMOOTH_HIGH);
		buffer.noStroke();
		OpenGLUtil.setTextureQualityHigh(buffer);
		
		// map it
		pgKeystone = new PGraphicsKeystone(p, buffer, 12);
		
		// load a shader
		textureShader = p.loadShader(FileUtil.getFile("shaders/textures/cacheflowe-down-void.glsl"));
	}
	
	public void drawApp() {
		p.background(0);
		p.noStroke();
		
		// update & draw shader
		textureShader.set("time", (float) p.frameCount * 0.03f);
		buffer.filter(textureShader);
		
		// draw buffer to screen
		if(DEBUG_MODE == true) pgKeystone.drawTestPattern();
		pgKeystone.update(p.g, true);
	}	
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'd') DEBUG_MODE = !DEBUG_MODE;
		if(p.keyCode == 8) pgKeystone.resetCorners(p.g);
	}
	
}

