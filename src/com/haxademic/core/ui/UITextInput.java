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
	protected Rectangle rect;
	protected boolean over;
	protected boolean pressed;
	protected boolean active = true;
	protected String text;
	protected String fontFile;
	protected float fontSize;
	protected boolean focused;
	protected float cursorX;
	protected float cursorPadding;
	protected float textY;
	protected float caretY;
	protected int padX;
	protected int _textColor;
	protected int textWidth;

	public UITextInput(String id, int fontSize, String fontFile, int textColor, int padX, int align, int x, int y, int w, int h ) {
		this._id = id;
		this._textColor = textColor;
		this.fontFile = fontFile;
		this.fontSize = h * 0.5f;//fontSize;
		this.padX = padX;
		cursorPadding = Math.round( fontSize / 6f ); 
		rect = new Rectangle( x, y, w, h );
		textY = rect.y + rect.height * 0.5f - fontSize * 0.3f;
		caretY = rect.y + rect.height * 0.5f - fontSize * 0.5f;
		over = false;
		pressed = false;
		focused = false;
		cursorX = ( align == PTextAlign.LEFT ) ? padX : rect.width/2;
		textWidth = rect.width - ( padX * 2 );
		text = "";
		
		P.p.registerMethod("mouseEvent", this);
		P.p.registerMethod("keyEvent", this);
	}
	
	public String id() {
		return _id;
	}
	
	public String text() {
		return text;
	}
	
	public void text(String newText) {
		text = newText;
	}
	
	public void blur() {
		focused = false;
	}
	
	public void focus() {
		focused = true;
	}
	
	public void reset() {
		text = "";
	}
	
	public void update( PGraphics pg ) {
		PG.setDrawFlat2d( pg, true );
		pg.noStroke();
		// draw input background
		if( pressed == true || focused == true ) {
			pg.fill( 60, 60, 60 );
		} else if( over == true ) {
			pg.fill( 80, 80, 80 );
		} else {
			pg.fill( 120, 120, 120);
		}
		pg.rect( rect.x, rect.y, rect.width, rect.height );

		// set font on context
		PFont font = FontCacher.getFont(fontFile, fontSize);
		FontCacher.setFontOnContext(pg, font, pg.color(255), 1f, PTextAlign.LEFT, PTextAlign.CENTER);
		
		// get text width for cursor and to "scroll" text
		String displayText = text;
		float textW = pg.textWidth(displayText);
		int maxTextW = rect.width - padX * 2;
		while(textW > maxTextW) {
			displayText = displayText.substring(1);	// shift chars off the front of the text
			textW = pg.textWidth(displayText);
		}

		pg.text(displayText, rect.x + padX, rect.y - rect.height * 0.05f, rect.width, rect.height);

		// draw blinking cursor
		cursorX = rect.x + textW + cursorPadding + padX;
		if(focused == true) {
			pg.fill( _textColor );
			if( P.p.millis() % 1000f > 500 ) pg.rect( cursorX, rect.y + rect.height * 0.25f, 2f, fontSize );
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
			pressed = rect.contains(mouseX,  mouseY);
			// if no textinputs are clicked, clear out ACTIVE_INPUT
			break;
		case MouseEvent.RELEASE:
			pressed = false;
			focused = rect.contains(mouseX, mouseY);
			if(focused) ACTIVE_INPUT = this;
			break;
		case MouseEvent.MOVE:
			over = rect.contains(mouseX,  mouseY);
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
				if(text.length() > 0) {
					text = text.substring( 0, text.length() - 1 );
				}
			} else if(key == PConstants.RETURN || key == PConstants.ENTER || key == PConstants.SHIFT || key == PConstants.TAB) {
				
			} else {
//				if(ValidateUtil.alphanumericCharactersWithSpecialCharacters(key+"")) {
					text += key;
//				}
			}
		}
	}

}
