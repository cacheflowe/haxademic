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

void main( void ) {
    
	
	vec2 p = vertTexCoord.xy;
	p.y -= 0.5;
	p.x -= 0.5;
	
	float an = atan(p.y, p.x);
	/*an = mod(an, PI);*/
	float dy = 1.0/(distance(p, vec2(0., 0.)))*((sin(time/2.)+1.02)*3.) + 2.*an;
	
	gl_FragColor = vec4( vec3(cos(time*10.0+dy)*50.0)+0.5,1.0 );
    
}