package com.haxademic.app.haxmapper;

import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.app.haxmapper.textures.TextureColorAudioSlide;
import com.haxademic.app.haxmapper.textures.TextureTwistingSquares;
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
		_texture = new TextureTwistingSquares( 200, 200 );
//		_texture2 = new TextureEQGrid( 200, 200 );
//		_texture3 = new TextureWaveformSimple( 400, 400 );
		_texture3 = new TextureColorAudioSlide( 400, 400 );
		_texture2 = new TextureSphereRotate( 500, 500 );
//		_texture2 = new TextureShaderTimeStepper( 300, 300, "bw-kaleido.glsl" );
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