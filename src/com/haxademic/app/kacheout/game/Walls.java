package com.haxademic.app.kacheout.game;

import toxi.color.TColor;
import toxi.geom.AABB;
import toxi.geom.Vec3D;

import com.haxademic.app.kacheout.KacheOut;
import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.EasingTColor;

public class Walls {
	protected KacheOut p;
	protected AABB _wallLeft, _wallTop, _wallRight;
	protected boolean _wallLeftHit, _wallTopHit, _wallRightHit;
	protected EasingTColor _colorLeft, _colorTop, _colorRight;
	public final int WALL_WIDTH = 10;
	protected final TColor GREEN = new TColor( TColor.GREEN );
	protected final TColor RED = new TColor( TColor.RED );
	protected final TColor YELLOW = new TColor( TColor.YELLOW );
	protected final TColor WHITE = new TColor( TColor.WHITE );

	public Walls() {
		p = (KacheOut) P.p;
		_colorLeft = new EasingTColor( WHITE, 0.03f );
		_colorTop = new EasingTColor( WHITE, 0.03f );
		_colorRight = new EasingTColor( WHITE, 0.03f );
		
		float sideX = p.stageHeight() / 2f;
		float sideHeight = p.stageHeight() / 2f + WALL_WIDTH;
		float topX = p.gameWidth() / 2f;
		float topWidth = p.gameWidth() / 2f - WALL_WIDTH;
		
		_wallLeft = new AABB( 1 );
		_wallLeft.set( 0, sideX, 0 );
		_wallLeft.setExtent( new Vec3D( WALL_WIDTH, sideHeight, WALL_WIDTH ) );

		_wallTop = new AABB( 1 );
		_wallTop.set( topX, 0, 0 );
		_wallTop.setExtent( new Vec3D( topWidth, WALL_WIDTH, WALL_WIDTH ) );

		_wallRight = new AABB( 1 );
		_wallRight.set( p.gameWidth(), sideX, 0 );
		_wallRight.setExtent( new Vec3D( WALL_WIDTH, sideHeight, WALL_WIDTH ) );

	} 
	
	public boolean leftHit(){ return _wallLeftHit; }
	public boolean topHit(){ return _wallTopHit; }
	public boolean rightHit(){ return _wallRightHit; }
	
	public boolean detectSphere( AABB sphere ) {
		_wallLeftHit = ( _wallLeft.intersectsBox( sphere ) ) ? true : false;
		_wallTopHit = ( _wallTop.intersectsBox( sphere ) ) ? true : false;
		_wallRightHit = ( _wallRight.intersectsBox( sphere ) ) ? true : false;
		if( _wallLeftHit == true || _wallTopHit == true || _wallRightHit == true ) {
			if( _wallLeftHit == true ) {
				_colorLeft.setCurColor( GREEN );
				_colorLeft.setTargetColor( WHITE );
			}
			if( _wallTopHit == true ) {
				_colorTop.setCurColor( RED );
				_colorTop.setTargetColor( WHITE );
			}
			if( _wallRightHit == true ) {
				_colorRight.setCurColor( YELLOW );
				_colorRight.setTargetColor( WHITE );
			}
			return true;
		}
		return false;
	}
	
	public void resetCollisions() {
		_wallLeftHit = false;
		_wallTopHit = false;
		_wallRightHit = false;
	}

	public void display() {
		p.noStroke();
		_colorLeft.update();
		_colorTop.update();
		_colorRight.update();
		p.fill( _colorLeft.color().toARGB() );
		p.toxi.box( _wallLeft ); 
		p.fill( _colorTop.color().toARGB() );
		p.toxi.box( _wallTop ); 
		p.fill( _colorRight.color().toARGB() );
		p.toxi.box( _wallRight ); 
	}
}
