package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.filters.shaders.BrightnessFilter;
import com.haxademic.core.draw.filters.shaders.ColorDistortionFilter;
import com.haxademic.core.draw.filters.shaders.ContrastFilter;
import com.haxademic.core.draw.filters.shaders.CubicLensDistortionFilterOscillate;
import com.haxademic.core.draw.filters.shaders.DeformTunnelFanFilter;
import com.haxademic.core.draw.filters.shaders.EdgesFilter;
import com.haxademic.core.draw.filters.shaders.KaleidoFilter;
import com.haxademic.core.draw.filters.shaders.RadialRipplesFilter;
import com.haxademic.core.draw.filters.shaders.SphereDistortionFilter;
import com.haxademic.core.draw.filters.shaders.VignetteFilter;
import com.haxademic.core.file.FileUtil;

import processing.opengl.PShader;

public class ShaderRender
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }


	PShader texShader;
	float _frames = 400;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "640" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, Math.round(_frames) );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, false );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "3" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, Math.round(_frames) );

	}

	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

		texShader = loadShader(FileUtil.getFile("haxademic/shaders/textures/square-twist.glsl"));
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
		
//		PixelateFilter.instance(p).setDivider(64f, p.width, p.height);
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

		CubicLensDistortionFilterOscillate.instance(p).setTime(1f + P.sin(radsComplete));
		CubicLensDistortionFilterOscillate.instance(p).applyTo(p);
		
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
	}
}

