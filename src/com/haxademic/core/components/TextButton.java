package com.haxademic.core.components;

import processing.core.PApplet;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.text.CustomFontText2D;
import com.haxademic.core.system.FileUtil;

public class TextButton 
extends Button {

	protected String _text;
	protected CustomFontText2D _fontRenderer;
	protected float _fontSize = 20f;
	
	public TextButton( PApplet p, String text, String id, int x, int y, int w, int h ) {
		super( id, x, y, w, h );
		_text = text;
		_fontRenderer = new CustomFontText2D( p, FileUtil.getHaxademicDataPath() + "fonts/GothamBold.ttf", _fontSize, p.color( 255, 255, 255 ), CustomFontText2D.ALIGN_CENTER, _rect.width, (int)_fontSize );
		_fontRenderer.updateText( _text );
	}

	public void update( PApplet p ) {
		p.hint( P.DISABLE_DEPTH_TEST );
		p.noStroke();
		if( _pressed == true ) {
			p.fill( 60, 60, 60 );
		} else if( _over == true ) {
			p.fill( 80, 80, 80 );
		} else {
			p.fill( 120, 120, 120);
		}
		p.rect( _rect.x, _rect.y, _rect.width, _rect.height );
		p.image( _fontRenderer.getTextPImage(), _rect.x, _rect.y + _rect.height * 0.5f - _fontSize * 0.5f );
		p.hint( P.ENABLE_DEPTH_TEST );
	}
}
