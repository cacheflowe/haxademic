#version 150

uniform mat4 transform;
uniform mat4 modelview;
uniform mat4 normalMatrix;
uniform mat4 texMatrix;

in vec4 position;
in vec4 color;
in vec2 texCoord;

out vec4 vertColor;
out vec4 vertTexCoord;
out vec3 v_texCoord3D;

uniform mat4 modelviewInv;
uniform float time = 0;
uniform float displaceAmp = 0.25f;


void main() {
	// apply inverse matrix to use models original position as uv coords
	vec4 tmp = position * modelviewInv;
	v_texCoord3D = tmp.xyz;
	vec3 p = tmp.xyz;
	float radsToCenter = atan(p.x, p.z);
	p.y += sin(time + radsToCenter * 14.) * (60. + 60. * sin(time));


	// send position-based stripes to fragment shader
	// float grey = smoothstep(0.05, 0.95, 0.5 + 0.5 * sin(time * 6. + p.y / 10.));
	float grey = 0.5 + 0.5 * sin(time * 6. + p.y / 10.);
	vec4 col = vec4(grey, grey, grey, 1.0);
	vertColor = col;

	// deform based on color
	float amp = (1. + displaceAmp * col.r);
	gl_Position = transform * (position * vec4(amp, amp, amp, 1.));
	vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
}
