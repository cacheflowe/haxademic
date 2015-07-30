// from: https://www.shadertoy.com/view/Xsl3zn
// Created by inigo quilez - iq/2013
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset; // resolution
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time;
uniform vec2 resolution;

void main()
{
    vec2 p = vertTexCoord.xy - 0.5;
    vec2 uv;
    
    float r = sqrt( dot(p,p) ) * 2.0;
	
    float a = atan(p.y,p.x) + 0.75*sin(0.5*r-0.5*time );
	
	float h = (0.5 + 0.5*cos(9.0*a));
    
	float s = smoothstep(0.4,0.5,h);
    
    uv.x = time + 1.0/( r + .1*s);
    uv.y = 0.5*a/3.1416;
    
    vec3 col = texture2D(texture,uv).xyz;
    //	col *= 1.25;
    
    float ao = smoothstep(0.0,0.3,h)-smoothstep(0.5,1.0,h);
    col *= 1.0-0.6*ao*r;
	col *= r*r;
    
    gl_FragColor = vec4(col,1.0);
}
