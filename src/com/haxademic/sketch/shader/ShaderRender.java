package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.image.filters.shaders.BrightnessFilter;
import com.haxademic.core.image.filters.shaders.ColorDistortionFilter;
import com.haxademic.core.image.filters.shaders.ContrastFilter;
import com.haxademic.core.image.filters.shaders.CubicLensDistortionFilter;
import com.haxademic.core.image.filters.shaders.DeformTunnelFanFilter;
import com.haxademic.core.image.filters.shaders.EdgesFilter;
import com.haxademic.core.image.filters.shaders.KaleidoFilter;
import com.haxademic.core.image.filters.shaders.RadialRipplesFilter;
import com.haxademic.core.image.filters.shaders.SphereDistortionFilter;
import com.haxademic.core.image.filters.shaders.VignetteFilter;
import com.haxademic.core.system.FileUtil;

import processing.opengl.PShader;

@SuppressWarnings("serial")
public class ShaderRender
extends PAppletHax{


	PShader texShader;
	float _frames = 400;


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

		texShader = loadShader(FileUtil.getFile("shaders/textures/square-twist.glsl"));
	}

	public void drawApp() {
		background(255);
		OpenGLUtil.setTextureRepeat(g);
		
		// rendering progress
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float radsComplete = P.TWO_PI * percentComplete;
		
//		texShader.set("time", 90000 +  P.sin(radsComplete) * 0.20f );
		texShader.set("time", p.frameCount / 40f );
		p.filter(texShader); 
		
//		PixelateFilter.instance(p).setDivider(64f, 64f * p.height/p.width);
//		PixelateFilter.instance(p).applyTo(p);
		
		DeformTunnelFanFilter.instance(p).setTime(p.frameCount / 40f);
		DeformTunnelFanFilter.instance(p).applyTo(p);
		
		KaleidoFilter.instance(p).setSides(4);
		KaleidoFilter.instance(p).applyTo(p);
		
		SphereDistortionFilter.instance(p).setTime(P.sin(radsComplete) * 1.70f);
		SphereDistortionFilter.instance(p).applyTo(p);
		
		RadialRipplesFilter.instance(p).setTime(p.frameCount / 140f);
		RadialRipplesFilter.instance(p).setAmplitude(0.5f + 0.5f * P.sin(radsComplete));
		RadialRipplesFilter.instance(p).applyTo(p);

		CubicLensDistortionFilter.instance(p).setTime(1f + P.sin(radsComplete));
		CubicLensDistortionFilter.instance(p).applyTo(p);
		
//		DeformBloomFilter.instance(p).setTime(p.frameCount / 40f);
//		DeformBloomFilter.instance(p).applyTo(p);
		
		VignetteFilter.instance(p).setDarkness(1f);
		VignetteFilter.instance(p).applyTo(p);

		BrightnessFilter.instance(p).setBrightness(2f);
		BrightnessFilter.instance(p).applyTo(p);
		ContrastFilter.instance(p).setContrast(2f);
		ContrastFilter.instance(p).applyTo(p);

		EdgesFilter.instance(p).applyTo(p);
		ColorDistortionFilter.instance(p).setTime(p.frameCount / 140f);
		ColorDistortionFilter.instance(p).setAmplitude(0.5f + 0.5f * P.sin(radsComplete));
		ColorDistortionFilter.instance(p).applyTo(p);

//		SaturationFilter.instance(p).setSaturation(0);
//		SaturationFilter.instance(p).applyTo(p);

		// stop rendering
		if( p.frameCount == _frames * 2 ) {
			if(_appConfig.getBoolean("rendering", false) ==  true) {				
				_renderer.stop();
				P.println("render done!");
			}
		}
	}


}

