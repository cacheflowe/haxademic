package com.haxademic.sketch.three_d.texture;

import krister.Ess.AudioInput;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import toxi.geom.AABB;
import toxi.geom.Sphere;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.IAudioTexture;
import com.haxademic.app.haxvisual.viz.textures.ColumnAudioTexture;
import com.haxademic.app.haxvisual.viz.textures.EQGridTexture;
import com.haxademic.app.haxvisual.viz.textures.EQSquareTexture;
import com.haxademic.app.haxvisual.viz.textures.WindowShadeTexture;
import com.haxademic.core.app.P;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.mesh.MeshUtil;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.math.MathUtil;

public class SphereTextureMap
extends PApplet
{
	
	PApplet p;
	ToxiclibsSupport _toxi;
	IAudioTexture _texture;
	PImage _image;
	Sphere _sphere, _sphereOuter;
	WETriangleMesh _sphereMesh, _sphereOuterMesh;
	AudioInputWrapper _audioInput;
	int _numEq = 512;
	boolean _useAudio = true;
	
	public SphereTextureMap() {
		p = this;
	}
	
	public void setup() {
		p.size( 800, 600, OPENGL );
		p.frameRate( 30 );
		p.colorMode( PConstants.RGB, 255, 255, 255, 1 );

		_toxi = new ToxiclibsSupport( p );
		_audioInput = new AudioInputWrapper( p, false );
		_audioInput.setNumAverages( _numEq );
		
		if( _useAudio == false ) {
			_image = p.loadImage("../data/images/globe-square.jpg");
		} else {
			newTexture();
//			_image = new PImage( 1, _numEq );
//			_graphics = p.createGraphics( _numEq, _numEq, P.P3D );
		}
		
		_sphere = new Sphere( 200 );
		AABB box = new AABB( 200 );
		_sphereMesh = new WETriangleMesh();
		_sphereMesh.addMesh( _sphere.toMesh( 30 ) );
//		_sphereMesh.computeVertexNormals();

		_sphereOuter = new Sphere( 1250 );
		_sphereOuterMesh = new WETriangleMesh();
		_sphereOuterMesh.addMesh( _sphereOuter.toMesh( 30 ) );
//		_sphereOuterMesh.computeVertexNormals();

		MeshUtil.calcTextureCoordinates( _sphereMesh );
		MeshUtil.calcTextureCoordinates( _sphereOuterMesh );
	}
	
	public void draw() {
		p.background( 0 );
		p.noStroke();
		DrawUtil.setBasicLights( p );
		
		if( _useAudio == true ) updateWithAudio();
				
		p.translate( p.width/2f, p.height/2f );
		p.rotateY( p.mouseX/100f );
		p.rotateX( p.mouseY/100f );
		
		MeshUtil.calcTextureCoordinates( _sphereMesh );

		MeshUtil.drawToxiMesh( p, _toxi, _sphereMesh, _texture.getTexture() );
		MeshUtil.drawToxiMesh( p, _toxi, _sphereOuterMesh, _texture.getTexture() );
//		drawToxiFaces( p, _toxi, _sphereOuterMesh, _texture.getTexture() );
		
		if( p.frameCount % 150 == 0 ) newTexture();
	}
	
	protected void newTexture() {
		int randy = MathUtil.randRange( 0, 3 );
		switch( randy ) {
			case 0 : 
				_texture = new ColumnAudioTexture( _numEq );
				break;
			case 1 : 
				_texture = new EQSquareTexture( _numEq, _numEq );
				break;
			case 2 : 
				_texture = new WindowShadeTexture( _numEq, _numEq );
				break;
			case 3 : 
				_texture = new EQGridTexture( _numEq, _numEq );
				break;
				
		}
	}
	
	public void updateWithAudio() {
		_texture.updateTexture( _audioInput );
	}
	
	public void drawToxiFaces( PApplet p, ToxiclibsSupport toxi, WETriangleMesh mesh, PImage image ) {
		p.textureMode(P.IMAGE);
		p.beginShape( P.TRIANGLES );
		p.texture( image );

		// draw vertices, mapping PImage
		// http://en.wikipedia.org/wiki/UV_mapping
		// uv points spread out from the center of the image - use standard sphere UV mapping locations to multiply from there
		float halfW = (float)image.width / 2f;
		float halfH = (float)image.height / 2f;
		float mapW = (float)image.width;
		float mapH = (float)image.height;

		// loop through model's vertices
		for( Face f : mesh.getFaces() ) {
			float divisorA = (float) ( Math.sqrt( f.a.x * f.a.x ) + Math.sqrt( f.a.y * f.a.y ) + Math.sqrt( f.a.z * f.a.z ) );
			float uA = halfW + mapW * ( f.a.x / divisorA );
			float vA = halfH + mapH * ( f.a.y / divisorA );
			p.vertex( f.a.x, f.a.y, f.a.z, uA, vA );
			
			float divisorB = (float) ( Math.sqrt( f.b.x * f.b.x ) + Math.sqrt( f.b.y * f.b.y ) + Math.sqrt( f.b.z * f.b.z ) );
			float uB = halfW + mapW * ( f.b.x / divisorB );
			float vB = halfH + mapH * ( f.b.y / divisorB );
			p.vertex( f.b.x, f.b.y, f.b.z, uB, vB );
			
			float divisorC = (float) ( Math.sqrt( f.c.x * f.c.x ) + Math.sqrt( f.c.y * f.c.y ) + Math.sqrt( f.c.z * (float)f.c.z ) );
			float uC = halfW + mapW * ( f.c.x / divisorC );
			float vC = halfH + mapH * ( f.c.y / divisorC );
			p.vertex( f.c.x, f.c.y, f.c.z, uC, vC );
	   	}
		
		p.endShape();
	}
		
	public float mapBoundsW( float num ) {
		return P.constrain( num, 0, _image.width );
	}

	public float mapBoundsH( float num ) {
		return P.constrain( num, 0, _image.height );
	}

	/**
	 * PApplet-level listener for AudioInput data from the ESS library
	 */
	public void audioInputData(AudioInput theInput) {
		_audioInput.getFFT().getSpectrum(theInput);
		_audioInput.detector.detect(theInput);
	}
}
