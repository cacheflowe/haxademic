// ported from: https://www.shadertoy.com/view/XtGyWc

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float controlX = 0.5;
uniform float mixAmp = 0.1;
uniform float gainCurve = 1.2;
uniform bool debug = false;

#define PI 3.141592653589793
#define HALF_PI 1.5707963267948966

float map(float value, float low1, float high1, float low2, float high2) {
   return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
}

float cubicIn(float t) {
  return t * t * t;
}

float cubicOut(float t) {
  float f = t - 1.0;
  return f * f * f + 1.0;
}

float exponentialIn(float t) {
  return t == 0.0 ? t : pow(2.0, 10.0 * (t - 1.0));
}

float exponentialOut(float t) {
  return t == 1.0 ? t : 1.0 - pow(2.0, -10.0 * t);
}

float sineInOut(float t) {
  return -0.5 * (cos(PI * t) - 1.0);
}

float sineIn(float t) {
  return sin((t - 1.0) * HALF_PI) + 1.0;
}

float sineOut(float t) {
  return sin(t * HALF_PI);
}

float quadraticIn(float t) {
  return t * t;
}

float quadraticOut(float t) {
  return -t * (t - 2.0);
}

// http://www.iquilezles.org/www/articles/functions/functions.htm
float gain(float x, float k) {
    float a = 0.5*pow(2.0*((x<0.5)?x:1.0-x), k);
    return (x<0.5)?a:1.0-a;
}

void main() {
  // get coordinates
  vec2 uv = vertTexCoord.xy;
  vec2 uvOrig = uv;

  // get distance from control point
  float distToControl = distance(controlX, uv.x);
  distToControl *= 2.25;
  distToControl = clamp(distToControl, 0., 1.);
  distToControl = 1. - distToControl;

  // calculate easing curve
  float curveLeft, curveRight;
  if(gainCurve != 0.) {
    curveLeft = gain( map(uv.x, 0.,controlX, 0., 1.), gainCurve );
    curveRight = gain( map(uv.x, controlX, 1., 0., 1.), gainCurve );
  } else {
    curveLeft = quadraticOut( map(uv.x, 0.,controlX, 0., 1.) );
    curveRight = quadraticIn( map(uv.x, controlX, 1., 0., 1.) );
  }

  float curveAmp = (uv.x < controlX) ? curveLeft : curveRight + 1.;

  // test
  // float curveMix = mix(curveAmp, distToControl, 0.5);
  // curveMix = min(curveMix, curveAmp);

  // uv.x = mix(uv.x, curveAmp, 0.5);
  uv.x = mix(uv.x, curveAmp / 2., mixAmp);

  // display warped texture
  gl_FragColor = texture(texture, uv);

  //////////////////////////////////////////
  // debug
  //////////////////////////////////////////

  if(debug == true) {
    // black line
    if(uv.y < 0.455) {
    	gl_FragColor = vec4(0., 0., 0., 1.);
    }
    // original texture
    if(uv.y < 0.45) {
    	gl_FragColor = texture(texture, uvOrig);
    }
    // black line
    if(uv.y < 0.155) {
    	gl_FragColor = vec4(0., 0., 0., 1.);
    }
    // show curve gradient
    if(uv.y < 0.15) {
      curveAmp = (uvOrig.x < controlX) ? curveLeft : 1.- curveRight;
      gl_FragColor = vec4(vec3(curveAmp), 1.);
    }
    // distance to controlX
    if(uv.y < 0.1) {
      gl_FragColor = vec4(distToControl, distToControl, distToControl, 1.);
    }
    // controlX center
    if(uv.y < 0.05) {
      float direction = sign(controlX - uvOrig.x);
      gl_FragColor = vec4(vec3(direction), 1.);
    }
  }
}
