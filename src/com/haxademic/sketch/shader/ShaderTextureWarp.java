package com.haxademic.sketch.shader;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;
import processing.opengl.Texture;

public class ShaderTextureWarp
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShader _warpShader;

	protected float _frames = 10;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "1" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, ""+ Math.round(_frames) );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames + _frames) );
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
