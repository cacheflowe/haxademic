package com.haxademic.app.haxmapper.textures;

import processing.video.Movie;

import com.haxademic.core.app.P;
import com.haxademic.core.image.filters.shaders.BlurHFilter;
import com.haxademic.core.image.filters.shaders.BlurVFilter;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.system.FileUtil;

public class TextureVideoPlayer
extends BaseTexture {

	protected Movie _movie;
	protected boolean _wasActive = false;

	public TextureVideoPlayer( int width, int height, String videoFile ) {
		super();
		
		_movie = new Movie( P.p, FileUtil.getHaxademicDataPath() + videoFile );
		_movie.speed(1f);
		_movie.play();
		_movie.loop();
		_movie.volume(0);
		_movie.pause();

		buildGraphics( width, height );
	}
	
	public BaseTexture setActive( boolean isActive ) {
		_wasActive = _active;
		super.setActive( isActive );
		_brightMode = MathUtil.randRange(0, 1);
		resetOnActiveChange();
		return this;
	}
	
	public void updateDraw() {
		_texture.image(_movie, 0, 0, _texture.width, _texture.height);
	}
	
	public void postProcess() {
		super.postProcess();
//		BlurHFilter.instance(P.p).setBlurByPercent(0.7f, _texture.width);
//		BlurHFilter.instance(P.p).applyTo(_texture);
//		BlurVFilter.instance(P.p).setBlurByPercent(0.7f, _texture.height);
//		BlurVFilter.instance(P.p).applyTo(_texture);
	}
	
	public void resetOnActiveChange() {
		if( _active == true && _wasActive == false ) {
			_movie.jump(MathUtil.randRangeDecimal(0, _movie.duration()));
			_movie.play();
			_movie.loop();
			_movie.volume(0);
		} else if( _active == false && _wasActive == true ) {
			_movie.pause();
		}
		_wasActive = _active;
	}

	public void updateTiming() {
		super.updateTiming();
//		_movie.speed(MathUtil.randRangeDecimal(-1.5f, 1.5f));
	}
	
	public void updateTimingSection() {
//		_movie.speed(MathUtil.randRangeDecimal(0.5f, 1.5f));
	}
	
	public void newLineMode() {

	}
	
	public void newRotation() {
	}

}
