package com.haxademic.core.draw.text;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.image.ImageUtil;

public class CustomFontText2D {
	
	public static final int ALIGN_LEFT = P.LEFT;
	public static final int ALIGN_CENTER = P.CENTER;
	public static final int ALIGN_RIGHT = P.RIGHT;
	
	protected PFont _font;
	protected float _fontSize;
	protected PGraphics _textCanvas;
	protected int _textColor;
	protected int _textAlign;
	protected int _textLeading = 0;

	public CustomFontText2D( PApplet p, String fontFile, float fontSize, int color, int align, int canvasW, int canvasH ) {
		_fontSize = fontSize;
		_textLeading = (int) fontSize;
		_textColor = color;
		_font = p.createFont( fontFile, _fontSize );
		_textAlign = align;
		_textCanvas = p.createGraphics( canvasW, canvasH, P.JAVA2D );
		_textCanvas.smooth( OpenGLUtil.SMOOTH_HIGH );
	}
	
	public void setTextColor( int color ) {
		_textColor = color;
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
		_textCanvas.background( 0, 0 );		// clear background with alpha = 0 (only works in PGraphics)
		_textCanvas.fill( _textColor );
		_textCanvas.textAlign( _textAlign );
		_textCanvas.textFont( _font, _fontSize );
		_textCanvas.textLeading( _textLeading );
		_textCanvas.text( txt, 1, 1, _textCanvas.width, _textCanvas.height );
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
		for( int i = rightmost; i < _textCanvas.width; i++ ) {
			boolean columnClear = true;
			for( int j=0; j < _textCanvas.height; j++ ) {
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

}
