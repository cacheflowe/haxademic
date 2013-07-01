package com.haxademic.app.kacheout.game;

import java.util.ArrayList;

import toxi.color.TColor;
import toxi.geom.AABB;
import toxi.geom.Vec3D;

import com.haxademic.app.kacheout.KacheOut;
import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.EasingTColor;
import com.haxademic.core.math.MathUtil;

public class Block {
	protected KacheOut p;
	// A cell object knows about its location in the grid as well as its size with the variables x,y,w,h.
	protected AABB _box, _boxOrigin;
	int _index;
	protected boolean _active;
	protected EasingTColor _color;
	protected TColor _colorStart;
	protected TColor _colorDead;
	protected TColor _colorAudio;
	protected ArrayList<Shard> _shards;
	protected ArrayList<Vec3D> _explodeVecs;
	protected float _scale, _speedX, _speedY;
	protected float _lastZAdd = 0;
	
	public Block( AABB box, int index, float scale, TColor color ) {
		p = (KacheOut) P.p;

		_box = box;
		_boxOrigin = _box.copy();
		_index = index;
		_scale = scale;
		
		// set up color fading
		_colorStart = color.copy().darken( 0.35f );
		_colorDead = new TColor( TColor.WHITE );
		_colorAudio = _colorStart.copy().getDesaturated( 0.75f ).setBrightness( 1f );//new TColor( TColor.BLACK );
		_color = new EasingTColor( _colorStart, 0.1f );
		
		reset( true );
	}
	
	public void reset( boolean shouldResetPosition ) {
		_color.setTargetColor( _colorStart );
		_active = true;
		if( shouldResetPosition ) {
			_box.x = _boxOrigin.x;
			_box.y = _boxOrigin.y;
		}
	}
	
	public boolean active() {
		return _active;
	}
	
	public boolean isReset() {
		if( Math.abs( _box.x - _boxOrigin.x ) < 1 && Math.abs( _box.y - _boxOrigin.y ) < 1 ) {
			return true;
		} else {
			return false;
		}
	}
	
	public AABB box() {
		return _box;
	}
	
	public void die( float speedX, float speedY ) {
		//if( _active == true ) createShatteredMesh( speedX, speedY );
		if( _active == true ) {
			_speedX = speedX*p.random(1.5f,3.5f);
			_speedY = speedY*p.random(1.5f,3.5f);
			_color.setTargetColor( _colorDead );
			_active = false;
		}
	}
	
//	protected void createShatteredMesh( float speedX, float speedY ) {
//		if( _shards == null ) {
//			_shards = new ArrayList<Shard>();
//			_explodeVecs = new ArrayList<Vec3D>();
//			for( int i=0; i < p.shatteredCubeMeshes.size(); i++ ) {
//				_shards.add( new Shard( p.shatteredCubeMeshes.get( i ).copy(), _box.x, _box.y, _box.z ) );
//				_shards.get(i).setSpeed( speedX*p.random(2.f,4.f), speedY*p.random(2.f,4.f), p.random(-2.5f,2.5f) );
//			}
//		}
//	}
	
	public void display() {
		_color.update();
		if( _active == true ) {
			// ease box to origin
			_box.set( MathUtil.easeTo( _box.x, _boxOrigin.x, 10f ), MathUtil.easeTo( _box.y, _boxOrigin.y, 10f ), 0 );
			
			// adjust cell z per brightness
			float zAdd = 6 + 50f * p._audioInput.getFFT().spectrum[_index+1 % 512];
			_box.setExtent( new Vec3D( _scale/200f, _scale/200f, zAdd ) );
			
			// ease towards white, or default color, depending on which way the audio eq's going
			if( _lastZAdd < zAdd )
				_color.setTargetColor( _colorAudio );
			else
				_color.setTargetColor( _colorStart );
			_lastZAdd = zAdd;
			
			//_color.color().alpha = p.constrain( 0.5f + zAdd, 0, 1 );
			p.fill( _color.color().toARGB() );
			p.noStroke();
			p.toxi.box( _box );	
			
		} else {
			if( _box.y < p.stageHeight() ) {
				// gravity
				_speedY += 0.75f;
				// move box
				_box.set( _box.x + _speedX, _box.y + _speedY, 0 );
				
				p.fill( _color.color().toARGB() );
				p.noStroke();
				p.toxi.box( _box );
			}
		}
	}	
}
