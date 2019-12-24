package com.haxademic.sketch.hardware.leap;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.leap.LeapRegionGrid;

import de.voidplus.leapmotion.LeapMotion;

public class LeapGrid 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public LeapMotion leapMotion = null;
	protected LeapRegionGrid _leapGrid;

	protected void config() {
		Config.setProperty( AppSettings.FILLS_SCREEN, "false" );
		Config.setProperty( AppSettings.WIDTH, "1200" );
		Config.setProperty( AppSettings.HEIGHT, "900" );
	}

	public void firstFrame() {
		leapMotion = new LeapMotion(this);
		DebugUtil.printErr("Make sure to run Processing LEAP Apps with JavaSE-1.7");
		// debug display
		_leapGrid = new LeapRegionGrid(leapMotion, 2, 3, 2, 0.05f);
		// _leapGrid = new LeapRegionGrid(2, 3, 2, 0.05f, 0, 100, true);
		// no debug display - control only
		// _leapGrid = new LeapRegionGrid(2, 3, 2, 0.05f, 0, 100);
	}

	public void drawApp() {
		// reset drawing 
		PG.resetGlobalProps( p );
		p.shininess(1000f); 
		p.lights();
		p.background(0);
		
		PG.setDrawCorner(p);
		PG.setColorForPImage(p);
		
		_leapGrid.update();
		_leapGrid.drawDebug(p.g);
	}
	
}
