package com.haxademic.demo.hardware.depthcamera.simpleopenni;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.hardware.depthcamera.KinectSilhouetteBasic;
import com.haxademic.core.hardware.depthcamera.KinectSilhouetteVectorField;

public class Demo_KinectSilhouetteVectorField
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected KinectSilhouetteBasic _silhouette;

	protected boolean _isDebug = false;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "640" );
		Config.setProperty( AppSettings.HEIGHT, "480" );
		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		Config.setProperty( AppSettings.KINECT_ACTIVE, "true" );
		Config.setProperty( "kinect_top_pixel", "0" );
		Config.setProperty( "kinect_bottom_pixel", "480" );
		Config.setProperty( "kinect_left_pixel", "0" );
		Config.setProperty( "kinect_right_pixel", "640" );
		Config.setProperty( "kinect_pixel_skip", "3" );
		Config.setProperty( "kinect_scan_frames", "400" );
		Config.setProperty( "kinect_depth_key_dist", "400" );
		Config.setProperty( "kinect_mirrored", "true" );
		
		Config.setProperty( "kinect_top_pixel", "130" );
		Config.setProperty( "kinect_bottom_pixel", "380" );
		Config.setProperty( "kinect_left_pixel", "90" );
		Config.setProperty( "kinect_right_pixel", "570" );
		
		Config.setProperty( "kinect_blob_bg_int", "80" );
	}

	public void firstFrame() {

//		_silhouette = new KinectSilhouetteBasic(false, true);
		_silhouette = new KinectSilhouetteVectorField(false, true);
	}
	
	public void drawApp() {
		p.background(0);
		boolean clearsCanvas = false; // adds feedback or not
		_silhouette.update(clearsCanvas);
		if(!_isDebug) {
			p.image(_silhouette._canvas, 0, 0);
		} else {
			p.image(_silhouette._kinectPixelated, 0, 0);
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		
		if( p.key == 'd' ){
			_isDebug = !_isDebug;
		}
	}
	
}