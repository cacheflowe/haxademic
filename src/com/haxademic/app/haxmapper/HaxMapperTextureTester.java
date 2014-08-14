package com.haxademic.app.haxmapper;

import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.app.haxmapper.textures.TextureColorAudioSlide;
import com.haxademic.app.haxmapper.textures.TextureEQConcentricCircles;
import com.haxademic.app.haxmapper.textures.TextureShaderTimeStepper;
import com.haxademic.app.haxmapper.textures.TextureSphereRotate;
import com.haxademic.core.app.PAppletHax;

@SuppressWarnings("serial")
public class HaxMapperTextureTester 
extends PAppletHax {
	
	protected BaseTexture _texture;
	protected BaseTexture _texture2;
	protected BaseTexture _texture3;

	protected void overridePropsFile() {
		_appConfig.setProperty( "fps", "30" );
		_appConfig.setProperty( "width", "1280" );
		_appConfig.setProperty( "height", "720" );
		_appConfig.setProperty( "rendering", "false" );
	}


	public void setup() {
		super.setup();	
		
//		_texture = new TextureWebCam();
//		_texture = new TextureTwistingSquares( 200, 200 );
		_texture = new TextureEQConcentricCircles( 200, 200 );
//		_texture2 = new TextureEQGrid( 200, 200 );
//		_texture3 = new TextureWaveformSimple( 400, 400 );
		_texture3 = new TextureColorAudioSlide( 400, 400 );
//		_texture2 = new TextureSphereRotate( 500, 500 );
//		_texture2 = new TextureShaderTimeStepper( 300, 300, "cog-tunnel.glsl" );
//		_texture2 = new TextureShaderTimeStepper( 500, 300, "space-swirl.glsl" );
//		_texture2 = new TextureShaderTimeStepper( 500, 300, "matrix-rain.glsl" );
//		_texture2 = new TextureShaderTimeStepper( 500, 300, "water-smoke.glsl" );
//		_texture2 = new TextureShaderTimeStepper( 500, 300, "stars-screensaver.glsl" );
//		_texture2 = new TextureShaderTimeStepper( 500, 300, "square-fade.glsl" );
//		_texture2 = new TextureShaderTimeStepper( 500, 300, "gradient-line.glsl" );
//		_texture2 = new TextureShaderTimeStepper( 500, 300, "stars-scroll.glsl" );
//		_texture2 = new TextureShaderTimeStepper( 400, 400, "square-twist.glsl" );
//		_texture2 = new TextureShaderTimeStepper( 400, 400, "hex-alphanumerics.glsl" );
//		_texture2 = new TextureShaderTimeStepper( 300, 300, "bw-eye-jacker-02.glsl" );
//		_texture2 = new TextureShaderTimeStepper( 300, 300, "bw-expand-loop.glsl" );
//		_texture2 = new TextureShaderTimeStepper( 300, 300, "bw-clouds.glsl" );
//		_texture2 = new TextureShaderTimeStepper( 300, 300, "stars-fractal-field.glsl" );
//		_texture2 = new TextureShaderTimeStepper( 300, 300, "circle-parts-rotate.glsl" );
//		_texture2 = new TextureShaderTimeStepper( 300, 300, "warped-tunnel.glsl" );
//		_texture2 = new TextureShaderTimeStepper( 300, 300, "supershape-2d.glsl" );
//		_texture = new TextureImageTimeStepper( 600, 600 );
//		_texture = new TextureSphereRotate( 400, 400 );
	}

	public void drawApp() {
		background(90,110,90);
		if(p.frameCount % 30 == 0) {
			_texture.updateTiming();
			_texture2.updateTiming();
			_texture3.updateTiming();
		}
		_texture.update();
		_texture2.update();
		_texture3.update();
		p.image( _texture.texture(), 0, 0 );
		p.image( _texture2.texture(), 0, 400 );
		p.image( _texture3.texture(), p.width - 400, 400 );
	}
}