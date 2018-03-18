package com.haxademic.sketch.hardware.kinect_v2_windows;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.BlurHFilter;
import com.haxademic.core.draw.filters.shaders.BlurVFilter;
import com.haxademic.core.draw.filters.shaders.LeaveBlackFilter;
import com.haxademic.core.draw.filters.shaders.SaturationFilter;
import com.haxademic.core.draw.filters.shaders.SharpenFilter;
import com.haxademic.core.draw.image.ImageSequenceRecorder;
import com.haxademic.core.draw.image.ImageUtil;

import KinectPV2.KinectPV2;
import processing.core.PGraphics;

public class Kinect2SilhouetteRecorder
extends PAppletHax {

	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected KinectPV2 kinect;
	protected PGraphics buffer;
	protected ImageSequenceRecorder recorder;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, true );
	}

	public void setupFirstFrame() {
		kinect = new KinectPV2(p);
		kinect.enableDepthImg(true);
		kinect.enableColorImg(true);
		kinect.enableDepthMaskImg(true);
		kinect.enableBodyTrackImg(true);
		kinect.enableInfraredImg(true);
		kinect.init();
		
		recorder = new ImageSequenceRecorder(640, 480, 30);
	}
	
	public void drawApp() {
		p.background( 0 );
		
		p.pushMatrix();
		DrawUtil.setDrawCenter(p);
		DrawUtil.setCenterScreen(p);
		p.image(kinect.getBodyTrackImage(), 0, 0);
		DrawUtil.setPImageAlpha(p, 0.5f);
		float rgbImgScale = (float) kinect.getBodyTrackImage().height / (float) kinect.getColorImage().height; 
		p.image(kinect.getColorImage(), 0, 0, kinect.getColorImage().width * rgbImgScale, kinect.getColorImage().height * rgbImgScale);
		p.popMatrix();
		
		DrawUtil.setDrawCorner(p);
		recorder.addFrame(kinect.getBodyTrackImage());
		recorder.drawDebug(p.g);
	}

}
