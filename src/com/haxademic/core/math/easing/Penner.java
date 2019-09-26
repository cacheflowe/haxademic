package com.haxademic.core.math.easing;

public class Penner {

	/**
	 * Converted from: https://github.com/jesusgollonet/processing-penner-easing
	 * Open source under the BSD License
	 * Copyright © 2001 Robert Penner 
	 *  
	 * t: current time
	 * b: start value
	 * c: change in value
	 * d: duration
	 */

	/* Back */

	public static float easeInBack(float normalizedProgress) {
		return easeInBack(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeInBack(float t, float b, float c, float d) {
		float s = 1.70158f;
		return easeInBack(t, b, c, d, s);
	}

	public static float easeInBack(float t, float b, float c, float d, float s) {
		return c*(t/=d)*t*((s+1)*t - s) + b;
	}

	public static float easeOutBack(float normalizedProgress) {
		return easeOutBack(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeOutBack(float t, float b, float c, float d) {
		float s = 1.70158f;
		return easeOutBack(t, b, c, d, s);
	}

	public static float easeOutBack(float t, float b, float c, float d, float s) {
		return c*((t=t/d-1)*t*((s+1)*t + s) + 1) + b;
	}

	public static float easeInOutBack(float normalizedProgress) {
		return easeInOutBack(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeInOutBack(float t, float b, float c, float d) {
		float s = 1.70158f;
		return easeInOutBack(t, b, c, d, s);
	}

	public static float easeInOutBack(float t,float b, float c, float d, float s) { 
		if ((t/=d/2) < 1) return c/2*(t*t*(((s*=(1.525f))+1)*t - s)) + b;
		return c/2*((t-=2)*t*(((s*=(1.525f))+1)*t + s) + 2) + b;
	}

	/* Bounce */

	public static float easeInBounce(float normalizedProgress) {
		return easeInBounce(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeInBounce(float t, float b, float c, float d) {
		return c - easeOutBounce (d-t, 0, c, d) + b;
	}

	public static float easeOutBounce(float normalizedProgress) {
		return easeOutBounce(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeOutBounce(float t, float b, float c, float d) {
		if ((t/=d) < (1/2.75f)) {
			return c*(7.5625f*t*t) + b;
		} else if (t < (2/2.75f)) {
			return c*(7.5625f*(t-=(1.5f/2.75f))*t + .75f) + b;
		} else if (t < (2.5/2.75)) {
			return c*(7.5625f*(t-=(2.25f/2.75f))*t + .9375f) + b;
		} else {
			return c*(7.5625f*(t-=(2.625f/2.75f))*t + .984375f) + b;
		}
	}

	public static float easeInOutBounce(float normalizedProgress) {
		return easeInOutBounce(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeInOutBounce(float t, float b, float c, float d) {
		if (t < d/2) return easeInBounce (t*2, 0, c, d) * .5f + b;
		else return easeOutBounce (t*2-d, 0, c, d) * .5f + c*.5f + b;
	}

	/* Circ */

	public static float easeInCirc(float normalizedProgress) {
		return easeInCirc(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeInCirc(float t, float b, float c, float d) {
		return -c * ((float)Math.sqrt(1 - (t/=d)*t) - 1) + b;
	}

	public static float easeOutCirc(float normalizedProgress) {
		return easeOutCirc(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeOutCirc(float t, float b, float c, float d) {
		return c * (float)Math.sqrt(1 - (t=t/d-1)*t) + b;
	}

	public static float easeInOutCirc(float normalizedProgress) {
		return easeInOutCirc(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeInOutCirc(float t, float b, float c, float d) {
		if ((t/=d/2) < 1) return -c/2 * ((float)Math.sqrt(1 - t*t) - 1) + b;
		return c/2 * ((float)Math.sqrt(1 - (t-=2)*t) + 1) + b;
	}


	/* Cubic */

	public static float easeInCubic(float normalizedProgress) {
		return easeInCubic(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeInCubic(float t, float b, float c, float d) {
		return c*(t/=d)*t*t + b;
	}

	public static float easeOutCubic(float normalizedProgress) {
		return easeOutCubic(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeOutCubic(float t, float b, float c, float d) {
		return c*((t=t/d-1)*t*t + 1) + b;
	}

	public static float easeInOutCubic(float normalizedProgress) {
		return easeInOutCubic(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeInOutCubic(float t, float b, float c, float d) {
		if ((t/=d/2) < 1) return c/2*t*t*t + b;
		return c/2*((t-=2)*t*t + 2) + b;
	}

	/* Elastic */

	public static float easeInElastic(float normalizedProgress) {
		return easeInElastic(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeInElastic(float t, float b, float c, float d ) {
		if (t==0) return b;  if ((t/=d)==1) return b+c;  
		float p=d*.3f;
		float a=c; 
		float s=p/4;
		return -(a*(float)Math.pow(2,10*(t-=1)) * (float)Math.sin( (t*d-s)*(2*(float)Math.PI)/p )) + b;
	}

	public static float easeInElastic(float t, float b, float c, float d, float a, float p) {
		float s;
		if (t==0) return b;  if ((t/=d)==1) return b+c;  
		if (a < Math.abs(c)) { a=c;  s=p/4; }
		else { s = p/(2*(float)Math.PI) * (float)Math.asin (c/a);}
		return -(a*(float)Math.pow(2,10*(t-=1)) * (float)Math.sin( (t*d-s)*(2*Math.PI)/p )) + b;
	}

	public static float easeOutElastic(float normalizedProgress) {
		return easeOutElastic(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeOutElastic(float t, float b, float c, float d) {
		if (t==0) return b;  if ((t/=d)==1) return b+c;  
		float p=d*.3f;
		float a=c; 
		float s=p/4;
		return (a*(float)Math.pow(2,-10*t) * (float)Math.sin( (t*d-s)*(2*(float)Math.PI)/p ) + c + b);  
	}

	public static float easeOutElastic(float t, float b, float c, float d, float a, float p) {
		float s;
		if (t==0) return b;  if ((t/=d)==1) return b+c;  
		if (a < Math.abs(c)) { a=c;  s=p/4; }
		else { s = p/(2*(float)Math.PI) * (float)Math.asin (c/a);}
		return (a*(float)Math.pow(2,-10*t) * (float)Math.sin( (t*d-s)*(2*(float)Math.PI)/p ) + c + b);  
	}

	public static float easeInOutElastic(float normalizedProgress) {
		return easeInOutElastic(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeInOutElastic(float t, float b, float c, float d) {
		if (t==0) return b;  if ((t/=d/2)==2) return b+c; 
		float p=d*(.3f*1.5f);
		float a=c; 
		float s=p/4;
		if (t < 1) return -.5f*(a*(float)Math.pow(2,10*(t-=1)) * (float)Math.sin( (t*d-s)*(2*(float)Math.PI)/p )) + b;
		return a*(float)Math.pow(2,-10*(t-=1)) * (float)Math.sin( (t*d-s)*(2*(float)Math.PI)/p )*.5f + c + b;
	}

	public static float easeInOutElastic(float t, float b, float c, float d, float a, float p) {
		float s;
		if (t==0) return b;  if ((t/=d/2)==2) return b+c;  
		if (a < Math.abs(c)) { a=c; s=p/4; }
		else { s = p/(2*(float)Math.PI) * (float)Math.asin (c/a);}
		if (t < 1) return -.5f*(a*(float)Math.pow(2,10*(t-=1)) * (float)Math.sin( (t*d-s)*(2*(float)Math.PI)/p )) + b;
		return a*(float)Math.pow(2,-10*(t-=1)) * (float)Math.sin( (t*d-s)*(2*(float)Math.PI)/p )*.5f + c + b;
	}

	/* Expo */

	public static float easeInExpo(float normalizedProgress) {
		return easeInExpo(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeInExpo(float t, float b, float c, float d) {
		return (t==0) ? b : c * (float)Math.pow(2, 10 * (t/d - 1)) + b;
	}

	public static float easeOutExpo(float normalizedProgress) {
		return easeOutExpo(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeOutExpo(float t, float b, float c, float d) {
		return (t==d) ? b+c : c * (-(float)Math.pow(2, -10 * t/d) + 1) + b; 
	}

	public static float easeInOutExpo(float normalizedProgress) {
		return easeInOutExpo(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeInOutExpo(float t, float b, float c, float d) {
		if (t==0) return b;
		if (t==d) return b+c;
		if ((t/=d/2) < 1) return c/2 * (float)Math.pow(2, 10 * (t - 1)) + b;
		return c/2 * (-(float)Math.pow(2, -10 * --t) + 2) + b;
	}

	/* Linear */

	public static float easeLinear(float normalizedProgress) {
		return easeNoneLinear(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeNoneLinear(float t, float b, float c, float d) {
		return c*t/d + b;
	}

	public static float easeInLinear(float t, float b, float c, float d) {
		return c*t/d + b;
	}

	public static float easeOutLinear(float t, float b, float c, float d) {
		return c*t/d + b;
	}

	public static float easeInOutLinear(float t, float b, float c, float d) {
		return c*t/d + b;
	}

	/* Quad */

	public static float easeInQuad(float normalizedProgress) {
		return easeInQuad(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeInQuad(float t, float b, float c, float d) {
		return c*(t/=d)*t + b;
	}

	public static float easeOutQuad(float normalizedProgress) {
		return easeOutQuad(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeOutQuad(float t, float b, float c, float d) {
		return -c *(t/=d)*(t-2) + b;
	}

	public static float easeInOutQuad(float normalizedProgress) {
		return easeInOutQuad(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeInOutQuad(float t, float b, float c, float d) {
		if ((t/=d/2) < 1) return c/2*t*t + b;
		return -c/2 * ((--t)*(t-2) - 1) + b;
	}

	/* Quart */

	public static float easeInQuart(float normalizedProgress) {
		return easeInQuart(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeInQuart(float t, float b, float c, float d) {
		return c*(t/=d)*t*t*t + b;
	}

	public static float easeOutQuart(float normalizedProgress) {
		return easeOutQuart(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeOutQuart(float t, float b, float c, float d) {
		return -c * ((t=t/d-1)*t*t*t - 1) + b;
	}

	public static float easeInOutQuart(float normalizedProgress) {
		return easeInOutQuart(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeInOutQuart(float t, float b, float c, float d) {
		if ((t/=d/2) < 1) return c/2*t*t*t*t + b;
		return -c/2 * ((t-=2)*t*t*t - 2) + b;
	}

	/* Quint */

	public static float easeInQuint(float normalizedProgress) {
		return easeInQuint(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeInQuint(float t, float b, float c, float d) {
		return c*(t/=d)*t*t*t*t + b;
	}

	public static float easeOutQuint(float normalizedProgress) {
		return easeOutQuint(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeOutQuint(float t, float b, float c, float d) {
		return c*((t=t/d-1)*t*t*t*t + 1) + b;
	}

	public static float easeInOutQuint(float normalizedProgress) {
		return easeInOutQuint(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeInOutQuint(float t, float b, float c, float d) {
		if ((t/=d/2) < 1) return c/2*t*t*t*t*t + b;
		return c/2*((t-=2)*t*t*t*t + 2) + b;
	}

	/* Sine */

	public static float easeInSine(float normalizedProgress) {
		return easeInSine(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeInSine(float t, float b, float c, float d) {
		return -c * (float)Math.cos(t/d * (Math.PI/2)) + c + b;
	}

	public static float easeOutSine(float normalizedProgress) {
		return easeOutSine(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeOutSine(float t, float b, float c, float d) {
		return c * (float)Math.sin(t/d * (Math.PI/2)) + b;  
	}

	public static float easeInOutSine(float normalizedProgress) {
		return easeInOutSine(normalizedProgress, 0, 1, 1);
	}
	
	public static float easeInOutSine(float t, float b, float c, float d) {
		return -c/2 * ((float)Math.cos(Math.PI*t/d) - 1) + b;
	}

}
