package com.haxademic.core.math.easing;

public class ElasticFloat3D {
	
	protected ElasticFloat _x, _y, _z;
	
	public ElasticFloat3D( float x, float y, float z, float fric, float accel ) {
		_x = new ElasticFloat( x, fric, accel );
		_y = new ElasticFloat( y, fric, accel );
		_z = new ElasticFloat( z, fric, accel );
	}
	
	public float x() {
		return _x.val();
	};

	public float y() {
		return _y.val();
	};

	public float z() {
		return _z.val();
	};

	public void setLoc( float x, float y, float z ) {
		_x.setValue( x );
		_y.setValue( y );
		_z.setValue( z );
	};

	public void setCurrent( float x, float y, float z ) {
		setLoc( x, y, z );
	}

	public void setTarget( float x, float y, float z ) {
		_x.setTarget( x );
		_y.setTarget( y );
		_z.setTarget( z );
	};

	public void setFriction( float fric ) {
		_x.setFriction( fric );
		_y.setFriction( fric );
		_z.setFriction( fric );
	};

	public void setAccel( float accel ) {
		_x.setAccel( accel );
		_y.setAccel( accel );
		_z.setAccel( accel );
	}

	public void update() {
		_x.update();
		_y.update();
		_z.update();
	}

}
