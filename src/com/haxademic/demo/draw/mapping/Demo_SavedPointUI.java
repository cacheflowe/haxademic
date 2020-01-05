package com.haxademic.demo.draw.mapping;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.mapping.SavedPointUI;
import com.haxademic.core.file.FileUtil;

public class Demo_SavedPointUI
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	SavedPointUI[] savedPoints;
	protected int pointIndex = -1;
	protected int rows = 4;
	protected int cols = 5;

	protected void firstFrame() {
		savedPoints = new SavedPointUI[20];
		for (int i = 0; i < savedPoints.length; i++) {
			savedPoints[i] = new SavedPointUI(0, 0, FileUtil.getPath("text/keystoning/points-demo"+i+".txt"));
		}
	}
	
	protected void resetPoints() {
		for (int i = 0; i < savedPoints.length; i++) {
			float col = i % cols;
			float row = P.floor((float) i / cols);
			float x = P.map(col, 0, cols - 1, 0.2f * p.width, 0.8f * p.width);
			float y = P.map(row, 0, rows - 1, 0.2f * p.height, 0.8f * p.height);
			savedPoints[i].setPosition(x, y);
		}
	}

	protected void drawApp() {
		p.background(0);
		
		// draw points 
		for (int i = 0; i < savedPoints.length; i++) {
			savedPoints[i].drawDebug(p.g, false);
		}
	}
	
	// UI INTERACTION

	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'r') resetPoints();
		if(p.key == ']') {
			pointIndex++;
			if(pointIndex >= savedPoints.length) pointIndex = 0;
			setActivePoint();
		}
		if(p.key == '[') {
			pointIndex--;
			if(pointIndex < 0) pointIndex = savedPoints.length - 1;
			setActivePoint();
		}
	}
	
	public void mouseMoved() {
		super.mouseMoved();
		// set hovered point as active
		boolean foundOne = false;
		for (int i = 0; i < savedPoints.length; i++) {
			if(savedPoints[i].isHovered()) {
				pointIndex = i;
				foundOne = true;
			}
		}
		if(foundOne == false) pointIndex = -1;
		setActivePoint();
	}
	
	protected void setActivePoint() {
		for (int i = 0; i < savedPoints.length; i++) {
			savedPoints[i].setActive(i == pointIndex);
		}
	}

}
