package com.haxademic.sketch.hardware.kinect_openkinect;

import org.openkinect.processing.Kinect2;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;

public class Kinect2CameraTest
extends PAppletHax {

	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	Kinect2 kinect2;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1024 );
		Config.setProperty( AppSettings.HEIGHT, 828 );
	}


	public void firstFrame() {

		kinect2 = new Kinect2(this);
		kinect2.initVideo();
		kinect2.initDepth();
		kinect2.initIR();
		kinect2.initRegistered();
		// Start all data
		kinect2.initDevice();
	}

	public void drawApp() {
		p.background(0);
		p.image(kinect2.getVideoImage(), 0, 0, kinect2.colorWidth*0.267f, kinect2.colorHeight*0.267f);
		p.image(kinect2.getDepthImage(), kinect2.depthWidth, 0);
		p.image(kinect2.getIrImage(), 0, kinect2.depthHeight);
		p.image(kinect2.getRegisteredImage(), kinect2.depthWidth, kinect2.depthHeight);
		p.fill(255);
		p.text("Framerate: " + (int)(frameRate), 10, 515);
	}

}
