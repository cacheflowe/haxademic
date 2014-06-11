package com.haxademic.core.draw.mesh;

import java.awt.Point;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.MouseEvent;

import com.haxademic.core.math.MathUtil;

public class PGraphicsKeystone {

	protected PGraphics pg;

	protected Point topLeft;
	protected Point topRight;
	protected Point bottomRight;
	protected Point bottomLeft;
	protected Point points[];
	protected Point _draggingPoint;

	protected boolean _isPressed = false;
	protected int curMouseX = 0;
	protected int curMouseY = 0;
	protected Point _mousePoint = new Point();

	public PGraphicsKeystone( PApplet p, PGraphics pg ) {
		this.pg = pg;

		// set up draggable corners
		topLeft = new Point(0,0);
		topRight = new Point(pg.width,0);
		bottomRight = new Point(pg.width,pg.height);
		bottomLeft = new Point(0,pg.height);
		points = new Point[] { topLeft, topRight, bottomRight, bottomLeft };

		// add delegate mouse response
		p.registerMethod("mouseEvent", this);
	}

	public void update( PGraphics canvas, boolean subdivide, float subDivideSteps ) {
		// draw to screen with pinned corner coords
		canvas.noStroke();
		canvas.beginShape(PConstants.QUAD);
		canvas.texture(pg);
		
		if( subdivide == true ) {
			// subdivide quad for better resolution
			float stepsX = subDivideSteps;
			float stepsY = subDivideSteps;

			for( float x=0; x < stepsX; x += 1f ) {
				float xPercent = x/stepsX;
				float xPercentNext = (x+1f)/stepsX;
				if( xPercentNext > 1 ) xPercentNext = 1;
				
				for( float y=0; y < stepsY; y += 1f ) {
					float yPercent = y/stepsY;
					float yPercentNext = (y+1f)/stepsY;
					if( yPercentNext > 1 ) yPercentNext = 1;

					// calc grid positions based on interpolating columns
					float colTopX = MathUtil.interp(topLeft.x, topRight.x, xPercent);
					float colTopY = MathUtil.interp(topLeft.y, topRight.y, xPercent);
					float colBotX = MathUtil.interp(bottomLeft.x, bottomRight.x, xPercent);
					float colBotY = MathUtil.interp(bottomLeft.y, bottomRight.y, xPercent);
					
					float nextColTopX = MathUtil.interp(topLeft.x, topRight.x, xPercentNext);
					float nextColTopY = MathUtil.interp(topLeft.y, topRight.y, xPercentNext);
					float nextColBotX = MathUtil.interp(bottomLeft.x, bottomRight.x, xPercentNext);
					float nextColBotY = MathUtil.interp(bottomLeft.y, bottomRight.y, xPercentNext);
					
					// calc quad coords
					float quadTopLeftX = MathUtil.interp(colTopX, colBotX, yPercent);
					float quadTopLeftY = MathUtil.interp(colTopY, colBotY, yPercent);
					float quadTopRightX = MathUtil.interp(nextColTopX, nextColBotX, yPercent);
					float quadTopRightY = MathUtil.interp(nextColTopY, nextColBotY, yPercent);
					float quadBotRightX = MathUtil.interp(nextColTopX, nextColBotX, yPercentNext);
					float quadBotRightY = MathUtil.interp(nextColTopY, nextColBotY, yPercentNext);
					float quadBotLeftX = MathUtil.interp(colTopX, colBotX, yPercentNext);
					float quadBotLeftY = MathUtil.interp(colTopY, colBotY, yPercentNext);
					
					// draw subdivided quads
					canvas.vertex(quadTopLeftX, quadTopLeftY, 0, 	pg.width * xPercent, pg.height * yPercent);
					canvas.vertex(quadTopRightX, quadTopRightY, 0, 	pg.width * xPercentNext, pg.height * yPercent);
					canvas.vertex(quadBotRightX, quadBotRightY, 0, 	pg.width * xPercentNext, pg.height * yPercentNext);
					canvas.vertex(quadBotLeftX, quadBotLeftY, 0, 	pg.width * xPercent, pg.height * yPercentNext);
				}
			}
		} else {
			// default single mapped quad
			canvas.vertex(topLeft.x, topLeft.y, 0, 			0, 0);
			canvas.vertex(topRight.x, topRight.y, 0, 		pg.width, 0);
			canvas.vertex(bottomRight.x, bottomRight.y, 0, 	pg.width, pg.height);
			canvas.vertex(bottomLeft.x, bottomLeft.y, 0, 	0, pg.height);
		}

		canvas.endShape();
	}

	public void mouseEvent(MouseEvent event) {
		_mousePoint.setLocation( event.getX(), event.getY() );
		switch (event.getAction()) {
			case MouseEvent.PRESS:
				for( int i=0; i < points.length; i++ ) {
					if( points[i].distance( _mousePoint.x, _mousePoint.y ) < 30 ) {
						_draggingPoint = points[i]; 
					}
				}
				break;
			case MouseEvent.RELEASE:
				_draggingPoint = null;
				break;
			case MouseEvent.DRAG:
				if( _draggingPoint != null ) {
					_draggingPoint.setLocation( _mousePoint );
				}
				break;
		}
	}
}
