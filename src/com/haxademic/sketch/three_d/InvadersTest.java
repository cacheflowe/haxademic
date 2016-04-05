package com.haxademic.sketch.three_d;

import com.haxademic.core.app.P;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.mesh.DrawMesh;
import com.haxademic.core.draw.shapes.Meshes;

import krister.Ess.AudioInput;
import processing.core.PApplet;
import processing.core.PConstants;
import toxi.color.TColor;
import toxi.geom.mesh.WETriangleMesh;

public class InvadersTest
	extends PApplet
{
	protected WETriangleMesh _invaderMesh_01, _invaderMesh_01_alt, _invaderMesh_02, _invaderMesh_02_alt, _invaderMesh_03, _invaderMesh_03_alt;
	protected AudioInputWrapper _audioInput;
	protected TColor _stroke, _fill;

	public void setup () {
		// set up stage and drawing properties
//		size( 800, 800, "hipstersinc.P5Sunflow" );
		size( 800, 800, P.P3D );				//size(screen.width,screen.height,P3D);
		frameRate( 30 );
		colorMode( PConstants.RGB, 255, 255, 255, 255 );
		background( 0 );
		shininess(1000); 
		lights();
		
		
		
		
		_invaderMesh_01 = Meshes.invader1( 1, 70 );
		_invaderMesh_01_alt = Meshes.invader1( 2, 70 );
		
		_invaderMesh_02 = Meshes.invader2( 1, 70 );
		_invaderMesh_02_alt = Meshes.invader2( 2, 70 );

		_invaderMesh_03 = Meshes.invader3( 1, 70 );
		_invaderMesh_03_alt = Meshes.invader3( 2, 70 );

		// set up audio input
		_audioInput = new AudioInputWrapper( this, false );
		_audioInput.setNumAverages( 3 );
		_audioInput.setDampening(.4f);
		
		_stroke = new TColor( TColor.WHITE );
		_fill = new TColor( TColor.WHITE );
	}

	public void draw() {
		if(frameCount < 2) return;
		background( 0 );
		
		translate( width/2, height/2, -400 );
		rotateX(P.PI/8f);
//		rotateY(0.5f + frameCount/100f);
		rotateY((float)mouseX/100f);
		
		WETriangleMesh mesh2 = ( P.round( this.frameCount / 30f ) % 2 == 0 ) ? _invaderMesh_02 : _invaderMesh_02_alt;
		DrawMesh.drawMeshWithAudio( this, mesh2, _audioInput, false, _fill, _stroke, 0.25f );

		translate(-800f, 0, 0);
		
		WETriangleMesh mesh3 = ( P.round( this.frameCount / 30f ) % 2 == 0 ) ? _invaderMesh_03 : _invaderMesh_03_alt;
		DrawMesh.drawMeshWithAudio( this, mesh3, _audioInput, false, _fill, _stroke, 0.25f );
		
		translate(1600f, 0, 0);
		
		WETriangleMesh mesh1 = ( P.round( this.frameCount / 30f ) % 2 == 0 ) ? _invaderMesh_01 : _invaderMesh_01_alt;
		DrawMesh.drawMeshWithAudio( this, mesh1, _audioInput, false, _fill, _stroke, 0.25f );
}
	
	// PApp-level listener for audio input data ------------------------ 
	public void audioInputData( AudioInput theInput ) {
		_audioInput.getFFT().getSpectrum(theInput);
		_audioInput.detector.detect(theInput);
	}

}
