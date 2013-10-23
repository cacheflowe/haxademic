// converted from: http://glsl.heroku.com/
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time;

void main( void ) {
	float amnt = 200.0;
	float nd = 0.0;
	vec4 cbuff = vec4(0.0);
    
	for(float i=0.0; i<10.0;i++){
        nd =sin(3.93*0.8*vertTexCoord.x + (i*0.2+sin(+time)*.8) + time)*0.4+0.1 + vertTexCoord.x;
        amnt = 1.5/abs(nd-vertTexCoord.y)*0.02;
        
        cbuff += vec4(amnt, amnt*0.3 , amnt*vertTexCoord.y, 081.0);
	}
	
    gl_FragColor = cbuff;
}