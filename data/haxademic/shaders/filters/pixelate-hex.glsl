// from https://shaderlab.mgz.me/edit/5cdb3535a20d6f00306c02de
// ███████╗██╗  ██╗ █████╗ ██████╗ ███████╗██████╗ ██╗      █████╗ ██████╗
// ██╔════╝██║  ██║██╔══██╗██╔══██╗██╔════╝██╔══██╗██║     ██╔══██╗██╔══██╗
// ███████╗███████║███████║██║  ██║█████╗  ██████╔╝██║     ███████║██████╔╝
// ╚════██║██╔══██║██╔══██║██║  ██║██╔══╝  ██╔══██╗██║     ██╔══██║██╔══██╗
// ███████║██║  ██║██║  ██║██████╔╝███████╗██║  ██║███████╗██║  ██║██████╔╝
// ╚╦═════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═════╝ ╚══════╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚════╦╝
//══╩═══════════════════════════╦═══════════════════════════════════╦═══╝
// Author: Marco Gomez			    ║ @marcogomez_ ( https://mgz.me )   ║
// Sample Shader for ShaderLab	║ https://shaderlab.mgz.me			    ║
//══════════════════════════════╩═══════════════════════════════════╝

#ifdef GL_ES
precision highp float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time = 0.;
uniform float divider = 100.;

const float PI    = 3.141592653590; // acos(-1.0)
const float tau   = 6.283185307180; // τ = π * 2
const float sin60 = 0.866025403784; // [cos30] √(3)/2
const float tan30 = 0.577350269190; // [cot60] √(3)/3
const float ttp   = 0.333333333333; // 1 / 3
const float tdPi  = 0.636619772367; // 2 / π

vec2 hex(vec2 pos, float zoom ) {
  pos *= zoom;
  vec3 hexPos = floor( vec3( pos.x / sin60, pos.y + tan30 * pos.x, pos.y - tan30 * pos.x ) );
  vec2 hexUV = vec2(
    floor( ( hexPos.x + ( 1.0 - mod( floor( ( hexPos.y + hexPos.z ) * ttp ), 2.0 ) ) ) * 0.5 ),
    floor( ( hexPos.y + hexPos.z ) * ttp )
  );
  return hexUV / zoom;
}

vec2 tri(vec2 pos, float zoom ) {
  pos *= zoom;
  vec3 hexPos = floor( vec3( pos.x / sin60, pos.y + tan30 * pos.x, pos.y - tan30 * pos.x ) );
  vec2 hexUV = vec2(
    ( hexPos.x + ( 1.0 - mod( floor( ( hexPos.y + hexPos.z ) * ttp ), 2.0 ) ) ) * 0.5,
    ( hexPos.y + hexPos.z ) * ttp
  );
  // hexUV.x *= texOffset.y / texOffset.x;
  return hexUV / zoom;
}

void mainMarco() {
  vec2 uv = vertTexCoord.xy * sin60 * 2.;// - 0.5;
  uv.x *= texOffset.y / texOffset.x;
  uv.y *= sin60;
  vec2 resolution = vec2(1./texOffset.x, 1./texOffset.y);     // resolution is actual pixel dimensions. texOffset is pixel dimensions converted to normalized value.

  // hex it
  uv = hex(uv, divider);

  // fix uv to even pixels
  gl_FragColor = texture2D(texture, uv);
}

// from: https://www.shadertoy.com/view/lsBGWz
// also: http://coding-experiments.blogspot.com.au/2010/06/pixelation.html

#define H 0.032
#define S ((3./2.) * H/sqrt(3.))

vec2 hexCoord(vec2 hexIndex) {
	float i = hexIndex.x;
	float j = hexIndex.y;
	vec2 r;
	r.x = i * S;
	r.y = j * H + (mod(i,2.0)) * H/2.;
	return r;
}

vec2 hexIndex(vec2 coord) {
	vec2 r;
	float x = coord.x;
	float y = coord.y;
	float it = float(floor(x/S));
	float yts = y - (mod(it,2.0)) * H/2.;
	float jt = float(floor((1./H) * yts));
	float xt = x - it * S;
	float yt = yts - jt * H;
	float deltaj = (yt > H/2.)? 1.0:0.0;
	float fcond = S * (2./3.) * abs(0.5 - yt/H);

	if (xt > fcond) {
		r.x = it;
		r.y = jt;
	}
	else {
		r.x = it - 1.0;
		r.y = jt - (mod(r.x,2.0)) + deltaj;
	}

	return r;
}

void main() {
  vec2 uv = vertTexCoord.xy;
  // uv.x *= texOffset.y / texOffset.x;

  // fix uv to even pixels
	vec2 resolution = vec2(1./texOffset.x, 1./texOffset.y);     // resolution is actual pixel dimensions. texOffset is pixel dimensions converted to normalized value.
	if(mod(uv.x * resolution.x, 2.) > 1.) uv.x -= texOffset.x;  // if sampling from an odd pixel, sample from the even pixel
	if(mod(uv.y * resolution.y, 2.) > 1.) uv.y -= texOffset.y;

  // fix uv to even pixels
  // hex it
  vec2 hexIx = hexIndex(uv);
  vec2 hexXy = hexCoord(hexIx);
  vec4 fcol = texture2D(texture, hexXy);
  gl_FragColor = fcol;
}

/*
precision highp float;
uniform	vec2 resolution;
uniform	vec2 mouse;
uniform	float time;
uniform float fft;

const float PI    = 3.141592653590; // acos(-1.0)
const float tau   = 6.283185307180; // τ = π * 2
const float sin60 = 0.866025403784; // [cos30] √(3)/2
const float tan30 = 0.577350269190; // [cot60] √(3)/3
const float ttp   = 0.333333333333; // 1 / 3
const float tdPi  = 0.636619772367; // 2 / π

#define EFFECT_LENGTH 5.0

#define F vec3(95.4337, 96.4337, 97.4337)
#define t1 time * 0.02
#define t2 time * 0.07
#define psx 1.0 / resolution.x
#define psy 1.0 / resolution.y

vec2 hex ( in vec2 pos, in float c ) {
    pos*=c;
    vec3 hexPos = floor( vec3( pos.x / sin60, pos.y + tan30 * pos.x, pos.y - tan30 * pos.x ) );
    return vec2(
        floor( ( hexPos.x + ( 1.0 - mod( floor( ( hexPos.y + hexPos.z ) * ttp ), 2.0 ) ) ) * 0.5 ),
        floor( ( hexPos.y + hexPos.z ) * ttp )
    ) / c;
}

float osc ( in float s, in float e, in float t ) {
    return ( e - s ) * 0.5 + s + sin( t ) * ( e - s ) * 0.5 ;
}

float ssin ( in float t ) {
    return tdPi * atan( sin( tau * t * 0.5 ) / 0.1 ) * 2.0;
}

void vignette ( inout vec3 c, in vec2 u ) {
    float v = pow( 16.0 * ( u.x * u.y * ( 1.0 - u.x ) * (1.0 - u.y ) ), 0.33 );
    float cv = clamp( v, 0.0, 1.0 );
    c *= v * v;
}

vec2 fade ( in vec2 t ) {
    return t * t * t * ( t * ( t * 6.0 - 15.0 ) + 10.0);
}

vec4 permute ( in vec4 x ) {
    return mod( ( ( x * 34.0 ) + 1.0 ) * x, 289.0 );
}

float perlin ( in vec2 p ) {
    vec4 pi = floor( p.xyxy ) + vec4( 0.0, 0.0, 1.0, 1.0 ); pi = mod( pi, 289.0 );
    vec4 pf = fract( p.xyxy ) - vec4( 0.0, 0.0, 1.0, 1.0 );
    vec4 ix = pi.xzxz, iy = pi.yyww, fx = pf.xzxz, fy = pf.yyww;
    vec4 i = permute( permute( ix ) + iy );
    vec4 gx = 2.0 * fract( i * 0.0243902439 ) - 1.0;
    vec4 gy = abs( gx ) - 0.5;
    vec4 tx = floor( gx + 0.5);
    gx = gx - tx;
    vec2 g00 = vec2( gx.x, gy.x ); vec2 g01 = vec2( gx.z, gy.z );
    vec2 g10 = vec2( gx.y, gy.y ); vec2 g11 = vec2( gx.w, gy.w );
    vec4 n = 1.79284291400159 - 0.85373472095314 * vec4(
        dot( g00, g00 ), dot( g01, g01 ),
        dot( g10, g10 ), dot( g11, g11 )
    );
    g00 *= n.x; g01 *= n.y;
    g10 *= n.z; g11 *= n.w;
    float n00 = dot( g00, vec2( fx.x, fy.x ) ); float n01 = dot( g01, vec2( fx.z, fy.z ) );
    float n10 = dot( g10, vec2( fx.y, fy.y ) ); float n11 = dot( g11, vec2( fx.w, fy.w ) );
    vec2 fxy = fade( pf.xy );
    vec2 nx = mix( vec2( n00, n01 ), vec2( n10, n11 ), fxy.x );
    float nxy = mix( nx.x, nx.y, fxy.y );
    return 2.3 * nxy;
}

vec2 curve ( in vec2 p ) {
    p = ( p - 0.5 ) * 2.0;
    p *= 1.1;
    p.x *= 1.0 + pow( ( abs( p.y ) * 0.20 ), 2.0 );
    p.y *= 1.0 + pow( ( abs( p.x ) * 0.25 ), 2.0 );
    p = ( p * 0.5 ) + 0.5;
    p = p * 0.92 + 0.04;
    return p;
}

vec3 findClosest ( in vec3 ref, in int mode ) {
	vec3 old = vec3 (100.0 * 255.0);
	#define tryColor(new) old = mix (new, old, step (length (old-ref), length (new-ref)));

	// AppleII series 16-color composite video palette representation, based on YIQ color space used by NTSC
	// practical 15 color palette as it count's  with 2 similar grey instances.
	if ( mode == 1 ) {
		tryColor (vec3 (  0.0,   0.0,   0.0));      //  0 - black           (YPbPr = 0.0  ,  0.0 ,  0.0 )
		tryColor (vec3 (133.0,  59.0,  81.0));      //  1 - magenta         (YPbPr = 0.25 ,  0.0 ,  0.5 )
		tryColor (vec3 ( 80.0,  71.0, 137.0));      //  2 - dark blue       (YPbPr = 0.25 ,  0.5 ,  0.0 )
		tryColor (vec3 (233.0,  93.0, 240.0));      //  3 - purple          (YPbPr = 0.5  ,  1.0 ,  1.0 )
		tryColor (vec3 (  0.0, 104.0,  82.0));      //  4 - dark green      (YPbPr = 0.25 ,  0.0 , -0.5 )
		tryColor (vec3 (146.0, 146.0, 146.0));      //  5 - gray #1         (YPbPr = 0.5  ,  0.0 ,  0.0 )
		tryColor (vec3 (  0.0, 168.0, 241.0));      //  6 - medium blue     (YPbPr = 0.5  ,  1.0 , -1.0 )
		tryColor (vec3 (202.0, 195.0, 248.0));      //  7 - light blue      (YPbPr = 0.75 ,  0.5 ,  0.0 )
		tryColor (vec3 ( 81.0,  92.0,  15.0));      //  8 - brown           (YPbPr = 0.25 , -0.5 ,  0.0 )
		tryColor (vec3 (235.0, 127.0,  35.0));      //  9 - orange          (YPbPr = 0.5  , -1.0 ,  1.0 )
		//tryColor(vec3(146.0, 146.0, 146.0));      // 10 - gray #2         (YPbPr = 0.5  ,  0.0 ,  0.0 )
		tryColor (vec3 (241.0, 166.0, 191.0));      // 11 - pink            (YPbPr = 0.75 ,  0.0 ,  0.5 )
		tryColor (vec3 (  0.0, 201.0,  41.0));      // 12 - green           (YPbPr = 0.5  , -1.0 , -1.0 )
		tryColor (vec3 (203.0, 211.0, 155.0));      // 13 - yellow          (YPbPr = 0.75 , -0.5 ,  0.0 )
		tryColor (vec3 (154.0, 220.0, 203.0));      // 14 - aqua            (YPbPr = 0.75 ,  0.0 , -0.5 )
		tryColor (vec3 (255.0, 255.0, 255.0));      // 15 - white           (YPbPr = 1.0  ,  0.0 ,  0.0 )
	}

	// Commodore VIC-20 based on MOS Technology VIC chip (also a 16-color YpbPr composite video palette)
	// this one lacks any intermediate grey shade and counts with 5 levels of luminance.
	if ( mode == 2 ) {
		tryColor (vec3 (  0.0,   0.0,   0.0));		//  0 - black           (YPbPr = 0.0  ,  0.0   ,  0.0   )
		tryColor (vec3 (255.0, 255.0, 255.0));		//  1 - white           (YPbPr = 1.0  ,  0.0   ,  0.0   )
		tryColor (vec3 (120.0,  41.0,  34.0));		//  2 - red             (YPbPr = 0.25 , -0.383 ,  0.924 )
		tryColor (vec3 (135.0, 214.0, 221.0));		//  3 - cyan            (YPbPr = 0.75 ,  0.383 , -0.924 )
		tryColor (vec3 (170.0,  95.0, 182.0));		//  4 - purple          (YPbPr = 0.5  ,  0.707 ,  0.707 )
		tryColor (vec3 ( 85.0, 160.0,  73.0));		//  5 - green           (YPbPr = 0.5  , -0.707 , -0.707 )
		tryColor (vec3 ( 64.0,  49.0, 141.0));		//  6 - blue            (YPbPr = 0.25 ,  1.0   ,  0.0   )
		tryColor (vec3 (191.0, 206.0, 114.0));		//  7 - yellow          (YPbPr = 0.75 , -1.0   ,  0.0   )
		tryColor (vec3 (170.0, 116.0,  73.0));		//  8 - orange          (YPbPr = 0.5  , -0.707 ,  0.707 )
		tryColor (vec3 (234.0, 180.0, 137.0));		//  9 - light orange    (YPbPr = 0.75 , -0.707 ,  0.707 )
		tryColor (vec3 (184.0, 105.0,  98.0));		// 10 - light red       (YPbPr = 0.5  , -0.383 ,  0.924 )
		tryColor (vec3 (199.0, 255.0, 255.0));		// 11 - light cyan      (YPbPr = 1.0  ,  0.383 , -0.924 )
		tryColor (vec3 (234.0, 159.0, 246.0));		// 12 - light purple    (YPbPr = 0.75 ,  0.707 ,  0.707 )
		tryColor (vec3 (148.0, 224.0, 137.0));		// 13 - light green     (YPbPr = 0.75 , -0.707 , -0.707 )
		tryColor (vec3 (128.0, 113.0, 204.0));		// 14 - light blue      (YPbPr = 0.5  ,  1.0   ,  0.0   )
		tryColor (vec3 (255.0, 255.0, 178.0));		// 15 - light yellow    (YPbPr = 1.0  , -1.0   ,  0.0   )
	}

	// Commodore 64 based on MOS Technology VIC-II chip (also a 16-color YpbPr composite video palette)
	// this one evolved from VIC-20 and now counts with 3 shades of grey
	if ( mode == 3 ) {
		tryColor (vec3 (  0.0,   0.0,   0.0));		//  0 - black           (YPbPr = 0.0   ,  0.0   ,  0.0   )
		tryColor (vec3 (255.0, 255.0, 255.0));		//  1 - white           (YPbPr = 1.0   ,  0.0   ,  0.0   )
		tryColor (vec3 (161.0,  77.0,  67.0));		//  2 - red             (YPbPr = 0.313 , -0.383 ,  0.924 )
		tryColor (vec3 (106.0, 193.0, 200.0));		//  3 - cyan            (YPbPr = 0.625 ,  0.383 , -0.924 )
		tryColor (vec3 (162.0,  86.0, 165.0));		//  4 - purple          (YPbPr = 0.375 ,  0.707 ,  0.707 )
		tryColor (vec3 ( 92.0, 173.0,  95.0));		//  5 - green           (YPbPr = 0.5   , -0.707 , -0.707 )
		tryColor (vec3 ( 79.0,  68.0, 156.0));		//  6 - blue            (YPbPr = 0.25  ,  1.0   ,  0.0   )
		tryColor (vec3 (203.0, 214.0, 137.0));		//  7 - yellow          (YPbPr = 0.75  , -1.0   ,  0.0   )
		tryColor (vec3 (163.0, 104.0,  58.0));		//  8 - orange          (YPbPr = 0.375 , -0.707 ,  0.707 )
		tryColor (vec3 (110.0,  83.0,  11.0));		//  9 - brown           (YPbPr = 0.25  , -0.924 ,  0.383 )
		tryColor (vec3 (204.0, 127.0, 118.0));		// 10 - light red       (YPbPr = 0.5   , -0.383 ,  0.924 )
		tryColor (vec3 ( 99.0,  99.0,  99.0));		// 11 - dark grey       (YPbPr = 0.313 ,  0.0   ,  0.0   )
		tryColor (vec3 (139.0, 139.0, 139.0));		// 12 - grey            (YPbPr = 0.469 ,  0.0   ,  0.0   )
		tryColor (vec3 (155.0, 227.0, 157.0));		// 13 - light green     (YPbPr = 0.75  , -0.707 , -0.707 )
		tryColor (vec3 (138.0, 127.0, 205.0));		// 14 - light blue      (YPbPr = 0.469 ,  1.0   ,  0.0   )
		tryColor (vec3 (175.0, 175.0, 175.0));		// 15 - light grey      (YPbPr = 0.625  , 0.0   ,  0.0   )
	}

	// MSX compatible computers using a Texas Instruments TMS9918 chip providing a proprietary 15-color YPbPr
	// ... encoded palette with a plus transparent color intended to be used by hardware sprites overlay.
	// ... curiously, TI TMS9918 focuses on 3 shades of green, 3 shades of red, and just 1 shade of grey
	if ( mode == 4 ) {
		//tryColor(vec3(  0.0,   0.0,   0.0));		//  0 - transparent     (YPbPr = 0.0  ,  0.0   ,  0.0   )
		tryColor (vec3 (  0.0,   0.0,   0.0));		//  1 - black           (YPbPr = 0.0  ,  0.0   ,  0.0   )
		tryColor (vec3 ( 62.0, 184.0,  73.0));		//  2 - medium green    (YPbPr = 0.53 , -0.509 , -0.755 )
		tryColor (vec3 (116.0, 208.0, 125.0));		//  3 - light green     (YPbPr = 0.67 , -0.377 , -0.566 )
		tryColor (vec3 ( 89.0,  85.0, 224.0));		//  4 - dark blue       (YPbPr = 0.40 ,  1.0   , -0.132 )
		tryColor (vec3 (128.0, 128.0, 241.0));		//  5 - light blue      (YPbPr = 0.53 ,  0.868 , -0.075 )
		tryColor (vec3 (185.0,  94.0,  81.0));		//  6 - dark red        (YPbPr = 0.47 , -0.321 ,  0.679 )
		tryColor (vec3 (101.0, 219.0, 239.0));		//  7 - cyan            (YPbPr = 0.73 ,  0.434 , -0.887 )
		tryColor (vec3 (219.0, 101.0,  89.0));		//  8 - medium red      (YPbPr = 0.53 , -0.377 ,  0.868 )
		tryColor (vec3 (255.0, 137.0, 125.0));		//  9 - light red       (YPbPr = 0.67 , -0.377 ,  0.868 )
		tryColor (vec3 (204.0, 195.0,  94.0));		// 10 - dark yellow     (YPbPr = 0.73 , -0.755 ,  0.189 )
		tryColor (vec3 (222.0, 208.0, 135.0));		// 11 - light yellow    (YPbPr = 0.80 , -0.566 ,  0.189 )
		tryColor (vec3 ( 58.0, 162.0,  65.0));		// 12 - dark green      (YPbPr = 0.47 , -0.453 , -0.642 )
		tryColor (vec3 (183.0, 102.0, 181.0));		// 13 - magenta         (YPbPr = 0.53 ,  0.377 ,  0.491 )
		tryColor (vec3 (204.0, 204.0, 204.0));		// 14 - grey            (YPbPr = 0.80 ,  0.0   ,  0.0   )
		tryColor (vec3 (255.0, 255.0, 255.0));		// 15 - white           (YPbPr = 1.0  ,  0.0   ,  0.0   )
	}

	// Part Two - IBM RGBi based palettes =============================================================================

	// CGA Mode 4 palette #1 with both intensities (low and high). The good old cyan-magenta "7-color" palette
	if ( mode == 5 ) {
		tryColor (vec3 ( 30.0,  30.0,  30.0));      //  0 - black
		tryColor (vec3 (  0.0, 170.0, 170.0));      //  1 - low intensity cyan
		tryColor (vec3 (170.0,   0.0, 170.0));      //  2 - low intensity magenta
		tryColor (vec3 (170.0, 170.0, 170.0));      //  3 - low intensity white / light grey
		tryColor (vec3 ( 85.0, 255.0, 255.0));      //  4 - high intensity cyan
		tryColor (vec3 (255.0,  85.0, 255.0));      //  5 - high intensity magenta
		tryColor (vec3 (255.0, 255.0, 255.0));      //  6 - high intensity grey / bright white
	}


	// 16-color RGBi IBM CGA as seen on registers from compatible monitors back then
	if ( mode == 6 ) {
		tryColor (vec3 (  0.0,   0.0,   0.0));      //  0 - black
		tryColor (vec3 (  0.0,  25.0, 182.0));      //  1 - low blue
		tryColor (vec3 (  0.0, 180.0,  29.0));      //  2 - low green
		tryColor (vec3 (  0.0, 182.0, 184.0));      //  3 - low cyan
		tryColor (vec3 (196.0,  31.0,  12.0));      //  4 - low red
		tryColor (vec3 (193.0,  43.0, 182.0));      //  5 - low magenta
		tryColor (vec3 (193.0, 106.0,  21.0));      //  6 - brown
		tryColor (vec3 (184.0, 184.0, 184.0));      //  7 - light grey
		tryColor (vec3 (104.0, 104.0, 104.0));      //  8 - dark grey
		tryColor (vec3 ( 95.0, 110.0, 252.0));      //  9 - high blue
		tryColor (vec3 ( 57.0, 250.0, 111.0));      // 10 - high green
		tryColor (vec3 ( 36.0, 252.0, 254.0));      // 11 - high cyan
		tryColor (vec3 (255.0, 112.0, 106.0));      // 12 - high red
		tryColor (vec3 (255.0, 118.0, 253.0));      // 13 - high magenta
		tryColor (vec3 (255.0, 253.0, 113.0));      // 14 - yellow
		tryColor (vec3 (255.0, 255.0, 255.0));      // 15 - white
	}

	else if ( mode == 7 ) {                    // 16 COLORS
		tryColor (vec3 (  0.0,   0.0,   0.0));
		tryColor (vec3 (255.0, 255.0, 255.0));
		tryColor (vec3 (255.0,   0.0,   0.0));
		tryColor (vec3 (  0.0, 255.0,   0.0));
		tryColor (vec3 (  0.0,   0.0, 255.0));
		tryColor (vec3 (255.0, 255.0,   0.0));
		tryColor (vec3 (  0.0, 255.0, 255.0));
		tryColor (vec3 (255.0,   0.0, 255.0));
		tryColor (vec3 (128.0,   0.0,   0.0));
		tryColor (vec3 (  0.0, 128.0,   0.0));
		tryColor (vec3 (  0.0,   0.0, 128.0));
		tryColor (vec3 (128.0, 128.0,   0.0));
		tryColor (vec3 (  0.0, 128.0, 128.0));
		tryColor (vec3 (128.0,   0.0, 128.0));
		tryColor (vec3 (128.0, 128.0, 128.0));
		tryColor (vec3 (255.0, 128.0, 128.0));
	}

	else if ( mode == 8 ) {                    // 16 COLORS
		tryColor (vec3 (  0.0,   0.0,   0.0));
	    tryColor (vec3 (255.0, 255.0, 255.0));
	    tryColor (vec3 (116.0,  67.0,  53.0));
	    tryColor (vec3 (124.0, 172.0, 186.0));
	    tryColor (vec3 (123.0,  72.0, 144.0));
	    tryColor (vec3 (100.0, 151.0,  79.0));
	    tryColor (vec3 ( 64.0,  50.0, 133.0));
	    tryColor (vec3 (191.0, 205.0, 122.0));
	    tryColor (vec3 (123.0,  91.0,  47.0));
	    tryColor (vec3 ( 79.0,  69.0,   0.0));
	    tryColor (vec3 (163.0, 114.0, 101.0));
	    tryColor (vec3 ( 80.0,  80.0,  80.0));
	    tryColor (vec3 (120.0, 120.0, 120.0));
	    tryColor (vec3 (164.0, 215.0, 142.0));
	    tryColor (vec3 (120.0, 106.0, 189.0));
	    tryColor (vec3 (159.0, 159.0, 150.0));
	}

	else if ( mode == 9 ) {                    // 16 COLORS
		tryColor (vec3 (  0.0,   0.0,   0.0));
		tryColor (vec3 (255.0, 255.0, 255.0));
		tryColor (vec3 (152.0,  75.0,  67.0));
		tryColor (vec3 (121.0, 193.0, 200.0));
		tryColor (vec3 (155.0,  81.0, 165.0));
		tryColor (vec3 (202.0, 160.0, 218.0));
		tryColor (vec3 (202.0, 160.0, 218.0));
		tryColor (vec3 (202.0, 160.0, 218.0));
		tryColor (vec3 (202.0, 160.0, 218.0));
		tryColor (vec3 (191.0, 148.0, 208.0));
		tryColor (vec3 (179.0, 119.0, 201.0));
		tryColor (vec3 (167.0, 106.0, 198.0));
		tryColor (vec3 (138.0, 138.0, 138.0));
		tryColor (vec3 (163.0, 229.0, 153.0));
		tryColor (vec3 (138.0, 123.0, 206.0));
		tryColor (vec3 (173.0, 173.0, 173.0));
	}

	else if ( mode == 10 ) {                   // 16 COLORS
		tryColor (vec3 (  0.0,   0.0,   0.0));
		tryColor (vec3 (255.0, 255.0, 255.0));
		tryColor (vec3 (255.0,   0.0,   0.0));
		tryColor (vec3 (  0.0, 255.0,   0.0));
		tryColor (vec3 (  0.0,   0.0, 255.0));
		tryColor (vec3 (255.0,   0.0, 255.0));
		tryColor (vec3 (255.0, 255.0,   0.0));
		tryColor (vec3 (  0.0, 255.0, 255.0));
		tryColor (vec3 (215.0,   0.0,   0.0));
		tryColor (vec3 (  0.0, 215.0,   0.0));
		tryColor (vec3 (  0.0,   0.0, 215.0));
		tryColor (vec3 (215.0,   0.0, 215.0));
		tryColor (vec3 (215.0, 215.0,   0.0));
		tryColor (vec3 (  0.0, 215.0, 215.0));
		tryColor (vec3 (215.0, 215.0, 215.0));
		tryColor (vec3 ( 40.0,  40.0,  40.0));
	}

	else if ( mode == 11 ) {                   // 13 COLORS
		tryColor (vec3 (  0.0,   0.0,   0.0));
		tryColor (vec3 (  1.0,   3.0,  31.0));
		tryColor (vec3 (  1.0,   3.0,  53.0));
		tryColor (vec3 ( 28.0,   2.0,  78.0));
		tryColor (vec3 ( 80.0,   2.0, 110.0));
		tryColor (vec3 (143.0,   3.0, 133.0));
		tryColor (vec3 (181.0,   3.0, 103.0));
		tryColor (vec3 (229.0,   3.0,  46.0));
		tryColor (vec3 (252.0,  73.0,  31.0));
		tryColor (vec3 (253.0, 173.0,  81.0));
		tryColor (vec3 (254.0, 244.0, 139.0));
		tryColor (vec3 (239.0, 254.0, 203.0));
		tryColor (vec3 (242.0, 255.0, 236.0));
	}

	else if ( mode == 12 ) {                   // 5 COLORS (GREENISH - GAMEBOY)
		tryColor (vec3 ( 41.0,  57.0,  65.0));
		tryColor (vec3 ( 72.0,  93.0,  72.0));
		tryColor (vec3 (133.0, 149.0,  80.0));
		tryColor (vec3 (186.0, 195.0, 117.0));
		tryColor (vec3 (242.0, 239.0, 231.0));
	}

	else if ( mode == 13 ) {                   // 5 COLORS (PURPLEISH)
		tryColor (vec3 ( 65.0,  49.0,  41.0));
		tryColor (vec3 ( 93.0,  72.0,  93.0));
		tryColor (vec3 ( 96.0,  80.0, 149.0));
		tryColor (vec3 (126.0, 117.0, 195.0));
		tryColor (vec3 (231.0, 234.0, 242.0));
	}

	else if ( mode == 14 ) {                   // 4 COLORS (GREENISH)
		tryColor (vec3 (156.0, 189.0,  15.0));
		tryColor (vec3 (140.0, 173.0,  15.0));
		tryColor (vec3 ( 48.0,  98.0,  48.0));
		tryColor (vec3 ( 15.0,  56.0,  15.0));
	}

	else if ( mode == 15 ) {                   // 11 COLORS (GRAYSCALE)
		tryColor (vec3 (255.0, 255.0, 255.0)); // L100
		tryColor (vec3 (226.0, 226.0, 226.0)); // L90
		tryColor (vec3 (198.0, 198.0, 198.0)); // L80
		tryColor (vec3 (171.0, 171.0, 171.0)); // L70
		tryColor (vec3 (145.0, 145.0, 145.0)); // L60
		tryColor (vec3 (119.0, 119.0, 119.0)); // L50
		tryColor (vec3 ( 94.0,  94.0,  94.0)); // L40
		tryColor (vec3 ( 71.0,  71.0,  71.0)); // L30
		tryColor (vec3 ( 48.0,  48.0,  48.0)); // L20
		tryColor (vec3 ( 27.0,  27.0,  27.0)); // L10
		tryColor (vec3 (  0.0,   0.0,   0.0)); // L0
	}

	else if ( mode == 16 ) {                   // 6 COLORS (GRAYSCALE)
		tryColor (vec3 (255.0, 255.0, 255.0)); // L100
		tryColor (vec3 (198.0, 198.0, 198.0)); // L80
		tryColor (vec3 (145.0, 145.0, 145.0)); // L60
		tryColor (vec3 ( 94.0,  94.0,  94.0)); // L40
		tryColor (vec3 ( 48.0,  48.0,  48.0)); // L20
		tryColor (vec3 (  0.0,   0.0,   0.0)); // L0
	}

	else if ( mode == 17 ) {                   // 3 COLORS (GRAYSCALE)
		tryColor (vec3 (255.0, 255.0, 255.0)); // L100
		tryColor (vec3 (145.0, 145.0, 145.0)); // L60
		tryColor (vec3 ( 48.0,  48.0,  48.0)); // L20
	}

	return old ;
}

float ditherMatrix (float x, float y) {
	return (
        mix(mix(mix(mix(mix(mix(0.0,32.0,step(1.0,y)),mix(8.0,40.0,step(3.0,y)),step(2.0,y)),
        mix(mix(2.0,34.0,step(5.0,y)),mix(10.0,42.0,step(7.0,y)),step(6.0,y)),step(4.0,y)),
        mix(mix(mix(48.0,16.0,step(1.0,y)),mix(56.0,24.0,step(3.0,y)),step(2.0,y)),
        mix(mix(50.0,18.0,step(5.0,y)),mix(58.0,26.0,step(7.0,y)),step(6.0,y)),step(4.0,y)),step(1.0,x)),
        mix(mix(mix(mix(12.0,44.0,step(1.0,y)),mix(4.0,36.0,step(3.0,y)),step(2.0,y)),
        mix(mix(14.0,46.0,step(5.0,y)),mix(6.0,38.0,step(7.0,y)),step(6.0,y)),step(4.0,y)),
        mix(mix(mix(60.0,28.0,step(1.0,y)),mix(52.0,20.0,step(3.0,y)),step(2.0,y)),
        mix(mix(62.0,30.0,step(5.0,y)),mix(54.0,22.0,step(7.0,y)),step(6.0,y)),step(4.0,y)),step(3.0,x)),step(2.0,x)),
        mix(mix(mix(mix(mix(3.0,35.0,step(1.0,y)),mix(11.0,43.0,step(3.0,y)),step(2.0,y)),
        mix(mix(1.0,33.0,step(5.0,y)),mix(9.0,41.0,step(7.0,y)),step(6.0,y)),step(4.0,y)),
        mix(mix(mix(51.0,19.0,step(1.0,y)),mix(59.0,27.0,step(3.0,y)),step(2.0,y)),
        mix(mix(49.0,17.0,step(5.0,y)),mix(57.0,25.0,step(7.0,y)),step(6.0,y)),step(4.0,y)),step(5.0,x)),
        mix(mix(mix(mix(15.0,47.0,step(1.0,y)),mix(7.0,39.0,step(3.0,y)),step(2.0,y)),
        mix(mix(13.0,45.0,step(5.0,y)),mix(5.0,37.0,step(7.0,y)),step(6.0,y)),step(4.0,y)),
        mix(mix(mix(63.0,31.0,step(1.0,y)),mix(55.0,23.0,step(3.0,y)),step(2.0,y)),
        mix(mix(61.0,29.0,step(5.0,y)),mix(53.0,21.0,step(7.0,y)),step(6.0,y)),step(4.0,y)),step(7.0,x)),step(6.0,x)),step(4.0,x))
    );
}

vec3 dither ( in vec3 col, in vec2 uv, in int mode) {
	col *= 255.0;
	col += ditherMatrix (mod (uv.x, 8.0), mod (uv.y, 8.0)) ;
	col = findClosest (clamp (col, 0.0, 255.0), mode);
	return col / 255.0;
}

vec3 getColorFX ( in int fx, in vec2 pos, in vec3 col ) {
    fx = int( fract( float( fx ) * 1.61456 ) * 18.0 );
    int temp = fx / 17;
    fx -= temp * 17;
    vec3 color = vec3( 0.0 );
         if ( fx ==  1 ) color = dither( col, pos,  1 );
    else if ( fx ==  2 ) color = dither( col, pos,  2 );
    else if ( fx ==  3 ) color = dither( col, pos,  3 );
    else if ( fx ==  4 ) color = dither( col, pos,  4 );
    else if ( fx ==  5 ) color = dither( col, pos,  5 );
    else if ( fx ==  6 ) color = dither( col, pos,  6 );
    else if ( fx ==  7 ) color = dither( col, pos,  7 );
	else if ( fx ==  8 ) color = dither( col, pos,  8 );
	else if ( fx ==  9 ) color = dither( col, pos,  9 );
	else if ( fx == 10 ) color = dither( col, pos, 10 );
	else if ( fx == 11 ) color = dither( col, pos, 11 );
	else if ( fx == 12 ) color = dither( col, pos, 12 );
	else if ( fx == 13 ) color = dither( col, pos, 13 );
	else if ( fx == 14 ) color = dither( col, pos, 14 );
	else if ( fx == 15 ) color = dither( col, pos, 15 );
	else if ( fx == 16 ) color = dither( col, pos, 16 );
	else if ( fx == 17 ) color = dither( col, pos, 17 );
    else                 color = col;
   	return color;
}

vec3 getScreenOutput( in vec2 uv, in vec3 col, in float eLength ) {
    int fx = int( time / eLength );
    float frac = mod( time, eLength ) / eLength;
    vec3 colorA = getColorFX( fx    , gl_FragCoord.xy * 0.5, col );
    vec3 colorB = getColorFX( fx - 1, gl_FragCoord.xy * 0.5, col );
    return mix( colorB, colorA, smoothstep( 0.7, 1.0, frac ) );
}

void main(void) {
	vec2 uvscr = gl_FragCoord.xy / resolution.xy;
	vec2 uv = ( 2.0 * gl_FragCoord.xy - resolution ) / max( resolution.x, resolution.y );
    uv *= 2.0;
	float ar = max( resolution.x, resolution.y ) / min( resolution.x, resolution.y );
    float nr = ( 32.0 * min( resolution.x, resolution.y ) ) / max( resolution.x, resolution.y );
	float df = nr + osc( 64.0, 96.0, time) + 48.0 * clamp( fft, 0.0, 1.0 );
	vec2 uva = hex( uv, df ) * 2.5;
	vec2 uvb = floor( uv * df ) / ( df / ar );
    float strangeSin = ssin( t2 );
	uv = mix( uva, uvb, osc( 0.0, 1.0, strangeSin ) );

	for ( int i = 1; i < 8; i++ ) {
        float fi = float( i );
		vec2 u = uv + t1;
		u.x += 0.5 / fi * sin( fi * uv.y + t2 + 0.3 * fi ) + 0.5 - ( mouse.x * 0.25 );
		u.y += ( 0.5 * ar ) / fi * sin( fi * uv.x + t2 + 0.3 * fi ) - 0.5 - ( mouse.y * 0.25 );
		uv = u;
	}

	vec3 col = vec3(
        sin( 3.0 * uv.x ) + osc( 0.2, 0.4, t2 ),
        sin( 3.0 * uv.y ) + osc( 0.1, 0.4, t1 ),
        sin( uv.x + uv.y ) ) * 1.5;
	vignette( col, uvscr );
	col += vec3( perlin( t1 + uv * 4.0 ) ) * 1.25;
	vec3 colB = col + exp( vec3( 0.2, 0.3, 0.7 ) ) * 0.42;
	col = mix(col, colB, fft);
    vec3 color = getScreenOutput( uv, col, 5.0 );
	gl_FragColor=vec4( color, 1.0 );
}
*/
