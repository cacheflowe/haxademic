package com.haxademic.core.math.easing;

public interface IEasingValue {
	public IEasingValue setTarget(float value);
	public IEasingValue setCurrent(float value);
	public IEasingValue setDelegate(IEasingValueDelegate delegate);
	public float value();
	public void update();
	public boolean isComplete();

	public interface IEasingValueDelegate {
		public void complete(IEasingValue easingObject);
	}
}
