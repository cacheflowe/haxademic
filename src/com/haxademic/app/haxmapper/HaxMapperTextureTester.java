package com.haxademic.app.haxmapper;

import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.app.haxmapper.textures.TextureEQColumns;
import com.haxademic.app.haxmapper.textures.TextureWebCam;
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
		
		_texture = new TextureWebCam();
//		_texture = new TextureEQColumns( 200, 100 );
	}

	public void drawApp() {
		background(0,127,0);
		_texture.update();
		p.image( _texture.texture(), 0, 0 );
	}
}