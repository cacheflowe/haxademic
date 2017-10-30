package com.haxademic.sketch.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PConstants;
import processing.core.PGraphics;

public class SinSaw
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PGraphics _texture;	
	
	public void setup() {
		super.setup();
		
		_texture = P.p.createGraphics( 520, 120, P.P3D );
		_texture.smooth(OpenGLUtil.SMOOTH_HIGH);
		_texture.beginDraw();
		_texture.background(0);
		_texture.endDraw();
	}
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.WIDTH, "520" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "120" );
	}
		
	public void drawApp() {
		p.background(0);

		// texture feedback
		// _texture.copy(_texture, 0, 0, _texture.width, _texture.height, -1.5f, 0, _texture.width, _texture.height);
//		_texture.copy(_texture, 0, 0, _texture.width, _texture.height, -2, -2, _texture.width, _texture.height + 4);

		// start texture drawing
		_texture.beginDraw();
		_texture.rectMode( PConstants.CENTER );
		_texture.ellipseMode( PConstants.CENTER );
		_texture.fill( 255, 255, 255, 255 );
		_texture.noStroke();
		
		// fade out
		_texture.fill( 0, 7f );
		_texture.rect(p.width/2f, p.height/2f, p.width, p.height);

		// increment oscillations
		float incrementer = p.frameCount / 20f;
		float halfH = p.height/2f;
		
		// draw sin
		float sin = P.sin(incrementer);
		_texture.fill(255, 255, 0);
		_texture.ellipse(250, halfH + 20 * sin, 20, 20);
		
		// draw saw
		float saw = MathUtil.saw(incrementer);
		_texture.fill(255, 0, 255);
		_texture.ellipse(350, halfH + 20 * saw, 20, 20);
		
		// draw saw tan
		float sawtan = MathUtil.sawTan(incrementer * 2f);
		_texture.fill(0, 255, 255);
		_texture.ellipse(450, halfH + 20 * sawtan, 20, 20);
		
		// finish drawing
		_texture.endDraw();
		
		// draw texture to stage
		DrawUtil.setColorForPImage(p);
		p.image(_texture, 0, 0);
	}
}
