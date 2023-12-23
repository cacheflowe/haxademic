package com.haxademic.core.draw.mapping;

import java.awt.Point;
import java.awt.Rectangle;

import com.haxademic.core.app.P;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.file.ConfigTextFile;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.event.MouseEvent;

public class SavedRectangle {
	
	protected ConfigTextFile savedFile;
	protected Rectangle rectangle;
	protected Rectangle rectangleMove;
	
	protected boolean isDragging = false;
	protected boolean isResizing = false;
	protected Point mouseStartPoint = new Point();
	protected Point mouseMovePoint = new Point();
	protected int dragCorner = 14;
	protected boolean draggingNewPoint = false;

	public static SavedRectangle curDragging = null;

	public SavedRectangle(String id, boolean draggable) {
		// create config text file with default coords
		// TODO: Convert to PrefText, since this is the only place we use ConfigTextFile
		savedFile = new ConfigTextFile(FileUtil.getPath("text/rectangles/"+id+".txt"), "0,0,100,100");
		String[] coords = savedFile.getSingleLineCSV();
		
		// build rectangle from text config
		rectangle = new Rectangle(
			ConvertUtil.stringToInt(coords[0]), 
			ConvertUtil.stringToInt(coords[1]),
			ConvertUtil.stringToInt(coords[2]), 
			ConvertUtil.stringToInt(coords[3])
		);
		P.out("Loading saved Rectangle = ", rectangle);
		
		// add delegate mouse listener
		if(draggable == true) P.p.registerMethod("mouseEvent", this);
	}
	
	// setters
	public void set(int x, int y, int w, int h) {
		rectangle.setBounds(x, y, w, h);
	}
	
	// mask getters for rectangle & state
	public int x() { return (isDragging == false) ? rectangle.x : rectangleMove.x; }
	public int y() { return (isDragging == false) ? rectangle.y : rectangleMove.y; }
	public int width() { return (isDragging == false) ? rectangle.width : rectangleMove.width; }
	public int height() { return (isDragging == false) ? rectangle.height : rectangleMove.height; }
	public boolean isDragging() { return isDragging; }
	
	// update rectangle and write to disk
	public void updateRectangle(int x, int y, int width, int height) {
		rectangle.setBounds(x, y, width, height);
		savedFile.writeSingleLine(toSaveString());
	}
	
	public void updateRectangle(Rectangle newRect) {
		rectangle.setBounds(newRect);
		savedFile.writeSingleLine(toSaveString());
	}
	
	public String toSaveString() {
		return rectangle.x + "," + rectangle.y + "," + rectangle.width + "," + rectangle.height;
	}
	
	// draw debug
	
	public void drawDebugToPG(PGraphics pg) {
		drawDebugToPG(pg, false);
	}

	public void drawDebugToPG(PGraphics pg, boolean drawInPlace) {
		int x = (drawInPlace == true) ? 0 : x();
		int y = (drawInPlace == true) ? 0 : y();
		if(isDragging() == true) 
			pg.fill(255, 100); 
		else 
			pg.fill(0,100,0, 100);
		pg.rect(x, y, width(), height());
		pg.fill(255);
		pg.rect(x + width() - dragCorner, y + height() - dragCorner, dragCorner, dragCorner);
		pg.text(""+x()+", "+y()+", "+width()+", "+height(), x + 10, y + 20);
	}
	
	// handle mouse events
	public void mouseEvent(MouseEvent event) {
		switch (event.getAction()) {
			case MouseEvent.PRESS:
				mouseStartPoint.setLocation(event.getX(), event.getY());
				if(rectangle.contains(mouseStartPoint) && SavedRectangle.curDragging == null) {
					isDragging = true;
					SavedRectangle.curDragging = this;
					float cornerClickDist = (float) mouseStartPoint.distance(rectangle.x + rectangle.width, rectangle.y + rectangle.height);
					if(cornerClickDist < dragCorner) {
						isResizing = true;
					} 
					rectangleMove = new Rectangle(rectangle); // build temp rectangle for moving
					P.out("START: EXISTING");
				} else {
					// TODO: handle this for multiple rectangles. currently only works for redrawing a single existing rectangle
					draggingNewPoint = true;
					isResizing = true;
					isDragging = true;
					rectangleMove = new Rectangle(mouseStartPoint.x, mouseStartPoint.y, 0, 0); // build temp rectangle for moving
				}
				break;
			case MouseEvent.RELEASE:
				if(isDragging == true) {
					updateRectangle(rectangleMove);
					SavedRectangle.curDragging = null;
					rectangleMove = null;
					isDragging = false;
					isResizing = false;
					draggingNewPoint = false;
				}
				break;
			case MouseEvent.MOVE:
				break;
			case MouseEvent.DRAG:
				if(isDragging == true) {
					mouseMovePoint.setLocation(event.getX(), event.getY());
					int mouseDeltaX = mouseMovePoint.x - mouseStartPoint.x;
					int mouseDeltaY = mouseMovePoint.y - mouseStartPoint.y;
					if(draggingNewPoint) {
						rectangleMove.setSize(mouseDeltaX, mouseDeltaY);
					} else if(isResizing == false) {
						rectangleMove.setLocation(rectangle.x + mouseDeltaX, rectangle.y + mouseDeltaY);
					} else {
						rectangleMove.setSize(rectangle.width + mouseDeltaX, rectangle.height + mouseDeltaY);
					}
				}
				break;
		}
	}

}
