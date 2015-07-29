package com.haxademic.sketch.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.system.FileUtil;

import processing.opengl.PShader;

@SuppressWarnings("serial")
public class ShaderRender
extends PAppletHax{


	PShader texShader;
	PShader fxShader;
	float _frames = 100;


	protected void overridePropsFile() {
		_appConfig.setProperty( "fills_screen", "false" );
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "640" );
		
		_appConfig.setProperty( "rendering", "false" );
		
		_appConfig.setProperty( "rendering_gif", "false" );
		_appConfig.setProperty( "rendering_gif_framerate", "40" );
		_appConfig.setProperty( "rendering_gif_quality", "15" );
		_appConfig.setProperty( "rendering_gif_startframe", "3" );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+Math.round(_frames+2) );

	}

	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

		texShader = loadShader(FileUtil.getFile("shaders/textures/bw-circles.glsl"));
//		fxShader = p.loadShader( FileUtil.getFile("shaders/filters/cubic-lens-distortion.glsl")); 
		fxShader = p.loadShader( FileUtil.getFile("shaders/filters/wobble.glsl")); 
	}

	public void drawApp() {
		background(255);
		OpenGLUtil.setTextureRepeat(g);
		
		// rendering progress
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float radsComplete = P.TWO_PI * percentComplete;
		
		texShader.set("time", P.sin(radsComplete) * 0.750f );
		p.filter(texShader);  
		
		fxShader.set("time", -2f - P.sin(radsComplete) * 2.0f );
		p.filter(fxShader);  

		
		// stop rendering
		if( p.frameCount == _frames * 2 ) {
			if(_appConfig.getBoolean("rendering", false) ==  true) {				
				_renderer.stop();
				P.println("render done!");
			}
		}
	}


}

