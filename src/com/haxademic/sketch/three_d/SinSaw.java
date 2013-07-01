package com.haxademic.sketch.three_d;

import processing.core.PConstants;
import processing.core.PGraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.math.MathUtil;

public class SinSaw
extends PAppletHax  
{	
	protected PGraphics _texture;	
	
	public void setup() {
		super.setup();
		_texture = P.p.createGraphics( 520, 120, P.P3D );
		_texture.background(255);

	}
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "width", "520" );
		_appConfig.setProperty( "height", "120" );
	}
		
	public void drawApp() {
		p.background(0);
		p.fill( 255 );
		p.noStroke();
		p.rectMode( PConstants.CENTER );
		_texture.rectMode( PConstants.CENTER );

		DrawUtil.setColorForPImage(p);
		p.image(_texture, 0, 0);
		
		_texture.beginDraw();
		_texture.fill( 255, 255, 255, 255 );
		_texture.noStroke();

		float incrementer = p.frameCount / 10f;
		
		// draw sin
		fill(255, 0, 0);
		float sin = P.sin(incrementer);
		rect(250, 50 + 20 * sin, 20, 20);
		_texture.rect(250, 50 + 20 * sin, 20, 20);
		
		// draw saw
		fill(0, 255, 0);
		float saw = MathUtil.saw(incrementer);
		rect(350, 50 + 20 * saw, 20, 20);
		_texture.rect(350, 50 + 20 * saw, 20, 20);
		
		// draw saw tan
		fill(0, 0, 255);
		float sawtan = MathUtil.sawTan(incrementer);
		rect(450, 50 + 20 * sawtan, 20, 20);
		_texture.rect(450, 50 + 20 * sawtan, 20, 20);
		
		_texture.endDraw();
		_texture.copy(_texture, 0, 0, _texture.width, _texture.height, -2, 0, _texture.width, _texture.height);
	}
}
