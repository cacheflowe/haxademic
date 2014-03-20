package com.haxademic.sketch.hardware.kinect_openni;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.hardware.kinect.KinectRegionGrid;


@SuppressWarnings("serial")
public class KinectHumanJoysticks 
extends PAppletHax {
	
	protected KinectRegionGrid _kinectGrid;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "kinect_active", "true" );
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "480" );
	}
	
	public void setup() {
		super.setup();
		
		_kinectGrid = new KinectRegionGrid(p, 2, 1, 500, 1000, 40, 0, 480);
	}

	public void drawApp() {		
		// reset drawing 
		DrawUtil.resetGlobalProps( p );
		p.shininess(1000f); 
		p.lights();
		p.background(0);
		
		DrawUtil.setDrawCorner(p);
		DrawUtil.setColorForPImage(p);
		// p.image( p.kinectWrapper.getRgbImage(), 0, 0);
		
		// _kinectGrid.update();
		_kinectGrid.updateDebug();
		
		// P.println( _kinectGrid.getRegion(0).controlX() + " , " + _kinectGrid.getRegion(0).controlZ() );
	}
	
	public void keyPressed() {
		super.keyPressed();
		if (p.key == ' ') {
			_kinectGrid.toggleDebugOverhead();
		}
	}
			
}

