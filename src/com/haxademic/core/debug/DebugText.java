package com.haxademic.core.debug;

import processing.core.PApplet;
import processing.core.PFont;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.util.DrawUtil;

public class DebugText {
	
	protected PApplet p;

	/**
	 * Load a font for debug help at the moment. 
	 */
	protected PFont _debugFont;	

	public DebugText( PApplet p5 ) {
		p = p5;
		createFont();
	}
	
	protected void createFont() {
		p.textMode( P.SCREEN );
		_debugFont = p.createFont("Arial",30);
	}
	
	public void draw( String message ) {
		DrawUtil.setDrawFlat2d(p, true);
		p.textFont( _debugFont );
		p.fill(255);
		p.text( message, 10, 10 );
		DrawUtil.setDrawFlat2d(p, false);
	}
}
