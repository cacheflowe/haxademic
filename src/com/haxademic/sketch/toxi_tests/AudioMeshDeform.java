package com.haxademic.sketch.toxi_tests;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.vendor.Toxiclibs;

import toxi.color.TColor;
import toxi.geom.Sphere;
import toxi.geom.mesh.WETriangleMesh;

public class AudioMeshDeform
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	WETriangleMesh _mesh, _meshDeform;

	public void firstFrame() {

		
//		_mesh = MeshUtil.meshFromOBJ( p, "../data/models/pointer_cursor_2_hollow.obj", 1.5f );
//		_mesh = MeshUtil.getExtrudedMesh( MeshUtil.meshFromSVG( p, "../data/svg/diamond.svg", -1, 3, 0.5f ), 20 );
//		_mesh = MeshUtil.meshFromOBJ( p, "../data/models/cacheflowe-3d.obj", 120f );
//		_mesh = MeshUtil.meshFromOBJ( p, "../data/models/mode-set.obj", 150f )
//		_mesh = MeshUtil.meshFromOBJ( p, "../data/models/diamond.obj", 1f );
//		_mesh = MeshUtil.meshFromImg( p, "../data/images/kacheout/ufo_1.gif", 14f );
		_mesh = new WETriangleMesh(  );
		_mesh.addMesh( (new Sphere(200)).toMesh( 25 ) );
		_meshDeform = _mesh.copy();
	}
	
	public void drawApp() {
		p.shininess(1000f); 
		p.lights();
		p.background(100);

		p.translate( 0, 0, -400 );
		//p.rotateX( P.PI/100 * p.mouseX );
		p.rotateX( 1.3f );
		//P.println(P.PI/100 * p.mouseX);
		
		
		TColor fill = new TColor( TColor.WHITE ).setAlpha( 1.0f );
		// TColor stroke = TColor.newRGB( 200, 200, 200 ).setAlpha( 0.3f );
		
		p.fill( fill.copy().darken(0.75f).toARGB() );
		p.noStroke();
		p.rect( 0, 0, 4000, 4000 );
		
//		deformWithTrig();
//		deformWithTrig2();
		deformWithAudio();
		
		p.translate( 0, 0, -60 );
		p.rotateY( (float) p.frameCount / 100f );
		p.rotateZ( (float) p.frameCount / 100f );
		p.fill( fill.toARGB() );
//		p.stroke( stroke.toARGB() );
		Toxiclibs.instance(p).toxi.mesh( _meshDeform );
		//DrawMesh.drawMeshWithAudio( (PApplet)this, _meshDeform, _audioInput, 3f, false, fill, stroke, 0.25f );

	}
	
	protected void deformWithAudio() {
		int numVertices = _mesh.getNumVertices();
		int eqStep = Math.round( 512f / (float) numVertices );
		for( int i = 0; i < numVertices; i++ ) {
			float eq = 1 + AudioIn.audioFreq(i*eqStep);
//			eq *= 2f;
			
			if( _mesh.getVertexForID( i ) != null ) {
				_meshDeform.getVertexForID( i ).x = _mesh.getVertexForID( i ).x * eq;
				_meshDeform.getVertexForID( i ).y = _mesh.getVertexForID( i ).y * eq;
				_meshDeform.getVertexForID( i ).z = _mesh.getVertexForID( i ).z * eq;
			}
		}

	}
	
	protected void deformWithTrig() {
		int numVertices = _mesh.getNumVertices();
		for( int i = 0; i < numVertices; i++ ) {
			if( _mesh.getVertexForID( i ) != null ) {
				_meshDeform.getVertexForID( i ).x = _mesh.getVertexForID( i ).x + 10*(0.7f + (float)Math.sin(p.frameCount*i/1000f));
				_meshDeform.getVertexForID( i ).y = _mesh.getVertexForID( i ).y + 10*(0.7f + (float)Math.cos(p.frameCount*i/1000f));
				_meshDeform.getVertexForID( i ).z = _mesh.getVertexForID( i ).z + 10*(0.7f + (float)Math.sin(p.frameCount*i/1000f));
			}
		}
	}

	protected void deformWithTrig2() {
		int numVertices = _mesh.getNumVertices();
		for( int i = 0; i < numVertices; i++ ) {
			if( _mesh.getVertexForID( i ) != null ) {
				_meshDeform.getVertexForID( i ).x = _mesh.getVertexForID( i ).x + 10*((float)Math.sin(p.frameCount*i/1000f));
				_meshDeform.getVertexForID( i ).y = _mesh.getVertexForID( i ).y + 10*((float)Math.cos(p.frameCount*i/1000f));
				_meshDeform.getVertexForID( i ).z = _mesh.getVertexForID( i ).z + 10*((float)Math.sin(p.frameCount*i/1000f));
			}
		}
	}

}
