package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.image.filters.shaders.CubicLensDistortionFilter;
import com.haxademic.core.image.filters.shaders.SphereDistortionFilter;
import com.haxademic.core.image.filters.shaders.VignetteFilter;
import com.haxademic.core.system.FileUtil;

import processing.opengl.PShader;

@SuppressWarnings("serial")
public class ShaderRender
extends PAppletHax{


	PShader texShader;
	float _frames = 40;


	protected void overridePropsFile() {
		_appConfig.setProperty( "fills_screen", "false" );
		_appConfig.setProperty( "width", "320" );
		_appConfig.setProperty( "height", "320" );
		
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
	}

	public void drawApp() {
		background(255);
		OpenGLUtil.setTextureRepeat(g);
		
		// rendering progress
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float radsComplete = P.TWO_PI * percentComplete;
		
		texShader.set("time", P.sin(radsComplete) * 0.70f );
		p.filter(texShader);  

		SphereDistortionFilter.instance(p).setTime(P.sin(radsComplete) * 0.70f);
		SphereDistortionFilter.instance(p).applyTo(p);
		
		float fxAmount = 1.8f;
		CubicLensDistortionFilter.instance(p).setTime(1f + P.sin(radsComplete) * fxAmount);
		CubicLensDistortionFilter.instance(p).applyTo(p);
		
		VignetteFilter.instance(p).applyTo(p);

		// stop rendering
		if( p.frameCount == _frames * 2 ) {
			if(_appConfig.getBoolean("rendering", false) ==  true) {				
				_renderer.stop();
				P.println("render done!");
			}
		}
	}


}

