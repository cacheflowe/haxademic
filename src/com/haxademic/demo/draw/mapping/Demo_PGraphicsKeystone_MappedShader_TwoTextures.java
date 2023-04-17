package com.haxademic.demo.draw.mapping;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;

public class Demo_PGraphicsKeystone_MappedShader_TwoTextures
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	protected boolean drawTestPattern = false;
	protected PGraphics buffer1;
	protected PGraphics buffer2;
	protected PGraphicsKeystone pgKeystone1;
	protected PGraphicsKeystone pgKeystone2;
	protected TextureShader textureShader1;
	protected TextureShader textureShader2;
	
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
		OpenGLUtil.setTextureQualityHigh(p.g);

		// init mappable main drawing canvas
		buffer1 = PG.newPG(p.width, p.height);
		buffer2 = PG.newPG(p.width, p.height);
//		OpenGLUtil.setTextureQualityHigh(buffer);
		DebugView.setTexture("buffer1", buffer1);
		DebugView.setTexture("buffer2", buffer2);
		
		// map it
		pgKeystone1 = new PGraphicsKeystone(p, buffer1, 12, FileUtil.getPath("text/keystoning/keystone-mapped-shader.txt") );
		pgKeystone2 = new PGraphicsKeystone(p, buffer2, 12, FileUtil.getPath("text/keystoning/keystone-mapped-shader-2.txt") );
		
		// load a shader
		textureShader1 = new TextureShader(TextureShader.cacheflowe_squound_tunnel);
		textureShader2 = new TextureShader(TextureShader.cacheflowe_scrolling_dashed_lines);
	}
	
	protected void drawApp() {
		p.background(0);
		p.noStroke();
		
		// update & draw shader
		textureShader1.setTime((float) p.frameCount * 0.03f);
		textureShader2.setTime((float) p.frameCount * 0.03f);
		buffer1.filter(textureShader1.shader());
		buffer2.filter(textureShader2.shader());
		
		// draw buffer to screen
		if(drawTestPattern == true) pgKeystone1.drawTestPattern();
		if(drawTestPattern == true) pgKeystone2.drawTestPattern();
		pgKeystone1.update(p.g);
		pgKeystone2.update(p.g);
		
		// show active indication for auto-saving when dragging keystone points
		int color1 = (pgKeystone1.isActive()) ? 0xff00ff00 : 0xffff0000;
		int color2 = (pgKeystone2.isActive()) ? 0xff00ff00 : 0xffff0000;
		PG.setDrawCorner(p);
		p.fill(color1);
		p.ellipse(20, 20, 50, 50);
		p.fill(color2);
		p.ellipse(p.width - 20 - 50, 20, 50, 50);
	}	
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 't') drawTestPattern = !drawTestPattern;
		if(p.key == 'r') pgKeystone1.resetCorners();
		if(p.key == 'R') pgKeystone2.resetCorners();
		if(p.key == '1') pgKeystone1.setActive(!pgKeystone1.isActive());
		if(p.key == '2') pgKeystone2.setActive(!pgKeystone2.isActive());
	}
	

	
}

