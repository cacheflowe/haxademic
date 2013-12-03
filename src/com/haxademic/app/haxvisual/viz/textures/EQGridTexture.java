package com.haxademic.app.haxvisual.viz.textures;

import processing.core.PImage;
import toxi.color.TColor;

import com.haxademic.app.haxvisual.viz.IAudioTexture;
import com.haxademic.core.app.P;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.color.TColorBlendBetween;

public class EQGridTexture
implements IAudioTexture
{
	
//	protected PGraphics _graphics;
	protected PImage _image;
	protected int _width, _height;
	protected int _cols, _rows;
	protected int _colW, _rowH;
	
	protected TColorBlendBetween _color;

	public EQGridTexture( int width, int height ) {
		_width = 32; //width;
		_height = 32; //height;
		_cols = 32;
		_rows = 32;
		_colW = _width / _cols;
		_rowH = _height / _rows;
//		_graphics = P.p.createGraphics( _width, _height, P.P3D );
		_image = new PImage( _width, _height, P.ARGB );
		_color = new TColorBlendBetween( TColor.BLACK.copy(), TColor.BLACK.copy() );
		
		/*
		for( int i=0; i < _rows; i++ ) {
			_image.set( 0, i, _color.argbWithPercent( audioInput.getFFT().spectrum[ ( i * eqStep ) % 512 ] ) );
		}

		 */
	}
	
	public void updateTexture( AudioInputWrapper audioInput ) {
		int eqStep = Math.round( 512f / (float) ( _cols * _rows ) );
//		_graphics.background( 0 );
//		_graphics.noStroke();
		int index = 0;
		for( int i=0; i < _cols; i++ ) {
			for( int j=0; j < _rows; j++ ) {
//				_graphics.beginDraw();
//				_graphics.fill( _color.argbWithPercent( audioInput.getFFT().spectrum[ ( index * eqStep ) % 512 ] ) );
//				_graphics.rect( i*_colW, j*_rowH, _colW, _rowH );
//				_graphics.endDraw();
				
				_image.set( i, j, _color.argbWithPercent( audioInput.getFFT().spectrum[ ( index * eqStep ) % 512 ] ) );
				
				index++;
			}
		}
	}
	
	public PImage getTexture() {
		return _image;
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

	public void updateColorSet( ColorGroup colors ) {
		_color.setColors( TColor.BLACK.copy(), colors.getRandomColor() );
		_color.lightenColor( 0.3f );
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
