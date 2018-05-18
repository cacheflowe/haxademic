// from: http://glsl.heroku.com/e#14875.0
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
uniform int mode;

//

#define PI 3.141592
#define PI2 6.283184

float angle(vec2 p){
	if(p.x<=0.0)return atan(p.y/p.x)/PI2+0.5;
	if(p.y>=0.0)return atan(p.y/p.x)/PI2;
	return atan(p.y/p.x)/PI2+1.0;
}
float dist(vec2 p){
	return distance(vec2(0.0,0.0),p*0.5);
}

void main( void ) {

    vec2 position = vertTexCoord.xy - vec2(.5,.5);
		position.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio

//	position.x=(gl_FragCoord.x/resolution.x-0.5)*(resolution.x/resolution.y)*2.0*2.0;
//	position.y=(gl_FragCoord.y/resolution.y-0.5)*2.0*2.0;

	vec2 p=position;
	float a=angle(p);//abs(angle(p)-0.5);
	float d=dist(p);
	//if(dist(p)<1.0)discard;
	//if(dist(p)>2.0)discard;
	float timer = time * 0.3;

	float v=cos(a*PI2*3.0+timer)*cos(d*PI2*0.5-4.0*timer);
	float vv=cos(v*PI2*2.0+timer);
	float vvv=cos(vv*15.0);

	vec3 color=vec3(vv*(2.0-d)*2.0);


	gl_FragColor = vec4( color, 1.0 );

}
