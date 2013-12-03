package com.haxademic.app.haxvisual.viz.textures;

import processing.core.PImage;
import toxi.color.TColor;

import com.haxademic.app.haxvisual.viz.IAudioTexture;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.color.TColorBlendBetween;

public class ColumnAudioTexture
implements IAudioTexture
{
	
	protected AudioInputWrapper _audioInput;
	protected PImage _image;
	protected int _rows;
	protected TColorBlendBetween _color;
	
	public ColumnAudioTexture( int numRows ) {
		_rows = numRows;
		_image = new PImage( 1, _rows );
		_color = new TColorBlendBetween( TColor.BLACK.copy(), TColor.BLACK.copy() );
	}
	
	public void updateTexture( AudioInputWrapper audioInput ) {
		int eqStep = Math.round( 512f / (float) _rows );
		for( int i=0; i < _rows; i++ ) {
			_image.set( 0, i, _color.argbWithPercent( audioInput.getFFT().spectrum[ ( i * eqStep ) % 512 ] ) );
		}
	}
	
	public PImage getTexture() {
		return _image;
	}
	
	public void updateColorSet( ColorGroup colors ) {
		_color.setColors( TColor.BLACK.copy(), colors.getRandomColor() );
		_color.lightenColor( 0.3f );
	}
	
	public void dispose() {
		
	}

	public void init() {
		// TODO Auto-generated method stub
	}

	public void update() {
		// TODO Auto-generated method stub
	}

	public void reset() {
		// TODO Auto-generated method stub
	}

	public void updateLineMode() {
		// TODO Auto-generated method stub
	}

	public void updateCamera() {
		// TODO Auto-generated method stub
	}
	
	public void updateTiming() {
		// TODO Auto-generated method stub
	}
	
	public void updateSection() {
		// TODO Auto-generated method stub
	}
}
