package com.haxademic.core.ui;

import java.awt.Rectangle;

import processing.core.PApplet;

public class TextInput
//implements IMouseable 
{
	 
	protected String _id;
	protected Rectangle _rect;
	protected Boolean _over;
	protected Boolean _pressed;
	
	protected String _text;
//	protected CustomFontText2D _fontRenderer;
	protected float _fontSize;
	protected Boolean _focused;
	protected float _cursorX;
	protected float _cursorPadding;
	protected float _textY;
	protected float _caretY;
	protected int _padX;
	protected int _textColor;
	protected int _textWidth;

	public TextInput( PApplet p, String id, int fontSize, String fontFile, int textColor, int padX, int align, int x, int y, int w, int h ) {
		_id = id;
		_textColor = textColor;
		_fontSize = fontSize;
		_padX = padX;
		_cursorPadding = Math.round( _fontSize / 6f ); 
		_rect = new Rectangle( x, y, w, h );
		_textY = _rect.y + _rect.height * 0.5f - _fontSize * 0.4f;
		_caretY = _rect.y + _rect.height * 0.5f - _fontSize * 0.5f;
		_over = false;
		_pressed = false;
		_focused = false;
//		_cursorX = ( align == CustomFontText2D.ALIGN_LEFT ) ? _padX : _rect.width/2;
//		_textWidth = _rect.width - ( _padX * 2 );
//		_fontRenderer = new CustomFontText2D( p, fontFile, _fontSize, _textColor, align, _textWidth, (int)_fontSize + 10 );
//		_text = "";
//		_fontRenderer.updateText( _text );
	}
	
//	public String id() {
//		return _id;
//	}
//	
//	public String text() {
//		return _text;
//	}
//	
//	public int length() {
//		return _text.length();
//	}
//	
//	public void blur() {
//		_focused = false;
//	}
//	
//	public void focus() {
//		_focused = true;
//	}
//	
//	public void reset() {
//		_text = "";
//		_fontRenderer.updateText( _text );
//	}
//	
//	public void update( PApplet p ) {
//		PG.setDrawFlat2d( p, true );
//		p.noStroke();
//		// draw input background
//		if( _pressed == true || _focused == true ) {
//			p.fill( 60, 60, 60 );
//		} else if( _over == true ) {
//			p.fill( 80, 80, 80 );
//		} else {
//			p.fill( 120, 120, 120);
//		}
//		p.rect( _rect.x, _rect.y, _rect.width, _rect.height );
//		// draw text
//		p.image( _fontRenderer.getTextPImage(), _rect.x + _padX, _textY );
//		// draw cursor
//		if( _focused == true ) {
//			p.fill( _textColor );
//			if( p.millis() % 1000f > 500 ) p.rect( _rect.x + _cursorX, _caretY, 2f, _fontSize );
//		}
//		PG.setDrawFlat2d( p, false );
//	}
//
//	public Boolean checkPress( int mouseX, int mouseY ) {
//		if( _rect.contains( mouseX,  mouseY ) ) {
//			_pressed = true;
//			return true;
//		} else {
//			_pressed = false;
//			return false;
//		}
//	}
//	
//	public Boolean checkRelease( int mouseX, int mouseY ) {
//		_pressed = false;
//		_focused = false;
//		if( _rect.contains( mouseX,  mouseY ) ) {
//			_focused = true;
//			return true;
//		} else {
//			_focused = false;
//			return false;
//		}
//	}
//	
//	public Boolean checkOver( int mouseX, int mouseY ) {
//		if( _rect.contains( mouseX,  mouseY ) ) {
//			_over = true;
//			return true;
//		} else {
//			_over = false;
//			return false;
//		}
//	}
//	
//	public void keyPressed( String character ) {
//		_text += character;
//		_fontRenderer.updateText( _text );
//		_cursorX = _fontRenderer.getRightmostPixel() + _cursorPadding + _padX;
//	}
//	
//	public void backspace() {
//		if( _text.length() > 0 ) {
//			_text = _text.substring( 0, _text.length() - 1 );
//			_fontRenderer.updateText( _text );
//			if( _text.length() > 0 ) {
//				_cursorX = _fontRenderer.getRightmostPixel() + _cursorPadding + _padX;
//			} else {
//				_cursorX = ( _fontRenderer.textAlign() == CustomFontText2D.ALIGN_LEFT ) ? _padX : _rect.width/2;
//			}
//		}
//	}
}
