// from: https://www.shadertoy.com/view/4dSXWR

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

void main()
{
	float t = time * 0.25;
	vec2 uv0 = vertTexCoord.xy - vec2(.5,.5);
	uv0.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
	uv0 *= 0.5;
    vec2 m = texOffset.xy;
    uv0 += vec2( 1.8*cos(.15*t)+.5*sin(.4*t) , sin(.22*t)-.5*cos(.3*t) );
	m = .5+.5*vec2(cos(t),sin(.6*t)); m.y*=m.x;

    vec2 uv = uv0*5., iuv=floor(uv);
    float d = mod(iuv.x+iuv.y,2.), s = mod(iuv.y,2.);
    uv = fract (uv);
    if (d==1.) uv.x = 1.-uv.x; // checkered tile coordinates
    uv = uv + vec2(-uv.y,uv.x); // rotate 45deg

    float q = sign(s-.5)*sign(d-.5),
      size0 = m.x+m.y*cos(.5*3.1415927*uv.y) *q,
          l = abs(uv.x)-size0,
          v = smoothstep(0.,.1,abs(l)),
         v0 = step(0.,l);

    float size = m.x+m.y*cos(.5*3.1415927*uv.x),
           ofs = (1.-size)*q; // corner distance
             l = (uv.y-1.)-ofs;
    float   v1 = step(0.,l),
            d0 =  mod(s+v1,2.),
            d1 =  mod(s+d+v1,2.); // corner area
    v0 = d1<1. ? v0 : 0.; // background
    v = (d1<1. ? v : 1.)*smoothstep(0.,.1,abs(l)); // contour

    float col = v0 *(cos(8.*31.4*uv0.x)*cos(8.*31.4*uv0.y))
          + (1.-v0)*( d1==1. ? cos(2.*31.4*( q>0. ? 2.-uv.y : uv.y )*m.x/size)
                             : cos(2.*31.4*(uv.x*m.x/size0)) );
	gl_FragColor = vec4(col*v);
}
