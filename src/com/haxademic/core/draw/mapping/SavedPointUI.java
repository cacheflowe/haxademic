package com.haxademic.core.draw.mapping;

import java.awt.Point;

import com.haxademic.core.app.P;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class SavedPointUI {

	public static SavedPointUI DRAGGING_POINT;
	protected float mouseActiveDist = 15;
	protected PVector position = new PVector();

	protected boolean isHovered = false;
	protected PVector mousePoint = new PVector();
	
	protected String filePath = null;
	protected boolean writesToFile = false;
	
	protected static int lastInteractTime = 0;
	protected int INTERACTION_TIMEOUT = 5000;
	protected boolean active = false;
	protected boolean shiftDown = false;

	public SavedPointUI(int x, int y, String filePath) {
		this.filePath = filePath;
		position.set(x, y);
		reset();
		
		P.p.registerMethod("mouseEvent", this); // add mouse listeners
		P.p.registerMethod("keyEvent", this);
		
		// prep txt file
		if(filePath != null) {
			writesToFile = true;
			loadMappingFile();
			createMappingFile();
		}
	}
	
	// public points setters
	
	public void setActive(boolean newActive) {
		if(active != newActive) {		// only toggle when dirty
			active = newActive;
			if(active) {
				resetInteractionTimeout();
				DRAGGING_POINT = this;
			}
		}
	}

	public void reset() {
		position.set(0, 0);
		if(filePath != null) save(); // only save after init
	}
	
	public void setPosition(float x, float y) {
		position.set(x, y);
		save();
	}
	
	public PVector position() {
		return position;
	}
	
	// state getters
	
	public boolean isActive() {
		return active;
	}
	
	public boolean isHovered() {
		return isHovered;
	}

	// USER INTERFACE ////////////////////////////////////////////
	
	public boolean isShowing() {
		return P.p.millis() < lastInteractTime + INTERACTION_TIMEOUT;
	}
	
	public void drawDebug(PGraphics pg, boolean offscreen) {
		if(isShowing())  {
			if(offscreen) pg.beginDraw();
			drawPoint(pg);
			if(offscreen) pg.endDraw();
		}
	}

	protected void drawPoint(PGraphics pg) {
		PG.setDrawCenter(pg);
		pg.noFill();
		if(active) {
			pg.stroke(0, 255, 0);
		} else {
			pg.stroke(255);
		}
		pg.strokeWeight((active) ? 3 : 1.5f);
		float indicatorSize = 20f + 3f * P.sin(P.p.frameCount / 10f);
		pg.ellipse(position.x, position.y, indicatorSize, indicatorSize);
		pg.strokeWeight(1f);
		pg.rect(position.x, position.y, 3, 3);
		PG.setDrawCorner(pg);
	}
	
	public void mouseEvent(MouseEvent event) {
		resetInteractionTimeout();
		mousePoint.set( event.getX(), event.getY() );
		checkHover(event);
		if(active == false) return;
		checkMouseDrag(event);
	}
	
	public void checkHover(MouseEvent event) {
		switch (event.getAction()) {
		case MouseEvent.MOVE:
			isHovered = (position.dist( mousePoint ) < mouseActiveDist);
			if(active && !isHovered) active = false;
			break;
		}
	}
	
	public void checkMouseDrag(MouseEvent event) {
		switch (event.getAction()) {
		case MouseEvent.PRESS:
			if(isHovered == true) {
				DRAGGING_POINT = this;
			}
			break;
		case MouseEvent.DRAG:
			if(DRAGGING_POINT == this) {
				position.set(mousePoint.x, mousePoint.y);
			}
			break;
		case MouseEvent.RELEASE:
			if(DRAGGING_POINT == this) save();
			DRAGGING_POINT = null;
			break;
		}
	}
	
	public void keyEvent(KeyEvent e) {
		if(active == false) return;
		if(e.getAction() == KeyEvent.PRESS) {
			// shift
			if(e.getKeyCode() == P.SHIFT) shiftDown = true;
			// reset timeout
			if(e.getKeyCode() == P.UP || e.getKeyCode() == P.LEFT || e.getKeyCode() == P.RIGHT || e.getKeyCode() == P.DOWN) resetInteractionTimeout();
			// translate if arrow key
			Point translatePoint = new Point(0, 0);
			if(e.getKeyCode() == P.UP) translatePoint.setLocation(0, -1);
			if(e.getKeyCode() == P.LEFT) translatePoint.setLocation(-1, 0);
			if(e.getKeyCode() == P.RIGHT) translatePoint.setLocation(1, 0);
			if(e.getKeyCode() == P.DOWN) translatePoint.setLocation(0, 1);
			if(shiftDown) { translatePoint.x *= 10; translatePoint.y *= 10; }
			// apply transformation if needed
			if(translatePoint.x != 0 || translatePoint.y != 0) {
				position.add(translatePoint.x, translatePoint.y);
				save();
			}
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
			position.set(ConvertUtil.stringToInt(posArray[0]), ConvertUtil.stringToInt(posArray[1]));
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
	
	protected void save() {
		if(this.filePath != null) writeToFile();	
		
	}
	
	protected void writeToFile() {
		if(writesToFile == false) return;
		String coordsStr = String.join(",", new String[] {
				Integer.toString((int) position.x),
				Integer.toString((int) position.y),
		});
		FileUtil.writeTextToFile(filePath, coordsStr);
	}

	public void enableFileWriting(boolean writes) {
		writesToFile = writes;
	}

}
