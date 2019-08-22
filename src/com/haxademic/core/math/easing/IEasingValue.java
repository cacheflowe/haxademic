package com.haxademic.core.math.easing;

public interface IEasingValue {
	public void setTarget(float value);
	public void setCurrent(float value);
	public float value();
	public void update();

	public interface IEasingValueDelegate {
		public void complete(IEasingValue easingObject);
	}
}
