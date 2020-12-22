/*

by @zavie - https://www.shadertoy.com/view/lslGzl
This shader experiments the effect of different tone mapping operators.
This is still a work in progress.

More info:
http://slideshare.net/ozlael/hable-john-uncharted2-hdr-lighting
http://filmicgames.com/archives/75
http://filmicgames.com/archives/183
http://filmicgames.com/archives/190
http://imdoingitwrong.wordpress.com/2010/08/19/why-reinhard-desaturates-my-blacks-3/
http://mynameismjp.wordpress.com/2010/04/30/a-closer-look-at-tone-mapping/
http://renderwonk.com/publications/s2010-color-course/

--
Zavie

*/

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform float gamma = 2.2;
uniform int mode = 1;
uniform float crossfade = 1.0;

varying vec4 vertColor;
varying vec4 vertTexCoord;


vec3 linearToneMapping(vec3 color)
{
	float exposure = 1.;
	color = clamp(exposure * color, 0., 1.);
	color = pow(color, vec3(1. / gamma));
	return color;
}

vec3 simpleReinhardToneMapping(vec3 color)
{
	float exposure = 1.5;
	color *= exposure/(1. + color / exposure);
	color = pow(color, vec3(1. / gamma));
	return color;
}

vec3 lumaBasedReinhardToneMapping(vec3 color)
{
	float luma = dot(color, vec3(0.2126, 0.7152, 0.0722));
	float toneMappedLuma = luma / (1. + luma);
	color *= toneMappedLuma / luma;
	color = pow(color, vec3(1. / gamma));
	return color;
}

vec3 whitePreservingLumaBasedReinhardToneMapping(vec3 color)
{
	float white = 2.;
	float luma = dot(color, vec3(0.2126, 0.7152, 0.0722));
	float toneMappedLuma = luma * (1. + luma / (white*white)) / (1. + luma);
	color *= toneMappedLuma / luma;
	color = pow(color, vec3(1. / gamma));
	return color;
}

vec3 RomBinDaHouseToneMapping(vec3 color)
{
  color = exp( -1.0 / ( 2.72*color + 0.15 ) );
	color = pow(color, vec3(1. / gamma));
	return color;
}

vec3 filmicToneMapping(vec3 color)
{
	color = max(vec3(0.), color - vec3(0.004));
	color = (color * (6.2 * color + .5)) / (color * (6.2 * color + 1.7) + 0.06);
	return color;
}

vec3 Uncharted2ToneMapping(vec3 color)
{
	float A = 0.15;
	float B = 0.50;
	float C = 0.10;
	float D = 0.20;
	float E = 0.02;
	float F = 0.30;
	float W = 11.2;
	float exposure = 2.;
	color *= exposure;
	color = ((color * (A * color + C * B) + D * E) / (color * (A * color + B) + D * F)) - E / F;
	float white = ((W * (A * W + C * B) + D * E) / (W * (A * W + B) + D * F)) - E / F;
	color /= white;
	color = pow(color, vec3(1. / gamma));
	return color;
}

//----------------------------------------------
// additions from @paniq - https://www.shadertoy.com/view/ldcSRN
///////////////////////////////////////////////

// ACES fitted
// from https://github.com/TheRealMJP/BakingLab/blob/master/BakingLab/ACES.hlsl

const mat3 ACESInputMat = mat3(
    0.59719, 0.35458, 0.04823,
    0.07600, 0.90834, 0.01566,
    0.02840, 0.13383, 0.83777
);

// ODT_SAT => XYZ => D60_2_D65 => sRGB
const mat3 ACESOutputMat = mat3(
     1.60475, -0.53108, -0.07367,
    -0.10208,  1.10813, -0.00605,
    -0.00327, -0.07276,  1.07602
);

vec3 RRTAndODTFit(vec3 v)
{
    vec3 a = v * (v + 0.0245786) - 0.000090537;
    vec3 b = v * (0.983729 * v + 0.4329510) + 0.238081;
    return a / b;
}

vec3 ACESFitted(vec3 color)
{
    color = color * ACESInputMat;

    // Apply RRT and ODT
    color = RRTAndODTFit(color);

    color = color * ACESOutputMat;

    // Clamp to [0, 1]
    color = clamp(color, 0.0, 1.0);

    return color;
}

// linear white point
const float W = 11.2;
float filmic_curve(float x) {
	return ((x*(0.22*x+0.1*0.3)+0.2*0.01)/(x*(0.22*x+0.3)+0.2*0.3))-0.01/0.3;
}
vec3 filmic(vec3 x) {
    float w = filmic_curve(W);
    return vec3(
        filmic_curve(x.r),
        filmic_curve(x.g),
        filmic_curve(x.b)) / w;
}
//----------------------------------------------

void main() {
  vec2 uv = vertTexCoord.xy;
  vec3 color = texture2D(texture, vertTexCoord.st).rgb;
  vec3 tonedColor = color;

  if (mode == 1) tonedColor = linearToneMapping(color);
  if (mode == 2) tonedColor = simpleReinhardToneMapping(color);
  if (mode == 3) tonedColor = lumaBasedReinhardToneMapping(color);
  if (mode == 4) tonedColor = whitePreservingLumaBasedReinhardToneMapping(color);
  if (mode == 5) tonedColor = RomBinDaHouseToneMapping(color);
  if (mode == 6) tonedColor = filmicToneMapping(color);
  if (mode == 7) tonedColor = Uncharted2ToneMapping(color);
  if (mode == 8) tonedColor = ACESFitted(color);
  if (mode == 9) tonedColor = filmic(color);

  // final mix
	gl_FragColor = vec4(mix(color, tonedColor, crossfade), 1.);
}
