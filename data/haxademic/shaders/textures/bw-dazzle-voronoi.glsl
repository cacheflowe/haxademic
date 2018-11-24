// from: http://codepen.io/xorxor_hu/pen/pNeRYK
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


vec2 hash( vec2 p ) {
	return fract( sin( vec2( p.x * p.y, p.x + p.y ) ) * vec2( 234342.1459123, 373445.3490423 ) );
}

// iq's voronoi
// https://www.shadertoy.com/view/ldl3W8
vec3 voronoi( in vec2 x )
{
	vec2 n = floor(x);
	vec2 f = fract(x);

	//----------------------------------
	// first pass: regular voronoi
	//----------------------------------
	vec2 mg, mr, mo;

	float md = 8.0;
	for( int j=-1; j<=1; j++ )
	for( int i=-1; i<=1; i++ )
	{
			vec2 g = vec2(float(i),float(j));
			vec2 o = hash( n + g );
			vec2 r = g + o - f;
			float d = dot(r,r);

			if( d<md ) {
					md = d;
					mr = r;
					mg = g;
					mo = o;
			}
	}

	//----------------------------------
	// second pass: distance to borders
	//----------------------------------
	md = 8.0;
	for( int j=-2; j<=2; j++ )
	for( int i=-2; i<=2; i++ )
	{
			vec2 g = mg + vec2(float(i),float(j));
			vec2 o = hash( n + g );
			vec2 r = g + o - f;

			if( dot(mr-r,mr-r)>0.00001 )
			md = min( md, dot( 0.5*(mr+r), normalize(r-mr) ) );
	}

	return vec3( md, mo );
}

void main()
{
	vec2 position = vertTexCoord.xy - vec2(.5,.5);
	position.x *= texOffset.y/texOffset.x; // fix aspect ratio
	float aspect = texOffset.x / texOffset.y;

	vec3 v = voronoi( position * (6.0 + 1. * sin(time * 0.1)) );

	float rr = 5.28 * v.z;
	float c = cos( rr );
	float s = sin( rr );
	mat2 r = mat2( c, -s, s, c );
	vec2 q = position * r;

	float o = smoothstep( 0.35, 0.4, 0.35 + 0.5 * sin( 150.0 * q.y + 5.0 * time ) );
	o = mix( 0.0, o, smoothstep( 0.05, 0.06, v.x ) );

	vec2 border = vec2( 0.03 );
	border.x /= aspect;
	for ( float i = 3.0; i > 0.0; i -= 1.0 )
	{
		float c = mod( i + 1.0, 2.0 );
		o = mix( c, o, step( i * border.x, 1. ) );
		o = mix( c, o, step( i * border.y, 1. ) );
		// o = mix( c, o, 1.0 - step( 1.0 - i * border.x, 1. ) );
		// o = mix( c, o, 1.0 - step( 1.0 - i * border.y, 1. ) );
	}

	gl_FragColor = vec4( vec3( o ), 1.0 );
}
