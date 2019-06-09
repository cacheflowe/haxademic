package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pgraphics.archive.PixelFilter;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;


public class Demo_Kinect_PixelatedData 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public static final float PIXEL_SIZE = 7;
	public static final int KINECT_TOP = 0;
	public static final int KINECT_BOTTOM = 480;
	public static final int KINECT_CLOSE = 1000;
	public static final int KINECT_FAR = 3000;
	
	protected PixelFilter _pixelFilter;
	
	public void setup() {
		super.setup();
		_pixelFilter = new PixelFilter(DepthCameraSize.WIDTH, DepthCameraSize.WIDTH, (int)PIXEL_SIZE);
	}

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
//		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, true );
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 480 );
	}
	
	public void drawApp() {
		PG.resetGlobalProps( p );
		p.shininess(1000f); 
		p.lights();
		p.background(0);
		
		// draw filtered web cam
		PG.setDrawCorner(p);
		PG.setColorForPImage(p);
		
		p.image(_pixelFilter.updateWithPImage( p.depthCamera.getRgbImage() ), 0, 0);


		// loop through kinect data within player's control range
		p.stroke(255, 127);
		float pixelDepth;
		for ( int x = 0; x < DepthCameraSize.WIDTH; x += PIXEL_SIZE ) {
			for ( int y = KINECT_TOP; y < KINECT_BOTTOM; y += PIXEL_SIZE ) {
				pixelDepth = p.depthCamera.getDepthAt( x, y );
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
