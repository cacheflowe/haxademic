package com.haxademic.demo.hardware.leapmotion;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.leap.LeapRegionGrid;

import de.voidplus.leapmotion.LeapMotion;

public class Demo_LeapRegionGrid 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public LeapMotion leapMotion = null;
	protected LeapRegionGrid _leapGrid;

	public void firstFrame() {
		leapMotion = new LeapMotion(this);
		// debug display
//		_leapGrid = new LeapRegionGrid(leapMotion, 2, 3, 2, 0.05f);
		 _leapGrid = new LeapRegionGrid(leapMotion, 2, 3, 2, 0.05f, 0, 100, true);
		// no debug display - control only
		// _leapGrid = new LeapRegionGrid(leapMotion, 2, 3, 2, 0.05f, 0, 100);
	}

	public void drawApp() {
		p.background(255);
		
		_leapGrid.update();
		_leapGrid.drawDebug(p.g);
		DebugView.setValue("leap x", _leapGrid.getRegion(0).controlX());
	}
	
}
