 package com.haxademic.render;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.toxi.DrawToxiMesh;

import processing.core.PVector;
import toxi.geom.Vec3D;
import toxi.geom.mesh.WETriangleMesh;
import toxi.geom.mesh.subdiv.DualSubdivision;

public class ModeSetLogo
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	WETriangleMesh _meshCrest;
	protected final EasingColor MODE_SET_BLUE = new EasingColor( 0, 200, 234, 255 );

	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
	}
	
	public void setupFirstFrame() {

//		_meshCrest = MeshUtil.getExtrudedMesh( MeshUtil.meshFromSVG( p, FileUtil.getHaxademicDataPath() + "svg/modeset-logotype.svg", 10, -1, 0.7f ), 250 );
//		_meshCrest = MeshUtil.meshFromSVG( p, FileUtil.getHaxademicDataPath() + "svg/modeset-crest.svg", -1, 20, 0.8f );
//		_meshCrest = MeshUtil.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/mode-set.obj", 300f );
		_meshCrest = new WETriangleMesh(); 
		manualBuildTriangleMesh();
		
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
	}
	
	public void drawApp() {
		PG.setBasicLights(p);
		PG.setCenterScreen(p);
		p.background(0);
		
//		if( p.frameCount == 1 ) p.beginRecord( P.PDF,  FileUtil.getHaxademicOutputPath() + "modeset-"+ SystemUtil.getTimestamp() +".pdf" );
		
//		p.rotateX(((float)p.mouseY - (float)p.height/2f) * -0.01f);
//		p.rotateY(((float)p.mouseX - (float)p.width/2f) * 0.01f);
		
		p.noFill();
		p.stroke( MODE_SET_BLUE.colorInt() );
//		p.noStroke();
//		p.fill( MODE_SET_BLUE.toARGB() );
//		p.toxi.mesh( _meshCrest );
		
		p.strokeWeight(3);
		p.strokeJoin(P.BEVEL); // MITER, ROUND
		DrawToxiMesh.drawToxiMeshFacesNative2d( p, _meshCrest );
		
//		if( p.frameCount == 1 ) p.endRecord();
	}
	
	protected void manualBuildTriangleMesh() {
		
		ArrayList<PVector> outerPoints = new ArrayList<PVector>(); 
		
		// set up hard-coded outer logo _points in a 1000x1000 px space
		outerPoints.add( new PVector( 148, 346 ) );
		outerPoints.add( new PVector( 148, 662 ) );
		outerPoints.add( new PVector( 236, 790 ) );
		outerPoints.add( new PVector( 236, 644 ) );
		outerPoints.add( new PVector( 436, 932 ) );
		outerPoints.add( new PVector( 436, 726 ) );
		outerPoints.add( new PVector( 492, 658 ) );
		outerPoints.add( new PVector( 492, 974 ) );
		outerPoints.add( new PVector( 694, 782 ) );
		outerPoints.add( new PVector( 694, 622 ) );
		outerPoints.add( new PVector( 738, 582 ) );
		outerPoints.add( new PVector( 738, 668 ) );
		outerPoints.add( new PVector( 848, 568 ) );
		outerPoints.add( new PVector( 848, 22 ) );
		outerPoints.add( new PVector( 738, 128 ) );
		outerPoints.add( new PVector( 738, 284 ) );
		outerPoints.add( new PVector( 694, 326 ) );
		outerPoints.add( new PVector( 694, 238 ) );
		outerPoints.add( new PVector( 590, 336 ) );
		outerPoints.add( new PVector( 402, 64 ) );
		outerPoints.add( new PVector( 402, 336 ) );
		outerPoints.add( new PVector( 206, 52 ) );
		outerPoints.add( new PVector( 206, 282 ) );
		
		// connect sub-triangles to fill in the inside of the logo
		addFaceToMesh( outerPoints.get(0), outerPoints.get(1), outerPoints.get(3) );
		addFaceToMesh( outerPoints.get(1), outerPoints.get(2), outerPoints.get(3) );
		addFaceToMesh( outerPoints.get(3), outerPoints.get(4), outerPoints.get(5) );
		addFaceToMesh( outerPoints.get(3), outerPoints.get(5), outerPoints.get(6) );
		addFaceToMesh( outerPoints.get(0), outerPoints.get(3), outerPoints.get(6) );
		addFaceToMesh( outerPoints.get(6), outerPoints.get(7), outerPoints.get(8) );
		addFaceToMesh( outerPoints.get(6), outerPoints.get(8), outerPoints.get(9) );
		addFaceToMesh( outerPoints.get(10), outerPoints.get(11), outerPoints.get(12) );
		addFaceToMesh( outerPoints.get(10), outerPoints.get(12), outerPoints.get(15) );
		addFaceToMesh( outerPoints.get(12), outerPoints.get(15), outerPoints.get(13) );
		addFaceToMesh( outerPoints.get(13), outerPoints.get(14), outerPoints.get(15) );
		addFaceToMesh( outerPoints.get(10), outerPoints.get(15), outerPoints.get(16) );
		addFaceToMesh( outerPoints.get(16), outerPoints.get(17), outerPoints.get(18) );
		addFaceToMesh( outerPoints.get(9), outerPoints.get(10), outerPoints.get(16) );
		addFaceToMesh( outerPoints.get(9), outerPoints.get(16), outerPoints.get(18) );
		addFaceToMesh( outerPoints.get(6), outerPoints.get(9), outerPoints.get(18) );
		addFaceToMesh( outerPoints.get(6), outerPoints.get(20), outerPoints.get(18) );
		addFaceToMesh( outerPoints.get(18), outerPoints.get(19), outerPoints.get(20) );
		addFaceToMesh( outerPoints.get(0), outerPoints.get(6), outerPoints.get(20) );
		addFaceToMesh( outerPoints.get(20), outerPoints.get(21), outerPoints.get(22) );
		addFaceToMesh( outerPoints.get(0), outerPoints.get(20), outerPoints.get(22) );
		
		_meshCrest.scale(0.5f);
		
//		_meshCrest.subdivide( new TriSubdivision() );
		_meshCrest.subdivide( new DualSubdivision() );
//		_meshCrest.subdivide( new MidpointSubdivision() );
//		_meshCrest.subdivide();
	}
	
	protected void addFaceToMesh( PVector point1, PVector point2, PVector point3 ) {
		_meshCrest.addFace( 
				new Vec3D( point1.x - 500, point1.y - 500, 0 ), 
				new Vec3D( point2.x - 500, point2.y - 500, 0 ), 
				new Vec3D( point3.x - 500, point3.y - 500, 0 )
				);
	}
}