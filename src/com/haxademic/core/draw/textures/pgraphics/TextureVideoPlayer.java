package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import processing.video.Movie;

public class TextureVideoPlayer
extends BaseTexture {

	protected Movie _movie;
	protected boolean _wasActive = false;

	public TextureVideoPlayer( int width, int height, String videoFile ) {
		super(width, height);
		
		
		
		_movie = new Movie( P.p, FileUtil.haxademicDataPath() + videoFile );
	}
	
	public BaseTexture setActive( boolean isActive ) {
		_wasActive = _active;
		super.setActive( isActive );
		_brightMode = MathUtil.randRange(0, 1);
		resetOnActiveChange();
		return this;
	}
	
	public void draw() {
		ImageUtil.drawImageCropFill(_movie, pg, true);
//		_texture.image(_movie, 0, 0, width, height);
	}
	
	public void postProcess() {
		super.postProcess();
	}
	
	public void resetOnActiveChange() {
		if( _active == true && _wasActive == false ) {
			_movie.jump(MathUtil.randRangeDecimal(0, _movie.duration()));
			_movie.speed(1f);
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
