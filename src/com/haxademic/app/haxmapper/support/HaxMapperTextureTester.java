package com.haxademic.app.haxmapper.support;

import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.app.haxmapper.textures.TextureAudioTube;
import com.haxademic.app.haxmapper.textures.TextureBarsEQ;
import com.haxademic.app.haxmapper.textures.TextureBlobSheet;
import com.haxademic.app.haxmapper.textures.TextureEQConcentricCircles;
import com.haxademic.app.haxmapper.textures.TextureEQFloatParticles;
import com.haxademic.app.haxmapper.textures.TextureEQGrid;
import com.haxademic.app.haxmapper.textures.TextureLinesEQ;
import com.haxademic.app.haxmapper.textures.TextureOuterSphere;
import com.haxademic.app.haxmapper.textures.TextureRotatingRings;
import com.haxademic.app.haxmapper.textures.TextureShaderTimeStepper;
import com.haxademic.app.haxmapper.textures.TextureSphereAudioTextures;
import com.haxademic.app.haxmapper.textures.TextureSvgPattern;
import com.haxademic.app.haxmapper.textures.TextureTwistingSquares;
import com.haxademic.app.haxmapper.textures.TextureWaveformCircle;
import com.haxademic.app.haxmapper.textures.TextureWaveformSimple;
import com.haxademic.app.haxmapper.textures.TextureWebCam;
import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.OpenGLUtil;

public class HaxMapperTextureTester 
extends PAppletHax {

	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	protected BaseTexture[] _textures;
	int w = 500;
	int h = 300;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 750 );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, true );
	}


	public void setup() {
		super.setup();
		
		w = Math.round((float) p.width / 4f);
		h = Math.round((float) w * (9f/16f));
		
		OpenGLUtil.setTextureRepeat(g);
		
		_textures = new BaseTexture[]{
//			new TextureShaderTimeStepper( w, h, "_drawing-stuff.glsl" ),
				
			new TextureWebCam( w, h ),
			new TextureEQFloatParticles( w, h ),
			
			new TextureShaderTimeStepper( w, h, "sdf-01-auto.glsl" ),
			new TextureShaderTimeStepper( w, h, "sdf-02-auto.glsl" ),
//			new TextureShaderTimeStepper( w, h, "sdf-02.glsl" ),
//			new TextureShaderTimeStepper( w, h, "morphing-bokeh-shape.glsl" ),
//			new TextureShaderTimeStepper( w, h, "bw-motion-illusion.glsl" ),
//			new TextureShaderTimeStepper( w, h, "bw-simple-sin.glsl" ),
//
//			new TextureShaderTimeStepper( w, h, "wobble-sin.glsl" ),
			new TextureShaderTimeStepper( w, h, "docking-tunnel.glsl" ),
//			new TextureShaderTimeStepper( w, h, "shiny-circle-wave.glsl" ),
//			new TextureShaderTimeStepper( w, h, "stars-nice.glsl" ),
//			new TextureShaderTimeStepper( w, h, "triangle-perlin.glsl" ),
//			new TextureShaderTimeStepper( w, h, "bw-circles.glsl" ),
//			new TextureShaderTimeStepper( w, h, "dot-grid-dof.glsl" ),
//			new TextureShaderTimeStepper( w, h, "flame-wisps.glsl" ),
//
//			new TextureShaderTimeStepper( w, h, "bubbles-iq.glsl" ),
//			new TextureShaderTimeStepper( w, h, "spinning-iq.glsl" ),
//			new TextureShaderTimeStepper( w, h, "inversion-iq.glsl" ),
//			new TextureShaderTimeStepper( w, h, "radial-waves.glsl" ),
//			new TextureShaderTimeStepper( w, h, "radial-burst.glsl" ),
//			new TextureShaderTimeStepper( w, h, "wavy-3d-tubes.glsl" ),
//			new TextureShaderTimeStepper( w, h, "fade-dots.glsl" ),
			new TextureBlobSheet( w, h ),
			new TextureOuterSphere( w, h ),
			new TextureAudioTube( w, h ),
			new TextureSvgPattern( w, h ),
			new TextureWaveformCircle( w, h ),
			new TextureRotatingRings( w, h ),
			new TextureLinesEQ( w, h ),
			new TextureBarsEQ( w, h ),
			new TextureEQConcentricCircles( w, h ),
			new TextureEQGrid( w, h ),
			new TextureWaveformSimple( w, h ),
			new TextureTwistingSquares( w, h ),
//			new TextureAppFrameEq2d( w, h ),
//			new TextureAppFrame2d( w, h ),
//			new TextureAppFrameWaveformCircle( w, h ),
//			new TextureBasicWindowShade( w, h ),
//			new TextureSphereAudioTextures( w, h ),
//			new TextureRotatorShape( w, h ),
//			new TextureMeshDeform( w, h ),
//		    new TextureColorAudioSlide( w, h ),
//		    new TextureSphereRotate( w, h ),
//		    new TextureImageTimeStepper( w, h ),
//		    new TextureShaderScrubber( w, h, "cog-tunnel.glsl" ),
//			new TextureVideoPlayer( w, h, "video/cacheflowe/render-2015-04-24-11-06-26-3x (Converted 2).mov" ),
//			new TextureVideoPlayer( w, h, "video/cacheflowe/render-2015-07-28-10-03-01-desktop.m4v" ),
//		    new TextureShaderTimeStepper( w, h, "cog-tunnel.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "space-swirl.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "matrix-rain.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "water-smoke.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "square-fade.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "gradient-line.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "stars-scroll.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "square-twist.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "hex-alphanumerics.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "bw-eye-jacker-02.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "bw-expand-loop.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "bw-clouds.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "circle-parts-rotate.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "warped-tunnel.glsl" ),
//		    new TextureShaderTimeStepper( w, h, "stars-fractal-field.glsl" ),
//			new TextureShaderTimeStepper( w, h, "morphing-bokeh-shape.glsl" ),
//			new TextureShaderTimeStepper( w, h, "basic-diagonal-stripes.glsl" ),
//			new TextureShaderTimeStepper( w, h, "basic-checker.glsl" ),
//			new TextureShaderTimeStepper( w, h, "water-smoke.glsl" ),
//			new TextureShaderTimeStepper( w, h, "flexi-spiral.glsl" ),
//			new TextureShaderTimeStepper( w, h, "noise-function.glsl" ),
//			new TextureShaderTimeStepper( w, h, "noise-simplex-2d-iq.glsl" ),
//			new TextureShaderTimeStepper( w, h, "light-leak.glsl" )
		};
		
		for(BaseTexture tex : _textures) {
			tex.setActive(true);
		}

		
//		_texture = new TextureWebCam();
	}

	public void drawApp() {
		background(40);
		
		if(p.frameCount % 30 == 0) {
			for(BaseTexture tex : _textures) {
				tex.updateTiming();
			}
		}
		if(p.frameCount % 120 == 0) {
			for(BaseTexture tex : _textures) {
				tex.updateTimingSection();
			}
			for(BaseTexture tex : _textures) {
				tex.setActive(false);
				tex.setActive(true);
			}
		}
		if(p.frameCount % 60 == 0) {
			for(BaseTexture tex : _textures) {
				tex.setColor(p.color(p.random(0, 255), p.random(0, 255), p.random(0, 255)));
			}
		}
		if(p.frameCount % 45 == 0) {
			for(BaseTexture tex : _textures) {
				tex.newLineMode();
			}
		}
		if(p.frameCount % 25 == 0) {
			for(BaseTexture tex : _textures) {
				tex.newMode();
			}
		}
		if(p.frameCount % 75 == 0) {
			for(BaseTexture tex : _textures) {
				tex.newRotation();
			}
		}
		
		int x = 0;
		int y = 0;
		for (int i = 0; i < _textures.length; i++) {
			BaseTexture tex = _textures[i];
			tex.update();
			p.image( tex.texture(), x, y );
			
			x += w;
			if(x + w > p.width) {
				x = 0;
				y += h;
			}
		}
		
//		float time = p.millis() / 10000f;
//		ColorDistortionFilter.instance(p).setTime(time);
//		ColorDistortionFilter.instance(p).setAmplitude(1.5f + 1.5f * P.sin(time/10f));
//		ColorDistortionFilter.instance(p).applyTo(p);
//		VignetteFilter.instance(p).applyTo(p);

	}
}