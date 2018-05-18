// from: https://www.shadertoy.com/view/XsKGRW by @Shane

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

/*
	Full Scene Radial Blur
	----------------------

	Radial blur - as a postprocessing effect - is one of the first things I considered doing
	when the multipass system came out. I've always loved this effect. Reminds me of the early
	demos from Aardbei et al.

	Anyway, Shadertoy user, Passion, did a really cool radial blur on a field of spheres that
	inspired me to do my own. Radial blurs are pretty straight forward, but it was still
    helpful to have Passion's version as a guide.

    As for the radial blur process, there's not much to it. Start off at the pixel position,
    then radiate outwards gathering up pixels with decreased weighting. The result is a
	blurring of the image in a radial fashion, strangely enough. :)

	Inspired by:

	Blue Dream - Passion
	https://www.shadertoy.com/view/MdG3RD

	Radial Blur - IQ
	https://www.shadertoy.com/view/4sfGRn

	Rays of Blinding Light - mu6k
	https://www.shadertoy.com/view/lsf3Dn

*/

// The radial blur section. Shadertoy user, Passion, did a good enough job, so I've used a
// slightly trimmed down version of that. By the way, there are accumulative weighting
// methods that do a slightly better job, but this method is good enough for this example.


// Radial blur samples. More is always better, but there's frame rate to consider.
const float SAMPLES = 48.;

// Radial blur factors.
//
// Falloff, as we radiate outwards.
uniform float decay = 0.97;
// Controls the sample density, which in turn, controls the sample spread.
uniform float density = 0.5;
// Sample weight. Decays as we radiate outwards.
uniform float weight = 0.1;
// Light rotation
uniform float rotation = 0.;
// effect mix
uniform float amp = 1.;


// 2x1 hash. Used to jitter the samples.
float hash( vec2 p ){ return fract(sin(dot(p, vec2(41, 289)))*45758.5453); }


// Light offset.
//
// I realized, after a while, that determining the correct light position doesn't help, since
// radial blur doesn't really look right unless its focus point is within the screen boundaries,
// whereas the light is often out of frame. Therefore, I decided to go for something that at
// least gives the feel of following the light. In this case, I normalized the light position
// and rotated it in unison with the camera rotation. Hacky, for sure, but who's checking? :)
vec3 lOff(){
    vec2 u = sin(vec2(1.57, 0) - rotation/2.);
    mat2 a = mat2(u, -u.y, u.x);

    vec3 l = normalize(vec3(1.5, 1., -0.5));
    l.xz = a * l.xz;
    l.xy = a * l.xy;

    return l;
}



void main() {

    // Screen coordinates.
    vec2 uv = vertTexCoord.xy;

    // Light offset. Kind of fake. See above.
    vec3 l = lOff();

    // Offset texture position (uv - .5), offset again by the fake light movement.
    // It's used to set the blur direction (a direction vector of sorts), and is used
    // later to center the spotlight.
    //
    // The range is centered on zero, which allows the accumulation to spread out in
    // all directions. Ie; It's radial.
    vec2 tuv =  uv - .5 - l.xy*.45;

    // Dividing the direction vector above by the sample number and a density factor
    // which controls how far the blur spreads out. Higher density means a greater
    // blur radius.
    vec2 dTuv = tuv*density/SAMPLES;

    // Grabbing a portion of the initial texture sample. Higher numbers will make the
    // scene a little clearer, but I'm going for a bit of abstraction.
    vec4 origColor = texture2D(texture, uv.xy);
    vec4 col = origColor * 0.25;

    // Jittering, to get rid of banding. Vitally important when accumulating discontinuous
    // samples, especially when only a few layers are being used.
    uv += dTuv*(hash(uv.xy + fract(rotation))*2. - 1.);

    // The radial blur loop. Take a texture sample, move a little in the direction of
    // the radial direction vector (dTuv) then take another, slightly less weighted,
    // sample, add it to the total, then repeat the process until done.
    float curWeight = weight;
    for(float i=0.; i < SAMPLES; i++){
        uv -= dTuv;
        col += texture2D(texture, uv) * curWeight;
        curWeight *= decay;
    }

    // Multiplying the final color with a spotlight centered on the focal point of the radial
    // blur. It's a nice finishing touch... that Passion came up with. If it's a good idea,
    // it didn't come from me. :)
    col *= (1. - dot(tuv, tuv)*.75);

    // Smoothstepping the final color, just to bring it out a bit, then applying some
    // loose gamma correction.
    vec4 effectColor = sqrt(smoothstep(0., 1., col));
    gl_FragColor = mix(origColor, effectColor, amp);

    // Bypassing the radial blur to show the raymarched scene on its own.
    //fragColor = sqrt(texture2D(iChannel0, fragCoord.xy / iResolution.xy));
}
