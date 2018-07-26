// from: http://glsl.heroku.com/e#14583.0
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

#define CLOUD_COVER		0.7
#define CLOUD_SHARPNESS		0.003

float hash( float n )
{
	return fract(sin(n)*43758.5453);
}

float noise( in vec2 x )
{
	vec2 p = floor(x);
	vec2 f = fract(x);
    f = f*f*(3.0-2.0*f);
    float n = p.x + p.y*57.0;
    float res = mix(mix( hash(n+  0.0), hash(n+  1.0),f.x), mix( hash(n+ 57.0), hash(n+ 58.0),f.x),f.y);
    return res;
}

float fbm( vec2 p )
{
    float f = 0.0;
    f += 0.50000*noise( p ); p = p*2.02;
    f += 0.25000*noise( p+time ); p = p*2.03;
    f += 0.12500*noise( p+time/2.0 ); p = p*2.01;
    f += 0.06250*noise( p); p = p*2.04;
    f += 0.03125*noise( p );
    return f/0.984375;
}

// Entry point
void main( void ) {
	// Wind - Used to animate the clouds
	vec2 wind_vec = vec2(0.001 + time*0.01, 0.003 + time * 0.01);
	
	// Enable raytracing
	bool enable_sun = false;
	
	// Set suns position to mouse coords
	vec3 sun_vec = vec3(0, 0, -20.0);
	
	
	// Set up domain
    vec2 q = vertTexCoord.xy - vec2(.5,.5);
//	vec2 q = ( gl_FragCoord.xy / resolution.xy );
	vec2 p = -1.0 + 5.0 * q + wind_vec;
	
	// Fix aspect ratio
//	p.x *= resolution.x / resolution.y;
    
	
	// Create noise using fBm
	float f = fbm( 1.0*p );
    
	float cover = CLOUD_COVER;
	float sharpness = CLOUD_SHARPNESS;
	
	float c = f - (1.0 - cover);
	if ( c < 0.0 )
		c = 0.0;
	
	f = 1.0 - (pow(sharpness, c));
	
    
	gl_FragColor = vec4( f, f, f, 1.0 );
}
