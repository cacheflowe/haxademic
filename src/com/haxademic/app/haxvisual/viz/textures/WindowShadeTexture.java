package com.haxademic.app.haxvisual.viz.textures;

import com.haxademic.app.haxvisual.viz.IAudioTexture;
import com.haxademic.core.app.P;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.draw.color.ColorGroup;

import processing.core.PGraphics;
import processing.core.PImage;

public class WindowShadeTexture
implements IAudioTexture
{
	
	protected PGraphics _graphics;
	protected int _width, _height;
	
	public WindowShadeTexture( int width, int height ) {
		DebugUtil.printErr("WindowShadeTexture: Fix performance issues by converting PGraphics to PImage");
		_width = width;
		_height = height;
		_graphics = P.p.createGraphics( _width, _height, P.P3D );
	}
	
	public void updateTexture( AudioInputWrapper audioInput ) {
		float color;
		float alpha;
		float eqVal;
		int eqStep = Math.round( 512f / (float) _width );
		_graphics.background( 0 );
		_graphics.rectMode( P.CENTER );
		_graphics.noStroke();
		_graphics.noSmooth();
		for( int i=0; i < _width; i++ ) {
			eqVal = audioInput.getFFT().spectrum[ ( i * eqStep ) % 512 ];
			color = eqVal * 255f;
			alpha = 1;//eqVal;
			
			_graphics.beginDraw();
			_graphics.fill( P.p.color( color, alpha ) );
			_graphics.rect( i, _height/2, 1, _height * eqVal);
			_graphics.endDraw();
		}
	}
	
	public PImage getTexture() {
		return _graphics;
	}
	
	public void dispose() {
		_graphics.resize(0,0);
		_graphics = null;
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

	public void updateColorSet(ColorGroup colors) {
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
