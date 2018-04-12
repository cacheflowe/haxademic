package com.haxademic.core.draw.mapping;

import java.awt.Point;

import com.haxademic.core.app.P;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class PGraphicsKeystone {

	protected PGraphics pg;
	protected float subDivideSteps;

	protected Point _topLeft;
	protected Point _topRight;
	protected Point _bottomRight;
	protected Point _bottomLeft;
	protected Point _points[];
	public static Point DRAGGING_POINT;
	public static PGraphicsKeystone DRAGGING_QUAD;
	protected Point _mousePressStart = new Point();
	protected float mouseActiveDist = 15;

	protected boolean _isPressed = false;
	protected boolean _isHovered = false;
	protected Point _mousePoint = new Point();
	protected Point _mouseDragged = new Point();
	
	protected String filePath = null;
	protected boolean writesToFile = false;
	
	protected boolean mouseShows = true;
	protected int lastMouseTime = 0;

	public PGraphicsKeystone( PApplet p, PGraphics pg, float subDivideSteps ) {
		this.pg = pg;
		this.subDivideSteps = subDivideSteps;
		resetCorners(pg);
		p.registerMethod("mouseEvent", this); // add mouse listeners
		p.registerMethod("keyEvent", this);
	}
	
	public PGraphicsKeystone( PApplet p, PGraphics pg, float subDivideSteps, String filePath ) {
		this(p, pg, subDivideSteps);
		if(filePath != null) {
			this.filePath = filePath;
			writesToFile = true;
			loadMappingFile();
			createMappingFile();
		}
	}
	
	public PGraphics pg() {
		return pg;
	}
	
	public void setMouseDist(float val) {
		mouseActiveDist = val;
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
	
	public void setPosition(float x, float y, float w, float h) {
		_topLeft.setLocation(x - w/2f, y - h/2f);
		_topRight.setLocation(x + w/2f, y - h/2f);
		_bottomRight.setLocation(x + w/2f, y + h/2f);
		_bottomLeft.setLocation(x - w/2f, y + h/2f);
		// save if we have a file
		if(this.filePath != null) writeToFile();
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
	
	public void enableFileWriting(boolean writes) {
		writesToFile = writes;
	}

	public void showMouse(boolean shows) {
		mouseShows = shows;
	}
	
	public void update( PGraphics canvas, boolean subdivide ) {
		update(canvas, subdivide, pg);
	}
	
	public void fillSolidColor( PGraphics canvas, int fill ) {
		// default single mapped quad
		canvas.noStroke();
		canvas.fill(fill);
		canvas.beginShape(PConstants.QUAD);
		canvas.vertex(_topLeft.x, _topLeft.y, 0);
		canvas.vertex(_topRight.x, _topRight.y, 0);
		canvas.vertex(_bottomRight.x, _bottomRight.y, 0);
		canvas.vertex(_bottomLeft.x, _bottomLeft.y, 0);
		canvas.endShape();
	}
		
	public void update( PGraphics canvas, boolean subdivide, PImage texture ) {
		update(canvas, subdivide, texture, 0, 0, texture.width, texture.height);
	}
	
	public void update( PGraphics canvas, boolean subdivide, PImage texture, float mapX, float mapY, float mapW, float mapH) {
		// draw to screen with pinned corner coords
		// inspired by: https://github.com/davidbouchard/keystone & http://marcinignac.com/blog/projectedquads-source-code/
		canvas.noStroke();
		canvas.fill(255);
		canvas.beginShape(PConstants.QUAD);
		canvas.texture(texture);
		
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
					canvas.vertex(quadTopLeftX, quadTopLeftY, 0, 		mapX + mapW * xPercent, 		mapY + mapH * yPercent);
					canvas.vertex(quadTopRightX, quadTopRightY, 0, 	mapX + mapW * xPercentNext, 	mapY + mapH * yPercent);
					canvas.vertex(quadBotRightX, quadBotRightY, 0, 	mapX + mapW * xPercentNext, 	mapY + mapH * yPercentNext);
					canvas.vertex(quadBotLeftX, quadBotLeftY, 0, 		mapX + mapW * xPercent, 		mapY + mapH * yPercentNext);
				}
			}
		} else {
			// default single mapped quad
			canvas.vertex(_topLeft.x, _topLeft.y, 0, 			mapX, mapY);
			canvas.vertex(_topRight.x, _topRight.y, 0, 		mapX + mapW, mapY);
			canvas.vertex(_bottomRight.x, _bottomRight.y, 0, 	mapX + mapW, mapY + mapH);
			canvas.vertex(_bottomLeft.x, _bottomLeft.y, 0, 	mapX, mapY + mapH);
		}

		canvas.endShape();
		
		// for debugging
		if(mouseShows) showMouse(canvas);
	}
	
	protected float interp( float lower, float upper, float n ) {
		return ( ( upper - lower ) * n ) + lower;
	}

	protected void showMouse(PGraphics canvas) {
		if(P.p.millis() < lastMouseTime + 3000) {
			// draw corner handles
			boolean isCornerHovered = false;
			if(DRAGGING_QUAD == null) {
				for( int i=0; i < _points.length; i++ ) {
					if( _points[i].distance( _mousePoint.x, _mousePoint.y ) < mouseActiveDist ) {
						DrawUtil.setDrawCenter(canvas);
						canvas.fill(255);
						canvas.stroke(0, 255, 0);
						canvas.strokeWeight(2);
						float indicatorSize = 13f + 3f * P.sin(P.p.frameCount / 10f);
						canvas.ellipse(_points[i].x, _points[i].y, indicatorSize, indicatorSize);
						DrawUtil.setDrawCorner(canvas);
						isCornerHovered = true;
					}
				}
			}
			// draw outline of hovered
			if(_isHovered == true || isCornerHovered == true) {
				canvas.stroke(0, 255, 0);
				canvas.strokeWeight(1.5f);
				canvas.noFill(); // canvas.fill(255, 40);
				canvas.beginShape();
				canvas.vertex(_points[0].x, _points[0].y);
				canvas.vertex(_points[1].x, _points[1].y);
				canvas.vertex(_points[2].x, _points[2].y);
				canvas.vertex(_points[3].x, _points[3].y);
				canvas.vertex(_points[0].x, _points[0].y);
				canvas.endShape();
				canvas.noStroke();
				canvas.fill(255);
			}
		}
	}
	
	public void mouseEvent(MouseEvent event) {
		_mousePoint.setLocation( event.getX(), event.getY() );
		switch (event.getAction()) {
			case MouseEvent.PRESS:
				if(DRAGGING_POINT == null) {
					for( int i=0; i < _points.length; i++ ) {
						if( _points[i].distance( _mousePoint.x, _mousePoint.y ) < mouseActiveDist ) {
							DRAGGING_POINT = _points[i]; 
						}
					}
				}
				if(DRAGGING_QUAD == null && DRAGGING_POINT == null) {
					if(_isHovered == true) {
						DRAGGING_QUAD = this;
						_mouseDragged.setLocation(_mousePoint.x, _mousePoint.y);
					}
				}
				break;
			case MouseEvent.RELEASE:
				DRAGGING_POINT = null;
				DRAGGING_QUAD = null;
				if(filePath != null) writeToFile();
				break;
			case MouseEvent.MOVE:
				lastMouseTime = P.p.millis();
				_isHovered = inside(_mousePoint, _points);
				break;
			case MouseEvent.DRAG:
				lastMouseTime = P.p.millis();
				if( DRAGGING_POINT != null ) {
					DRAGGING_POINT.setLocation( _mousePoint );
				}
				if(DRAGGING_QUAD == this) {
					_mouseDragged.setLocation(_mousePoint.x - _mouseDragged.x, _mousePoint.y - _mouseDragged.y);
					for( int i=0; i < _points.length; i++ ) {
						_points[i].translate(_mouseDragged.x, _mouseDragged.y);
					}
					_mouseDragged.setLocation(_mousePoint.x, _mousePoint.y);
				}
				break;
		}
	}
	
	protected boolean inside(Point point, Point[] vertices) {
	    // ray-casting algorithm based on
	    // http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
		// https://github.com/substack/point-in-polygon
	    float x = point.x;
	    float y = point.y;

	    boolean inside = false;
	    for (int i = 0, j = vertices.length - 1; i < vertices.length; j = i++) {
	        float xi = vertices[i].x, yi = vertices[i].y;
	        float xj = vertices[j].x, yj = vertices[j].y;
	        boolean intersect = ((yi > y) != (yj > y)) && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
	        if (intersect) inside = !inside;
	    }

	    return inside;
	};
	
	public void keyEvent(KeyEvent e) {
		if(e.getAction() == KeyEvent.PRESS) {
			if(e.getKeyCode() == P.UP || e.getKeyCode() == P.LEFT || e.getKeyCode() == P.RIGHT || e.getKeyCode() == P.DOWN) lastMouseTime = P.p.millis();
			Point translatePoint = new Point(0, 0);
			if(e.getKeyCode() == P.UP) translatePoint.setLocation(0, -1);
			if(e.getKeyCode() == P.LEFT) translatePoint.setLocation(-1, 0);
			if(e.getKeyCode() == P.RIGHT) translatePoint.setLocation(1, 0);
			if(e.getKeyCode() == P.DOWN) translatePoint.setLocation(0, 1);
			
			if(DRAGGING_POINT == _points[0] || DRAGGING_POINT == _points[1] || DRAGGING_POINT == _points[2] || DRAGGING_POINT == _points[3]) {
				DRAGGING_POINT.translate(translatePoint.x, translatePoint.y);
			} else if(DRAGGING_QUAD == this) {
				for( int i=0; i < _points.length; i++ ) {
					_points[i].translate(translatePoint.x, translatePoint.y);
				}
			}
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
					pg.fill(0, 160);
				} else {
					pg.fill(255, 160);
				}
				pg.rect(x,y,spacing,spacing);
			}
		}
		pg.endDraw();
	}

	protected void writeToFile() {
		if(writesToFile == false) return;
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
