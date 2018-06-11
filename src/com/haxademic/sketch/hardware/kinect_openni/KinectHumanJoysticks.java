package com.haxademic.sketch.hardware.kinect_openni;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.hardware.kinect.KinectRegionGrid;


public class KinectHumanJoysticks 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected KinectRegionGrid _kinectGrid;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, "true" );
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "480" );
	}
	
	public void setup() {
		super.setup();
		
		// debug display
		_kinectGrid = new KinectRegionGrid(2, 2, 1000, 2000, 40, 0, 480, 20, 10);
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

