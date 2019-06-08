package com.haxademic.demo.hardware.depthcamera.simpleopenni;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.hardware.depthcamera.KinectSilhouetteBasic;
import com.haxademic.core.hardware.depthcamera.KinectSilhouetteVectorField;

public class Demo_KinectSilhouetteVectorField
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected KinectSilhouetteBasic _silhouette;

	protected boolean _isDebug = false;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "480" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, "true" );
		p.appConfig.setProperty( "kinect_top_pixel", "0" );
		p.appConfig.setProperty( "kinect_bottom_pixel", "480" );
		p.appConfig.setProperty( "kinect_left_pixel", "0" );
		p.appConfig.setProperty( "kinect_right_pixel", "640" );
		p.appConfig.setProperty( "kinect_pixel_skip", "3" );
		p.appConfig.setProperty( "kinect_scan_frames", "400" );
		p.appConfig.setProperty( "kinect_depth_key_dist", "400" );
		p.appConfig.setProperty( "kinect_mirrored", "true" );
		
		p.appConfig.setProperty( "kinect_top_pixel", "130" );
		p.appConfig.setProperty( "kinect_bottom_pixel", "380" );
		p.appConfig.setProperty( "kinect_left_pixel", "90" );
		p.appConfig.setProperty( "kinect_right_pixel", "570" );
		
		p.appConfig.setProperty( "kinect_blob_bg_int", "80" );
	}

	public void setup() {
		super.setup();
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