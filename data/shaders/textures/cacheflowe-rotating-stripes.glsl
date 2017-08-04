#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time = 0;
uniform float amp = 10.0;
uniform float rot = 0;
uniform vec3 color1 = vec3(0.0, 0.0, 0.0);
uniform vec3 color2 = vec3(1.0, 1.0, 1.0);

#define PI     3.14159265358
#define TWO_PI 6.28318530718

vec3 rgb255to1(float r, float g, float b) {
  return vec3(r / 255.0, g / 255.0, b / 255.0);
}

void main()
{
    vec2 uv = vertTexCoord.xy - 0.5;
    uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    // generate stripe size
    uv *= amp;
    // rotate
    uv *= mat2(cos(rot), sin(rot), -sin(rot), cos(rot));
    // create stripes with sin()
    float progress = time;									// adjust time
    float xOsc = 0.5 + 0.5 * sin(uv.x + progress * TWO_PI);
    xOsc = smoothstep(0.45, 0.55, xOsc);
    vec3 colorMix = mix(color1, color2, xOsc);
    gl_FragColor = vec4(colorMix, 1.0);
}
