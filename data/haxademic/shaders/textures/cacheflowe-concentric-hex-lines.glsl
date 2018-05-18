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

vec2 rotateCoord(vec2 uv, float rads) {
    uv *= mat2(cos(rads), sin(rads), -sin(rads), cos(rads));
	  return uv;
}

void main()
{
    float timeAdjusted = time * 0.5;
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    // uv *= 0.5; // zoom in
    uv = rotateCoord(uv, PI * 0.25);
    float rads = atan(uv.x, uv.y);
    float vertices = 6.;
    float baseRadius = 0.7;
    float extraRadius = 0.03 + 0.03 * sin(timeAdjusted * 0.5);
    float curRadius = baseRadius + extraRadius * sin(rads * vertices);
    vec2 edge = curRadius* normalize(uv);
    float distFromCenter = length(uv);
    float distFromEdge = distance(edge, uv);
    float freq = 24.;
    if(distFromCenter > curRadius) freq *= 3.;
    float col = smoothstep(0.45, 0.55, abs(sin(timeAdjusted + distFromEdge * freq)));
    // col += distFromCenter * 0.1;
	  gl_FragColor = vec4(vec3(col), 1.);
}
