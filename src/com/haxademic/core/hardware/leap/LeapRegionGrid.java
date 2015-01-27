package com.haxademic.core.hardware.leap;

import java.util.ArrayList;

import processing.core.PGraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.joystick.IJoystickCollection;
import com.haxademic.core.hardware.joystick.IJoystickControl;
import com.haxademic.core.math.MathUtil;

public class LeapRegionGrid
implements IJoystickCollection {

	protected PGraphics _pg;
	protected int _leapClose = 0;
	protected int _leapFar = 0;
	protected int _leapDepth = 0;

	public ArrayList<IJoystickControl> leapRegions;
	
	public LeapRegionGrid(int cols, int rows, int depthCells, float padding, int leapClose, int leapFar) {
		this(cols, rows, depthCells, padding, leapClose, leapFar, false);
	}
	
	public LeapRegionGrid(int cols, int rows, int depthCells, float padding, int leapClose, int leapFar, boolean debug) {
		int sketchW = P.p.width;
		int sketchH = P.p.height;
		
		if(debug == true) {
			_pg = P.p.createGraphics(sketchW, sketchH, P.OPENGL);
		}
		
		_leapClose = leapClose;
		_leapFar = leapFar;
		_leapDepth = _leapFar - _leapClose;
		
		// set up rectangles for position detection
		leapRegions = new ArrayList<IJoystickControl>();
		int paddingW = Math.round(padding * sketchW);
		int colW = (sketchW - paddingW*(cols-1)) / cols;
		int paddingH = Math.round(padding * sketchH);
		int rowH = (sketchH - paddingH*(rows-1)) / rows;
		int paddingDepth = Math.round(padding * _leapDepth);
		int depthSize = (_leapDepth - paddingDepth*(cols-1)) / depthCells;	 // 0-100 is the general leap depth range 
		
		// create grid cells
		for ( int x = 0; x < cols; x++ ) {
			for ( int y = 0; y < rows; y++ ) {
				for ( int z = 0; z < depthCells; z++ ) {
					LeapRegion region = new LeapRegion(
						colW * x + paddingW * x, 
						colW * x + paddingW * x + colW, 
						rowH * y + paddingH * y, 
						rowH * y + paddingH * y + rowH, 
						leapClose + z * depthSize + paddingDepth * z,
						leapClose + z * depthSize + paddingDepth * z + depthSize,
						P.p.color( MathUtil.randRange(130,255), MathUtil.randRange(130,255), MathUtil.randRange(130,255) )
					);
					leapRegions.add( region );
				}
			}
		}
	}
	
	public IJoystickControl getRegion( int index ) {
		return leapRegions.get(index);
	}
	
	public void update() {
		if(_pg == null) {
			updateRegions();			
		} else {
			updateDebug();
		}
	}
	
	public void updateRegions() {
		for( int i=0; i < leapRegions.size(); i++ ) {
			leapRegions.get(i).detect(_pg);
		}
	}
	
	public void updateDebug() {		
		_pg.beginDraw();
		_pg.clear();
		
		_pg.shininess(1000f); 
		_pg.lights();

		
		// draw regions' rectangles ----------------------------
		_pg.pushMatrix();
		
		_pg.translate(0, 0, -300);
				
		// loop through leap data within rectangles ----------
		updateRegions();
		
		for( int i=0; i < leapRegions.size(); i++ ) {
			leapRegions.get(i).drawDebug(_pg);
		}
		
		_pg.popMatrix();
		
		_pg.endDraw();
	}
	
	public void drawDebug(PGraphics pg) {
		if(_pg == null) return;
		pg.image(_pg, 0, 0);
	}

}
