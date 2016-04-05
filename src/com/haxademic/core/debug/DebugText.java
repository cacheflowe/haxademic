package com.haxademic.core.debug;

import processing.core.PApplet;
import processing.core.PFont;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.util.DrawUtil;

public class DebugText {
	
	protected PApplet p;
	protected PFont _debugFont;	

	public DebugText( PApplet p ) {
		this.p = p;
		createFont();
	}
	
	protected void createFont() {
		p.textMode( P.SCREEN );
		_debugFont = p.createFont("Arial",12);
	}
	
	public void draw( String[] messages ) {
		DrawUtil.setDrawCorner(p);
		DrawUtil.setDrawFlat2d(p, true);
		p.textFont( _debugFont );
		p.fill(255);
		for (int i = 0; i < messages.length; i++) {
			p.text( messages[i], 10, 20 + i * 20 );
		}
		DrawUtil.setDrawFlat2d(p, false);
	}
}
