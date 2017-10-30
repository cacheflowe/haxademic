package com.haxademic.sketch.hardware.kinect_openni;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.hardware.kinect.KinectAmbientActivityMonitor;


public class KinectAmbientActivity 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public static final float PIXEL_SIZE = 6;
	public static final int KINECT_TOP = 0;
	public static final int KINECT_BOTTOM = 480;
	public static final int KINECT_CLOSE = 500;
	public static final int KINECT_FAR = 10000;
		
	protected KinectAmbientActivityMonitor _kinectMonitor;
	
	public void setup() {
		super.setup();
		_kinectMonitor = new KinectAmbientActivityMonitor( PIXEL_SIZE, KINECT_CLOSE, KINECT_FAR );
	}

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, "true" );
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "480" );
	}
	
	public void drawApp() {
		DrawUtil.resetGlobalProps( p );
		p.shininess(1000f); 
		p.lights();
		p.background(0);
		
		// draw filtered web cam
		DrawUtil.setDrawCorner(p);
		DrawUtil.setColorForPImage(p);
		
		p.image( p.kinectWrapper.getRgbImage(), 0, 0);


		// loop through kinect data within player's control range
//		p.stroke(255, 127);
//		float pixelDepth;
//		for ( int x = 0; x < KinectSize.WIDTH; x += PIXEL_SIZE ) {
//			for ( int y = KINECT_TOP; y < KINECT_BOTTOM; y += PIXEL_SIZE ) {
//				pixelDepth = p.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
//				if( pixelDepth != 0 && pixelDepth > KINECT_CLOSE && pixelDepth < KINECT_FAR ) {
//					p.pushMatrix();
//					p.fill(((pixelDepth - KINECT_CLOSE) / (KINECT_FAR - KINECT_CLOSE)) * 255f);
//					p.rect(x, y, PIXEL_SIZE, PIXEL_SIZE);
//					p.popMatrix();
//				}
//			}
//		}
		
		_kinectMonitor.update(p.kinectWrapper, true );
	}
	
}
