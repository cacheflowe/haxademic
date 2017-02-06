// converted from: https://www.shadertoy.com/view/MtlGRn
// Reference: http://www.crytek.com/download/Sousa_Graphics_Gems_CryENGINE3.pdf on slide 36
// Lightning
//
// Modified by: Eivind Magnus Hvidevold
// hvidevold at gmail dot com
//
// By: Brandon Fogerty
// bfogerty at gmail dot com
// xdpixel.com
// ported from: http://glslsandbox.com/e#25772.7
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform vec2 texOffset;
uniform float time;


float random( vec2 p )
{
	return fract( sin( fract( sin( p.x ) ) + p.y) * 42.17563);
}

float worley( vec2 p, float timeSpeed )
{
	float d = 10.0;
	for( int xo = -1; xo <= 1; xo++ )
	{
		for( int yo = -1; yo <= 1; yo++ )
			{
			vec2 test_cell = floor(p) + vec2( xo, yo );

			float f1 = random( test_cell );
			float f2 = random( test_cell + vec2(1.0,83.0) );

			float xp = mix( f1, f2, sin(f1*time*timeSpeed) );
			float yp = mix( f1, f2, cos(f2*time*timeSpeed) );

			vec2 c = test_cell + vec2(xp,yp);

			vec2 cTop = p - c;
			d = min( d, dot(cTop,cTop) );
		}
	}
	return d;
}

float pass( vec2 uv, float timeSpeed )
{
	float t = worley( gl_FragCoord.xy * 0.05, timeSpeed );
	//t = pow(t, 2.0 );

	return t;
}

float Hash( vec2 p)
{
     vec3 p2 = vec3(p.xy,1.0);
    return fract(sin(dot(p2,vec3(35.234234231,21.2342323437, 1242.245234354)))*3758.5453123);
}

float noise(in vec2 p)
{
    vec2 i = floor(p);
     vec2 f = fract(p);
     f *= f * (3.0-2.0*f);

    return mix(mix(Hash(i + vec2(0.,0.)), Hash(i + vec2(1.,0.)),f.x),
               mix(Hash(i + vec2(0.,1.)), Hash(i + vec2(1.,1.)),f.x),
               f.y);
}

float fbm(vec2 p)
{
     float v = 0.0;
     v += noise(p*1.0)*.5;
     v += noise(p*2.)*.25;
     v += noise(p*4.)*.125;
     return v;
}

void main( void )
{

	vec2 uv = vertTexCoord.xy * 2.0 - 1.0;
	uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio

	vec2 uvNonPolar = uv;
	uv = vec2((sqrt(uv.x*uv.x+uv.y*uv.y)-1.0)*(sin(time)+2.0)/2.0, sin(atan(uv.y, uv.x)*2.0+time));

	float timeVal = time;
	vec3 finalColor = vec3( 0.0 );
	for( int i=0; i < 20; ++i )
	{
		float indexAsFloat = float(i);
		float p = pass(uvNonPolar, 6.0);
		float indexScale = indexAsFloat / 20.0;
		float amp = 125.0 + (indexAsFloat*0.01);
		float period = 0. + (indexAsFloat*0.01+.5) + sin(p)/1000.0;
		float thickness = mix( 0.7, 1.0, noise(uv*10.0) ) * 0.1;
		float t = abs( 1.0 / (sin(uv.x + fbm( uv + timeVal * period) ) * amp) * thickness );

		finalColor += t * vec3( indexScale*0.87, 0.43, 0.63 ) * 5.0;
		//finalColor *= pass(uv, 6.0);
	}

	gl_FragColor = vec4( finalColor, 1.0 );

}
