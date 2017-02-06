// from: http://glsl.heroku.com/e#15216.3
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time;

float map(vec3 p)
{
	return 2.0 - length(p.xy) + (sin(p.x * 3.0) + sin(p.y * 7.0) + sin(p.z * 5.0)) * 0.1;
}

void main( void )
{
	vec2 uv = vertTexCoord.xy - vec2(.5,.5);
	uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
	vec3 dir = normalize(vec3(uv, 1.0));
    float timer = time * 0.3;
	vec3 pos = vec3(sin(timer),cos(timer),timer * 5.0);
	float t = 0.0;
	for(int i = 0 ; i < 76; i++)
	{
		float k = map(pos + dir * t);
		if(k < 0.001) break;
		t += k * 0.95;
	}
	gl_FragColor = max((0.741 * vec4(0.05 * t + map( (t * dir + pos) + 0.15) ) * vec4(2,2,2,3).zxyw), 0.0);
}
