package com.haxademic.demo.hardware.depthcamera.kinectpv2;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageFramesHistory;

import KinectPV2.KinectPV2;
import processing.core.PGraphics;

public class Demo_KinectV2_SilhouetteRecorder
extends PAppletHax {

	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected KinectPV2 kinect;
	protected PGraphics buffer;
	protected ImageFramesHistory recorder;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1920 );
		Config.setProperty( AppSettings.HEIGHT, 1080 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.FILLS_SCREEN, false );
	}

	protected void firstFrame() {
		kinect = new KinectPV2(p);
		kinect.enableDepthImg(true);
		kinect.enableColorImg(true);
		kinect.enableDepthMaskImg(true);
		kinect.enableBodyTrackImg(true);
		kinect.enableInfraredImg(true);
		kinect.init();
		
		recorder = new ImageFramesHistory(p.width, p.height, 30);
	}
	
	protected void drawApp() {
		p.background( 0 );
		
		p.pushMatrix();
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
		p.image(kinect.getBodyTrackImage(), 0, 0);
		PG.setPImageAlpha(p, 0.5f);
		float rgbImgScale = (float) kinect.getBodyTrackImage().height / (float) kinect.getColorImage().height; 
		p.image(kinect.getColorImage(), 0, 0, kinect.getColorImage().width * rgbImgScale, kinect.getColorImage().height * rgbImgScale);
		p.popMatrix();
		
		PG.setDrawCorner(p);
		recorder.addFrame(kinect.getBodyTrackImage());
		recorder.drawDebug(p.g);
	}

}
