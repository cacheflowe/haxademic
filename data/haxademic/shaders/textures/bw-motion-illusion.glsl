// from: https://www.shadertoy.com/view/4sjGRR
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

void main()
{
	vec2 uv = vertTexCoord.xy - vec2(.5,.5);
	uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio

	float r = length(uv), a = atan(uv.y,uv.x);
	vec3 col;

	if (r<.01) {
		col=vec3(0.,0.,0.);
	} else {
        float phase = 200.*r;
        phase += 7.*(sin(uv.x*15.)+cos(uv.y*15.)+min(1.,r*10.)*sin(a*2.));
        col = vec3(max(0.,sin(phase-10.*time)));//+.5*sin(4.*phase-16.*t));
    }
	gl_FragColor = vec4(col,1.0);
}
