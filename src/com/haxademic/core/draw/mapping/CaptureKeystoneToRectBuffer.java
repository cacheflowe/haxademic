package com.haxademic.core.draw.mapping;

import java.awt.Point;

import com.haxademic.core.app.P;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class CaptureKeystoneToRectBuffer {
	
	protected PGraphics sourceBuffer;
	protected PGraphics dest;
	protected int destW;
	protected int destH;
	
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
	
	protected int lastMouseTime = 0;
	protected boolean debug = false;

	
	public CaptureKeystoneToRectBuffer(PGraphics source, int destW, int destH, String mapFilePath) {
		// destination buffer for mapping result
		sourceBuffer = source;
		dest = P.p.createGraphics(destW, destH, PRenderers.P3D);
		// build points
		resetCorners();
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
		// draw mapped capture to buffer
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
	
	public void drawDebug(PGraphics pg) {
		sourceBuffer.beginDraw();
		showMouse(sourceBuffer);
		if(P.p.millis() < lastMouseTime + 3000) showMappedRect(sourceBuffer);
		sourceBuffer.endDraw();
	}
	
	// USER INTERFACE ////////////////////////////////////////////
	
	protected void showMouse(PGraphics pg) {
		if(debug == false) return;
		if(P.p.millis() < lastMouseTime + 3000) {
			// draw corner handles
			boolean isCornerHovered = false;
			if(DRAGGING_QUAD == null) {
				for( int i=0; i < _points.length; i++ ) {
					if( _points[i].distance( _mousePoint.x, _mousePoint.y ) < mouseActiveDist ) {
						DrawUtil.setDrawCenter(pg);
						pg.fill(255);
						pg.stroke(0, 255, 0);
						pg.strokeWeight(2);
						float indicatorSize = 13f + 3f * P.sin(P.p.frameCount / 10f);
						pg.ellipse(_points[i].x, _points[i].y, indicatorSize, indicatorSize);
						DrawUtil.setDrawCorner(pg);
						isCornerHovered = true;
					}
				}
			}
		}
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
	
	public void mouseEvent(MouseEvent event) {
		if(debug == false) return;
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
			break;
		case MouseEvent.RELEASE:
			DRAGGING_POINT = null;
			DRAGGING_QUAD = null;
			if(filePath != null) writeToFile();
			break;
		case MouseEvent.MOVE:
			lastMouseTime = P.p.millis();
			break;
		case MouseEvent.DRAG:
			lastMouseTime = P.p.millis();
			if( DRAGGING_POINT != null ) {
				DRAGGING_POINT.setLocation( _mousePoint );
			}
			break;
		}
	}
	
	public void keyEvent(KeyEvent e) {
		if(debug == false) return;
		if(e.getAction() == KeyEvent.PRESS) {
			if(e.getKeyCode() == P.UP || e.getKeyCode() == P.LEFT || e.getKeyCode() == P.RIGHT || e.getKeyCode() == P.DOWN) lastMouseTime = P.p.millis();
			Point translatePoint = new Point(0, 0);
			if(e.getKeyCode() == P.UP) translatePoint.setLocation(0, -1);
			if(e.getKeyCode() == P.LEFT) translatePoint.setLocation(-1, 0);
			if(e.getKeyCode() == P.RIGHT) translatePoint.setLocation(1, 0);
			if(e.getKeyCode() == P.DOWN) translatePoint.setLocation(0, 1);
			
			if(DRAGGING_POINT == _points[0] || DRAGGING_POINT == _points[1] || DRAGGING_POINT == _points[2] || DRAGGING_POINT == _points[3]) {
				DRAGGING_POINT.translate(translatePoint.x, translatePoint.y);
			} 
		}
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
