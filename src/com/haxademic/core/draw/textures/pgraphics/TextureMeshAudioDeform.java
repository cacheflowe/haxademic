package com.haxademic.core.draw.textures.pgraphics;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeSolid;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PShape;
import processing.core.PVector;

public class TextureMeshAudioDeform 
extends BaseTexture {

	protected ArrayList<PShapeSolid> _meshPool;
	protected PShapeSolid _curMesh;
	protected int _meshIndex = -1;
	protected boolean _isWireframe = true;

	protected PVector _rotSpeed = new PVector( 0, 0, 0 );
	protected PVector _rotation = new PVector( 0, 0, 0 );
	protected PVector _rotationTarget = new PVector( 0, 0, 0 );

	public TextureMeshAudioDeform( int width, int height ) {
		super(width, height);
		
		init();
	}

	public void init() {
		_meshPool = new ArrayList<PShapeSolid>();
		_meshPool.add(prepShape(P.p.loadShape(FileUtil.getFile("models/unicorn-head-lowpoly.obj"))));
		_meshPool.add(prepShape(P.p.loadShape(FileUtil.getFile("models/the-discovery-multiplied-seied.obj"))));
		_meshPool.add(prepShape(P.p.loadShape(FileUtil.getFile("models/topsecret-seied.obj"))));
		_meshPool.add(prepShape(P.p.loadShape(FileUtil.getFile("models/skull.obj"))));
		_meshPool.add(prepShape(P.p.loadShape(FileUtil.getFile("models/poly-hole-penta.obj"))));
		_meshPool.add(prepShape(P.p.loadShape(FileUtil.getFile("models/poly-hole-square.obj"))));
		_meshPool.add(prepShape(P.p.loadShape(FileUtil.getFile("models/poly-hole-tri.obj"))));
		selectNewModel();
	}

	protected PShapeSolid prepShape(PShape shape) {
		// scale it to fit the window
//		shape = shape.getTessellation();
//		PShapeUtil.repairMissingSVGVertex(shape);
		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleShapeToHeight(shape, height * 0.5f);
		// add UV coordinates to OBJ
		PShapeUtil.addTextureUVSpherical(shape, null);
		// create PShapeSolid
		return new PShapeSolid(shape);
	}

	public void newLineMode() {
		_isWireframe = MathUtil.randBoolean( P.p );
	}

	public void newRotation() {
		// rotate
		float circleSegment = (float) ( Math.PI * 2f );
		_rotationTarget.x = MathUtil.randRangeDecimal( -circleSegment, circleSegment );
		_rotationTarget.y = MathUtil.randRangeDecimal( -circleSegment, circleSegment );
		_rotationTarget.z = MathUtil.randRangeDecimal( -circleSegment, circleSegment );

		_rotSpeed.x = MathUtil.randRangeDecimal( 0.001f, 0.01f );
		_rotSpeed.y = MathUtil.randRangeDecimal( 0.001f, 0.01f );
		_rotSpeed.z = MathUtil.randRangeDecimal( 0.001f, 0.01f );
	}

	public void updateRotation() {
		//		_rotation.easeToPoint( _rotationTarget, 5 );
		//		_texture.rotateX( _rotation.x );
		//		_texture.rotateY( _rotation.y );
		//		_texture.rotateZ( _rotation.z );

		_rotationTarget.x += _rotSpeed.x;
		_rotationTarget.y += _rotSpeed.y;
		_rotationTarget.z += _rotSpeed.z;
	}

	public void newMode() {
		selectNewModel();
	}
	public void selectNewModel() {
		_meshIndex++;
		if( _meshIndex >= _meshPool.size() ) _meshIndex = 0;
		_curMesh = _meshPool.get(_meshIndex);
	}

	public void updateDraw() {
//		_texture.clear();
		_texture.background(0);
		
		PG.setDrawCorner(_texture);
		PG.setCenterScreen(_texture);
		_texture.pushMatrix();

		_rotation.x += _rotSpeed.x;
		_rotation.y += _rotSpeed.y;
		_rotation.z += _rotSpeed.z;
		_texture.rotateX( _rotation.x );
		_texture.rotateY( _rotation.y );
		_texture.rotateZ( _rotation.z );

		if( _isWireframe ) {
			_texture.noFill(); 
			_texture.strokeWeight( 3 );
		} else {
			_texture.noStroke(); 
			_texture.fill(255);
		}

		// deform and draw mesh
		if(_curMesh != null) {
			// _curMesh.deformWithAudioByNormals();
			_curMesh.deformWithAudio(3);
			_curMesh.setVertexColorWithAudio(255);
			_texture.shape(_curMesh.shape());
			// PShapeUtil.drawTriangles(_texture, _curMesh.shape(), null, 1f); // img
		}

		_texture.popMatrix();		
	}

}
