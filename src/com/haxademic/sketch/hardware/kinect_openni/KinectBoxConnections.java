package com.haxademic.sketch.hardware.kinect_openni;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.toxi.MeshPool;
import com.haxademic.core.draw.toxi.MeshUtilToxi;
import com.haxademic.core.hardware.depthcamera.SkeletonsTrackerKinectV1;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.vendor.Toxiclibs;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import toxi.geom.mesh.WETriangleMesh;

public class KinectBoxConnections
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected SkeletonsTrackerKinectV1 _skeletonTracker;
	protected PGraphics _texture;
	protected MeshPool _meshPool;
	protected WETriangleMesh _mesh;
	protected ArrayList<String> _meshKeys;
	protected int _meshIndex = 0;
	protected float _meshRot = 0;

	protected void firstFrame() {
		DepthCamera.instance(DepthCameraType.KinectV1);
		
		_skeletonTracker = new SkeletonsTrackerKinectV1();
		
		_meshPool = new MeshPool( p );

//		_meshPool.addMesh( "POINTER", MeshUtilToxi.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/pointer_cursor_2_hollow.obj", 1f ), 1.5f );
//		_meshPool.addMesh( "DIAMOND", MeshUtil.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/diamond.obj", 1f ), 1.2f );
		_meshPool.addMesh( "DIAMOND_2D", MeshUtilToxi.getExtrudedMesh( MeshUtilToxi.meshFromSVG( p, "../data/svg/diamond.svg", -1, 3, 0.5f ), 20 ), 1 );
		_meshPool.addMesh( "MONEY_2D", MeshUtilToxi.getExtrudedMesh( MeshUtilToxi.meshFromSVG( p, "../data/svg/money.svg", -1, 3, 0.5f ), 20 ), 1 );
		_meshPool.addMesh( "MONEY_BAG_2D", MeshUtilToxi.getExtrudedMesh( MeshUtilToxi.meshFromSVG( p, "../data/svg/money-bag.svg", -1, 3, 0.5f ), 20 ), 1 );
		_meshPool.addMesh( "GUN_2D", MeshUtilToxi.getExtrudedMesh( MeshUtilToxi.meshFromSVG( p, "../data/svg/gun.svg", -1, 3, 0.5f ), 20 ), 1 );
		_meshPool.addMesh( "WEED_2D", MeshUtilToxi.getExtrudedMesh( MeshUtilToxi.meshFromSVG( p, "../data/svg/weed.svg", -1, 3, 0.5f ), 20 ), 1 );
//		_meshPool.addMesh( "CACHEFLOWE", MeshUtilToxi.meshFromOBJ( p, "../data/models/cacheflowe-3d.obj", 1f ), 150 );
		
		_meshKeys = _meshPool.getIds();
	}
	
	protected void drawApp() {
		PG.resetGlobalProps(p);
		PG.setDrawCorner(p);
		PG.setColorForPImage(p);

		p.shininess(1000f); 
		p.lights();
		p.background(0);

		_skeletonTracker.update();
		
		// draw skeleton(s)
		_skeletonTracker.drawSkeletons();
				
		p.pushMatrix();
		p.translate(0, 400, -1000);
		p.scale(0.7f);
		if( _skeletonTracker.hasASkeleton() ) {
			// set up draw colors
			p.fill(255);
			p.noStroke();
			p.strokeWeight(3f);
			
			drawBoxesPerUser();	
		}
		p.popMatrix();
	}
	
	protected void drawWebCam( float rotations ) {
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		
		// draw cam
		PG.setColorForPImage(p);
		PG.setPImageAlpha(p, 0.25f);
		PImage drawCamImg = depthCamera.getRgbImage();
		for( int i=0; i < rotations; i++ ) {
			p.rotate((float)P.TWO_PI/rotations * (float)i);
			p.image( drawCamImg, 0, 0 );
		}
	}
		
	protected void drawBoxesPerUser() {
		// loop through attractors and store the closest & our distance for coloring
		int[] users = _skeletonTracker.getUserIDs();
		_meshRot += 0.002f;
		for(int i=0; i < users.length; i++) { 
			if( _skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_RIGHT_HIP) != null && _skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_RIGHT_HIP).z < 3500 ) {
				Shapes.boxBetween( p.g,
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_RIGHT_ELBOW),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_RIGHT_HAND),
						100
						);
				Shapes.boxBetween( p.g,
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_RIGHT_ELBOW),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_RIGHT_SHOULDER),
						100
						);
				Shapes.boxBetween( p.g,
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_RIGHT_FOOT),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_RIGHT_KNEE),
						100
						);
				Shapes.boxBetween( p.g,
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_RIGHT_HIP),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_RIGHT_KNEE),
						100
						);
				Shapes.boxBetween( p.g,
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_RIGHT_HIP),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_RIGHT_SHOULDER),
						100
						);
				
				
				
				
				Shapes.boxBetween( p.g,
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_RIGHT_HIP),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_LEFT_HIP),
						100
						);
				Shapes.boxBetween( p.g,
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_RIGHT_SHOULDER),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_LEFT_SHOULDER),
						100
						);
	
				
				
				Shapes.boxBetween( p.g,
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_LEFT_ELBOW),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_LEFT_HAND),
						100
						);
				Shapes.boxBetween( p.g,
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_LEFT_ELBOW),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_LEFT_SHOULDER),
						100
						);
				Shapes.boxBetween( p.g,
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_LEFT_FOOT),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_LEFT_KNEE),
						100
						);
				Shapes.boxBetween( p.g,
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_LEFT_HIP),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_LEFT_KNEE),
						100
						);
				Shapes.boxBetween( p.g,
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_LEFT_HIP),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_LEFT_SHOULDER),
						100
						);
				
				// add funny head
				int headIndex = _meshIndex + i;
				
				PVector headPosition = _skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_HEAD); 
				if( headPosition != null ) {
					p.pushMatrix();
					_mesh = _meshPool.getMesh( _meshKeys.get( headIndex % _meshKeys.size() ) );
					_mesh.rotateY( _meshRot + i );
					p.translate(headPosition.x, headPosition.y - 50, 0);
					Toxiclibs.instance(p).toxi.mesh( _mesh );
					p.popMatrix();
				}
			}
		}
	}
		
	public void keyPressed() {
		if(p.key == ' ') {
			_meshIndex++;
		}
	}

	
}
