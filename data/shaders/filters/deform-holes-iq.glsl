// from https://www.shadertoy.com/view/4sXGzn
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
uniform vec2 texOffset; // resolution
uniform vec2 mouse;

void main()
{
    vec2 p = vertTexCoord.xy;
    vec2 m = -1.0 + 2.0 * mouse.xy;
    
    float a1 = atan(p.y-m.y,p.x-m.x);
    float r1 = sqrt(dot(p-m,p-m));
    float a2 = atan(p.y+m.y,p.x+m.x);
    float r2 = sqrt(dot(p+m,p+m));
    
    vec2 uv;
    uv.x = 0.2*time + (r1-r2)*0.25;
    uv.y = asin(sin(a1-a2))/3.1416;
	
    
    vec3 col = texture2D( texture, 0.125*uv ).zyx;
    
    float w = exp(-15.0*r1*r1) + exp(-15.0*r2*r2);
    
    w += 0.25*smoothstep( 0.93,1.0,sin(128.0*uv.x));
    w += 0.25*smoothstep( 0.93,1.0,sin(128.0*uv.y));
	
    gl_FragColor = vec4(col+w,1.0);
}