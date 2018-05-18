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

// saw method ported from my Processing code to be a drop-in replacement for sin()
// there's probably a way better way to do this..
float saw(float rads) {
    rads += PI * 0.5; // sync oscillation up with sin()
    float percent = mod(rads, PI) / PI;
    float dir = sign(sin(rads));
    percent *= 2. * dir;
    percent -= dir;
    return percent;
}

void main()
{
    // float time = iGlobalTime * 1.5;
    // center coordinates
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    vec2 center = vec2(0);
    // wavy vertical - oscillate x position by sin(y)
    uv += vec2(0.15 * sin(uv.y * 6. * (1. + sin(time))), 0);
	// rotation of uv from: http://stackoverflow.com/questions/28074977/rotating-a-texture-on-a-fragment-shader-in-glsl-es
    float rotRads = 0.3 + 0.3 * sin(time);
    uv *= mat2(cos(rotRads), sin(rotRads), -sin(rotRads), cos(rotRads));
    // stripes
    float thickness = 35. + 10. * sin(time);
    // separate color by moving green component by an x offset. use saw() for stripes
    // vec3 color = 0.5 + 0.5 * vec3(
    //     saw(uv.x * thickness),
    //     saw((uv.x - (0.025 + 0.025 * sin(time))) * thickness),
    //     saw(uv.x * thickness)
    // );
    float color = 0.5 + 0.5 * saw(uv.x * thickness);
    color = smoothstep(0.45, 0.55, color);
	  gl_FragColor = vec4(vec3(color), 1.0);
}
