package com.haxademic.sketch.hardware.kinect_openni;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.hardware.kinect.KinectWrapper;
import com.haxademic.core.image.filters.PixelFilter;


@SuppressWarnings("serial")
public class KinectPixelated 
extends PAppletHax {

	public static final float PIXEL_SIZE = 7;
	public static final int KINECT_TOP = 0;
	public static final int KINECT_BOTTOM = 480;
	public static final int KINECT_CLOSE = 1000;
	public static final int KINECT_FAR = 3000;
	
	protected PixelFilter _pixelFilter;
	
	public void setup() {
		super.setup();
		_pixelFilter = new PixelFilter(KinectWrapper.KWIDTH, KinectWrapper.KHEIGHT, (int)PIXEL_SIZE);
	}

	protected void overridePropsFile() {
		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "kinect_active", "true" );
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "480" );
	}
	
	public void drawApp() {
		DrawUtil.resetGlobalProps( p );
		p.shininess(1000f); 
		p.lights();
		p.background(0);
		
		// draw filtered web cam
		DrawUtil.setDrawCorner(p);
		DrawUtil.setColorForPImage(p);
		
		p.image(_pixelFilter.updateWithPImage( p.kinectWrapper.getRgbImage() ), 0, 0);


		// loop through kinect data within player's control range
		p.stroke(255, 127);
		float pixelDepth;
		for ( int x = 0; x < KinectWrapper.KWIDTH; x += PIXEL_SIZE ) {
			for ( int y = KINECT_TOP; y < KINECT_BOTTOM; y += PIXEL_SIZE ) {
				pixelDepth = p.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
				if( pixelDepth != 0 && pixelDepth > KINECT_CLOSE && pixelDepth < KINECT_FAR ) {
					p.pushMatrix();
					p.fill(((pixelDepth - KINECT_CLOSE) / (KINECT_FAR - KINECT_CLOSE)) * 255f);
					p.rect(x, y, PIXEL_SIZE, PIXEL_SIZE);
					p.popMatrix();
				}
			}
		}
	}
}
