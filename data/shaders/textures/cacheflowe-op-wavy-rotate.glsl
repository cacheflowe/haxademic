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
    float timeAdjusted = time * 0.5;
    // center coordinates
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    vec2 uvWave = uv;
    vec2 center = vec2(0);
    // vec2 uv = (2. * fragCoord.xy - iResolution.xy) / iResolution.y,
    //      uvWave = (2. * fragCoord.xy - iResolution.xy) / iResolution.y,
    // wavy vertical - oscillate x position by sin(y)
    uvWave += vec2(0.2 * sin(uv.y * 4. * sin(timeAdjusted * 2.)), 0);
	  // rotation of uv from: http://stackoverflow.com/questions/28074977/rotating-a-texture-on-a-fragment-shader-in-glsl-es
    uv *= mat2(cos(timeAdjusted), sin(timeAdjusted), -sin(timeAdjusted), cos(timeAdjusted));
    uvWave *= mat2(cos(timeAdjusted), sin(timeAdjusted), -sin(timeAdjusted), cos(timeAdjusted));
    // stripes
    float thickness = 30. + 15. * sin(timeAdjusted);
    float color = 0.5 + 0.5 * sin(timeAdjusted + uvWave.x * thickness);
    color = smoothstep(0.45, 0.55, color);
    // invert center
    if(distance(uv, center) < 0.35) {
        color = 1. - color;
    }
	  gl_FragColor = vec4(vec3(color), 1.0);
}
