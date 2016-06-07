package com.haxademic.core.hardware.kinect;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.OpenGLUtil;

import controlP5.ControlP5;

public class KinectConfigureApp
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public int kinectLeft = 0;
	public int kinectRight = 0;
	public int kinectTop = 0;
	public int kinectBottom = 0;
	public int kinectNear = 0;
	public int kinectFar = 0;
	public int pixelSkip = 0;
	public int depthDivider = 1;
	public float pixelDrawSize = 1;
	protected ControlP5 _cp5;

	protected boolean _isDebug = false;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "480" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, "true" );
		p.appConfig.setProperty( "kinect_top_pixel", "0" );
		p.appConfig.setProperty( "kinect_bottom_pixel", "480" );
		p.appConfig.setProperty( "kinect_mirrored", "false" );
		p.appConfig.setProperty( "kinect_flipped", "false" );
	}

	public void setup() {
		super.setup();	
		p.smooth(OpenGLUtil.SMOOTH_LOW);

		int controlY = 0;
		int controlSpace = 12;
		_cp5 = new ControlP5(this);
		_cp5.addSlider("kinectLeft").setPosition(20,controlY+=controlSpace).setWidth(200).setRange(0,KinectSize.WIDTH/2).setValue(0);
		_cp5.addSlider("kinectRight").setPosition(20,controlY+=controlSpace).setWidth(200).setRange(KinectSize.WIDTH/2,KinectSize.WIDTH).setValue(KinectSize.WIDTH);
		_cp5.addSlider("kinectTop").setPosition(20,controlY+=controlSpace).setWidth(200).setRange(0,KinectSize.HEIGHT/2).setValue(0);
		_cp5.addSlider("kinectBottom").setPosition(20,controlY+=controlSpace).setWidth(200).setRange(KinectSize.HEIGHT/2,KinectSize.HEIGHT).setValue(KinectSize.HEIGHT);
		_cp5.addSlider("kinectNear").setPosition(20,controlY+=controlSpace).setWidth(200).setRange(300,12000).setValue(300);
		_cp5.addSlider("kinectFar").setPosition(20,controlY+=controlSpace).setWidth(200).setRange(300,12000).setValue(12000);
		_cp5.addSlider("pixelSkip").setPosition(20,controlY+=controlSpace).setWidth(200).setRange(1,10).setValue(5);
		_cp5.addSlider("depthDivider").setPosition(20,controlY+=controlSpace).setWidth(200).setRange(1,100).setValue(50);
		_cp5.addSlider("pixelDrawSize").setPosition(20,controlY+=controlSpace).setWidth(200).setRange(0,1).setValue(0.5f);
	}

	public void drawApp() {
		background(0);
		
		p.pushMatrix();
		
		p.rotateY(P.map(p.mouseX, 0, p.width, 0f, 1f));
		
		p.fill(0,127);
		p.rect(10, 10, 280, 12 * 10);
		
		int numPixelsProcessed = 0;

		p.fill(255f);
		p.noStroke();
		float pixelsize = (float) pixelSkip * pixelDrawSize;
		for ( int x = kinectLeft; x < kinectRight; x += pixelSkip ) {
			for ( int y = kinectTop; y < kinectBottom; y += pixelSkip ) {
				int pixelDepth = p.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
				if( pixelDepth != 0 && pixelDepth > kinectNear && pixelDepth < kinectFar ) {
					p.translate(0, 0, -pixelDepth/depthDivider);
					if(_isDebug == false) {
						if(p.appConfig.getBoolean("kinect_flipped", false) == false) {
							p.fill(p.kinectWrapper.getRgbImage().get(x, y));
						} else {
							p.fill(p.kinectWrapper.getRgbImage().get(KinectSize.WIDTH - 1 - x, KinectSize.HEIGHT- 1 - y));
						}
					}
					p.rect(x, y, pixelsize, pixelsize);
					p.translate(0, 0, pixelDepth/depthDivider);
					numPixelsProcessed++;
				}
			}
		}

		p.fill(255);
		p.text(numPixelsProcessed, 20, p.height - 20);
		
		p.popMatrix();
	}
	
	public void handleInput( boolean isMidi ) {
		super.handleInput( isMidi );
		
		if( p.key == 'd' ){
			_isDebug = !_isDebug;
		}
	}
	

}
