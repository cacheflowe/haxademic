package com.haxademic.sketch.particle;

import java.util.ArrayList;
import java.util.List;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.mesh.MeshPool;
import com.haxademic.core.draw.mesh.MeshUtil;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.math.easing.EasingFloat3d;
import com.haxademic.core.math.easing.ElasticFloat3D;
import com.haxademic.core.system.FileUtil;

import toxi.geom.mesh.Vertex;
import toxi.geom.mesh.WETriangleMesh;
import wblut.external.ProGAL.AlphaComplex;
import wblut.external.ProGAL.CTriangle;
import wblut.external.ProGAL.Point;

public class ModelMorph
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	MeshPool _meshPool;
	WETriangleMesh _mesh;
	ArrayList<String> _modelIds;
	EasingFloat3d _rot;
	int _meshIndex;
	boolean _wireFrame = false;
	
	ArrayList<ElasticFloat3D> _points;
	List<Point> _alphaPoints;
	int _numPoints = 400;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "800" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "600" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );

		p.appConfig.setProperty( AppSettings.SUNFLOW, "true" );
		p.appConfig.setProperty( AppSettings.SUNFLOW_ACTIVE, "false" );
		p.appConfig.setProperty( AppSettings.SUNFLOW_QUALITY, "low" );
	}
	
	public void setup() {
		super.setup();
		
		_rot = new EasingFloat3d( 0, 0, 0, 10f );
		_meshPool = new MeshPool( p );
		_meshIndex = 0;
		_wireFrame = false;
		
		_meshPool.addMesh( "chicken", MeshUtil.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/chicken.obj", 1f ), 50 );
		_meshPool.addMesh( "library_chair", MeshUtil.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/library-chair.obj", 1f ), 50 );
		_meshPool.addMesh( "strat.obj", MeshUtil.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/strat.obj", 1f ), 30 );
		_meshPool.addMesh( "skull.obj", MeshUtil.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/skull.obj", 1f ), 20 );
		_meshPool.addMesh( "POLY_HOLE_PENT", MeshUtil.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/poly-hole-penta.obj", 1f ), 70f );
		_meshPool.addMesh( "POLY_HOLE_SQUARE", MeshUtil.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/poly-hole-square.obj", 1f ), 70f );
		_meshPool.addMesh( "POLY_HOLE_TRI", MeshUtil.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/poly-hole-tri.obj", 1f ), 70f );

		_modelIds = _meshPool.getIds();
		_mesh = _meshPool.getMesh( _modelIds.get( 0 ) );
		
		_points = new ArrayList<ElasticFloat3D>();
		_alphaPoints = new ArrayList<Point>();
		for( int i=0; i < _numPoints; i++ ) {
			_points.add(new ElasticFloat3D(0, 0, 0, 0.5f, 0.5f) );
			_alphaPoints.add( new Point(0,0,0) );
		}
	}
	
	public void drawApp() {
		DrawUtil.setBasicLights( p );
		p.background(0);
		
		if( _wireFrame ) {
			p.stroke(255, 249, 0, 255);
			p.noFill();
		} else {
			p.fill(255, 50);
			p.noStroke();
		}

		
		p.translate(0, 0, -500);
		p.rotateY( p.frameCount * 0.01f );
		// toxi.mesh( _mesh );
		


		setPointsToMesh();
		
		for( int i=0; i < _numPoints; i++ ) {
			_points.get(i).update();

			_alphaPoints.get(i).setX( _points.get(i).x() );
			_alphaPoints.get(i).setY( _points.get(i).y() );
			_alphaPoints.get(i).setZ( _points.get(i).z() );
		}
		
		// draw points
		p.fill(255);
		for( int i=0; i < _numPoints; i++ ) {
			p.pushMatrix();
			p.translate(_points.get(i).x(), _points.get(i).y(), _points.get(i).z());
			p.box(2);
			p.popMatrix();
		}

		// draw alpha complex
//		AlphaFiltration af = new AlphaFiltration(_alphaPoints);
//		List<CTriangle> triangles = af.getAlphaShape(100);
//		for(CTriangle tri: triangles) {

		AlphaComplex ac = new AlphaComplex(_alphaPoints, 120);
		for(CTriangle tri: ac.getTriangles()){		
			p.fill( 50, 200, 50 );
//			_jw.jr.fill(JoonsWrapper.MATERIAL_SHINY, 190, 210, 190, 0.25f);
			beginShape(TRIANGLES);
			vertex( (float) tri.getP1().x(), (float) tri.getP1().y(), (float) tri.getP1().z() );
			vertex( (float) tri.getP2().x(), (float) tri.getP2().y(), (float) tri.getP2().z() );
			vertex( (float) tri.getP3().x(), (float) tri.getP3().y(), (float) tri.getP3().z() );
			endShape();
		}

	}
	
	public void keyPressed() {
		// cycle through images
		if( key == ' ' ) {
			_meshIndex++;
			if( _meshIndex >= _modelIds.size() ) _meshIndex = 0;
			_mesh = _meshPool.getMesh( _modelIds.get( _meshIndex ) );
		}
		if( key == 'w' ) {
			_wireFrame = !_wireFrame;
		}
	}

	public void setPointsToMesh() {
//		int faceIndex = 0;
//		Face f;
//		int numVertices = _mesh.getNumVertices();
//		float eqStep = (float) numVertices / (float) _numPoints;		
//		for( int i=0; i < _numPoints; i++ ) {
//			faceIndex = Math.round( (eqStep * i) % numVertices );
//			P.println(faceIndex);
//			f = _mesh.faces.get(faceIndex);
//			_points.get(i).setTarget(f.b.x, f.b.y, f.b.z);
//		}
		
		// reset points
		for( int i=0; i < _numPoints; i++ ) _points.get(i % _numPoints).setTarget(0,0,0);
		
		
		_mesh.getNumVertices();
		int vertexIndex = 0;
		int numVertices = _mesh.getNumVertices();
		float eqStep = (float) numVertices / (float) _numPoints;	
		int i = 0;
		for (Vertex v : _mesh.vertices.values()) {
			vertexIndex = Math.round( (eqStep * i) % numVertices );
			_points.get(i % _numPoints).setTarget(v.x(), v.y(), v.z());
			i++;
		}

	}

	
}