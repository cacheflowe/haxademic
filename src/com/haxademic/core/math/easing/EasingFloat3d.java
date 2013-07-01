package com.haxademic.core.math.easing;


public class EasingFloat3d
implements IEasingValue {
	
	public EasingFloat _x, _y, _z;
	public float _easeFactor;
		   
	public EasingFloat3d( float x, float y, float z, float easeFactor ) {
		_x = new EasingFloat( x, easeFactor );
		_y = new EasingFloat( y, easeFactor );
		_z = new EasingFloat( z, easeFactor );
	}
	
	public float x() {
		return _x.value();
	}
	
	public float y() {
		return _y.value();
	}
	
	public float z() {
		return _z.value();
	}
	
	public void setTargetX( float value ) {
		_x.setTarget( value );
	}
	
	public void setTargetY( float value ) {
		_y.setTarget( value );
	}
	
	public void setTargetZ( float value ) {
		_z.setTarget( value );
	}
	
	public void setCurrentX( float value ) {
		_x.setCurrent( value );
	}
	
	public void setCurrentY( float value ) {
		_y.setCurrent( value );
	}
	
	public void setCurrentZ( float value ) {
		_z.setCurrent( value );
	}
	
	public void setEaseFactor( float value ) {
		_easeFactor = value;
		_x.setEaseFactor( _easeFactor );
		_y.setEaseFactor( _easeFactor );
		_z.setEaseFactor( _easeFactor );
	}
	
	public void update() {
		_x.update();
		_y.update();
		_z.update();
	}
}
