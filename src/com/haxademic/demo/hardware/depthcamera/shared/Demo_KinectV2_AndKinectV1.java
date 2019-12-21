package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.KinectWrapperV1;
import com.haxademic.core.hardware.depthcamera.cameras.KinectWrapperV2;
import com.haxademic.core.math.MathUtil;

public class Demo_KinectV2_AndKinectV1
extends PAppletHax {

	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected IDepthCamera kinectWrapperV1;
	protected IDepthCamera kinectWrapperV2;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1200 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 900 );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	public void setupFirstFrame() {
		kinectWrapperV1 = new KinectWrapperV1( p, true, true);
		kinectWrapperV1.setMirror(true);

		kinectWrapperV2 = new KinectWrapperV2( p, true, true);
		kinectWrapperV2.setMirror(true);
	}

	public void drawApp() {
		p.background(0);

		float imgWidth = 400;

		// update kinects
		kinectWrapperV1.update();
		kinectWrapperV2.update();

		// kinect v1
		DebugView.setTexture("kinectWrapperV1.getRgbImage", kinectWrapperV1.getRgbImage());
		DebugView.setTexture("kinectWrapperV1.getDepthImage", kinectWrapperV1.getDepthImage());
//
		p.image(kinectWrapperV1.getDepthImage(), 0, 0, imgWidth, kinectWrapperV1.getDepthImage().height * MathUtil.scaleToTarget(kinectWrapperV1.getDepthImage().width, imgWidth));
		p.image(kinectWrapperV1.getRgbImage(), imgWidth * 2f, 0, imgWidth, kinectWrapperV1.getRgbImage().height * MathUtil.scaleToTarget(kinectWrapperV1.getRgbImage().width, imgWidth));

		// kinect v2
		DebugView.setTexture("kinectWrapperV2.getRgbImage", kinectWrapperV2.getRgbImage());
		DebugView.setTexture("kinectWrapperV2.getIRImage", kinectWrapperV2.getIRImage());
		DebugView.setTexture("kinectWrapperV2.getDepthImage", kinectWrapperV2.getDepthImage());

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
