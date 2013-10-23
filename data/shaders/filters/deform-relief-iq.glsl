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
    vec2 p = -1.0 + 2.0 * texOffset.xy;
    vec2 uv;
    
    float r = sqrt( dot(p,p) ) * 2.0;
	
    float a = atan(p.y,p.x) + 0.75*sin(0.5*r-0.5*time );
	
	float h = (0.5 + 0.5*cos(9.0*a));
    
	float s = smoothstep(0.4,0.5,h);
    
    uv.x = time + 1.0/( r + .1*s);
    uv.y = 3.0*a/3.1416;
    
    vec3 col = texture2D(texture,uv).xyz;
    //	col *= 1.25;
    
    float ao = smoothstep(0.0,0.3,h)-smoothstep(0.5,1.0,h);
    col *= 1.0-0.6*ao*r;
	col *= r*r;
    
    gl_FragColor = vec4(col,1.0);
}

//void main(void)
//{
//    vec2 p = -1.0 + 2.0 * texOffset.xy;
//    vec2 uv;
//    
//    float r = pow( pow(p.x*p.x,16.0) + pow(p.y*p.y,16.0), 1.0/32.0 );
//    uv.x = .5*time + 0.5/r;
//    uv.y = atan(p.y,p.x)/3.1416;
//	
//	float h = sin(32.0*uv.y);
//    uv.x += 0.85*smoothstep( -0.1,0.1,h);
//    vec3 col = texture2D( texture, 2.0*uv ).xyz;
//    col = mix( col, texture2D( texture, uv ).xyz, smoothstep(0.9,1.1,abs(p.x/p.y) ) );
//	
//    r *= 1.0 - 0.3*(smoothstep( 0.0, 0.3, h ) - smoothstep( 0.3, 0.96, h ));
//	
//    gl_FragColor = vec4( col*r*r*1.2,1.0);
//}

//void main(void)
//{
//    vec2 p = -1.0+2.0*vertTexCoord.xy/resolution.y;
//    
//    float an = time*0.1;
//    float x = p.x*cos(an)-p.y*sin(an);
//    float y = p.x*sin(an)+p.y*cos(an);
//    
//    vec2 uv = 0.2*vec2(x,1.0)/abs(y);
//    uv.xy += 0.20*time;
//	
//	float w = max(-0.1, 0.6-abs(y) );
//	gl_FragColor = vec4( texture2D(texture, uv).xyz+w, 1.0);
//}
