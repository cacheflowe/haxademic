package com.haxademic.sketch.three_d;


import processing.core.PApplet;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.TColorInit;

public class SunflowColorTest
extends PAppletHax  
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Auto-initialization of the main class.
	 * @param args
	 */
	public static void main(String args[]) {
		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", "com.haxademic.sketch.three_d.SunflowColorTest" });
	}
		
	public void setup() {
		super.setup();
		
		// Sunflow needs to use colors between 0-1. Default processing color mode is 0-255. 
		// TColor likes the former, so we normalize with TColorInit, depending on the rendering mode.
		// This way we can always use normal 0-255 RGB color blending in either case.
		if( _graphicsMode == P.OPENGL ) {
			p.colorMode( P.RGB, 1f, 1f, 1f, 1f );
		} else {
			p.colorMode( P.RGB, 255f, 255f, 255f, 255f );
		}
		
		// for sunflow, we need to set these before the first draw()
		p.background( 0 );
		p.smooth();
		p.noStroke();
	}
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "sunflow", "true" );
		_appConfig.setProperty( "rendering", "false" );
	}
		
	public void drawApp() {
//		DrawUtil.setBasicLights( p );
		// draw background and set to center
		if( _graphicsMode == P.OPENGL ) p.background(0,0,0,255);
		
		p.translate(p.width/2, p.height/2, -400);
		p.rotateY(p.frameCount/100f);
		
		p.noStroke();
		p.fill( TColorInit.newRGBA( 0, 200f, 234f, 255f ).toARGB() );
		
		p.box( 200f );
	}


}
