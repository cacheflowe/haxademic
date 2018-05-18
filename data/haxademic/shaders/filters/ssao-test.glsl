// https://www.shadertoy.com/view/Ms23Wm#
// Created by inigo quilez - iq/2014
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time;

uniform float radius = 12.0;
uniform float offsetAdd = 0.01;
uniform float noiseMult = 5.0;
uniform int steps = 8;


// https://www.shadertoy.com/view/4sfGzS

float hash( float n ) { return fract(sin(n)*753.5453123); }
float noise( vec3 x )
{
    vec3 p = floor(x);
    vec3 f = fract(x);
    f = f*f*(3.0-2.0*f);
    
    float n = p.x + p.y*157.0 + 113.0*p.z;
    return mix(mix(mix( hash(n+  0.0), hash(n+  1.0),f.x),
                   mix( hash(n+157.0), hash(n+158.0),f.x),f.y),
               mix(mix( hash(n+113.0), hash(n+114.0),f.x),
                   mix( hash(n+270.0), hash(n+271.0),f.x),f.y),f.z);
}

// http://shadertoy.wikia.com/wiki/Noise
vec2 hash2( vec2 p )
{
    return fract(sin(vec2(dot(p,vec2(127.1,311.7)),dot(p,vec2(269.5,183.3))))*43758.5453);
}



void main() {
    // sample zbuffer (in linear eye space) at the current shading point
    float zr = 1.0 - texture2D( texture, vertTexCoord.xy ).x;
    
    // sample neighbor pixels
    float ao = 0.0;
    for( int i=0; i < steps; i++ ) {
        // get a random 2D offset vector
        vec2 noise = hash2(vec2(vertTexCoord.xy + offsetAdd * float(i)));
        vec2 off = -1.0 + 2.0 * noise;  // /iChannelResolution[1].xy
        // sample the zbuffer at a neightbor pixel (in a 16 pixel radious)
//        vec2 offSet = vertTexCoord.xy + floor(off * radius);
        float z = 1.0-texture2D( texture, vertTexCoord.xy + floor(off*radius) ).x;
//        float z = 1.0 - noise(vec3(offSet.x, offSet.y, 0) * noiseMult); // add  * 200.0 for final draw resolution     // /iResolution.xy
        // accumulate occlusion if difference is less than 0.1 units
        ao = ao + clamp((zr-z)/0.1, 0.0, 1.0);
    }
    // average down the occlusion
    ao = clamp(1.0 - ao/(steps * 1.0), 0.0, 1.0);
    vec3 col = vec3(ao);
//    col *= texture2D( texture, vertTexCoord.xy ).xyz; // add in original texture - maybe remove this for blurring/compositing
    gl_FragColor = vec4(col,1.0);
//    gl_FragColor = vec4(hash2(vertTexCoord.xy).x, hash2(vertTexCoord.xy).y, hash2(vertTexCoord.xy).x,1.0);
}


/*
 
 // https://www.shadertoy.com/view/MddGWB#

float SAMPLES = 30;
float dGamma = 152.;//1:10:2
float oscSpeed = 3.;//0:10:5

float radAttenuation = 5.;//0:2:1
float radius = 0.01;//0:0.6:0.024
float spiral = 16524.56;//1:100:50
float spinSpeed = .10;


void main() {
    
    vec2 uv = vertTexCoord.xy;
    uv.y/=1.6;
    uv *= 1.+0.1*sin(time/3.5);
    uv+=vec2(time/15., sin(time)/15.);
    float dp = texture2D(texture, uv).r;
    
    float f;
    vec2 offset;
    float dTotal;
    
    for(int i = 0;i<SAMPLES;i++) {
        f = float(i)/float(SAMPLES);
        offset = vec2(radius*pow(f, radAttenuation)*sin(f*spiral+time*spinSpeed),
                      radius*pow(f, radAttenuation)*cos(f*spiral+time*spinSpeed));
        float dd = texture2D(texture,uv+offset).r-dp;
        dTotal+=max(dd,0.);
    }
    dTotal/=float(SAMPLES);
    dTotal = (1.-dTotal);
    dTotal = pow(dTotal,dGamma);
    gl_FragColor.rgb = vec3(dTotal*(0.25*dp+0.65));
    gl_FragColor.rgb = mix(gl_FragColor.rgb, vec3(dp), 0.5+0.5*sin(time*oscSpeed));
    gl_FragColor.a = 1.;
}

*/
