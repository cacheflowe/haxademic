// from: http://glsl.heroku.com/e#17304.13
// rotated mirrored gradient
// jkozniewski.com

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

void main(void) {

	vec4 colorA = vec4(1.0, 1.0, 1.0, 1.0);
	vec4 colorB = vec4(0.0, 0.0, 0.0, 1.0);

	vec2 uv = vertTexCoord.xy - 0.5;/// - vec2(.5,.5);
  uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio

	vec2 center = vec2(0); // rotation center in the middle of texture

	float rotation = time; // in radians - try dirrernt values ( 1.570 for ex. )
	float angle = atan(uv.y - center.y, uv.x - center.x); // get angle between current uv coord and center
	float newAngle = angle + rotation; // offset original angle by certain rotation

	float len = length(uv - center); // radius
	vec2 newUv = vec2( len * cos(newAngle), len * sin(newAngle) ); // new rotated coords

	float ratio = 2.0 * abs((center.x-newUv.x) - 0.); // get rotated distance gradient, abs for mirroring
	ratio = 1.0 - smoothstep( 0.0, 1.0, ratio ); // scale range - play with the first parameter of smoothstep

	gl_FragColor = vec4( mix( colorA, colorB, ratio) ); // mix two colors based on ratio factor
}
