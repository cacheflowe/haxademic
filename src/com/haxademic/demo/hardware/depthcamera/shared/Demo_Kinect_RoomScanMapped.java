package com.haxademic.demo.hardware.depthcamera.shared;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.KinectRoomScanDiff;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.system.SystemUtil;

import processing.core.PGraphics;

public class Demo_Kinect_RoomScanMapped
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected KinectRoomScanDiff kinectDiff;
	protected PGraphics mappedKinectBuffer;
	protected PGraphicsKeystone mappedKinectKeystone;
	protected boolean DEBUG = false;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 720 );
	}

	protected void firstFrame() {
		DepthCamera.instance(DepthCameraType.KinectV1);
		kinectDiff = new KinectRoomScanDiff(DepthCamera.instance().camera);
		mappedKinectBuffer = PG.newPG(pg.width, pg.height);
		mappedKinectKeystone = new PGraphicsKeystone( p, kinectDiff.resultSmoothed(), 12, FileUtil.getPath("text/keystoning/keystone-kinect.txt") );

	}
	
	public void resetKinectRoomScan() {
		kinectDiff.reset();
	}
	
	public PGraphics kinectDiffImage() {
		return kinectDiff.depthDifference();
	}
	
	public PGraphics kinectDepthBuffer() {
		return kinectDiff.depthBuffer();
	}
	
	public PGraphicsKeystone mappedKinectKeystone() {
		return mappedKinectKeystone;
	}
	
	protected void drawApp() {
		p.background(0, 127, 0);
		
		// update kinect room scan
		kinectDiff.update();
		
		// draw debug room scan
		p.image(kinectDiff.roomScanBuffer(), 0, 0);
		p.image(kinectDiff.depthBuffer(), kinectDiff.roomScanBuffer().width, 0);
		p.image(kinectDiff.resultSmoothed(), kinectDiff.roomScanBuffer().width * 2, 0);
		
		// draw mapped kinect buffer to offscreen buffer
		mappedKinectKeystone.setActive(DEBUG);
		mappedKinectBuffer.beginDraw();
		mappedKinectBuffer.background(0);
		mappedKinectKeystone.drawTestPattern();
		mappedKinectKeystone.update(mappedKinectBuffer);
		mappedKinectBuffer.endDraw();
		
		// then draw result to screen
		p.image(mappedKinectBuffer, 0, 0);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') kinectDiff.reset();
		if(p.key == 'r') SystemUtil.setTimeout(delayedReset, 4000);
		if(p.key == 'd') DEBUG = !DEBUG;
		if(p.key == 'k') mappedKinectKeystone.setPosition(pg.width / 2, pg.height / 2, pg.width, pg.height);
	}
	
	protected ActionListener delayedReset = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			kinectDiff.reset();
		}
	};
}
