// from: https://www.shadertoy.com/view/4lBBRz

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
uniform float crossfade = 1.;
uniform float amp = 1.;

vec4 posterize(vec4 color, float numColors)
{
    return floor(color * numColors - 0.5) / numColors;
}

vec2 quantize(vec2 v, float steps)
{
    return floor(v * steps) / steps;
}

float dist(vec2 a, vec2 b)
{
    return sqrt(pow(b.x - a.x, 2.0) + pow(b.y - a.y, 2.0));
}

void main() {
    vec2 resolution = vec2(1./texOffset.x, 1./texOffset.y);
	  vec2 uv = vertTexCoord.xy;
    float amount = pow(amp, 2.0);
    vec2 pixel = 1.0 / resolution.xy;
    vec4 color = texture2D(texture, uv);
    float t = mod(mod(time, amount * 100.0 * (amount - 0.5)) * 109.0, 1.0);
    vec4 postColor = posterize(color, 16.0);
    vec4 a = posterize(texture2D(texture, quantize(uv, 64.0 * t) + pixel * (postColor.rb - vec2(.5)) * 100.0), 5.0).rbga;
    vec4 b = posterize(texture2D(texture, quantize(uv, 32.0 - t) + pixel * (postColor.rg - vec2(.5)) * 1000.0), 4.0).gbra;
    vec4 c = posterize(texture2D(texture, quantize(uv, 16.0 + t) + pixel * (postColor.rg - vec2(.5)) * 20.0), 16.0).bgra;
    vec4 glitchColor = mix(
        			texture2D(texture,
                              uv + amount * (quantize((a * t - b + c - (t + t / 2.0) / 10.0).rg, 16.0) - vec2(.5)) * pixel * 100.0),
                    (a + b + c) / 3.0,
                    (0.5 - (dot(color, postColor) - 1.5)) * amount);
    gl_FragColor = mix(color, glitchColor, crossfade);
}
