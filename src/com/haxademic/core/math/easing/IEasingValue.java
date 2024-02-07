package com.haxademic.core.math.easing;

public interface IEasingValue {
	public IEasingValue setTarget(float value);
	public IEasingValue setCurrent(float value);
	public IEasingValue setDelegate(IEasingValueDelegate delegate);
	public IEasingValue setDelay(int delay);
	public IEasingValue setEaseFactor(float easeFactor);
	public float value();
	public float target();
	public IEasingValue update();
	public boolean isComplete();

	public interface IEasingValueDelegate {
		public void easingValueComplete(IEasingValue easingObject);
	}
}
