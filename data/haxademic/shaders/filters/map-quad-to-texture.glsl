// ported from @iq - https://www.shadertoy.com/view/lsBSDm
// see also: http://iquilezles.org/www/articles/ibilinear/ibilinear.htm


#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D sourceTexture;
uniform vec2 botLeft = vec2(0.,0.);
uniform vec2 botRight = vec2(1.,0.);
uniform vec2 topRight = vec2(1.,1.);
uniform vec2 topLeft = vec2(0.,1.);


// The MIT License
// Copyright © 2014 Inigo Quilez
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


// Inverse bilinear interpolation: given four points defining a quadrilateral, compute the uv
// coordinates of any point in the plane that would give result to that point as a bilinear
// interpolation of the four points.
//
// The problem can be solved through a quadratic equation. More information in this article:
//
// http://www.iquilezles.org/www/articles/ibilinear/ibilinear.htm


float cross2d( in vec2 a, in vec2 b ) { return a.x*b.y - a.y*b.x; }

// given a point p and a quad defined by four points {a,b,c,d}, return the bilinear
// coordinates of p in the quad. Returns (-1,-1) if the point is outside of the quad.
vec2 invBilinear( in vec2 p, in vec2 a, in vec2 b, in vec2 c, in vec2 d )
{
    vec2 res = vec2(-1.0);

    vec2 e = b-a;
    vec2 f = d-a;
    vec2 g = a-b+c-d;
    vec2 h = p-a;

    float k2 = cross2d( g, f );
    float k1 = cross2d( e, f ) + cross2d( h, g );
    float k0 = cross2d( h, e );

    // if edges are parallel, this is a linear equation. Do not this test here though, do
    // it in the user code
    //if( abs(k2)<0.001 )
    //{
	//	  float v = -k0/k1;
	//    float u  = (h.x*k1+f.x*k0) / (e.x*k1-g.x*k0);
    //
    //    if( v>0.0 && v<1.0 && u>0.0 && u<1.0 )  res = vec2( u, v );
    //}
	//else
    {
        // otherwise, it's a quadratic
        float w = k1*k1 - 4.0*k0*k2;
        if( w<0.0 ) return vec2(-1.0);
        w = sqrt( w );

        #if 1
            float ik2 = 0.5/k2;
            float v = (-k1 - w)*ik2; if( v<0.0 || v>1.0 ) v = (-k1 + w)*ik2;
            float u = (h.x - f.x*v)/(e.x + g.x*v);
            if( u<0.0 || u>1.0 || v<0.0 || v>1.0 ) return vec2(-1.0);
            res = vec2( u, v );
		#else
            float v1 = (-k1 - w)/(2.0*k2);
            float v2 = (-k1 + w)/(2.0*k2);
            float u1 = (h.x - f.x*v1)/(e.x + g.x*v1);
            float u2 = (h.x - f.x*v2)/(e.x + g.x*v2);
            bool  b1 = v1>0.0 && v1<1.0 && u1>0.0 && u1<1.0;
            bool  b2 = v2>0.0 && v2<1.0 && u2>0.0 && u2<1.0;

            if(  b1 && !b2 ) res = vec2( u1, v1 );
            if( !b1 &&  b2 ) res = vec2( u2, v2 );
		#endif
    }

    return res;
}

float sdSegment( in vec2 p, in vec2 a, in vec2 b )
{
	vec2 pa = p - a;
	vec2 ba = b - a;
	float h = clamp( dot(pa,ba)/dot(ba,ba), 0.0, 1.0 );
	return length( pa - ba*h );
}

void main() {
	vec2 p = vertTexCoord.xy;


  // map the quad
  vec2 uv = invBilinear( p, botLeft, botRight, topRight, topLeft );
  // if( uv.x>-0.5 )
  // {
      vec3 col = texture2D( sourceTexture, uv ).xyz;
  // }

  // vec4 colorCurrent = texture2D(texture, uv);
  // vec4 colorTarget = texture2D(targetTexture, uv);
	gl_FragColor = vec4(col.rgb, 1.); // vec4(mix(colorCurrent.rgb, colorTarget.rgb, blendLerp), 1.);
}
