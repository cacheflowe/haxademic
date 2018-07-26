#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform float time;

#define PI     3.14159265358
#define TWO_PI (PI * 2.)

float patternForPos(vec2 uv, float reso, float time) {
    float timeOsc = sin(time) * 0.15;								// oscillation helper
    float dist = 0.;												// start distance count from 0
    for(float i=10.; i < 20.; i++) {								// create x control points
        float rads = timeOsc + i;									// get rads for control point
        vec2 ctrlPoint = vec2(sin(rads), cos(rads));				// control points in a circle
        ctrlPoint *= abs(cos(rads)) * 15.;							// oscillate control point radius - the magic happens w/abs()
        dist += sin(i + reso * distance(uv, ctrlPoint));			// sum up oscillated distance between control points
    }
    return dist;
}

vec2 rotateCoord(vec2 uv, float rads) {
    uv *= mat2(cos(rads), sin(rads), -sin(rads), cos(rads));
	return uv;
}

void main()
{
    float timeAdjusted = time / 10.;
    // grab postion and rotate per layer
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    uv *= 0.6; // zoom
    vec2 uv2 = rotateCoord(uv + 1., timeAdjusted + 1.);
    vec2 uv3 = rotateCoord(uv * 2., timeAdjusted + 2.);
    vec2 uv4 = rotateCoord(uv + 1.5, timeAdjusted + 3.);
    // create pattern at different resolutions
  	float col = patternForPos(uv, 4., timeAdjusted);
  	float col2 = patternForPos(uv2, 9., timeAdjusted);
  	float col3 = patternForPos(uv3, 3., timeAdjusted);
  	float col4 = patternForPos(uv3, 2.25, timeAdjusted);
    // create final pattern
    vec3 color = vec3(0);
  	float colorSum = col + col2 + col3 + col4;
    color = vec3(sin(colorSum * 2.));
    gl_FragColor = vec4(color,1.0);
}
