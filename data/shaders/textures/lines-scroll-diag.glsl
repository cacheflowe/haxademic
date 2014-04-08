#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time;

#define PI 3.1415926

void main( ) {
    
    vec2 position = vertTexCoord.xy + (time / 10.0);
	float color = 0.0;
	
	float rx = sin(dot(position,vec2(200,200)));
	float gx = cos(dot(position,vec2(200,200)));
	float bx = sin(dot(position,vec2(200,200)));
	
	gl_FragColor = vec4(rx,gx,bx,1.0);
	
}