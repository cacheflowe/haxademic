package com.haxademic.core.draw.mesh;

import java.awt.Point;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.MouseEvent;

import com.haxademic.core.math.MathUtil;

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

					// calc grid positions based on interpolating columns
					float colTopX = MathUtil.interp(_topLeft.x, _topRight.x, xPercent);
					float colTopY = MathUtil.interp(_topLeft.y, _topRight.y, xPercent);
					float colBotX = MathUtil.interp(_bottomLeft.x, _bottomRight.x, xPercent);
					float colBotY = MathUtil.interp(_bottomLeft.y, _bottomRight.y, xPercent);
					
					float nextColTopX = MathUtil.interp(_topLeft.x, _topRight.x, xPercentNext);
					float nextColTopY = MathUtil.interp(_topLeft.y, _topRight.y, xPercentNext);
					float nextColBotX = MathUtil.interp(_bottomLeft.x, _bottomRight.x, xPercentNext);
					float nextColBotY = MathUtil.interp(_bottomLeft.y, _bottomRight.y, xPercentNext);
					
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
			canvas.vertex(_topLeft.x, _topLeft.y, 0, 			0, 0);
			canvas.vertex(_topRight.x, _topRight.y, 0, 			pg.width, 0);
			canvas.vertex(_bottomRight.x, _bottomRight.y, 0, 	pg.width, pg.height);
			canvas.vertex(_bottomLeft.x, _bottomLeft.y, 0, 	0, 	pg.height);
		}

		canvas.endShape();
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
}
