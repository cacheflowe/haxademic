package com.haxademic.sketch.test;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;

import toxi.color.TColor;

public class DrawPieCursorsSmooth
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected TColor GREEN;
	protected TColor WHITE;
	
	protected float CURSOR_PIE_DIAMETER = 34;
	protected float CURSOR_RING_DIAMETER = 44;
	protected float CURSOR_OUTER_DIAMETER = 54;
	protected float CURSOR_EASING_FACTOR = 7;
	protected float CURSOR_STROKE = 4;
	
	public void setup() {
		super.setup();	
		GREEN = TColor.newHex("00b159");
		WHITE = TColor.WHITE.copy();
		p.smooth( OpenGLUtil.SMOOTH_HIGH );
	}

	public void drawApp() {
		background(0);
		
		DrawUtil.setColorForPImage( p );
		DrawUtil.resetPImageAlpha( p );
		DrawUtil.setPImageAlpha(p, 1f);

		float timePercent = ( p.millis() / 1000f ) % 1;

		p.noStroke();
		
		// draw cursor background
		p.fill( GREEN.toARGB() );
		p.arc( 100, 100, CURSOR_OUTER_DIAMETER, CURSOR_OUTER_DIAMETER, 0, (float) P.TWO_PI, P.CHORD );
		p.arc( 200, 150, CURSOR_OUTER_DIAMETER, CURSOR_OUTER_DIAMETER, 0, (float) P.TWO_PI, P.CHORD );

		// draw cursor pie percentage
		p.fill( WHITE.toARGB() );
		p.arc( 100, 100, CURSOR_PIE_DIAMETER, CURSOR_PIE_DIAMETER, 0, timePercent * (float) P.TWO_PI, P.PIE );
		p.arc( 200, 150, CURSOR_PIE_DIAMETER, CURSOR_PIE_DIAMETER, 0, timePercent * (float) P.TWO_PI, P.PIE );
		
		// always draw outer cursor circle
		p.noFill();
		p.stroke( WHITE.toARGB() );
		p.strokeWeight( CURSOR_STROKE );
		p.arc( 100, 100, CURSOR_RING_DIAMETER, CURSOR_RING_DIAMETER, 0, (float) P.TWO_PI, P.CHORD );
		p.arc( 200, 150, CURSOR_RING_DIAMETER, CURSOR_RING_DIAMETER, 0, (float) P.TWO_PI, P.CHORD );
	}

}
