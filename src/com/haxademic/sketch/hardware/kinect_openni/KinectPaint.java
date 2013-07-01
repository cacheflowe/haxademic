
package com.haxademic.sketch.hardware.kinect_openni;

import processing.core.PGraphics;
import toxi.geom.Vec3D;
import SimpleOpenNI.SimpleOpenNI;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.hardware.kinect.SkeletonsTracker;

@SuppressWarnings("serial")
public class KinectPaint
extends PAppletHax {
	
	protected SkeletonsTracker _skeletonTracker;
	protected PGraphics _texture;
	public static final int KINECT_CLOSE = 1000;
	public static final int KINECT_FAR = 1500;

	public void setup() {
		super.setup();
		
		// do something
		_skeletonTracker = new SkeletonsTracker();
		_texture = P.p.createGraphics( p.width, p.height, P.P2D );
		
		p.background(0);
	}
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "rendering", "true" );
		_appConfig.setProperty( "kinect_active", "true" );
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "480" );
	}
	
	public void drawApp() {
		DrawUtil.resetGlobalProps( p );

		_skeletonTracker.update();
//		_skeletonTracker.drawSkeletons();
				
		if( _skeletonTracker.hasASkeleton() ) {
			// set up draw colors
			p.fill(0, 255, 0, 40);
			p.noStroke();
			drawPaintbrushes();	
		} else {
			p.background(0);
		}
	}
	
	protected void drawPaintbrushes() {
		// loop through attractors and store the closest & our distance for coloring
		int[] users = _skeletonTracker.getUserIDs();
		for(int i=0; i < users.length; i++) {
			Vec3D position = _skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_RIGHT_HAND);
			if( position != null ) {
				float pixelDepth = p.kinectWrapper.getMillimetersDepthForKinectPixel( (int) position.x, (int) position.y );
				p.fill(((pixelDepth - KINECT_CLOSE) / (KINECT_FAR - KINECT_CLOSE)) * 255f);
				p.ellipse( position.x, position.y , 30, 30 );
			}
			position = _skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_LEFT_HAND);
			if( position != null ) {
				float pixelDepth = p.kinectWrapper.getMillimetersDepthForKinectPixel( (int) position.x, (int) position.y );
				p.fill(((pixelDepth - KINECT_CLOSE) / (KINECT_FAR - KINECT_CLOSE)) * 255f);
				p.ellipse( position.x, position.y , 30, 30 );
			}
		}
	}
	

}
