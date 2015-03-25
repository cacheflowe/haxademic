package com.haxademic.sketch.hardware.leap;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.hardware.leap.LeapRegionGrid;

@SuppressWarnings("serial")
public class LeapGrid 
extends PAppletHax {
	

	protected LeapRegionGrid _leapGrid;

	protected void overridePropsFile() {
		_appConfig.setProperty( "fills_screen", "false" );
		_appConfig.setProperty( "leap_active", "true" );
		_appConfig.setProperty( "width", "1200" );
		_appConfig.setProperty( "height", "900" );
	}

	public void setup() {
		super.setup();
		DebugUtil.printErr("Make sure to run Processing LEAP Apps with JavaSE-1.7");
		// debug display
		_leapGrid = new LeapRegionGrid(2, 3, 2, 0.05f);
		// _leapGrid = new LeapRegionGrid(2, 3, 2, 0.05f, 0, 100, true);
		// no debug display - control only
		// _leapGrid = new LeapRegionGrid(2, 3, 2, 0.05f, 0, 100);
	}

	public void drawApp() {
		// reset drawing 
		DrawUtil.resetGlobalProps( p );
		p.shininess(1000f); 
		p.lights();
		p.background(0);
		
		DrawUtil.setDrawCorner(p);
		DrawUtil.setColorForPImage(p);
		
		_leapGrid.update();
		_leapGrid.drawDebug(p.g);
	}
	
}
