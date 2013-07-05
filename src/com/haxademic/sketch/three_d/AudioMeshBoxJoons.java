package com.haxademic.sketch.three_d;

import processing.core.PVector;
import toxi.color.TColor;
import toxi.geom.Sphere;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.WETriangleMesh;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.BoxBetween;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.render.JoonsWrapper;

@SuppressWarnings("serial")
public class AudioMeshBoxJoons
extends PAppletHax {
	
	WETriangleMesh _mesh, _meshDeform;
	PVector vec = new PVector();
	PVector vec2 = new PVector();

	protected void overridePropsFile() {
		_appConfig.setProperty( "sunflow", "true" );
		_appConfig.setProperty( "sunflow_active", "true" );
		_appConfig.setProperty( "sunflow_quality", "high" );
		_appConfig.setProperty( "sunflow_save_images", "true" );
		_appConfig.setProperty( "width", "1300" );
		_appConfig.setProperty( "height", "1000" );
		_appConfig.setProperty( "rendering", "false" );
	}

	public void setup() {
		super.setup();
		
//		_mesh = MeshUtil.meshFromOBJ( p, "../data/models/pointer_cursor_2_hollow.obj", 1.5f );
//		_mesh = MeshUtil.getExtrudedMesh( MeshUtil.meshFromSVG( p, "../data/svg/diamond.svg", -1, 3, 0.5f ), 20 );
//		_mesh = MeshUtil.meshFromOBJ( p, "../data/models/cacheflowe-3d.obj", 120f );
//		_mesh = MeshUtil.meshFromOBJ( p, "../data/models/mode-set.obj", 150f )
//		_mesh = MeshUtil.meshFromOBJ( p, "../data/models/diamond.obj", 1f );
//		_mesh = MeshUtil.meshFromImg( p, "../data/images/kacheout/ufo_1.gif", 14f );
		_mesh = new WETriangleMesh(  );
		_mesh.addMesh( (new Sphere(30)).toMesh( 5 ) );
		_meshDeform = _mesh.copy();
	}
	
	public void drawApp() {
		background(0);
//		DrawUtil.resetGlobalProps( p );
//		DrawUtil.setCenter( p );

		p.lights();

		// draw a dark room
//		if( _jw != null ) _jw.drawRoomWithSizeAndColor( width, height, JoonsWrapper.MATERIAL_MIRROR, -1, p.color( 200, 200, 200 ) );
		if( _jw != null ) _jw.drawRoomWithSizeAndColor( width, height, JoonsWrapper.MATERIAL_DIFFUSE, -1, p.color( 200, 230, 200 ) );
		
		
//		p.rect( 0, 0, 4000, 4000 );
		
		deformWithAudio();
		
		p.rotateY( (float) p.frameCount / 100f );
		p.rotateZ( (float) p.frameCount / 100f );
		

		p.fill( 100, 200, 150 );
		
		float thickness = 1;
		float radius = thickness * 1;//0.5f;
		int numFaces = _meshDeform.faces.size();
		Face f;
		for( int i = 0; i < numFaces; i++ ) {
			// draw 3 boxes
			f = _meshDeform.faces.get( i );
			p.vertex(f.a.x, f.a.y, f.a.z);
			p.vertex(f.b.x, f.b.y, f.b.z);
			p.vertex(f.c.x, f.c.y, f.c.z);
			vec.set(f.a.x, f.a.y, f.a.z);
			vec2.set(f.b.x, f.b.y, f.b.z);
			BoxBetween.draw(p, vec, vec2, thickness);
			vec2.set(f.c.x, f.c.y, f.c.z);
			BoxBetween.draw(p, vec, vec2, thickness);
			vec.set(f.c.x, f.c.y, f.c.z);
			BoxBetween.draw(p, vec, vec2, thickness);

			if( _jw != null ) _jw.addColorForObject( JoonsWrapper.MATERIAL_SHINY, p.color( 100, 200, 150 ), 1, false );

			// spheres at connections
			p.fill( 60, 80, 60 );

			p.pushMatrix();
			translate( f.a.x, f.a.y, f.a.z);
			sphere(radius);
			p.popMatrix();

			if( _jw != null ) _jw.addColorForObject( JoonsWrapper.MATERIAL_DIFFUSE, p.color( 60, 80, 60 ), 1, true );

			if( i == numFaces - 1 ) {
				p.pushMatrix();
				translate( f.c.x, f.c.y, f.c.z);
				sphere(radius);
				p.popMatrix();
				if( _jw != null ) _jw.addColorForObject( JoonsWrapper.MATERIAL_DIFFUSE, p.color( 60, 80, 60 ), 1, true );
			} else if( i == 0 ) {
				p.pushMatrix();
				translate( f.b.x, f.b.y, f.b.z);
				sphere(radius);
				p.popMatrix();
				if( _jw != null ) _jw.addColorForObject( JoonsWrapper.MATERIAL_DIFFUSE, p.color( 60, 80, 60 ), 1, true );
			}
		}
		
	}
	
	protected void deformWithAudio() {
		int numVertices = _mesh.getNumVertices();
		int eqStep = Math.round( 512f / (float) numVertices );
		for( int i = 0; i < numVertices; i++ ) {
			float eq = 1 + p._audioInput.getFFT().spectrum[(i*eqStep)%512];
			
			if( _mesh.getVertexForID( i ) != null ) {
				_meshDeform.getVertexForID( i ).x = _mesh.getVertexForID( i ).x * eq;
				_meshDeform.getVertexForID( i ).y = _mesh.getVertexForID( i ).y * eq;
				_meshDeform.getVertexForID( i ).z = _mesh.getVertexForID( i ).z * eq;
			}
		}

	}

}
