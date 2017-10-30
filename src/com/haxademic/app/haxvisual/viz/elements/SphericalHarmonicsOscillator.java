package com.haxademic.app.haxvisual.viz.elements;

import processing.core.PApplet;
import toxi.color.TColor;
import toxi.geom.mesh.SphericalHarmonics;
import toxi.geom.mesh.SurfaceMeshBuilder;
import toxi.geom.mesh.TriangleMesh;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.data.Point3D;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.toxi.DrawMesh;
import com.haxademic.core.draw.toxi.ThreeDeeUtil;
import com.haxademic.core.math.MathUtil;

public class SphericalHarmonicsOscillator
extends ElementBase 
implements IVizElement {
	
	protected TriangleMesh mesh = new TriangleMesh();
	protected boolean isWireFrame;
	protected float[] m = new float[8];
	
	protected float _curHarmonicsAudioIndex = 0;
	protected float _audioIndexVal = 0;
	protected float _targetAudioIndexVal = 0;
	protected float _curHarmonicsFramecountIndex = 6;
	protected float _colorGradientDivider = 1;
	
	protected TColor _baseColor = null;
	protected TColor _strokeColor = null;
	protected boolean _isWireframe = false;
	protected boolean _isPoints = false;
	
	protected Point3D _rotSpeed = new Point3D( 0, 0, 0 );
	protected Point3D _rotation = new Point3D( 0, 0, 0 );
	protected Point3D _rotationTarget = new Point3D( 0, 0, 0 );

	public SphericalHarmonicsOscillator( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}

	public void init() {
		randomizeMesh();
	}
	
	public void setDrawProps() {

	}

	public void updateColorSet( ColorGroup colors ) {
		_baseColor = colors.getRandomColor().copy().lighten( 20 );
		_strokeColor = colors.getRandomColor().copy().lighten( 20 );
	}
	
	public void update() {
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setCenter( p );
		p.pushMatrix();
		p.translate( 0, 0, -550 );
		
		updateRotation();
		
		_targetAudioIndexVal = _audioData.getFFT().spectrum[200] * 9;
		_audioIndexVal = MathUtil.easeTo( _audioIndexVal, _targetAudioIndexVal, 4 );
		
		// cycle through harmonics, with one index responding to audio EQ and another responding to frameCount oscillation 
		for (int i = 0; i < 8; i++) {
			if (i == _curHarmonicsAudioIndex) {
				m[i] = _audioIndexVal;
			}
			if (i == _curHarmonicsFramecountIndex) {
				m[i] = p.sin(i + p.frameCount / 500f) * 4.5f + 4.5f;
			}
		}
		SurfaceMeshBuilder b = new SurfaceMeshBuilder(new SphericalHarmonics(m));
		TriangleMesh mesh = (TriangleMesh) b.createMesh(null, 40, 120);
//		mesh.scale(1.5f);

//		drawAxes(400);
//		isWireFrame = true;
//		if (isWireFrame) {
//			p.noFill();
//			p.stroke(0.5f);
//		} else {
//			p.fill(255);
//			p.noStroke();
//		}
//		fillColor.alpha = 0.2f;
		p.stroke( _baseColor.toARGB(), _baseColor.alpha );
		
		if( _isPoints == true ) {
			DrawMesh.drawPointsWithAudio( p, ThreeDeeUtil.GetWETriangleMeshFromTriangleMesh(mesh), _audioData, _colorGradientDivider, 15, _baseColor, _strokeColor, 0.3f );
		} else {
			DrawMesh.drawMeshWithAudio( p, ThreeDeeUtil.GetWETriangleMeshFromTriangleMesh(mesh), _audioData, _isWireframe, _baseColor, _strokeColor, 0.1f );
		}
		
		p.popMatrix();
	}
	
	protected void updateRotation() {
		_rotation.easeToPoint( _rotationTarget, 5 );
		p.rotateX( _rotation.x );
		p.rotateY( _rotation.y );
		p.rotateZ( _rotation.z );
		
		_rotationTarget.x += _rotSpeed.x;
		_rotationTarget.y += _rotSpeed.y;
		_rotationTarget.z += _rotSpeed.z;
	}
	
	public void reset() {
		randomizeMesh();
		updateCamera();
		updateLineMode();
	}
	
	public void updateLineMode() {
		int linesMode = 1; // p.round( p.random( 0, 1 ) );
		if( linesMode == 0 ) {
			_isWireframe = true;
			_isPoints = false;
		} else if( linesMode == 1 ) {
			_isWireframe = false;
			_isPoints = false;
		} else if( linesMode == 2 ) {
			_isWireframe = false;
			_isPoints = true;
		}
	}
	
	public void updateCamera() {
		// rotate
		float circleSegment = (float) ( Math.PI * 2f );
		_rotationTarget.x = p.random( -circleSegment, circleSegment );
		_rotationTarget.y = p.random( -circleSegment, circleSegment );
		_rotationTarget.z = p.random( -circleSegment, circleSegment );
		
		_rotSpeed.x = p.random( 0.001f, 0.01f );
		_rotSpeed.y = p.random( 0.001f, 0.01f );
		_rotSpeed.z = p.random( 0.001f, 0.01f );
	}
	
	public void randomizeMesh() {
		_colorGradientDivider = p.random( 0.1f, 5f );
		for(int i=0; i<8; i++) {
			m[i] = (int)p.random(7);
		}
		
	}

	public void dispose() {
		_audioData = null;
	}
}
