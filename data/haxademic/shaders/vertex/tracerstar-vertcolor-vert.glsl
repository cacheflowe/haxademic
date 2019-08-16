
uniform mat4 transform;
uniform mat4 modelview;
uniform mat4 normalMatrix;
uniform mat4 texMatrix;

attribute vec4 position;
attribute vec4 color;
attribute vec2 texCoord;

varying vec4 vertColor;
varying vec4 vertTexCoord;

varying vec3 v_texCoord3D;

uniform mat4 modelviewInv;
uniform float time = 0;
uniform float displaceAmp = 0.25f;


void main() {
	// apply inverse matrix to use models original position as uv coords
	vec4 tmp = position * modelviewInv;
	vec3 p = tmp.xyz;
	float radsToCenter = atan(p.x, p.z);
	p.y += sin(time + radsToCenter * 8.) * 200.;

	// send position-based stripes to fragment shader
	float grey = smoothstep(0.25, 0.75, 0.5 + 0.5 * sin(time + p.y / 25.));
	vec4 col = vec4(grey, grey, grey, 1.0);
	vertColor = col;

	// deform based on color
	float amp = (1. + displaceAmp * col.r);
	// gl_Position = transform * vec4(position.x * amp, position.y * amp, position.x * amp, 1.0);
	gl_Position = transform * (position * vec4(amp, amp, amp, 1.));
	vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
}
