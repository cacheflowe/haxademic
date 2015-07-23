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
		
		// debug display
		_kinectGrid = new KinectRegionGrid(2, 2, 1000, 2000, 0, 40, 0, 480, 20, 10, true);
		// no debug display - control only
		// _kinectGrid = new KinectRegionGrid(2, 2, 1000, 2000, 40, 0, 480, 20, 10);
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
		
		_kinectGrid.update();
		_kinectGrid.drawDebug(p.g);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if (p.key == ' ') {
			_kinectGrid.toggleDebugOverhead();
		}
	}
			
}

