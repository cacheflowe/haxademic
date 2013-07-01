package com.haxademic.app.kacheout.game;

import toxi.color.TColor;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.geom.mesh.WETriangleMesh;

import com.haxademic.app.kacheout.KacheOut;
import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.EasingTColor;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.ElasticFloat;

public class Ball {
	
	protected KacheOut p;
	protected float _ballSize;
	protected float _ballSizeAdd;
	protected float _ballSizeAddStartFrame;
	protected ElasticFloat _ballSizeElastic;
	protected int BALL_RESOLUTION = 6;
//	protected Sphere _sphere;
	protected AABB _box;
	protected float _x, _y, _speedX, _speedY;
//	protected float SPEED_UP = 1.0015f;
	protected float SPEED_UP = 1.0007f;
	
	protected float BASE_ALPHA = 0.8f;
	protected EasingFloat _alpha;
	protected boolean _waitingToLaunch = true;

	protected EasingTColor _color;
	protected final TColor YELLOW = new TColor( TColor.YELLOW );
	protected final TColor WHITE = new TColor( TColor.WHITE );

	protected float _baseSpeed;
	protected float _curBaseSpeed;
	protected WETriangleMesh _ballMesh;
	
	public Ball() {
		p = (KacheOut) P.p;
		// TODO: convert speed to use radians
		_baseSpeed = p.stageHeight() / 80f;
		_speedX = ( MathUtil.randBoolean( p ) == true ) ? _baseSpeed : -_baseSpeed;
		_x = p.random( 0, p.gameWidth() );
		_y = p.random( p.stageHeight() / 2, p.stageHeight() );
		_color = new EasingTColor( YELLOW, 0.05f );
		_alpha = new EasingFloat( 0, 7f );
		
		_ballSize = p.stageHeight() / 20f;
		_ballSizeElastic = new ElasticFloat( 0, 0.66f, 0.48f );
		_box = new AABB();
		_box.setExtent( new Vec3D( _ballSize, _ballSize, _ballSize/6f ) );
		
		_ballMesh = new WETriangleMesh();
		_ballMesh.addMesh( _box.toMesh() );
	}
	
	public float x() { return _x; }
	public float y() { return _y; }
	public float speedX() { return _speedX; }
	public float speedY() { return _speedY; }
	public AABB sphere() { return _box; }
	public float radius() { return _ballSize; }
	
	public void reset() {
		_curBaseSpeed = _baseSpeed;
		_alpha.setTarget( 0 );
		_ballSizeAdd = 0;
		_ballSizeAddStartFrame = 0;
	}
	
	public void launch( Paddle paddle ) {
		_alpha.setCurrent( 0 );
		_alpha.setTarget( BASE_ALPHA );
		_waitingToLaunch = true;
		_x = paddle.x(); 
		resetY( paddle );
	}
	
	public void bounceX() {
		_speedX *= -1;
		_x += _speedX;
	}
	
	public void bounceY() {
		_speedY *= -1;
		_y += _speedY;
	}
	
	/**
	 * Visually bounce the Ball on collisions
	 */
	public void bounceBall() {
		_color.setCurAndTargetColors( WHITE, YELLOW );
		_ballSizeElastic.setValue( _ballSize * 0.7f );
		_ballSizeElastic.setTarget( _ballSize );
	}
	
	public void display( Paddle paddle ) {
		// set position based on current game mode
		if( _waitingToLaunch == true ) {
			_ballSizeElastic.setTarget( _ballSize );
			_x = paddle.x();
			resetY( paddle );
		} else if( p.gameState() == KacheOut.GAME_ON ) {
			_x += _speedX;
			_y += _speedY;
		} else if( p.gameState() == KacheOut.GAME_OVER ) {
			_ballSizeElastic.setTarget( 0 );
		}
		_box.set( _x, _y, 0 );
				
		// always fade color
		_color.update();
		_color.color().alpha = _alpha.value();
		p.fill( _color.color().toARGB() );
		
		// fade in alpha before relaunching
		_alpha.update();
		if( _alpha.value() == BASE_ALPHA && _waitingToLaunch == true ) {
			_waitingToLaunch = false;
			_speedX = ( MathUtil.randBoolean( p ) == true ) ? _curBaseSpeed : -_curBaseSpeed;
			_speedY = -_curBaseSpeed;
			p.sounds.playSound( KacheOut.LAUNCH_SOUND );
		}
		
		// start increasing box size after 40 seconds
		_ballSizeAddStartFrame++;
		if( _ballSizeAddStartFrame > 40 * 30 ) {	
			if( _ballSizeAdd < 30 ) _ballSizeAdd += 0.1f;
		}
		
		// update elastic scale and redraw box
		_ballSizeElastic.update();
		float ballScale = _ballSizeElastic.val() / _ballSize;		
		_box.setExtent( new Vec3D( _ballSize * ballScale + _ballSizeAdd, _ballSize * ballScale + _ballSizeAdd, _ballSize/6f * ballScale ) );
		p.toxi.box( _box ); 
	}
	
	public void resetY( Paddle paddle ) {
		_y = paddle.y() - paddle.height() - ( _ballSize + _ballSizeAdd ) - 10;
	}
	
	public void detectWalls( boolean leftHit, boolean topHit, boolean rightHit ) {
		boolean didHit = false;
		if( leftHit == true ) {
			_x -= _speedX;
			_speedX *= -1;
			didHit = true;
		}
		if( rightHit == true ) {
			_x -= _speedX;
			_speedX *= -1;
			didHit = true;
		}
		if( topHit == true ) {
			_y -= _speedY;
			_speedY *= -1;
			didHit = true;
		}
		
		if( didHit == true ) bounceBall();
	}

	public boolean detectBox( AABB box ) {
//		_wallLeft.intersectsBox( sphere )
		// speed up a little every time we knock a block off
		if( box.intersectsBox( _box ) ) {
			_curBaseSpeed *= SPEED_UP;
			_speedX *= SPEED_UP;
			_speedY *= SPEED_UP;
			return true;
		}
		return false;
	}
	
	public void bounceOffPaddle( Paddle paddle ) {
		if( _speedY > 0 ) {
			_speedX = ( _x - paddle.x() ) / 10;
			_speedX = P.constrain( _speedX, -_curBaseSpeed * 1.4f, _curBaseSpeed * 1.4f );
			bounceY();
			bounceBall();
			_color.setCurAndTargetColors( WHITE, YELLOW );
			p.sounds.getSound( "PADDLE_BOUNCE" ).play(0);
			paddle.hit();
		}
	}

}
