package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;


public class Demo_DepthCamera_FloorWatch 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public static float PIXEL_SIZE = 7;
	public static int KINECT_TOP = 220;
	public static int KINECT_BOTTOM = 240;
	public static int KINECT_CLOSE = 1000;
	public static int KINECT_FAR = 5700;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 480 );
	}
	
	protected void firstFrame() {
		DepthCamera.instance(DepthCameraType.KinectV1);
	}

	protected void drawApp() {
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		p.background(0);
		
		// draw filtered web cam
		PG.setDrawCenter(p);
		PG.setColorForPImage(p);
		
		KINECT_TOP = 220;
		KINECT_BOTTOM = 240;
		
		// loop through kinect data within player's control range
		p.noStroke();
		p.stroke(255f);
		float pixelDepth;
		float avgX = 0;
		float avgY = 0;
		float numPoints = 0;
		for ( int x = 0; x < DepthCameraSize.WIDTH; x += PIXEL_SIZE ) {
			for ( int y = KINECT_TOP; y < KINECT_BOTTOM; y += PIXEL_SIZE ) {
				pixelDepth = depthCamera.getDepthAt( x, y );
				if( pixelDepth != 0 && pixelDepth > KINECT_CLOSE && pixelDepth < KINECT_FAR ) {
					p.pushMatrix();
//					p.fill(((pixelDepth - KINECT_CLOSE) / (KINECT_FAR - KINECT_CLOSE)) * 255f);
					float userZ = P.map(pixelDepth, KINECT_CLOSE, KINECT_FAR, 0, p.height);
					p.rect(x, userZ, 6, 6);
					p.popMatrix();
					
					numPoints++;
					avgX += x;
					avgY += userZ;
				}
			}
		}
		
		// show CoM
		p.ellipse(avgX / numPoints, avgY / numPoints, 20, 20);
	}
}
