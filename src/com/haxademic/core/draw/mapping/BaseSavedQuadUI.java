package com.haxademic.core.draw.mapping;

import java.awt.Point;
import java.awt.geom.Point2D;

import com.haxademic.core.app.P;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class BaseSavedQuadUI {

	protected int w;
	protected int h;
	protected Point topLeft;
	protected Point topRight;
	protected Point bottomRight;
	protected Point bottomLeft;
	protected Point points[];
	public static Point SELECTED_POINT;
	public static BaseSavedQuadUI DRAGGING_QUAD;
	protected float mouseActiveDist = 15;
	protected Point2D.Float center = new Point2D.Float();

	protected boolean isPressed = false;
	protected boolean isHovered = false;
	protected Point mousePoint = new Point();
	protected Point mouseDragged = new Point();
	
	protected String filePath = null;
	protected boolean writesToFile = false;
	
	protected int lastInteractTime = 0;
	protected int INTERACTION_TIMEOUT = 5000;
	protected boolean active = true;
	protected boolean shiftDown = false;

	public BaseSavedQuadUI(int w, int h, String filePath) {
		this.filePath = filePath;
		this.w = w;
		this.h = h;
		
		resetCorners();
		
		P.p.registerMethod("mouseEvent", this); // add mouse listeners
		P.p.registerMethod("keyEvent", this);
		
		// prep txt file
		if(filePath != null) {
			writesToFile = true;
			loadMappingFile();
			createMappingFile();
		}
		updateCenter();
	}
	
	// public points setters
	
	public void setActive(boolean debug) {
		if(active != debug) {		// only toggle when dirty
			active = debug;
			if(active) {
				resetInteractionTimeout();
				DRAGGING_QUAD = this;
				SELECTED_POINT = null;
			} else {
				for( int i=0; i < points.length; i++ ) {
					if(points[i] == SELECTED_POINT) SELECTED_POINT = null;
				}
			}
		}
	}

	public void resetCorners() {
		topLeft = new Point(0, 0);
		topRight = new Point(w, 0);
		bottomRight = new Point(w, h);
		bottomLeft = new Point(0, h);
		points = new Point[] { topLeft, topRight, bottomRight, bottomLeft };
		if(filePath != null) save(); // only save after init
	}
	
	public void setPosition(float x, float y, float w, float h) {
		topLeft.setLocation(x - w/2f, y - h/2f);
		topRight.setLocation(x + w/2f, y - h/2f);
		bottomRight.setLocation(x + w/2f, y + h/2f);
		bottomLeft.setLocation(x - w/2f, y + h/2f);
		save();
	}
	
	// state getters
	
	public boolean isActive() {
		return active;
	}
	
	public boolean isHovered() {
		return isHovered;
	}

	public float centerX() {
		return center.x;
	}
	
	public float centerY() {
		return center.y;
	}
	
	// USER INTERFACE ////////////////////////////////////////////
	
	public void drawDebug(PGraphics pg, boolean offscreen) {
		if(active && P.p.millis() < lastInteractTime + INTERACTION_TIMEOUT)  {
			if(offscreen) pg.beginDraw();
			showSelectedPoint(pg);
			showMappedRect(pg);
			if(offscreen) pg.endDraw();
		}
	}

	protected void showSelectedPoint(PGraphics pg) {
		if(active == false) return;
		if(SELECTED_POINT != null) drawPoint(pg, SELECTED_POINT);
	}
	
	protected void drawPoint(PGraphics pg, Point point) {
		PG.setDrawCenter(pg);
		pg.fill(255, 75);
		pg.stroke(0, 255, 0);
		pg.strokeWeight(2);
		float indicatorSize = 20f + 3f * P.sin(P.p.frameCount / 10f);
		pg.ellipse(point.x, point.y, indicatorSize, indicatorSize);
		PG.setDrawCorner(pg);
	}
	
	protected void showMappedRect(PGraphics pg) {
		pg.noFill();
		pg.stroke(0, 255, 0);
		pg.strokeWeight(1);
		pg.line(topLeft.x, topLeft.y, topRight.x, topRight.y);
		pg.line(topRight.x, topRight.y, bottomRight.x, bottomRight.y);
		pg.line(bottomRight.x, bottomRight.y, bottomLeft.x, bottomLeft.y);
		pg.line(bottomLeft.x, bottomLeft.y, topLeft.x, topLeft.y);
		pg.ellipse(center.x - 4, center.y - 4, 8, 8);
	}
	
	public void mouseEvent(MouseEvent event) {
		mousePoint.setLocation( event.getX(), event.getY() );
		checkHoverQuad(event);
		if(active == false) return;
		resetInteractionTimeout();
		checkMouseDragPoint(event);
		checkMouseDragQuad(event);
	}
	
	public void checkMouseDragPoint(MouseEvent event) {
		switch (event.getAction()) {
		case MouseEvent.PRESS:
			break;
		case MouseEvent.RELEASE:
			if(SELECTED_POINT != null) save();
			SELECTED_POINT = null;
			break;
		case MouseEvent.MOVE:
			checkMouseHoverPoint();
			break;
		case MouseEvent.DRAG:
			if( SELECTED_POINT != null ) {
				SELECTED_POINT.setLocation( mousePoint );
				updateCenter();
			}
			break;
		}
	}
	
	protected void checkMouseHoverPoint() {
		boolean hoveredPoint = false;
		for( int i=0; i < points.length; i++ ) {
			if(points[i].distance( mousePoint.x, mousePoint.y ) < mouseActiveDist) {
				SELECTED_POINT = points[i]; 
				hoveredPoint = true;
				DRAGGING_QUAD = null;
			}
		}
		if(hoveredPoint == false) SELECTED_POINT = null; 
	}
	
	public void checkHoverQuad(MouseEvent event) {
		switch (event.getAction()) {
		case MouseEvent.MOVE:
			isHovered = inside(mousePoint, points);
			break;
		}
	}
	
	public void checkMouseDragQuad(MouseEvent event) {
		switch (event.getAction()) {
		case MouseEvent.PRESS:
			if(isHovered == true && SELECTED_POINT == null) {
				DRAGGING_QUAD = this;
				mouseDragged.setLocation(mousePoint.x, mousePoint.y);
				updateCenter();
			}
			break;
		case MouseEvent.RELEASE:
			if(DRAGGING_QUAD != null) save();
			DRAGGING_QUAD = null;
			break;
		case MouseEvent.DRAG:
			if(DRAGGING_QUAD == this) {
				mouseDragged.setLocation(mousePoint.x - mouseDragged.x, mousePoint.y - mouseDragged.y);
				for( int i=0; i < points.length; i++ ) {
					points[i].translate(mouseDragged.x, mouseDragged.y);
				}
				mouseDragged.setLocation(mousePoint.x, mousePoint.y);
				updateCenter();
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
		if(active == false) return;
		if(e.getAction() == KeyEvent.PRESS) {
			// shift
			if(e.getKeyCode() == P.SHIFT) shiftDown = true;
			// reset timeout
			if(e.getKeyCode() == P.UP || e.getKeyCode() == P.LEFT || e.getKeyCode() == P.RIGHT || e.getKeyCode() == P.DOWN || e.getKeyCode() == P.TAB) resetInteractionTimeout();
			// translate if arrow key
			Point translatePoint = new Point(0, 0);
			if(e.getKeyCode() == P.UP) translatePoint.setLocation(0, -1);
			if(e.getKeyCode() == P.LEFT) translatePoint.setLocation(-1, 0);
			if(e.getKeyCode() == P.RIGHT) translatePoint.setLocation(1, 0);
			if(e.getKeyCode() == P.DOWN) translatePoint.setLocation(0, 1);
			if(shiftDown) { translatePoint.x *= 10; translatePoint.y *= 10; }
			// tab to next point
			if(e.getKeyCode() == P.TAB) {
				if(SELECTED_POINT == points[0]) SELECTED_POINT = points[1];
				else if(SELECTED_POINT == points[1]) SELECTED_POINT = points[2];
				else if(SELECTED_POINT == points[2]) SELECTED_POINT = points[3];
				else SELECTED_POINT = points[0];
				resetInteractionTimeout();
			}
			// apply transformation if needed
			if(translatePoint.x != 0 || translatePoint.y != 0) {
				if(SELECTED_POINT == points[0] || SELECTED_POINT == points[1] || SELECTED_POINT == points[2] || SELECTED_POINT == points[3]) {
					SELECTED_POINT.translate(translatePoint.x, translatePoint.y);
					save();
				} else if(DRAGGING_QUAD == this) {
					for( int i=0; i < points.length; i++ ) {
						points[i].translate(translatePoint.x, translatePoint.y);
					}
					save();
				}
			}
			updateCenter();
		}
		if(e.getAction() == KeyEvent.RELEASE) {
			if(e.getKeyCode() == P.SHIFT) shiftDown = false;
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
			topLeft.setLocation(ConvertUtil.stringToInt(posArray[0]), ConvertUtil.stringToInt(posArray[1]));
			topRight.setLocation(ConvertUtil.stringToInt(posArray[2]), ConvertUtil.stringToInt(posArray[3]));
			bottomRight.setLocation(ConvertUtil.stringToInt(posArray[4]), ConvertUtil.stringToInt(posArray[5]));
			bottomLeft.setLocation(ConvertUtil.stringToInt(posArray[6]), ConvertUtil.stringToInt(posArray[7]));
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
	
	protected void updateCenter() {
		center.setLocation(
			(float) (topLeft.x + topRight.x + bottomRight.x + bottomLeft.x) / 4f, 
			(float) (topLeft.y + topRight.y + bottomRight.y + bottomLeft.y) / 4f
		);	
	}
	
	protected void save() {
		updateCenter();
		if(this.filePath != null) writeToFile();	
		
	}
	
	protected void writeToFile() {
		if(writesToFile == false) return;
		String coordsStr = String.join(",", new String[] {
				Integer.toString(topLeft.x),
				Integer.toString(topLeft.y),
				Integer.toString(topRight.x),
				Integer.toString(topRight.y),
				Integer.toString(bottomRight.x),
				Integer.toString(bottomRight.y),
				Integer.toString(bottomLeft.x),
				Integer.toString(bottomLeft.y)
		});
		FileUtil.writeTextToFile(filePath, coordsStr);
	}

	public void enableFileWriting(boolean writes) {
		writesToFile = writes;
	}

}
