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

void main()
{
	vec2 uv = vertTexCoord.xy;
    
	float d = length(uv);
	vec2 st = uv*0.1 + 0.2*vec2(cos(0.071*time+d),
								sin(0.073*time-d));
    
    vec3 col = texture2D( texture, st ).xyz;
    float w = col.x;
	col *= 1.0 - texture2D( texture, 0.4*uv + 0.1*col.xy  ).xyy;
	col *= w*2.0;
	
	col *= 1.0 + 2.0*d;
	gl_FragColor = vec4(col,1.0);
}