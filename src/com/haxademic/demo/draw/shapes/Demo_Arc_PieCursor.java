package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.context.PG;

public class Demo_Arc_PieCursor
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected EasingColor GREEN;
	protected EasingColor WHITE;
	
	protected float CURSOR_PIE_DIAMETER = 34;
	protected float CURSOR_RING_DIAMETER = 44;
	protected float CURSOR_OUTER_DIAMETER = 54;
	protected float CURSOR_EASING_FACTOR = 7;
	protected float CURSOR_STROKE = 4;
	
	public void setupFirstFrame() {
	
		GREEN = new EasingColor("00b159");
		WHITE = new EasingColor("ffffff");
	}
	
	public void drawApp() {
		background(0);
		
		PG.setColorForPImage( p );
		PG.resetPImageAlpha( p );
		PG.setPImageAlpha(p, 1f);

		float timePercent = ( p.millis() / 1000f ) % 1;

		p.noStroke();
		
		// draw cursor background
		p.fill( GREEN.colorInt() );
		p.arc( 100, 100, CURSOR_OUTER_DIAMETER, CURSOR_OUTER_DIAMETER, 0, (float) P.TWO_PI, P.CHORD );
		p.arc( 200, 150, CURSOR_OUTER_DIAMETER, CURSOR_OUTER_DIAMETER, 0, (float) P.TWO_PI, P.CHORD );

		// draw cursor pie percentage
		p.fill( WHITE.colorInt() );
		p.arc( 100, 100, CURSOR_PIE_DIAMETER, CURSOR_PIE_DIAMETER, 0, timePercent * (float) P.TWO_PI, P.PIE );
		p.arc( 200, 150, CURSOR_PIE_DIAMETER, CURSOR_PIE_DIAMETER, 0, timePercent * (float) P.TWO_PI, P.PIE );
		
		// always draw outer cursor circle
		p.noFill();
		p.stroke( WHITE.colorInt() );
		p.strokeWeight( CURSOR_STROKE );
		p.arc( 100, 100, CURSOR_RING_DIAMETER, CURSOR_RING_DIAMETER, 0, (float) P.TWO_PI, P.CHORD );
		p.arc( 200, 150, CURSOR_RING_DIAMETER, CURSOR_RING_DIAMETER, 0, (float) P.TWO_PI, P.CHORD );
		
		// separate: draw line arc
		float startRads = P.PI * 1.5f;
		float cursorRadius = 50f;
		float cursorFrames = 100f;
		float cursorProgress = (float)(p.frameCount % cursorFrames) / cursorFrames;
		p.noFill();
		p.stroke(0, 255, 0);
		p.arc(400, 400, cursorRadius, cursorRadius, startRads, startRads + cursorProgress * P.TWO_PI, P.OPEN);
		
		// zoom image
//		PG.drawTestPattern(p.g);
//		PG.zoomReTexture(p.g, 0.66f + 0.33f * P.sin(p.frameCount * 0.01f));
	}

}
