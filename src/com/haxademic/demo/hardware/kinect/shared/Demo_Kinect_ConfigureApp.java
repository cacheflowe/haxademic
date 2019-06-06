package com.haxademic.demo.hardware.kinect.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.kinect.KinectSize;

public class Demo_Kinect_ConfigureApp
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected String kinectLeft = "kinectLeft";
	protected String kinectRight = "kinectRight";
	protected String kinectTop = "kinectTop";
	protected String kinectBottom = "kinectBottom";
	protected String kinectNear = "kinectNear";
	protected String kinectFar = "kinectFar";
	protected String pixelSkip = "pixelSkip";
	protected String depthDivider = "depthDivider";
	protected String pixelDrawSize = "pixelDrawSize";

	protected boolean _isDebug = false;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
//		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, true );
		p.appConfig.setProperty(AppSettings.SHOW_SLIDERS, true);
	}
	
	public void setupFirstFrame() {
		p.ui.addSlider(kinectLeft, 0, 0, KinectSize.WIDTH/2, 1, false);
		p.ui.addSlider(kinectRight, KinectSize.WIDTH, KinectSize.WIDTH/2,KinectSize.WIDTH, 1, false);
		p.ui.addSlider(kinectTop, 0, 0, KinectSize.HEIGHT/2, 1, false);
		p.ui.addSlider(kinectBottom, KinectSize.HEIGHT, KinectSize.HEIGHT/2,KinectSize.HEIGHT, 1, false);
		p.ui.addSlider(kinectNear, 300, 300, 12000, 1, false);
		p.ui.addSlider(kinectFar, 12000, 300, 12000, 1, false);
		p.ui.addSlider(pixelSkip, 5, 1, 10, 1, false);
		p.ui.addSlider(depthDivider, 50, 1, 100, 0.1f, false);
		p.ui.addSlider(pixelDrawSize, 0.5f, 0, 1, 0.01f, false);
	}

	public void drawApp() {
		background(0);
		PG.setDrawCorner(p);

		p.pushMatrix();
		
//		kinectWrapper.drawPointCloudForRect(p, true, pixelSkip, 1f, 1, kinectNear, kinectFar, kinectTop, kinectRight, kinectBottom, kinectLeft);
//		p.rotateY(P.map(p.mouseX, 0, p.width, 0f, 1f));
		
		// draw controls background
		p.noStroke();
		p.fill(0,127);
		p.rect(10, 10, 280, 12 * 10);
		
		// move kinect depth pixels over
		p.translate(290, 20);
		
		// draw frame
		p.noFill();
		p.stroke(0, 255, 0);
		p.strokeWeight(0.5f);
		p.rect(0, 0, KinectSize.WIDTH, KinectSize.HEIGHT);

		// set depth pixel color
		p.fill(255f);
		p.noStroke();
				
		// draw kinect depth
		int pixelSkipp = p.ui.valueInt(pixelSkip);
		float kNear = p.ui.valueInt(kinectNear);
		float kFar = p.ui.valueInt(kinectFar);
		int kLeft = p.ui.valueInt(kinectLeft);
		float kRight = p.ui.valueInt(kinectRight);
		int kTop= p.ui.valueInt(kinectTop);
		float kBottom = p.ui.valueInt(kinectBottom);
		float depthDiv = p.ui.valueInt(depthDivider);
		
		int numPixelsProcessed = 0;
		float pixelsize = (float) pixelSkipp * p.ui.value(pixelDrawSize);
		for ( int x = kLeft; x < kRight; x += pixelSkipp ) {
			for ( int y = kTop; y < kBottom; y += pixelSkipp ) {
				int pixelDepth = p.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
				if( pixelDepth != 0 && pixelDepth > kNear && pixelDepth < kFar ) {
					p.pushMatrix();
					p.translate(0, 0, -pixelDepth/depthDiv);
					p.fill(P.map(pixelDepth, kNear, kFar, 255, 0));
					p.rect(x, y, pixelsize, pixelsize);
					p.popMatrix();
					numPixelsProcessed++;
				}
			}
		}

		// debug view
		p.debugView.setTexture(p.kinectWrapper.getDepthImage());
		p.debugView.setValue("numPixelsProcessed", numPixelsProcessed);
		
		p.popMatrix();
	}
	
	public void keyPressed() {
		super.keyPressed();	
		if( p.key == 'd' ){
			_isDebug = !_isDebug;
		}
	}
	

}
