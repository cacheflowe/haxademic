// from: http://glslsandbox.com/e#24303.0
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time;

// burl figure

const float Pi = 3.14159;

void main() {
	vec2 p = vertTexCoord.xy;
	
	for(int i = 1; i < 6; i++) {
		vec2 newp = p;
		newp.x += 0.6 / float(i) * cos(float(i) * p.y + (time * 10.0) / 10.0 + 0.3 * float(i)) + 400. / 20.0;		
		newp.y += 0.6 / float(i) * cos(float(i) * p.x + (time * 10.0) / 10.0 + 0.3 * float(i + 10)) - 400. / 20.0 + 15.0;
		p = newp;
	}
	vec3 col = vec3(0.5 * sin(3.0 * p.x) + 0.5, 0.5 * sin(3.0 * p.y) + 0.5, sin(p.x + p.y));
	gl_FragColor = vec4(col, 1.0);
}
