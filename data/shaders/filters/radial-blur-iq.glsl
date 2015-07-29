// from: https://www.shadertoy.com/view/4sfGRn
// Created by inigo quilez - iq/2013
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
uniform vec2 resolution;

vec3 deform( vec2 p )
{
    vec2 uv;
    
    vec2 q = vec2( sin(1.1*time+p.x),sin(1.2*time+p.y) );
    
    float a = atan(q.y,q.x);
    float r = sqrt(dot(q,q));
    
    uv.x = sin(0.0+1.0*time)+p.x*sqrt(r*r+1.0);
    uv.y = sin(0.6+1.1*time)+p.y*sqrt(r*r+1.0);
    
    return texture2D( texture, uv*.3).yxx;
}

void main()
{
    vec2 p = vertTexCoord.xy - 0.5;
    vec2 s = p;
    
    vec3 total = vec3(0.0);
    vec2 d = (vec2(0.0,0.0)+p)/10.0;    // slight adjustment by cacheflowe
    float w = 1.0;
    for( int i=0; i<40; i++ )
    {
        vec3 res = deform(s);
        res = smoothstep(0.0,1.0,res);
        total += w*res;
        w *= .99;
        s += d;
    }
    total = total / 40.0;
    float r = 3.0;
    
	gl_FragColor = vec4( total*r,1.0);
}