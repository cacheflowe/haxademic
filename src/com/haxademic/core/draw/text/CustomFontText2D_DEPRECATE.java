package com.haxademic.core.draw.text;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

public class CustomFontText2D_DEPRECATE {
	
	public static final int ALIGN_LEFT = P.LEFT;
	public static final int ALIGN_CENTER = P.CENTER;
	public static final int ALIGN_RIGHT = P.RIGHT;
	
	protected PFont _font;
	protected float _fontSize;
	protected PGraphics _textCanvas;
	protected int _textColor;
	protected int _textStroke = -1;
	protected int _textAlign;
	protected int _textLeading = 0;

	public CustomFontText2D_DEPRECATE( PApplet p, String fontFile, float fontSize, int color, int align, int canvasW, int canvasH ) {
		_fontSize = fontSize;
		_textLeading = (int) fontSize;
		_textColor = color;
		_textStroke = p.color(255);
		_font = p.createFont( fontFile, _fontSize );
		_textAlign = align;
		_textCanvas = p.createGraphics( canvasW, canvasH, P.P2D ); // P.P2D? Whay does this cause issues in Catchy?
		_textCanvas.smooth( OpenGLUtil.SMOOTH_MEDIUM );
	}
	
	public void setTextColor( int color, int strokeColor ) {
		_textColor = color;
		_textStroke = strokeColor;
	}
	
	public void setTextAlign( int textAlign ) {
		_textAlign = textAlign;
	}
	
	public void setTextLeading( int textLeading ) {
		_textLeading = textLeading;
	}
	
	public int textAlign() {
		return _textAlign;
	}
	
	public void updateText( String txt ) {
		if( txt == null ) return;
		_textCanvas.beginDraw();
		_textCanvas.clear();
		_textCanvas.textAlign( _textAlign );
		_textCanvas.textFont( _font, _fontSize );
		_textCanvas.textLeading( _textLeading );

		if( _textStroke != -1 ) {
			_textCanvas.fill( _textStroke );
			_textCanvas.text( txt, 2, -5, _textCanvas.width, _textCanvas.height );
			_textCanvas.text( txt, 2, 1, _textCanvas.width, _textCanvas.height );
			_textCanvas.text( txt, -2, -5, _textCanvas.width, _textCanvas.height );
			_textCanvas.text( txt, -2, 1, _textCanvas.width, _textCanvas.height );
		}
		_textCanvas.fill( _textColor );
		_textCanvas.text( txt, 0, _textCanvas.height / 2f - _fontSize * 0.5f, _textCanvas.width, _textCanvas.height );
		_textCanvas.endDraw();
	}
	
	public PImage getTextPImage() {
		return _textCanvas;
	}
	
	public int getRightmostPixel() {
		int y = Math.round( (float) _textCanvas.height / 2f );
		int rightmost = 0;
		// check pixels across horizontal center to get a rough idea
		for( int i=0; i < _textCanvas.width; i++ ) {
			if( ImageUtil.getPixelColor( _textCanvas, i, y ) != ImageUtil.EMPTY_INT ) rightmost = i;
		}
		// then go down columns to find the real last pixel - allow for a few clear columns before calling it finished
		int columnsClear = 0;
		int rowSkip = Math.round( _textCanvas.height / 40f );
		for( int i = rightmost; i < _textCanvas.width; i++ ) {
			boolean columnClear = true;
			for( int j=0; j < _textCanvas.height; j+= rowSkip) {
				if( ImageUtil.getPixelColor( _textCanvas, i, j ) != ImageUtil.EMPTY_INT ) {
					rightmost = i;
					columnsClear = 0;
					columnClear = false;
					break;
				}
			}
			if( columnClear == true ) {
				columnsClear++;
				if( columnsClear > 3 ) break;
			}
		}		
		return rightmost;
	}

	public int getLeftmostPixel() {
		int y = Math.round( (float) _textCanvas.height / 2f );
		int leftMost = _textCanvas.width;
		// check pixels across horizontal center to get a rough idea
		for( int i=_textCanvas.width - 1; i > 0; i-- ) {
			if( ImageUtil.getPixelColor( _textCanvas, i, y ) != ImageUtil.EMPTY_INT ) leftMost = i;
		}
		// then go down columns to find the real last pixel - allow for a few clear columns before calling it finished
		int columnsClear = 0;
		int rowSkip = Math.round( _textCanvas.height / 40f );
		for( int i = leftMost; i > 0; i-- ) {
			boolean columnClear = true;
			for( int j=0; j < _textCanvas.height; j+= rowSkip) {
				if( ImageUtil.getPixelColor( _textCanvas, i, j ) != ImageUtil.EMPTY_INT ) {
					leftMost = i;
					columnsClear = 0;
					columnClear = false;
					break;
				}
			}
			if( columnClear == true ) {
				columnsClear++;
				if( columnsClear > 3 ) break;
			}
		}		
		return leftMost;
	}
	
}
