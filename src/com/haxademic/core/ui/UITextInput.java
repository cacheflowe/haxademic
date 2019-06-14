package com.haxademic.core.ui;

import java.awt.Rectangle;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.text.FontCacher;

import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class UITextInput {
	
	public static UITextInput ACTIVE_INPUT = null;
	protected String _id;
	protected Rectangle _rect;
	protected boolean _over;
	protected boolean _pressed;
	protected boolean active = true;
	protected String _text;
	protected String fontFile;
	protected float _fontSize;
	protected boolean _focused;
	protected float _cursorX;
	protected float _cursorPadding;
	protected float _textY;
	protected float _caretY;
	protected int _padX;
	protected int _textColor;
	protected int _textWidth;

	public UITextInput(String id, int fontSize, String fontFile, int textColor, int padX, int align, int x, int y, int w, int h ) {
		_id = id;
		_textColor = textColor;
		this.fontFile = fontFile;
		_fontSize = fontSize;
		_padX = padX;
		_cursorPadding = Math.round( _fontSize / 6f ); 
		_rect = new Rectangle( x, y, w, h );
		_textY = _rect.y + _rect.height * 0.5f - _fontSize * 0.4f;
		_caretY = _rect.y + _rect.height * 0.5f - _fontSize * 0.5f;
		_over = false;
		_pressed = false;
		_focused = false;
		_cursorX = ( align == PTextAlign.LEFT ) ? _padX : _rect.width/2;
		_textWidth = _rect.width - ( _padX * 2 );
		_text = "";
		
		P.p.registerMethod("mouseEvent", this);
		P.p.registerMethod("keyEvent", this);
	}
	
	public String id() {
		return _id;
	}
	
	public String text() {
		return _text;
	}
	
	public int length() {
		return _text.length();
	}
	
	public void blur() {
		_focused = false;
	}
	
	public void focus() {
		_focused = true;
	}
	
	public void reset() {
		_text = "";
	}
	
	public void update( PGraphics pg ) {
		PG.setDrawFlat2d( pg, true );
		pg.noStroke();
		// draw input background
		if( _pressed == true || _focused == true ) {
			pg.fill( 60, 60, 60 );
		} else if( _over == true ) {
			pg.fill( 80, 80, 80 );
		} else {
			pg.fill( 120, 120, 120);
		}
		pg.rect( _rect.x, _rect.y, _rect.width, _rect.height );
		// draw text
//		p.image( _fontRenderer.getTextPImage(), _rect.x + _padX, _textY );
		
		PFont font = FontCacher.getFont(fontFile, _fontSize);
		FontCacher.setFontOnContext(pg, font, pg.color(255), 1f, PTextAlign.LEFT, PTextAlign.CENTER);
		pg.text(_text, _rect.x + _padX, _textY);

		// draw blinking cursor
		float textW = pg.textWidth(_text);
		_cursorX = textW + _cursorPadding + _padX;
		if( _focused == true ) {
			pg.fill( _textColor );
			if( P.p.millis() % 1000f > 500 ) pg.rect( _rect.x + _cursorX, _caretY, 2f, _fontSize );
		}
		PG.setDrawFlat2d( pg, false );
	}

	/////////////////////////////////////////
	// Mouse events
	/////////////////////////////////////////
	
	public void mouseEvent(MouseEvent event) {
		if(active == false) return;
		int mouseX = event.getX();
		int mouseY = event.getY();
		switch (event.getAction()) {
		case MouseEvent.PRESS:
			_pressed = _rect.contains(mouseX,  mouseY);
			// if no textinputs are clicked, clear out ACTIVE_INPUT
			break;
		case MouseEvent.RELEASE:
			_pressed = false;
			_focused = _rect.contains(mouseX, mouseY);
			if(_focused) ACTIVE_INPUT = this;
			break;
		case MouseEvent.MOVE:
			_over = _rect.contains(mouseX,  mouseY);
			break;
		case MouseEvent.DRAG:
			break;
		}
	}

	/////////////////////////////////////////
	// Keyboard events
	/////////////////////////////////////////
	
	public void keyEvent(KeyEvent e) {
		if(active == false) return;
		if(ACTIVE_INPUT != this) return;
		if(e.getAction() == KeyEvent.PRESS) {
			char key = e.getKey();
			if(key == PConstants.BACKSPACE) {
				
			} else if(key == PConstants.RETURN || key == PConstants.ENTER) {
				
			} else {
				_text += key;
				
			}
//			if(e.getKeyCode() == P.LEFT) { value -= dragStep; value = P.max(value, valueMin); }
//			if(e.getKeyCode() == P.RIGHT) { value += dragStep; value = P.min(value, valueMax); }
		}
	}

	/*
//			if( p.key == PConstants.BACKSPACE ) P.println("BACKSPACE");
//			if( p.key == PConstants.TAB ) P.println("TAB");
//			if( p.key == PConstants.RETURN ) P.println("RETURN");
//			if( p.key == PConstants.ENTER ) P.println("ENTER");
//			if( p.key == PConstants.BACKSPACE ) {
//				if( _activeTextInput != null ) {
//					_activeTextInput.backspace();
//				}
//			} else if( p.keyCode == P.TAB ) {
//
//				//		} else if( p.key == PConstants.TAB ) {
//				P.println("TAB!!!!");
//				if( _activeTextInput == _initialsInput ) {
//					_initialsInput.blur();
//					_emailInput.focus();
//					_activeTextInput = _emailInput;
//				} else if( _activeTextInput == _emailInput ) {
//					_emailInput.blur();
//					_initialsInput.focus();
//					_activeTextInput = _initialsInput;
//				}
//			} else {
//				if( _activeTextInput != null ) {
//					if( _activeTextInput.id() == "email" ) {
//						if( ValidateUtil.validateEmailCharacter( p.key+"" ) == true ) {
//							_activeTextInput.keyPressed( p.key+"" );
//						}
//					} else if( _activeTextInput.id() == "initials" ) {
//						if( _activeTextInput.length() < 3 && ValidateUtil.validateAlphanumericCharacter( p.key+"" ) == true ) {
//							_activeTextInput.keyPressed( (p.key+"").toUpperCase() );
//						}
//					} 
//				}
//			}
//		}

	 * 
	 * 
	public void keyPressed( String character ) {
		_text += character;
		_cursorX = _fontRenderer.getRightmostPixel() + _cursorPadding + _padX;
	}
	
	public void backspace() {
		if( _text.length() > 0 ) {
			_text = _text.substring( 0, _text.length() - 1 );
			_fontRenderer.updateText( _text );
			if( _text.length() > 0 ) {
				_cursorX = _fontRenderer.getRightmostPixel() + _cursorPadding + _padX;
			} else {
				_cursorX = ( _fontRenderer.textAlign() == CustomFontText2D.ALIGN_LEFT ) ? _padX : _rect.width/2;
			}
		}
	}
	*/
}
