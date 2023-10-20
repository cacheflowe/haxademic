package com.haxademic.demo.draw.mapping;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.mapping.SavedRectangle;

public class Demo_SavedRectangle
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected SavedRectangle rectangle;
	protected String RECT_ID = "RECT_ID";

	protected void firstFrame() {
		rectangle = new SavedRectangle(RECT_ID, true);
	}
	
	protected void resetPoints() {
		rectangle.set(10, 10, 100, 100);
	}

	protected void drawApp() {
		p.background(0);
		rectangle.drawDebugToPG(p.g);
	}
	
	// UI INTERACTION

	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'r') resetPoints();
	}
	
}
