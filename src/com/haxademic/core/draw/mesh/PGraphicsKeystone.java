package com.haxademic.core.draw.mesh;

import java.awt.Point;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.MouseEvent;

public class PGraphicsKeystone {

	protected PGraphics pg;
	protected float _subDivideSteps;

	protected Point _topLeft;
	protected Point _topRight;
	protected Point _bottomRight;
	protected Point _bottomLeft;
	protected Point _points[];
	protected Point _draggingPoint;

	protected boolean _isPressed = false;
	protected Point _mousePoint = new Point();

	public PGraphicsKeystone( PApplet p, PGraphics pg, float subDivideSteps ) {
		this.pg = pg;
		_subDivideSteps = subDivideSteps;

		// set up draggable corners
		_topLeft = new Point(0,0);
		_topRight = new Point(pg.width,0);
		_bottomRight = new Point(pg.width,pg.height);
		_bottomLeft = new Point(0,pg.height);
		_points = new Point[] { _topLeft, _topRight, _bottomRight, _bottomLeft };

		// add delegate mouse response
		p.registerMethod("mouseEvent", this);
	}

	public void update( PGraphics canvas, boolean subdivide ) {
		// draw to screen with pinned corner coords
		canvas.noStroke();
		canvas.beginShape(PConstants.QUAD);
		canvas.texture(pg);
		
		if( subdivide == true ) {
			// subdivide quad for better resolution
			float stepsX = _subDivideSteps;
			float stepsY = _subDivideSteps;

			for( float x=0; x < stepsX; x += 1f ) {
				float xPercent = x/stepsX;
				float xPercentNext = (x+1f)/stepsX;
				if( xPercentNext > 1 ) xPercentNext = 1;
				
				for( float y=0; y < stepsY; y += 1f ) {
					float yPercent = y/stepsY;
					float yPercentNext = (y+1f)/stepsY;
					if( yPercentNext > 1 ) yPercentNext = 1;

					// calc grid positions based on interpolating columns between corners
					float colTopX = interp(_topLeft.x, _topRight.x, xPercent);
					float colTopY = interp(_topLeft.y, _topRight.y, xPercent);
					float colBotX = interp(_bottomLeft.x, _bottomRight.x, xPercent);
					float colBotY = interp(_bottomLeft.y, _bottomRight.y, xPercent);
					
					float nextColTopX = interp(_topLeft.x, _topRight.x, xPercentNext);
					float nextColTopY = interp(_topLeft.y, _topRight.y, xPercentNext);
					float nextColBotX = interp(_bottomLeft.x, _bottomRight.x, xPercentNext);
					float nextColBotY = interp(_bottomLeft.y, _bottomRight.y, xPercentNext);
					
					// calc quad coords
					float quadTopLeftX = interp(colTopX, colBotX, yPercent);
					float quadTopLeftY = interp(colTopY, colBotY, yPercent);
					float quadTopRightX = interp(nextColTopX, nextColBotX, yPercent);
					float quadTopRightY = interp(nextColTopY, nextColBotY, yPercent);
					float quadBotRightX = interp(nextColTopX, nextColBotX, yPercentNext);
					float quadBotRightY = interp(nextColTopY, nextColBotY, yPercentNext);
					float quadBotLeftX = interp(colTopX, colBotX, yPercentNext);
					float quadBotLeftY = interp(colTopY, colBotY, yPercentNext);
					
					// draw subdivided quads
					canvas.vertex(quadTopLeftX, quadTopLeftY, 0, 	pg.width * xPercent, pg.height * yPercent);
					canvas.vertex(quadTopRightX, quadTopRightY, 0, 	pg.width * xPercentNext, pg.height * yPercent);
					canvas.vertex(quadBotRightX, quadBotRightY, 0, 	pg.width * xPercentNext, pg.height * yPercentNext);
					canvas.vertex(quadBotLeftX, quadBotLeftY, 0, 	pg.width * xPercent, pg.height * yPercentNext);
				}
			}
		} else {
			// default single mapped quad
			canvas.vertex(_topLeft.x, _topLeft.y, 0, 			0, 0);
			canvas.vertex(_topRight.x, _topRight.y, 0, 			pg.width, 0);
			canvas.vertex(_bottomRight.x, _bottomRight.y, 0, 	pg.width, pg.height);
			canvas.vertex(_bottomLeft.x, _bottomLeft.y, 0, 	0, 	pg.height);
		}

		canvas.endShape();
	}
	
	protected float interp( float lower, float upper, float n ) {
		return ( ( upper - lower ) * n ) + lower;
	}

	public void mouseEvent(MouseEvent event) {
		_mousePoint.setLocation( event.getX(), event.getY() );
		switch (event.getAction()) {
			case MouseEvent.PRESS:
				for( int i=0; i < _points.length; i++ ) {
					if( _points[i].distance( _mousePoint.x, _mousePoint.y ) < 30 ) {
						_draggingPoint = _points[i]; 
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
	
	public void drawTestPattern() {
		pg.beginDraw();
		pg.noStroke();
		
		for( int x=0; x < pg.width; x+= 50) {
			for( int y=0; y < pg.height; y+= 50) {
				if( ( x % 100 == 0 && y % 100 == 0 ) || ( x % 100 == 50 && y % 100 == 50 ) ) {
					pg.fill(0, 200);
				} else {
					pg.fill(255, 200);
				}
				pg.rect(x,y,50,50);
			}
		}
		pg.endDraw();
	}

}
