package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.KinectWrapperV1;
import com.haxademic.core.hardware.depthcamera.cameras.KinectWrapperV2;
import com.haxademic.core.math.MathUtil;

public class Demo_KinectV2_AndKinectV1
extends PAppletHax {

	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected IDepthCamera kinectWrapperV1;
	protected IDepthCamera kinectWrapperV2;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1200 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 900 );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
//		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 12 );
	}

	public void setupFirstFrame() {
		kinectWrapperV1 = new KinectWrapperV1( p,  p.appConfig.getBoolean( "kinect_rgb", true ), p.appConfig.getBoolean( "kinect_depth_image", true ) );
		kinectWrapperV1.setMirror( p.appConfig.getBoolean( "kinect_mirrored", true ) );
		
		kinectWrapperV2 = new KinectWrapperV2( p, p.appConfig.getBoolean( "kinect_depth", true ), p.appConfig.getBoolean( "kinect_rgb", true ), p.appConfig.getBoolean( "kinect_depth_image", true ) );
		kinectWrapperV2.setMirror( p.appConfig.getBoolean( "kinect_mirrored", true ) );
	}
	
	public void drawApp() {
		p.background(0);
		
//		p.image(p.webCamWrapper.getImage(), 0, 0);
		
		float imgWidth = 400;
		
		// update kinects
		kinectWrapperV1.update();
		kinectWrapperV2.update();
		
		// kinect v1
		p.debugView.setTexture(kinectWrapperV1.getRgbImage());
		p.debugView.setTexture(kinectWrapperV1.getDepthImage());
//		
		p.image(kinectWrapperV1.getDepthImage(), 0, 0, imgWidth, kinectWrapperV1.getDepthImage().height * MathUtil.scaleToTarget(kinectWrapperV1.getDepthImage().width, imgWidth));
		p.image(kinectWrapperV1.getRgbImage(), imgWidth * 2f, 0, imgWidth, kinectWrapperV1.getRgbImage().height * MathUtil.scaleToTarget(kinectWrapperV1.getRgbImage().width, imgWidth));
		
		// kinect v2
		p.debugView.setTexture(kinectWrapperV2.getRgbImage());
		p.debugView.setTexture(kinectWrapperV2.getIRImage());
		p.debugView.setTexture(kinectWrapperV2.getDepthImage());

		p.image(kinectWrapperV2.getDepthImage(), 0, imgWidth, imgWidth, kinectWrapperV2.getDepthImage().height * MathUtil.scaleToTarget(kinectWrapperV2.getDepthImage().width, imgWidth));
		p.image(kinectWrapperV2.getIRImage(), imgWidth, imgWidth, imgWidth, kinectWrapperV2.getIRImage().height * MathUtil.scaleToTarget(kinectWrapperV2.getIRImage().width, imgWidth));
		p.image(kinectWrapperV2.getRgbImage(), imgWidth * 2f, imgWidth, imgWidth, kinectWrapperV2.getRgbImage().height * MathUtil.scaleToTarget(kinectWrapperV2.getRgbImage().width, imgWidth));
	}
	
	public void stop() {
		kinectWrapperV1.stop();
		kinectWrapperV2.stop();
		super.stop();
	}


}
