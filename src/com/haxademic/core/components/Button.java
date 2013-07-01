package com.haxademic.core.components;

import java.awt.Rectangle;

import processing.core.PApplet;

import com.haxademic.core.app.P;

public class Button
implements IMouseable {
	 
	protected String _id;
	protected Rectangle _rect;
	protected Boolean _over;
	protected Boolean _pressed;
	
	public Button( String id, int x, int y, int w, int h ) {
		_id = id;
		_rect = new Rectangle( x, y, w, h );
		_over = false;
		_pressed = false;
	}
	
	public String id() {
		return _id;
	}
	
	public void update( PApplet p ) {
		p.hint( P.DISABLE_DEPTH_TEST );
		p.noStroke();
		if( _pressed == true ) {
			p.fill( 255, 255, 127 );
		} else if( _over == true ) {
			p.fill( 255, 255, 255 );
		} else {
			p.fill( 127, 255, 127);
		}
		p.rect( _rect.x, _rect.y, _rect.width, _rect.height );
		p.hint( P.ENABLE_DEPTH_TEST );
	}

	public Boolean checkPress( int mouseX, int mouseY ) {
		if( _rect.contains( mouseX,  mouseY ) ) {
			_pressed = true;
			return true;
		} else {
			_pressed = false;
			return false;
		}
	}
	
	public Boolean checkRelease( int mouseX, int mouseY ) {
		_pressed = false;
		if( _rect.contains( mouseX,  mouseY ) ) return true;
		return false;
	}
	
	public Boolean checkOver( int mouseX, int mouseY ) {
		if( _rect.contains( mouseX,  mouseY ) ) {
			if( _over != true ) mouseOver();
			_over = true;
			return true;
		} else {
			if( _over != false ) mouseOut();
			_over = false;
			return false;
		}
	}
	
	public void mouseOver() {
		//		P.println("over");
	}

	public void mouseOut() {
		//		P.println("out");
	}
}
