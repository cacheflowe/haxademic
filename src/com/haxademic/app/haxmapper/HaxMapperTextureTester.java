package com.haxademic.app.haxmapper;

import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.app.haxmapper.textures.TextureAppFrame2d;
import com.haxademic.app.haxmapper.textures.TextureAppFrameEq2d;
import com.haxademic.app.haxmapper.textures.TextureAppFrameWaveformCircle;
import com.haxademic.app.haxmapper.textures.TextureAudioTube;
import com.haxademic.app.haxmapper.textures.TextureBasicWindowShade;
import com.haxademic.app.haxmapper.textures.TextureShaderTimeStepper;
import com.haxademic.app.haxmapper.textures.TextureSvgPattern;
import com.haxademic.core.app.PAppletHax;

@SuppressWarnings("serial")
public class HaxMapperTextureTester 
extends PAppletHax {
	
	protected BaseTexture[] _textures;
	int w = 320;
	int h = 240;

	protected void overridePropsFile() {
		_appConfig.setProperty( "fps", "30" );
		_appConfig.setProperty( "width", "1280" );
		_appConfig.setProperty( "height", "960" );
		_appConfig.setProperty( "rendering", "false" );
	}


	public void setup() {
		super.setup();
		
		_textures = new BaseTexture[]{
			new TextureAudioTube( w, h ),
			new TextureSvgPattern( w, h ),
			new TextureAppFrameEq2d( w, h ),
			new TextureAppFrame2d( w, h ),
			new TextureAppFrameWaveformCircle( w, h ),
			new TextureBasicWindowShade( w, h ),
//			new TextureSphereAudioTextures( w, h ),
//			new TextureWaveformCircle( w, h ),
//			new TextureRotatorShape( w, h ),
//			new TextureRotatingRings( w, h ),
//			new TextureMeshDeform( w, h ),
//			new TextureLinesEQ( w, h ),
//			new TextureBlobSheet( w, h ),
//			new TextureBarsEQ( w, h ),
//		    new TextureEQConcentricCircles( w, h ),
//		    new TextureColorAudioSlide( w, h ),
//		    new TextureOuterSphere( w, h ),
//		    new TextureEQGrid( w, h ),
//		    new TextureWaveformSimple( w, h ),
//		    new TextureSphereRotate( w, h ),
//		    new TextureTwistingSquares( w, h ),
//		    new TextureImageTimeStepper( w, h ),
//		    new TextureShaderScrubber( w, h, "cog-tunnel.glsl" ),
//			new TextureVideoPlayer( w, h, "video/loops/water.mp4" ),
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
		    new TextureShaderTimeStepper( w, h, "circle-parts-rotate.glsl" ),
		    new TextureShaderTimeStepper( w, h, "warped-tunnel.glsl" ),
		    new TextureShaderTimeStepper( w, h, "stars-fractal-field.glsl" ),
			new TextureShaderTimeStepper( w, h, "morphing-bokeh-shape.glsl" ),
			new TextureShaderTimeStepper( w, h, "basic-diagonal-stripes.glsl" ),
			new TextureShaderTimeStepper( w, h, "basic-checker.glsl" ),
			new TextureShaderTimeStepper( w, h, "water-smoke.glsl" ),
			new TextureShaderTimeStepper( w, h, "flexi-spiral.glsl" ),
			new TextureShaderTimeStepper( w, h, "light-leak.glsl" )
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
	}
}