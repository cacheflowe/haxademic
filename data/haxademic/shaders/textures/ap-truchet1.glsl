//adapted from https://www.shadertoy.com/view/4s3GD8
// Truchet Flip Planes - written 2015 by JT, compactified & documented by FabriceNeyret2
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
// based on
// https://www.shadertoy.com/view/Xd33W8 Checkers Perspective Planes (JT)
// https://www.shadertoy.com/view/4st3R7 TruchetTilesFlip (JT)
// https://www.shadertoy.com/view/lst3R7 TruchetTilesFlip2 (FabriceNeyret2)


#ifdef GL_ES
precision mediump float;
#endif

#define S(v) smoothstep(.45,.55, len(v))        // use either len() or length()

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

// 3 hacked in mix levels
uniform float _mix1;
uniform float _mix2;
uniform float _mix3;

float t,s,d;

float len(vec2 v) {                             // roundness tuning. richer than length(v)
    float w = .2 + 1.6*abs(fract(t/16.)-.5);    // 1/w in [1/5 .. 1]
    v = pow(v, vec2(1./w));
    return pow(v.x+v.y, w);
}


float random( vec2 p )
{
    vec2 K1 = vec2(23.14069263277926,2.665144142690225);
    return fract( cos( dot(p,K1) ) * 12345.6789 );
}

void main( void ) {

	vec2 I = vec2(gl_FragCoord.x, gl_FragCoord.y);
	vec4 O = vec4(0.,0.,0.,0.);

	//fixed res for now
	vec2 res = vec2(1280.,640.);

	t = time;

	vec2 w = res.xx;
	I = (I+I-w)/w.y;
	I /= vec2(2, d=I.y) * abs(d) / 25.;
	I.y += t;
	w = ceil(I);
	I.x *= s = sign( random(w/128.) - .5 );          // random flip (for orientation and coloring)
	s *= 2.*mod(w.x+w.y,2.)-1.;                 // checkboard flip (for coloring)
	O -= O -(s*S(I=fract(I))*S(1.-I) + .5-.5*s)*d*d;

	gl_FragColor = O;
}
