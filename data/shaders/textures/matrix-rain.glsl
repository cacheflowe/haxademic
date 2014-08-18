// converted from: http://glsl.heroku.com/e#17716.0
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time;

//	vec2 uv = vertTexCoord.xy - vec2(.5,.5);

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(1.9898,20.233))) * 4.5453);
}
// function named char won't compile reliably

float chr(vec2 outer, vec2 inner) {
	//return float(rand(floor(inner * 2.0) + outer) > 0.9);
	
	vec2 seed = floor(inner * 2.0) + outer.y;
	if (rand(vec2(outer.y, 4.0)) > 0.98) {
		seed += floor((time + rand(vec2(outer.y, 5.0))) * 3.0);
	}
	
	return float(rand(seed) > .2);
}

void main( void ) {
    
	vec2 position = vertTexCoord.xy;
	position.y /= vertTexCoord.x / vertTexCoord.y;
    
	float rx = gl_FragCoord.x / 0.05;
	float mx = mod(gl_FragCoord.x, 10.0);
	
	if (mx > 7.0) {
		gl_FragColor = vec4(0);
	} else {
        float x = floor(rx);
		float ry = gl_FragCoord.y + rand(vec2(x, x * 13.0)) * 100000.0 + time * rand(vec2(x, 23.0)) * 120.0;
		float my = mod(ry, 10.20);
		if (my > 12.0) {
			gl_FragColor = vec4(0);
		} else {
            
			float y = floor(ry / 1.0);
			
			float b = chr(vec2(rx, floor((ry) / 15.0)), vec2(mx, my) / 12.0);
			float col = max(mod(-y, 50.0) - 3.0, 0.0) / 40.0;
			vec3 c = col < 0.2 ? vec3(0.1, col / 10.8, 0.0) : mix(vec3(0.5, 0.4, 0.0), vec3(1.0), (col - 0.9) / 0.4);
			
			gl_FragColor = vec4(c * b, 1.0);
		}
	}

}