// from: https://www.shadertoy.com/view/MslGWn


#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform float time;
varying vec4 vertColor;
varying vec4 vertTexCoord;


void main()
{
	float t = time * 0.5;
	vec2 uv = vertTexCoord.xy;
	float aspect = 1.0;
	
	mat3 xform = mat3(cos(sin(t)), sin(t  *0.25), 0.0,
					  -sin(t * 0.25), cos(cos(t)), 0.0,
					  cos(t / 2.0) * 0.2, sin(t) * 0.2, 1.0);
	
	uv = (xform * vec3(uv, 1.0)).xy * vec2(aspect, 1.0);
	
	uv.x -= sin(t) + cos(t * 2.0 + cos(uv.x) * sin(t * 2.0) * 2.0) / 2.0;
	uv.y += cos(t + uv.y * 0.5) + sin(uv.y * cos(t)) + sin(cos(t * 0.5) * length(uv));
	
	vec3 color = texture2D(texture, uv).xyz;
	
	gl_FragColor.xyz = color;
	gl_FragColor.w = 1.0;
}
