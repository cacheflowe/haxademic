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

uniform float time = 0.;
uniform float zoom = 5.;

// modified from: https://www.shadertoy.com/view/MslGD8

// The MIT License
// Copyright © 2013 Inigo Quilez
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

vec2 hash( vec2 p ) { p=vec2(dot(p,vec2(127.1,311.7)),dot(p,vec2(269.5,183.3))); return fract(sin(p)*18.5453); }

// return distance, and cell id
vec2 voronoi( in vec2 x, in float time )
{
    vec2 n = floor( x );
    vec2 f = fract( x );

		vec3 m = vec3( 8.0 );
    for( int j=-1; j<=1; j++ )
    for( int i=-1; i<=1; i++ )
    {
        vec2  g = vec2( float(i), float(j) );
        vec2  o = hash( n + g );
      	//vec2  r = g - f + o;
	    	vec2  r = g - f + (0.5+0.5*sin(time+6.2831*o));
				float d = dot( r, r );
        if( d < m.x )
            m = vec3( d, o );
    }
    return vec2( sqrt(m.x), m.y+m.z );
}


void main()
{
	vec2 uv = vertTexCoord.xy - vec2(.5,.5);
	uv.x *= texOffset.y/texOffset.x; // fix aspect ratio
	float aspect = texOffset.x / texOffset.y;

	// computer voronoi patterm
	vec2 c = voronoi(uv * zoom, time);

	// colorize
	// vec3 col = 0.5 + 0.5*cos( c.y*6.2831 + vec3(0.0,1.0,2.0) );
	vec3 col = vec3(floor((c.y/2.) * 10.) / 10.);
	//col *= clamp(1.0 - 0.4*c.x*c.x,0.0,1.0);
	//col -= (1.0-smoothstep( 0.08, 0.09, c.x));
	gl_FragColor = vec4( col, 1.0 );
}
