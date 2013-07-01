package com.haxademic.app.kacheout.game;

import toxi.color.TColor;
import toxi.geom.AABB;
import toxi.geom.Vec3D;

import com.haxademic.app.kacheout.KacheOut;
import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.EasingTColor;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.ElasticFloat;

public class Paddle {
	protected EasingFloat _x, _z;
	protected ElasticFloat _y;
	protected float _stageVPadding;
	protected float _height;
	protected float _baseY;
	protected float _width = 0;
	protected float _easing = 1.5f;
	protected float _center;
	protected float _xPosPercent;
	protected EasingTColor _color;
	protected final TColor GREEN = new TColor( TColor.GREEN );
	protected final TColor WHITE = new TColor( TColor.WHITE );
	protected final TColor RED = new TColor( TColor.RED );
	protected boolean _passedPaddle = false;
	protected AABB _box;
	protected KacheOut p;

	public Paddle() {
		p = (KacheOut) P.p;
		_xPosPercent = 0.5f;
		
		_width = (float)p.gameWidth() / 6f;
		_height = (float)p.stageHeight() * 0.025f;
		
		_center = ( p.gameWidth() + _width) / 2;
		_x = new EasingFloat( _center, _easing );
		_stageVPadding = p.stageHeight() * 0.01f;
		_baseY = p.stageHeight() - _stageVPadding;
		_y = new ElasticFloat( _baseY, 0.6f, 0.4f );
		
		_color = new EasingTColor( WHITE, 0.2f );
		
		_box = new AABB( 1 );
		_box.setExtent( new Vec3D( _width, _height, _height ) );
	} 
	
	public float x() { return _x.value(); }
	public float y() { return _y.val(); }
	public float height() { return _height; }
	public float xPosPercent() { return _xPosPercent; }
	public boolean passedPaddle() { return _passedPaddle; }

	public AABB box() {
		return _box;
	}

	public void setTargetXByPercent( float percent ) {
		_xPosPercent = percent;
		percent = 1 - percent;
		_x.setTarget( _width + percent * (p.gameWidth() - _width*2f) );
	}

//	public boolean detectSphere( Sphere sphere ) {
//		if( _box.intersectsSphere( sphere ) ) return true;
//		return false;
//	}
	
	public void launch() {
		_passedPaddle = false;
	}
	
	public void hit() {
		_color.setCurColor( GREEN );
		_color.setTargetColor( WHITE );
		_y.setValue( _baseY + 20f );
		_y.setTarget( _baseY );
	}
	
	public void ballPassedPaddle() {
		if( _passedPaddle == false ) {
			_passedPaddle = true;
			p.sounds.playSound( KacheOut.LOSE_BALL_SOUND );
			_color.setCurColor( RED );
			_color.setTargetColor( WHITE );
		}
	}

	public void display() {
		_x.update();
		_y.update();
		_color.update();
		
		_box.set( _x.value(), _y.val(), 0 );
//		_box.rotateX( p.frameCount / 30f );
//		_color.alpha = 0.5f + p._audioInput.getFFT().averages[1];
		
		_color.color().alpha = ( p.gameState() == KacheOut.GAME_ON || p.gameState() == KacheOut.GAME_OVER ) ? 1 : 0.4f;
		
		p.fill( _color.color().toARGB() );
		p.noStroke();
		p.toxi.box( _box ); 
	}
}
