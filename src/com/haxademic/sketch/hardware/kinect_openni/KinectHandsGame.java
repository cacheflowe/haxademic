
package com.haxademic.sketch.hardware.kinect_openni;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.depthcamera.SkeletonsTracker;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.math.MathUtil;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PGraphics;
import toxi.geom.Vec3D;

public class KinectHandsGame
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected SkeletonsTracker _skeletonTracker;
	protected PGraphics _texture;
	
	protected GamePiece gamePiece;

	protected void firstFrame() {
		DepthCamera.instance(DepthCameraType.KinectV1);
		
		// do something
		_skeletonTracker = new SkeletonsTracker();
		_texture = P.p.createGraphics( p.width, p.height, P.P2D );
		
		gamePiece = new GamePiece();
	}
	
	protected void drawApp() {
		IDepthCamera depthCamera = DepthCamera.instance().camera;

		PG.resetGlobalProps( p );
		p.background(0);

		_skeletonTracker.update();
		PG.setDrawCorner(p);
		p.image( depthCamera.getRgbImage(), 0, 0 );
		PG.setDrawCorner(p);

		gamePiece.update();
				
		if( _skeletonTracker.hasASkeleton() ) {
			// set up draw colors
			p.fill(0, 255, 0);
			p.noStroke();
			drawHands();	
		}
	}
	
	protected void drawHands() {
		// loop through attractors and store the closest & our distance for coloring
		int[] users = _skeletonTracker.getUserIDs();
		for(int i=0; i < users.length; i++) {
			Vec3D position = _skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_RIGHT_HAND);
			if( position != null ) {
				float distance = MathUtil.getDistance( position.x, position.y, gamePiece.x, gamePiece.y );
				if( distance < 40 ) {
					gamePiece.reset();
				}
				p.ellipse( position.x, position.y , 30, 30 );
			}
			position = _skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_LEFT_HAND);
			if( position != null ) {
				float distance = MathUtil.getDistance( position.x, position.y, gamePiece.x, gamePiece.y );
				if( distance < 40 ) {
					gamePiece.reset();
				}
				p.ellipse( position.x, position.y , 30, 30 );
			}
		}
	}
	
	
	public class GamePiece {
		public float x;
		public float y;
		
		public GamePiece() {
			x = p.width / 2;
			y = 0;
		}
		
		public void update() {
			y += 10;
			if( y > p.height ) {
				reset();
			}
			p.rect( x, y, 50, 50 );
		}
		
		public void reset() {
			y = 0;
			x = (float) Math.random() * p.width;
		}
	}
}
