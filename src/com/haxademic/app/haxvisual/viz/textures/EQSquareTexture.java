package com.haxademic.app.haxvisual.viz.textures;

import processing.core.PImage;
import toxi.color.TColor;

import com.haxademic.app.haxvisual.viz.IAudioTexture;
import com.haxademic.core.app.P;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.color.TColorBlendBetween;

public class EQSquareTexture
implements IAudioTexture
{
	
//	protected PGraphics _graphics;
	protected PImage _image;
	protected int _width, _height;
	protected TColorBlendBetween _color;
	
	public EQSquareTexture( int width, int height ) {
//		DebugUtil.printErr("EQSquareTexture: Fix performance issues by converting PGraphics to PImage");
		_width = width;
		_height = height;
//		_graphics = P.p.createGraphics( _width, _height, P.P3D );
		_image = new PImage( _width, _height, P.ARGB );
		_color = new TColorBlendBetween( TColor.BLACK.copy(), TColor.BLACK.copy() );
	}
	
	public void updateTexture( AudioInputWrapper audioInput ) {
		float eqVal;
		int eqStep = Math.round( 512f / (float) _width );
//		_graphics.background( 0 );
		for( int i=0; i < _width; i++ ) {
			for( int j=0; j < _height; j++ ) {
				eqVal = audioInput.getFFT().spectrum[ ( i * eqStep ) % 512 ];
//				_graphics.beginDraw();
//				_graphics.stroke( _color.argbWithPercent( eqVal ) );
//				_graphics.line(i, 0, i, _height * eqVal );
//				_graphics.endDraw();
				
				if( j < _height * eqVal )
					_image.set( i, j, _color.argbWithPercent( eqVal ) );
				else
					_image.set( i, j, P.p.color( 0 ) ); // , 80
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

	public void pause() {
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
