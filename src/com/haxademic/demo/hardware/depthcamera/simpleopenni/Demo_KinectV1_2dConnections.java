
package com.haxademic.demo.hardware.depthcamera.simpleopenni;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.toxi.VectorFlyerToxi;
import com.haxademic.core.hardware.depthcamera.SkeletonsTrackerKinectV1;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.vendor.Toxiclibs;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PGraphics;
import processing.core.PImage;
import toxi.geom.Triangle3D;
import toxi.geom.Vec3D;

public class Demo_KinectV1_2dConnections
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected SkeletonsTrackerKinectV1 _skeletonTracker;
	protected PGraphics _texture;
	
	protected void config() {
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 480 );
	}
	
	protected void firstFrame() {
		DepthCamera.instance(DepthCameraType.KinectV1);

		// do something
		_skeletonTracker = new SkeletonsTrackerKinectV1();
		_texture = P.p.createGraphics( p.width, p.height, P.P3D );
	}
	
	protected void drawApp() {
		PG.resetGlobalProps( p );

		p.shininess(1000f); 
		p.lights();
		p.background(0);

		_skeletonTracker.update();
		PG.setDrawCenter(p);
		
		// draw skeleton(s)
//		_skeletonTracker.drawSkeletons();
		
		// draw kaleidoscopes
		p.pushMatrix();
		float rotations = 8f;
		
		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
//		drawWebCam(rotations);
		p.popMatrix();
		
		if( _skeletonTracker.hasASkeleton() ) {
			// set up draw colors
			p.fill(0, 255, 0, 40);
			p.stroke(127, 255, 127, 255);
			p.strokeWeight(2f);
			_texture.beginDraw();
			_texture.noFill();
			_texture.stroke(255);
			_texture.strokeWeight(3f);
			
			// generate kinect skeleton lines / texture, then draw to screen
			ImageUtil.clearPGraphics(_texture);
			drawTriangles();	
			
			p.translate(p.width/2, p.height/2);
			drawSkeletonLines(rotations);
			_texture.endDraw();
		}

		p.popMatrix();
	}
	
	protected void drawWebCam( float rotations ) {
		IDepthCamera depthCamera = DepthCamera.instance().camera;

		// draw cam
		PG.setColorForPImage(p);
		PG.setPImageAlpha(p, 0.25f);
		PImage drawCamImg = depthCamera.getRgbImage();
//		PImage drawCamImg = getFilteredCam();
		for( int i=0; i < rotations; i++ ) {
			p.rotate((float)P.TWO_PI/rotations * (float)i);
			p.image( drawCamImg, 0, 0 );
		}
	}
	
	protected void drawSkeletonLines( float rotations ) {
		// draw kinect skeleton lines
		PG.setColorForPImage(p);
		PG.setPImageAlpha(p, 0.4f);
		for( int i=0; i < rotations; i++ ) {
			p.rotate((float)P.TWO_PI/rotations * (float)i);
			p.image( _texture, 0, 0 );
		}
	}
	
	protected void drawTriangles() {
		// loop through attractors and store the closest & our distance for coloring
		int[] users = _skeletonTracker.getUserIDs();
		for(int i=0; i < users.length; i++) { 
			draw3PointsTriangle(
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_HEAD),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_RIGHT_HAND),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_LEFT_HAND)
					);

			draw3PointsTriangle(
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_LEFT_ELBOW),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_LEFT_HAND),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_LEFT_SHOULDER)
					);
			draw3PointsTriangle(
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_RIGHT_ELBOW),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_RIGHT_HAND),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_RIGHT_SHOULDER)
					);
			
			draw3PointsTriangle(
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_LEFT_ELBOW),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_HEAD),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_NECK)
					);
			draw3PointsTriangle(
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_RIGHT_ELBOW),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_HEAD),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_NECK)
					);
			
			draw3PointsTriangle(
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_LEFT_SHOULDER),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_LEFT_HIP),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_LEFT_HAND)
					);
			draw3PointsTriangle(
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_RIGHT_SHOULDER),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_RIGHT_HIP),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_RIGHT_HAND)
					);
			
			draw3PointsTriangle(
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_RIGHT_HIP),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_NECK),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_LEFT_SHOULDER)
					);
			draw3PointsTriangle(
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_LEFT_HIP),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_NECK),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_RIGHT_SHOULDER)
					);

			draw3PointsTriangle(
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_LEFT_KNEE),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_LEFT_HIP),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_LEFT_HAND)
					);
			draw3PointsTriangle(
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_RIGHT_KNEE),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_RIGHT_HIP),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_RIGHT_HAND)
					);

			draw3PointsTriangle(
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_LEFT_KNEE),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_LEFT_HIP),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_RIGHT_FOOT)
					);
			draw3PointsTriangle(
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_RIGHT_KNEE),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_RIGHT_HIP),
					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_LEFT_FOOT)
					);

//			draw3PointsTriangle(
//					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_RIGHT_HIP),
//					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_WAIST),
//					_skeletonTracker.getBodyPart2d(users[i], SimpleOpenNI.SKEL_LEFT_HIP)
//					);
			
		}
	}
	
	protected float particleDistanceToBodyPart( float minDist, VectorFlyerToxi particle, Vec3D bodyPartLoc ) {
		if( bodyPartLoc == null ) return minDist;
		if( bodyPartLoc.distanceTo(particle.position()) < minDist ) {
			particle.setTarget( bodyPartLoc );
			return bodyPartLoc.distanceTo(particle.position());
		} else {
			return minDist;
		}

	}
	
	public void draw3PointsTriangle( Vec3D point1, Vec3D point2, Vec3D point3 ) {
		// draw a line - currently disabled from noStroke()
		if( point1 == null || point2 == null || point3 == null ) return; 
		
		// draw triangles
		Triangle3D tri = new Triangle3D(point1, point2, point3); 
		Toxiclibs.instance(p).toxi.triangle( tri );

		// draw mesh
//		WETriangleMesh mesh = new WETriangleMesh();
//		mesh.addFace( point1, point2, point3 );
//		
//		SubdivisionStrategy subdiv = new TriSubdivision();
//		mesh.subdivide( subdiv );
//		mesh.subdivide( subdiv );
//		toxi.mesh( mesh );
		
		// draw lines
//		toxi.line( new Line3D( point1, point2 ) );
//		toxi.line( new Line3D( point2, point3 ) );
//		toxi.line( new Line3D( point3, point1 ) );		
		
		// draw lines into texture
//		_texture.beginDraw();
//		_texture.beginShape(P.TRIANGLES);
//		_texture.vertex( point1.x, point1.y, point1.z );
//		_texture.vertex( point2.x, point2.y, point2.z );
//		_texture.vertex( point3.x, point3.y, point3.z );
//		_texture.endShape();
//		_texture.endDraw();
	}
	
}
