package com.haxademic.demo.draw.mapping;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class Demo_PGraphicsKeystone_MappedShader
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	protected PGraphics buffer;
	protected PGraphicsKeystone pgKeystone;
	protected boolean drawTestPattern = false;
	protected PShader textureShader;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, true );
	}

	public void setupFirstFrame() {
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
		pgKeystone = new PGraphicsKeystone(p, buffer, 12, FileUtil.getFile("text/keystoning/keystone-mapped-shader.txt") );
		
		// load a shader
		textureShader = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/cacheflowe-down-void.glsl"));
	}
	
	public void drawApp() {
		p.background(0);
		p.noStroke();
		
		// update & draw shader
		textureShader.set("time", (float) p.frameCount * 0.03f);
		buffer.filter(textureShader);
		
		// draw buffer to screen
		if(drawTestPattern == true) pgKeystone.drawTestPattern();
		pgKeystone.update(p.g);
	}	
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 't') drawTestPattern = !drawTestPattern;
		if(p.key == 'r') pgKeystone.resetCorners();
	}
	
}

