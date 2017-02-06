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

void main()
{
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    // float timeOsc = sin(time) * 0.05;								// oscillation helper
    float timeOsc = time * 0.02;								// oscillation helper
    float dist = 0.;												// start distance count from 0
    for(float i=10.; i < 60.; i++) {								// create x control points
        float rads = timeOsc + i;									// get rads for control point
        vec2 ctrlPoint = vec2(sin(rads), cos(rads));				// control points in a circle
        ctrlPoint *= abs(cos(rads)) * 5.;							// oscillate control point radius - the magic happens w/abs()
        dist += sin(i + 35. * distance(uv, ctrlPoint));				// sum up oscillated distance between control points
    }
	  gl_FragColor = vec4(vec3(dist),1.0);
}
