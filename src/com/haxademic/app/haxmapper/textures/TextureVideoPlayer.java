package com.haxademic.app.haxmapper.textures;

import processing.video.Movie;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.FileUtil;

public class TextureVideoPlayer
extends BaseTexture {

	protected Movie _movie;
	protected boolean _wasActive = false;

	public TextureVideoPlayer( int width, int height, String videoFile ) {
		super();
		
		_movie = new Movie( P.p, FileUtil.getHaxademicDataPath() + videoFile );
		_movie.volume(0);
		_movie.speed(1f);
		_movie.play();
		_movie.loop();
		_movie.pause();

		buildGraphics( width, height );
	}
	
	public void setActive( boolean isActive ) {
		_wasActive = _active;
		super.setActive( isActive );
		resetOnActiveChange();
	}
	
	public void updateDraw() {
		_texture.image(_movie, 0, 0, _texture.width, _texture.height);
	}
	
	public void resetOnActiveChange() {
		if( _active == true && _wasActive == false ) {
			_movie.jump(MathUtil.randRangeDecimal(0, _movie.duration()));
			_movie.volume(0);
			_movie.play();
			_movie.loop();
		} else if( _active == false && _wasActive == true ) {
			_movie.pause();
		}
		_wasActive = _active;
	}
}
