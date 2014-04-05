package com.haxademic.app.haxmapper.textures;

import processing.video.Movie;

import com.haxademic.core.app.P;
import com.haxademic.core.system.FileUtil;

public class TextureVideoPlayer
extends BaseTexture {

	Movie _movie;

	public TextureVideoPlayer( int width, int height, String videoFile ) {
		super();
		
		_movie = new Movie( P.p, FileUtil.getHaxademicDataPath() + videoFile );
		// _movie.play();
		_movie.loop();
		_movie.volume(0);
		_movie.speed(0.5f);

		buildGraphics( width, height );
	}
	
	public void setActive( boolean isActive ) {
		super.setActive( isActive );
		if( _active == true ) {
			_movie.play();
		} else {
			_movie.pause();
		}
	}
	
	public void update() {
		super.update();

		_texture.beginDraw();
		_texture.image(_movie, 0, 0, _texture.width, _texture.height);
		_texture.endDraw();
	}
}
