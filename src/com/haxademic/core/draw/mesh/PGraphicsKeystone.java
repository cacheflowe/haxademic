package com.haxademic.core.draw.mesh;

import java.awt.Point;

import com.haxademic.core.app.P;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.MouseEvent;

public class PGraphicsKeystone {

	protected PGraphics pg;
	protected float subDivideSteps;

	protected Point _topLeft;
	protected Point _topRight;
	protected Point _bottomRight;
	protected Point _bottomLeft;
	protected Point _points[];
	protected Point _draggingPoint;

	protected boolean _isPressed = false;
	protected Point _mousePoint = new Point();
	
	protected String filePath = null;
	
	protected int lastMouseTime = 0;

	public PGraphicsKeystone( PApplet p, PGraphics pg, float subDivideSteps ) {
		this.pg = pg;
		this.subDivideSteps = subDivideSteps;
		resetCorners(pg);
		p.registerMethod("mouseEvent", this); // add mouse listeners
	}
	
	public PGraphicsKeystone( PApplet p, PGraphics pg, float subDivideSteps, String filePath ) {
		this(p, pg, subDivideSteps);
		this.filePath = filePath;
		loadMappingFile();
		createMappingFile();
	}
	
	protected void loadMappingFile() {
		if(FileUtil.fileOrPathExists(filePath) == true) {
			String[] mappingStr = FileUtil.readTextFromFile(filePath); // p.loadStrings(filePath);
			String[] posArray = mappingStr[0].split(",");
			_topLeft.setLocation(ConvertUtil.stringToInt(posArray[0]), ConvertUtil.stringToInt(posArray[1]));
			_topRight.setLocation(ConvertUtil.stringToInt(posArray[2]), ConvertUtil.stringToInt(posArray[3]));
			_bottomRight.setLocation(ConvertUtil.stringToInt(posArray[4]), ConvertUtil.stringToInt(posArray[5]));
			_bottomLeft.setLocation(ConvertUtil.stringToInt(posArray[6]), ConvertUtil.stringToInt(posArray[7]));
		}
	}
	
	protected void createMappingFile() {
		String mappingFilePath = FileUtil.pathForFile(this.filePath);
		if(FileUtil.fileOrPathExists(mappingFilePath) == false) {
			FileUtil.createDir(mappingFilePath);
		}
	}
	
	public void resetCorners(PGraphics p) {
		_topLeft = new Point(0, 0);
		_topRight = new Point(p.width, 0);
		_bottomRight = new Point(p.width, p.height);
		_bottomLeft = new Point(0, p.height);
		_points = new Point[] { _topLeft, _topRight, _bottomRight, _bottomLeft };
		// save if we have a file
		if(this.filePath != null) writeToFile();
	}

	public void update( PGraphics canvas, boolean subdivide ) {
		// draw to screen with pinned corner coords
		// inspired by: https://github.com/davidbouchard/keystone & http://marcinignac.com/blog/projectedquads-source-code/
		canvas.noStroke();
		canvas.fill(255);
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
		
		// for debugging
		showMouse(canvas);
	}
	
	protected float interp( float lower, float upper, float n ) {
		return ( ( upper - lower ) * n ) + lower;
	}

	protected void showMouse(PGraphics canvas) {
		if(P.p.millis() < lastMouseTime + 3000) {
			for( int i=0; i < _points.length; i++ ) {
				if( _points[i].distance( _mousePoint.x, _mousePoint.y ) < 30 ) {
					DrawUtil.setDrawCenter(canvas);
					canvas.fill(255);
					canvas.stroke(0);
					canvas.strokeWeight(3);
					float indicatorSize = 13f + 3f * P.sin(P.p.frameCount / 10f);
					canvas.ellipse(_points[i].x, _points[i].y, indicatorSize, indicatorSize);
					DrawUtil.setDrawCorner(canvas);
				}
			}
		}
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
				if(filePath != null) writeToFile();
				break;
			case MouseEvent.MOVE:
				lastMouseTime = P.p.millis();
				break;
			case MouseEvent.DRAG:
				lastMouseTime = P.p.millis();
				if( _draggingPoint != null ) {
					_draggingPoint.setLocation( _mousePoint );
				}
				break;
		}
	}
	
	public void drawTestPattern() {
		pg.beginDraw();
		pg.noStroke();
		
		float spacing = 50;
		float spacing2x = spacing * 2;
		
		for( int x=0; x <= pg.width + spacing2x; x += spacing) {
			for( int y=0; y <= pg.height + spacing2x; y += spacing) {
				if( ( x % spacing2x == 0 && y % spacing2x == 0 ) || ( x % spacing2x == spacing && y % spacing2x == spacing ) ) {
					pg.fill(0, 200);
				} else {
					pg.fill(255, 200);
				}
				pg.rect(x,y,spacing,spacing);
			}
		}
		pg.endDraw();
	}

	protected void writeToFile() {
		String coordsStr = String.join(",", new String[] {
			Integer.toString(_topLeft.x),
			Integer.toString(_topLeft.y),
			Integer.toString(_topRight.x),
			Integer.toString(_topRight.y),
			Integer.toString(_bottomRight.x),
			Integer.toString(_bottomRight.y),
			Integer.toString(_bottomLeft.x),
			Integer.toString(_bottomLeft.y)
		});
		FileUtil.writeTextToFile(filePath, coordsStr);
	}
}
