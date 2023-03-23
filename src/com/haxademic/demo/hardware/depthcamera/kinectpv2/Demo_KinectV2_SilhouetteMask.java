package com.haxademic.demo.hardware.depthcamera.kinectpv2;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.draw.image.ImageUtil;

import KinectPV2.KinectPV2;
import processing.core.PGraphics;

public class Demo_KinectV2_SilhouetteMask
extends PAppletHax {

	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected KinectPV2 kinect;
	protected PGraphics bufferRgb;
	protected PGraphics bufferMask;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 960 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		// init camera
		kinect = new KinectPV2(p);
		kinect.enableDepthImg(true);
		kinect.enableColorImg(true);
		kinect.enableDepthMaskImg(true);
		kinect.enableBodyTrackImg(true);
		kinect.enableInfraredImg(true);
		kinect.init();
		
		// init buffers
		bufferRgb = PG.newPG(p.width, p.height);
		bufferMask = PG.newPG(p.width, p.height);
	}
	
	protected void drawApp() {
		p.background( 0 );
		
		// crop images into buffers to match sizes
		ImageUtil.cropFillCopyImage(kinect.getColorImage(), bufferRgb, true);
		ImageUtil.cropFillCopyImage(kinect.getBodyTrackImage(), bufferMask, true);
		InvertFilter.instance().setOnContext(bufferMask);
		
		// apply mask and draw to the screen
		bufferRgb.mask(bufferMask);
		p.image(bufferRgb, 0, 0);
	}

}
