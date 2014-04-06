package com.haxademic.app.haxmapper;

import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.app.haxmapper.textures.TextureShaderTimeStepper;
import com.haxademic.core.app.PAppletHax;

@SuppressWarnings("serial")
public class HaxMapperTextureTester 
extends PAppletHax {
	
	protected BaseTexture _texture;

	protected void overridePropsFile() {
		_appConfig.setProperty( "fps", "60" );
		_appConfig.setProperty( "width", "1280" );
		_appConfig.setProperty( "height", "720" );
		_appConfig.setProperty( "rendering", "false" );
	}


	public void setup() {
		super.setup();	
		
//		_texture = new TextureWebCam();
//		_texture = new TextureEQColumns( 200, 100 );
		_texture = new TextureShaderTimeStepper( 400, 400, "dots-orbit.glsl" );
//		_texture = new TextureSphereRotate( 400, 400 );
	}

	public void drawApp() {
		background(90,110,90);
		if(p.frameCount % 30 == 0) _texture.updateTiming();
		_texture.update();
		p.image( _texture.texture(), 0, 0 );
	}
}