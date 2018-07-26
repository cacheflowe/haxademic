// ported from: http://glslsandbox.com/e#25632.0
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform vec2 texOffset;
uniform float time;



	//vec2 p = vertTexCoord.xy * 1.0 - 0.5;
float rand(vec2 co){
  return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);
}

void main (void) {
	// Divide the coordinates into a grid of squares
	vec2 v = gl_FragCoord.xy / 50.0;
//  v.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio

	// Calculate a pseudo-random brightness value for each square
	vec3 brightness = vec3 ( fract(rand(floor(v)) + time) , fract(rand(floor(v)) + time/3.), fract(rand(floor(v)) + time/5.)) ;
	// Reduce brightness in pixels away from the square center
	brightness *= 0.5 - length(fract(v) - vec2(0.5, 0.5));

	// gl_FragColor = vec4(brightness.r * 4.0, brightness.g * brightness.b * 2., brightness.b - brightness.r + 0.2, 1.0); // color version
	float g = brightness.g * 3.0;
	gl_FragColor = vec4(g, g, g, 1.0);
}
