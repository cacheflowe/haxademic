package com.haxademic.core.draw.mapping;

import java.awt.Point;

import com.haxademic.core.app.P;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class CaptureKeystoneToRectBuffer {
	
	protected PGraphics sourceBuffer;
	protected PGraphics dest;
	protected int destW;
	protected int destH;
//	protected PShader quadMapper;
	
	protected Point _topLeft;
	protected Point _topRight;
	protected Point _bottomRight;
	protected Point _bottomLeft;
	protected Point _points[];
	public static Point SELECTED_POINT;
	protected Point _mousePressStart = new Point();
	protected float mouseActiveDist = 15;

	protected boolean _isPressed = false;
	protected boolean _isHovered = false;
	protected Point _mousePoint = new Point();
	protected Point _mouseDragged = new Point();
	
	protected String filePath = null;
	protected boolean writesToFile = false;
	
	protected int lastInteractTime = 0;
	protected int INTERACTION_TIMEOUT = 5000;
	protected boolean debug = false;

	
	public CaptureKeystoneToRectBuffer(PGraphics source, int destW, int destH, String mapFilePath) {
		// destination buffer for mapping result
		sourceBuffer = source;
		dest = P.p.createGraphics(destW, destH, PRenderers.P3D);
		// build points
		resetCorners();
//		quadMapper = P.p.loadShader(FileUtil.getFile("shaders/filters/map-quad-to-texture.glsl"));
		// listeners
		P.p.registerMethod("mouseEvent", this); // add mouse listeners
		P.p.registerMethod("keyEvent", this);
		// prep txt file
		if(mapFilePath != null) {
			filePath = FileUtil.getFile(mapFilePath);
			writesToFile = true;
			loadMappingFile();
			createMappingFile();
		}
	}
	
	public PGraphics mappedBuffer() {
		return dest;
	}
	
	public void resetCorners() {
		_topLeft = new Point(0, 0);
		_topRight = new Point(sourceBuffer.width, 0);
		_bottomRight = new Point(sourceBuffer.width, sourceBuffer.height);
		_bottomLeft = new Point(0, sourceBuffer.height);
		_points = new Point[] { _topLeft, _topRight, _bottomRight, _bottomLeft };
	}
	
	// RE-DRAW ///////////////////////////////////////////////
	
	public void update(boolean debug) {
		this.debug = debug;
		// attempt at shader option:
		// draw mapped capture to buffer
		//		quadMapper.set("sourceTexture", sourceBuffer);
		//		quadMapper.set("topLeft", (float) _topLeft.x / (float) sourceBuffer.width, (float) _topLeft.y / (float) sourceBuffer.height);
		//		quadMapper.set("botLeft", (float) _bottomLeft.x / (float) sourceBuffer.width, (float) _bottomLeft.y / (float) sourceBuffer.height);
		//		quadMapper.set("topRight", 1f + 1f * (float) _topRight.x / (float) sourceBuffer.width, 1f + 1f * (float) _topRight.y / (float) sourceBuffer.height);
		//		quadMapper.set("botRight", (float) _bottomRight.x / (float) sourceBuffer.width, 1f - (float) _bottomRight.y / (float) sourceBuffer.height);
		//		dest.filter(quadMapper);
		// textured vertices
		dest.beginDraw();
		dest.beginShape(P.QUADS);
		dest.texture(sourceBuffer);
		dest.vertex(0, 0, 0, 					_topLeft.x, _topLeft.y);
		dest.vertex(dest.width, 0, 0, 			_topRight.x, _topRight.y);
		dest.vertex(dest.width, dest.height, 0, 	_bottomRight.x, _bottomRight.y);
		dest.vertex(0, dest.height, 0, 			_bottomLeft.x, _bottomLeft.y);
		dest.endShape();
		dest.endDraw();
	}
	
//	public void update(boolean debug) {
//		this.debug = debug;
//
//		// draw to screen with pinned corner coords
//		// inspired by: https://github.com/davidbouchard/keystone & http://marcinignac.com/blog/projectedquads-source-code/
//		dest.beginDraw();
//		dest.beginShape(P.QUADS);
//		dest.texture(sourceBuffer);
//		dest.noStroke();
//		
//		float mapX = 0;
//		float mapY = 0;
//		float mapW = dest.width;
//		float mapH = dest.height;
//		
//			// subdivide quad for better resolution
//			float subDivideSteps = 8f;
//			float stepsX = subDivideSteps;
//			float stepsY = subDivideSteps;
//
//			for( float x=0; x < stepsX; x += 1f ) {
//				float xPercent = x/stepsX;
//				float xPercentNext = (x+1f)/stepsX;
//				if( xPercentNext > 1 ) xPercentNext = 1;
//				
//				for( float y=0; y < stepsY; y += 1f ) {
//					float yPercent = y/stepsY;
//					float yPercentNext = (y+1f)/stepsY;
//					if( yPercentNext > 1 ) yPercentNext = 1;
//
//					// calc grid positions based on interpolating columns between corners
//					float colTopX = interp(_topLeft.x, _topRight.x, xPercent);
//					float colTopY = interp(_topLeft.y, _topRight.y, xPercent);
//					float colBotX = interp(_bottomLeft.x, _bottomRight.x, xPercent);
//					float colBotY = interp(_bottomLeft.y, _bottomRight.y, xPercent);
//					
//					float nextColTopX = interp(_topLeft.x, _topRight.x, xPercentNext);
//					float nextColTopY = interp(_topLeft.y, _topRight.y, xPercentNext);
//					float nextColBotX = interp(_bottomLeft.x, _bottomRight.x, xPercentNext);
//					float nextColBotY = interp(_bottomLeft.y, _bottomRight.y, xPercentNext);
//					
//					// calc quad coords
//					float quadTopLeftX = interp(colTopX, colBotX, yPercent);
//					float quadTopLeftY = interp(colTopY, colBotY, yPercent);
//					float quadTopRightX = interp(nextColTopX, nextColBotX, yPercent);
//					float quadTopRightY = interp(nextColTopY, nextColBotY, yPercent);
//					float quadBotRightX = interp(nextColTopX, nextColBotX, yPercentNext);
//					float quadBotRightY = interp(nextColTopY, nextColBotY, yPercentNext);
//					float quadBotLeftX = interp(colTopX, colBotX, yPercentNext);
//					float quadBotLeftY = interp(colTopY, colBotY, yPercentNext);
//					
//					// draw subdivided quads
//					dest.vertex(quadTopLeftX, quadTopLeftY, 0, 		_topLeft.x, _topLeft.y);
//					dest.vertex(quadTopRightX, quadTopRightY, 0, 	_topRight.x, _topRight.y);
//					dest.vertex(quadBotRightX, quadBotRightY, 0, 	_bottomRight.x, _bottomRight.y);
//					dest.vertex(quadBotLeftX, quadBotLeftY, 0, 		_bottomLeft.x, _bottomLeft.y);
//
//					dest.vertex(quadTopLeftX, quadTopLeftY, 0, 		mapW * xPercent, 		mapY + mapH * yPercent);
//					dest.vertex(quadTopRightX, quadTopRightY, 0, 	mapX + mapW * xPercentNext, 	mapY + mapH * yPercent);
//					dest.vertex(quadBotRightX, quadBotRightY, 0, 	mapX + mapW * xPercentNext, 	mapY + mapH * yPercentNext);
//					dest.vertex(quadBotLeftX, quadBotLeftY, 0, 		mapX + mapW * xPercent, 		mapY + mapH * yPercentNext);
//				}
//			}
//			dest.endShape();
//			dest.endDraw();
//	}
	
	protected float interp( float lower, float upper, float n ) {
		return ( ( upper - lower ) * n ) + lower;
	}
	
	public void drawDebug(PGraphics pg) {
		sourceBuffer.beginDraw();
		showSelectedPoint(sourceBuffer);
		if(P.p.millis() < lastInteractTime + INTERACTION_TIMEOUT) showMappedRect(sourceBuffer);
		sourceBuffer.endDraw();
	}
	
	// USER INTERFACE ////////////////////////////////////////////
	
	protected void showSelectedPoint(PGraphics pg) {
		if(debug == false) return;
		if(P.p.millis() < lastInteractTime + INTERACTION_TIMEOUT) {
			if(SELECTED_POINT != null) drawPoint(pg, SELECTED_POINT);
		}
	}
	
	protected void drawPoint(PGraphics pg, Point point) {
		DrawUtil.setDrawCenter(pg);
		pg.fill(255, 75);
		pg.stroke(0, 255, 0);
		pg.strokeWeight(2);
		float indicatorSize = 20f + 3f * P.sin(P.p.frameCount / 10f);
		pg.ellipse(point.x, point.y, indicatorSize, indicatorSize);
		DrawUtil.setDrawCorner(pg);
	}
	
	protected void showMappedRect(PGraphics pg) {
		pg.noFill();
		pg.stroke(0, 255, 0);
		pg.strokeWeight(1);
		pg.line(_topLeft.x, _topLeft.y, _topRight.x, _topRight.y);
		pg.line(_topRight.x, _topRight.y, _bottomRight.x, _bottomRight.y);
		pg.line(_bottomRight.x, _bottomRight.y, _bottomLeft.x, _bottomLeft.y);
		pg.line(_bottomLeft.x, _bottomLeft.y, _topLeft.x, _topLeft.y);
	}
	
	protected void checkMouseHover() {
//		if(SELECTED_POINT == null) {
			boolean hoveredPoint = false;
			for( int i=0; i < _points.length; i++ ) {
				if(_points[i].distance( _mousePoint.x, _mousePoint.y ) < mouseActiveDist) {
					SELECTED_POINT = _points[i]; 
					hoveredPoint = true;
				}
			}
//			if(hoveredPoint == false)
//		}
	}
	
	public void mouseEvent(MouseEvent event) {
		if(debug == false) return;
		_mousePoint.setLocation( event.getX(), event.getY() );
		switch (event.getAction()) {
		case MouseEvent.PRESS:
			break;
		case MouseEvent.RELEASE:
			SELECTED_POINT = null;
			if(filePath != null) writeToFile();
			break;
		case MouseEvent.MOVE:
			resetInteractionTimeout();
			checkMouseHover();
			break;
		case MouseEvent.DRAG:
			resetInteractionTimeout();
			if( SELECTED_POINT != null ) {
				SELECTED_POINT.setLocation( _mousePoint );
			}
			break;
		}
	}
	
	public void keyEvent(KeyEvent e) {
		if(debug == false) return;
		if(e.getAction() == KeyEvent.PRESS) {
			// reset timeout
			if(e.getKeyCode() == P.UP || e.getKeyCode() == P.LEFT || e.getKeyCode() == P.RIGHT || e.getKeyCode() == P.DOWN || e.getKeyCode() == P.TAB) resetInteractionTimeout();
			// translate if arrow key
			Point translatePoint = new Point(0, 0);
			if(e.getKeyCode() == P.UP) translatePoint.setLocation(0, -1);
			if(e.getKeyCode() == P.LEFT) translatePoint.setLocation(-1, 0);
			if(e.getKeyCode() == P.RIGHT) translatePoint.setLocation(1, 0);
			if(e.getKeyCode() == P.DOWN) translatePoint.setLocation(0, 1);
			// tab to next point
			if(e.getKeyCode() == P.TAB) {
				if(SELECTED_POINT == _points[0]) SELECTED_POINT = _points[1];
				else if(SELECTED_POINT == _points[1]) SELECTED_POINT = _points[2];
				else if(SELECTED_POINT == _points[2]) SELECTED_POINT = _points[3];
				else SELECTED_POINT = _points[0];
				resetInteractionTimeout();
			}
			// apply transformation if needed
			if(SELECTED_POINT == _points[0] || SELECTED_POINT == _points[1] || SELECTED_POINT == _points[2] || SELECTED_POINT == _points[3]) {
				SELECTED_POINT.translate(translatePoint.x, translatePoint.y);
			} 
		}
	}
	
	protected void resetInteractionTimeout() {
		lastInteractTime = P.p.millis();
	}
	
	// SAVE TO FILE //////////////////////////////////
	
	protected void loadMappingFile() {
		if(FileUtil.fileOrPathExists(filePath) == true) {
			String[] mappingStr = FileUtil.readTextFromFile(filePath); // p.loadStrings(filePath);
			String[] posArray = mappingStr[0].split(",");
			_topLeft.setLocation(ConvertUtil.stringToInt(posArray[0]), ConvertUtil.stringToInt(posArray[1]));
			_topRight.setLocation(ConvertUtil.stringToInt(posArray[2]), ConvertUtil.stringToInt(posArray[3]));
			_bottomRight.setLocation(ConvertUtil.stringToInt(posArray[4]), ConvertUtil.stringToInt(posArray[5]));
			_bottomLeft.setLocation(ConvertUtil.stringToInt(posArray[6]), ConvertUtil.stringToInt(posArray[7]));
		} else {
			createMappingFile();
		}
	}
	
	protected void createMappingFile() {
		String mappingFilePath = FileUtil.pathForFile(this.filePath);
		if(FileUtil.fileOrPathExists(mappingFilePath) == false) {
			FileUtil.createDir(mappingFilePath);
		}
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
