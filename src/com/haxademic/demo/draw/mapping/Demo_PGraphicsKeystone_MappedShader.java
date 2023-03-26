package com.haxademic.demo.draw.mapping;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;

public class Demo_PGraphicsKeystone_MappedShader
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	protected PGraphics buffer;
	protected PGraphicsKeystone pgKeystone;
	protected boolean drawTestPattern = false;
	protected TextureShader textureShader;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 600 );
		Config.setProperty( AppSettings.FULLSCREEN, true );
	}

	protected void firstFrame() {
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
		DebugView.setTexture("buffer", buffer);
		
		// map it
		pgKeystone = new PGraphicsKeystone(p, buffer, 12, FileUtil.getPath("text/keystoning/keystone-mapped-shader.txt") );
		
		// load a shader
		textureShader = new TextureShader(TextureShader.cacheflowe_squound_tunnel);
	}
	
	protected void drawApp() {
		p.background(0);
		p.noStroke();
		
		// update & draw shader
		textureShader.setTime((float) p.frameCount * 0.03f);
		buffer.filter(textureShader.shader());
		
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

