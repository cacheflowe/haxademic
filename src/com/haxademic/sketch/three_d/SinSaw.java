package com.haxademic.sketch.three_d;

import processing.core.PConstants;
import processing.core.PGraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.MathUtil;

public class SinSaw
extends PAppletHax  
{	
	protected PGraphics _texture;	
	
	public void setup() {
		super.setup();
		_texture = P.p.createGraphics( 520, 120, P.P3D );
		_texture.background(255);
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_texture.smooth(OpenGLUtil.SMOOTH_HIGH);
	}
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "width", "520" );
		_appConfig.setProperty( "height", "120" );
		_appConfig.setProperty( "fps", "60" );
	}
		
	public void drawApp() {
		p.background(0);
		p.fill( 255 );
		p.noStroke();
		p.rectMode( PConstants.CENTER );
		p.ellipseMode( PConstants.CENTER );
		_texture.rectMode( PConstants.CENTER );
		_texture.ellipseMode( PConstants.CENTER );

		DrawUtil.setColorForPImage(p);
		p.image(_texture, 0, 0);
		
		_texture.beginDraw();
		_texture.fill( 255, 255, 255, 255 );
		_texture.noStroke();
		
		_texture.fill( 0, 7f );
		_texture.rect(p.width/2f, p.height/2f, p.width, p.height);

		float incrementer = p.frameCount / 20f;
		float halfH = p.height/2f;
		
		// draw sin
		fill(255, 255, 0);
		_texture.fill(255, 255, 0);
		float sin = P.sin(incrementer);
		ellipse(250, halfH + 20 * sin, 20, 20);
		_texture.ellipse(250, halfH + 20 * sin, 20, 20);
		
		// draw saw
		fill(255, 0, 255);
		_texture.fill(255, 0, 255);
		float saw = MathUtil.saw(incrementer);
		ellipse(350, halfH + 20 * saw, 20, 20);
		_texture.ellipse(350, halfH + 20 * saw, 20, 20);
		
		// draw saw tan
		fill(0, 255, 255);
		_texture.fill(0, 255, 255);
		float sawtan = MathUtil.sawTan(incrementer * 2f);
		ellipse(450, halfH + 20 * sawtan, 20, 20);
		_texture.ellipse(450, halfH + 20 * sawtan, 20, 20);
		
		_texture.endDraw();
		_texture.copy(_texture, 0, 0, _texture.width, _texture.height, -1, 0, _texture.width, _texture.height);
	}
}
