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
    float timeAdjusted = time * 2.5;
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    uv *= 1.6;
    vec2 uvRepeat = fract(uv * 4.) - 0.5;										// set repeating grid and center the circle (-0.5)
    float distanceOsc = 8. * distance(uv, uvRepeat);							// increase oscillation freq per distance
    float radius = 0.25 + 0.15 * sin(timeAdjusted + distanceOsc);						// use distance from center as oscillation offset
    float col = smoothstep(radius - radius * 0.1, radius, length(uvRepeat));	// smoothstep to antialias edge
    gl_FragColor = vec4(vec3(col), 1.);
}
