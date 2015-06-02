package com.haxademic.sketch.test;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;
import processing.opengl.Texture;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class ShaderWarpTest
extends PAppletHax {

	protected PShader _warpShader;

	protected float _frames = 10;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "480" );
		
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "rendering_gif", "false" );
		_appConfig.setProperty( "rendering_gif_framerate", "40" );
		_appConfig.setProperty( "rendering_gif_quality", "1" );
		_appConfig.setProperty( "rendering_gif_startframe", ""+ Math.round(_frames) );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+Math.round(_frames + _frames) );
	}

	public void setup() {
		super.setup();

		((PGraphics)g).textureWrap(Texture.REPEAT);
//		_warpShader = p.loadShader(FileUtil.getFile("shaders/texturewarp/round-tunnel.glsl"));
		_warpShader = p.loadShader(FileUtil.getFile("shaders/texturewarp/square-tunnel.glsl"));
		_warpShader.set("time", p.frameCount/100f);
		PImage img = p.loadImage(FileUtil.getFile("images/ello-grid-crap-512.png"));
		_warpShader.set("textureInput", img);
	}

	public void drawApp() {
		p.background(0);
		_warpShader.set("time", (p.frameCount % 10f) * 0.033f);
		p.filter(_warpShader);
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {}
	}

}
