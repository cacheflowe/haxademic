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

const float TWO_PI = 6.28318530718;
const float vertices = 8.;
const float startIndex = vertices;
const float endIndex = vertices * 2.;

float metaballs(vec2 uv, float time) {
    float timeOsc = sin(time);										// oscillation helper
    float size = 0.5;												// base size
    float radSegment = TWO_PI / vertices;
    for(float i = startIndex; i < endIndex; i++) {					// create x control points
        float rads = i * radSegment;								// get rads for control point
        float radius = 1. + 1.5 * sin(time + rads * 1.);
        vec2 ctrlPoint = radius * vec2(sin(rads), cos(rads));		// control points in a circle
		size += 1. / pow(i, distance(uv, ctrlPoint));				// metaball calculation
    }
    return size;
}

void main(void)
{
  vec2 uv = vertTexCoord.xy - 0.5;
  uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
  // vec2 uv = (2. * fragCoord - iResolution.xy) / iResolution.y;	// center coordinates
	uv *= 2.6; 														// zoom out
  float col = metaballs(uv, time);
	col = smoothstep(0., fwidth(col)*1.5, col - 1.);				// was simple but aliased: smoothstep(0.98, 0.99, col);
	gl_FragColor = vec4(1. - sqrt(vec3(col)), 1); 						// Rough gamma correction.
}
