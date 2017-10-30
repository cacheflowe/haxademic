package com.haxademic.app.haxvisual.viz.elements;

import java.util.ArrayList;
import java.util.Vector;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toxi.color.TColor;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;
import SimpleOpenNI.SimpleOpenNI;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.toxi.MeshPool;
import com.haxademic.core.draw.toxi.MeshUtilToxi;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.kinect.IKinectWrapper;
import com.haxademic.core.hardware.kinect.SkeletonsTracker;
import com.haxademic.core.vendor.Toxiclibs;

public class KinectMeshHead
extends ElementBase 
implements IVizElement {
		
	protected TColor _baseColor;
	protected TColor _fillColor;
	protected TColor _strokeColor;
	
	protected Vector<TColor> _colorGroup;
	
	protected SkeletonsTracker _skeletonTracker;
	protected PGraphics _texture;
	protected MeshPool _meshPool;
	protected WETriangleMesh _mesh;
	protected ArrayList<String> _meshKeys;
	protected int _meshIndex = 0;
	protected float _meshRot = 0;

	
	protected PAppletHax pHax;
	
	public KinectMeshHead( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData, IKinectWrapper kinectWrapper ) {
		super( p, toxi, audioData );
		init();
	}

	public void init() {
		_skeletonTracker = new SkeletonsTracker();
		
		_meshPool = new MeshPool( p );

		_meshPool.addMesh( "POINTER", MeshUtilToxi.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/pointer_cursor_2_hollow.obj", 1f ), 1.5f );
//		_meshPool.addMesh( "DIAMOND", MeshUtil.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/diamond.obj", 1f ), 1.2f );
		_meshPool.addMesh( "DIAMOND_2D", MeshUtilToxi.getExtrudedMesh( MeshUtilToxi.meshFromSVG( p, "../data/svg/diamond.svg", -1, 3, 0.5f ), 20 ), 1 );
		_meshPool.addMesh( "MONEY_2D", MeshUtilToxi.getExtrudedMesh( MeshUtilToxi.meshFromSVG( p, "../data/svg/money.svg", -1, 3, 0.5f ), 20 ), 1 );
		_meshPool.addMesh( "MONEY_BAG_2D", MeshUtilToxi.getExtrudedMesh( MeshUtilToxi.meshFromSVG( p, "../data/svg/money-bag.svg", -1, 3, 0.5f ), 20 ), 1 );
		_meshPool.addMesh( "GUN_2D", MeshUtilToxi.getExtrudedMesh( MeshUtilToxi.meshFromSVG( p, "../data/svg/gun.svg", -1, 3, 0.5f ), 20 ), 1 );
		_meshPool.addMesh( "WEED_2D", MeshUtilToxi.getExtrudedMesh( MeshUtilToxi.meshFromSVG( p, "../data/svg/weed.svg", -1, 3, 0.5f ), 20 ), 1 );
		_meshPool.addMesh( "CACHEFLOWE", MeshUtilToxi.meshFromOBJ( p, "../data/models/cacheflowe-3d.obj", 1f ), 150 );
		
		_meshKeys = _meshPool.getIds();

	}
	
	public void updateColorSet( ColorGroup colors ) {
		_colorGroup = colors.getCurGroup();
		
		_baseColor = colors.getRandomColor().copy();
		_fillColor = _baseColor.copy().lighten( 15 );
		_strokeColor = _baseColor.copy().lighten( 30 );
	}

	public void update() {

		// draw filtered web cam
		DrawUtil.resetGlobalProps(p);
		DrawUtil.setTopLeft(p);
		DrawUtil.setDrawCorner(p);

		_skeletonTracker.update();
		
		// draw skeleton(s)
		_skeletonTracker.drawSkeletons();
				
		p.pushMatrix();
		p.translate(0, 600, -1000);
		
		if( _skeletonTracker.hasASkeleton() ) {
			// set up draw colors
			p.fill(255);
			p.noStroke();
			p.strokeWeight(3f);
			
			p.scale(0.7f);
			drawBoxesPerUser();	
		}
		p.popMatrix();
	}
		
	protected void drawBoxesPerUser() {
		// loop through attractors and store the closest & our distance for coloring
		int[] users = _skeletonTracker.getUserIDs();
		_meshRot += 0.01f;
		for(int i=0; i < users.length; i++) { 
			PVector testPvec = _skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_RIGHT_HIP);
			if( testPvec != null && testPvec.z < 3500 ) {
				if( _colorGroup != null ) {
					p.fill( _colorGroup.get( i % ( _colorGroup.size() - 1 ) ).toARGB() );
				}
				
				drawBoxBetween(
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_RIGHT_ELBOW),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_RIGHT_HAND)
						);
				drawBoxBetween(
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_RIGHT_ELBOW),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_RIGHT_SHOULDER)
						);
				drawBoxBetween(
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_RIGHT_FOOT),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_RIGHT_KNEE)
						);
				drawBoxBetween(
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_RIGHT_HIP),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_RIGHT_KNEE)
						);
				drawBoxBetween(
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_RIGHT_HIP),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_RIGHT_SHOULDER)
						);
				
				
				
				
				drawBoxBetween(
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_RIGHT_HIP),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_LEFT_HIP)
						);
				drawBoxBetween(
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_RIGHT_SHOULDER),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_LEFT_SHOULDER)
						);
	
				
				
				drawBoxBetween(
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_LEFT_ELBOW),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_LEFT_HAND)
						);
				drawBoxBetween(
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_LEFT_ELBOW),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_LEFT_SHOULDER)
						);
				drawBoxBetween(
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_LEFT_FOOT),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_LEFT_KNEE)
						);
				drawBoxBetween(
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_LEFT_HIP),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_LEFT_KNEE)
						);
				drawBoxBetween(
						_skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_LEFT_HIP),
						_skeletonTracker.getBodyPartPVec2(users[i], SimpleOpenNI.SKEL_LEFT_SHOULDER)
						);
				
				// add funny head
				int headIndex = _meshIndex + i;
				
				PVector headPosition = _skeletonTracker.getBodyPartPVec(users[i], SimpleOpenNI.SKEL_HEAD); 
				if( headPosition != null ) {
					p.pushMatrix();
					_mesh = _meshPool.getMesh( _meshKeys.get( headIndex % _meshKeys.size() ) );
					p.translate(headPosition.x, headPosition.y - 50, 0);
					p.rotateY( _meshRot + i );
					Toxiclibs.instance(p).toxi.mesh( _mesh );
					p.popMatrix();
				}
			}
		}
	}
	
	public void drawBoxBetween( PVector point1, PVector point2 ) {
		if( point1 == null || point2 == null ) return;
		point1.z = 0;
		point2.z = 0;
		
		p.line(point1.x, point1.y, point2.x, point2.y);
		
		PVector pointMid = point1.get();
		pointMid.lerp(point2, 0.5f);

		// Rotation vectors
		// use to perform orientation to velocity vector
		PVector new_dir = PVector.sub(point1,point2);
		float r = P.sqrt(new_dir.x * new_dir.x + new_dir.y * new_dir.y + new_dir.z * new_dir.z);
		float theta = P.atan2(new_dir.y, new_dir.x);
		float phi = P.acos(new_dir.z / r);

		p.pushMatrix();
		// update location
		p.translate(pointMid.x, pointMid.y, pointMid.z);
		// orientation to velocity
		p.rotateZ(theta);
		p.rotateY(phi);
		p.rotateX(P.HALF_PI);

		// draw your stuff here
		p.box(100, point1.dist(point2), 100);

		p.popMatrix(); 

	}
	
	public void updateSection() {
		_meshIndex++;
	}
	
	public void reset() {
		updateCamera();
		updateLineMode();
	}
	
	public void updateLineMode() {
	}
	
	public void updateCamera() {

	}

	public void dispose() {
		_audioData = null;
	}

}
