// from: http://glsl.heroku.com/e#17355.0
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


void main( void ) {
	
	vec2 p = vertTexCoord.xy - vec2(.5,.5);
    vec2 uv;
	//shadertoy deform "relief tunnel"-gt
    float r = sqrt( dot(p,p) );
    float a = atan(p.y,p.x) + 0.9*sin(0.5*r-0.5*time);
    
	float s = 0.5 + 0.5*cos(7.0*a);
    s = smoothstep(0.0,1.0,s);
    s = smoothstep(0.0,1.0,s);
    s = smoothstep(0.0,1.0,s);
    s = smoothstep(0.0,1.0,s);
    
    uv.x = time + 1.0/( r + .2*s);
    //uv.y = 3.0*a/3.1416;
	uv.y = 1.0*a/10.1416;
    
    float w = (0.5 + 0.5*s)*r*r;
    
   	// vec3 col = texture2D(tex0,uv).xyz;
    
    float ao = 0.5 + 0.5*cos(42.0*a);//amp up the ao-gt
    ao = smoothstep(0.0,0.4,ao)-smoothstep(0.4,0.7,ao);
    ao = 1.0-0.5*ao*r;
	
	
	//faux shaded texture-gt
	float px = gl_FragCoord.x/texOffset.x;
	float py = gl_FragCoord.y/texOffset.y;
	float x = mod(uv.x*texOffset.x,texOffset.x/3.5);
	float y = mod(uv.y*texOffset.y+(texOffset.y/2.),texOffset.y/3.5);
	float v =  (x / y)-2.;
	gl_FragColor = vec4(vec3(.5-v,.5-v,.5-v)*w*ao,1.0);
    
}