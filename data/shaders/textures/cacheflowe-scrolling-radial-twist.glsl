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
#define TWO_PI 6.28318530718

void main()
{
    // set time & centered position
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    // deform uv - spin a little
    uv *= 0.3;
    float dist = length(uv);
    float rot = 0.06 * sin(dist * 3. + time);							// oscillate rotation
    uv *= mat2(cos(rot), sin(rot), -sin(rot), cos(rot));				// rotate
    // find position on circle
    float rads = PI + atan(uv.x, uv.y);
    float segments = 32.;
    float segmentRads = segments / TWO_PI;
    float segmentIndex = floor((rads/TWO_PI) * segments);
    // color
    float scroll = (mod(segmentIndex, 2.) == 0.) ? time : -time;		// reverse direction on odd segments
    float col = abs(sin(29. * pow(0.75 + length(uv), 1.8) + scroll));	// concentric lines width - progressively smaller
    col = smoothstep(0.4, 0.53, col);									// tighten up line definition
    if(scroll > 0.) col = 1. - col;										// invert colors on odd segments
	  //col -= (2.0 - length(uv)) * 0.5;									// reverse vignette
    gl_FragColor = vec4(vec3(col),1.0);
}
