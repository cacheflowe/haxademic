// from: https://www.shadertoy.com/view/llX3DS
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
uniform vec2 resolution;




#define PI 3.141592653589793

float blinnPhongSpecular(
                         vec3 lightDirection,
                         vec3 viewDirection,
                         vec3 surfaceNormal,
                         float shininess) {

    //Calculate Blinn-Phong power
    vec3 H = normalize(viewDirection + lightDirection);
    return pow(max(0.0, dot(surfaceNormal, H)), shininess);
}

float fogFactorExp2(
                    const float dist,
                    const float density
                    ) {
    const float LOG2 = -1.442695;
    float d = density * dist;
    return 1.0 - clamp(exp2(d * d * LOG2), 0.0, 1.0);
}

float orenNayarDiffuse(
                       vec3 lightDirection,
                       vec3 viewDirection,
                       vec3 surfaceNormal,
                       float roughness,
                       float albedo) {

    float LdotV = dot(lightDirection, viewDirection);
    float NdotL = dot(lightDirection, surfaceNormal);
    float NdotV = dot(surfaceNormal, viewDirection);

    float s = LdotV - NdotL * NdotV;
    float t = mix(1.0, max(NdotL, NdotV), step(0.0, s));

    float sigma2 = roughness * roughness;
    float A = 1.0 + sigma2 * (albedo / (sigma2 + 0.13) + 0.5 / (sigma2 + 0.33));
    float B = 0.45 * sigma2 / (sigma2 + 0.09);

    return albedo * max(0.0, NdotL) * (A + B * s / t) / PI;
}

float backOut(float t) {
    float f = t < 0.5 ? 2.0 * t : 1.0 - (2.0 * t - 1.0);
    float g = pow(f, 3.0) - f * sin(f * PI);
    return t < 0.5 ? 0.5 * g : 0.5 * (1.0 - g) + 0.5;
}

vec4 a_x_mod289(vec4 x) {
    return x - floor(x * (1.0 / 289.0)) * 289.0;
}
float a_x_mod289(float x) {
    return x - floor(x * (1.0 / 289.0)) * 289.0;
}
vec4 a_x_permute(vec4 x) {
    return a_x_mod289(((x * 34.0) + 1.0) * x);
}
float a_x_permute(float x) {
    return a_x_mod289(((x * 34.0) + 1.0) * x);
}
vec4 a_x_taylorInvSqrt(vec4 r) {
    return 1.79284291400159 - 0.85373472095314 * r;
}
float a_x_taylorInvSqrt(float r) {
    return 1.79284291400159 - 0.85373472095314 * r;
}
vec4 a_x_grad4(float j, vec4 ip) {
    const vec4 ones = vec4(1.0, 1.0, 1.0, -1.0);
    vec4 p, s;
    p.xyz = floor(fract(vec3(j) * ip.xyz) * 7.0) * ip.z - 1.0;
    p.w = 1.5 - dot(abs(p.xyz), ones.xyz);
    s = vec4(lessThan(p, vec4(0.0)));
    p.xyz = p.xyz + (s.xyz * 2.0 - 1.0) * s.www;
    return p;
}
#define F4 0.309016994374947451

float snoise(vec4 v) {
    const vec4 C = vec4(0.138196601125011, 0.276393202250021, 0.414589803375032, -0.447213595499958);
    vec4 i = floor(v + dot(v, vec4(F4)));
    vec4 x0 = v - i + dot(i, C.xxxx);
    vec4 i0;
    vec3 isX = step(x0.yzw, x0.xxx);
    vec3 isYZ = step(x0.zww, x0.yyz);
    i0.x = isX.x + isX.y + isX.z;
    i0.yzw = 1.0 - isX;
    i0.y += isYZ.x + isYZ.y;
    i0.zw += 1.0 - isYZ.xy;
    i0.z += isYZ.z;
    i0.w += 1.0 - isYZ.z;
    vec4 i3 = clamp(i0, 0.0, 1.0);
    vec4 i2 = clamp(i0 - 1.0, 0.0, 1.0);
    vec4 i1 = clamp(i0 - 2.0, 0.0, 1.0);
    vec4 x1 = x0 - i1 + C.xxxx;
    vec4 x2 = x0 - i2 + C.yyyy;
    vec4 x3 = x0 - i3 + C.zzzz;
    vec4 x4 = x0 + C.wwww;
    i = a_x_mod289(i);
    float j0 = a_x_permute(a_x_permute(a_x_permute(a_x_permute(i.w) + i.z) + i.y) + i.x);
    vec4 j1 = a_x_permute(a_x_permute(a_x_permute(a_x_permute(i.w + vec4(i1.w, i2.w, i3.w, 1.0)) + i.z + vec4(i1.z, i2.z, i3.z, 1.0)) + i.y + vec4(i1.y, i2.y, i3.y, 1.0)) + i.x + vec4(i1.x, i2.x, i3.x, 1.0));
    vec4 ip = vec4(1.0 / 294.0, 1.0 / 49.0, 1.0 / 7.0, 0.0);
    vec4 p0 = a_x_grad4(j0, ip);
    vec4 p1 = a_x_grad4(j1.x, ip);
    vec4 p2 = a_x_grad4(j1.y, ip);
    vec4 p3 = a_x_grad4(j1.z, ip);
    vec4 p4 = a_x_grad4(j1.w, ip);
    vec4 norm = a_x_taylorInvSqrt(vec4(dot(p0, p0), dot(p1, p1), dot(p2, p2), dot(p3, p3)));
    p0 *= norm.x;
    p1 *= norm.y;
    p2 *= norm.z;
    p3 *= norm.w;
    p4 *= a_x_taylorInvSqrt(dot(p4, p4));
    vec3 m0 = max(0.6 - vec3(dot(x0, x0), dot(x1, x1), dot(x2, x2)), 0.0);
    vec2 m1 = max(0.6 - vec2(dot(x3, x3), dot(x4, x4)), 0.0);
    m0 = m0 * m0;
    m1 = m1 * m1;
    return 49.0 * (dot(m0 * m0, vec3(dot(p0, x0), dot(p1, x1), dot(p2, x2))) + dot(m1 * m1, vec2(dot(p3, x3), dot(p4, x4))));
}

//------------------------------------------------------------------------
// Camera
//
// Move the camera. In this case it's using time and the mouse position
// to orbitate the camera around the origin of the world (0,0,0), where
// the yellow sphere is.
//------------------------------------------------------------------------
void doCamera( out vec3 camPos, out vec3 camTar, in float time, in vec2 mouse )
{
    float an = 10.0*mouse.x;
    camPos = vec3(3.5*sin(an),10.0*(mouse.y-0.2),3.5*cos(an));
    camTar = vec3(0.0,0.0,0.0);
}


//------------------------------------------------------------------------
// Background
//
// The background color. In this case it's just a black color.
//------------------------------------------------------------------------
vec3 doBackground( void )
{
    return vec3(0.001);
}

float smin( float a, float b, float k )
{
    float h = clamp( 0.5+0.5*(b-a)/k, 0.0, 1.0 );
    return mix( b, a, h ) - k*h*(1.0-h);
}

//------------------------------------------------------------------------
// Modelling
//
// Defines the shapes (a sphere in this case) through a distance field, in
// this case it's a sphere of radius 1.
//------------------------------------------------------------------------
float doModel( vec3 p )
{
    vec3  sep = vec3(sin(time * 0.5 + p.x * 0.05), 0, 0);
    float rad = 0.5;
    float d = 100000.0;
    float t = time * 0.5;
    float swell = 1.0; //(1.0 + snoise(vec4(p * 3.0, time)) * 0.06125);
    float blend = 0.6;

    sep.x = backOut(0.5 * (sep.x + 1.0)) * 1.5 - 0.25;

    d = smin(d, length(p) - rad * swell, blend);

    for (int i = 0; i < 4; i++) {
        float I = float(i);
        vec3 off = vec3(sin(I + t), cos(I + t), sin(I - t));

        d = smin(d, length(p + off) - rad * 0.5, blend);

        for (int j = 0; j < 6; j++) {
            float J = float(j);
            float T = t * 0.5;
            vec3 off2 = vec3(cos(J + T), sin(J + T), cos(J - T));

            d = smin(d, length(p + off + off2), blend);
        }
    }

    return d;
}

//------------------------------------------------------------------------
// Material
//
// Defines the material (colors, shading, pattern, texturing) of the model
// at every point based on its position and normal. In this case, it simply
// returns a constant yellow color.
//------------------------------------------------------------------------
vec3 doMaterial( in vec3 pos, in vec3 nor )
{
    return vec3(0.35, 0.4, 0.45);
}

//------------------------------------------------------------------------
// Lighting
//------------------------------------------------------------------------
float calcSoftshadow( in vec3 ro, in vec3 rd );

vec3 doLighting( in vec3 pos, in vec3 nor, in vec3 rd, in float dis, in vec3 mal )
{
    vec3 lin = vec3(0.0);

    // key light
    //-----------------------------
    vec3  lig1 = normalize(vec3(0.5, 2.5, 2.0));
    vec3  lig2 = normalize(vec3(-1.5, -5.5, -2.0));
    float dif1 = orenNayarDiffuse(lig1, normalize(rd), nor, 0.5, 1.0);
    float dif2 = orenNayarDiffuse(lig2, normalize(rd), nor, 0.5, 1.0);
    float spc1 = blinnPhongSpecular(lig1, normalize(rd), nor, 0.5);
    float spc2 = blinnPhongSpecular(lig2, normalize(rd), nor, 0.5);
    float sha1 = 0.0; if( dif1>0.01 ) sha1=max(0.0, calcSoftshadow( pos+0.01*nor, lig1 ));
    float sha2 = 0.0; if( dif2>0.01 ) sha2=max(0.0, calcSoftshadow( pos+0.01*nor, lig2 ));

    vec3 mal2 = vec3(0.001, 0.015, 0.04);

    lin += mal*dif1*vec3(4.00,4.00,4.00)*sha1;
    lin += mal2*dif2*vec3(4.00,4.00,4.00)*sha2;
    lin += mal*spc1*vec3(1.0, 1.5, 2.5)*sha1;
    lin += mal2*spc2*vec3(1.0, 1.5, 2.5)*sha2;
    //lin += vec3(0.02);


    // surface-light interacion
    //-----------------------------
    vec3 col = lin;
    //vec3 col = mal * (length(lin) > 0.95 ? 3.0 : 0.2);

    return col;
}

float calcIntersection( in vec3 ro, in vec3 rd )
{
    const float maxd = 20.0;           // max trace distance
    const float precis = 0.001;        // precission of the intersection
    float h = precis*2.0;
    float t = 0.0;
    float res = -1.0;
    for( int i=0; i<90; i++ )          // max number of raymarching iterations is 90
    {
        if( h<precis||t>maxd ) break;
        h = doModel( ro+rd*t );
        t += h;
    }

    if( t<maxd ) res = t;
    return res;
}

vec3 calcNormal( in vec3 pos )
{
    const float eps = 0.002;             // precision of the normal computation

    const vec3 v1 = vec3( 1.0,-1.0,-1.0);
    const vec3 v2 = vec3(-1.0,-1.0, 1.0);
    const vec3 v3 = vec3(-1.0, 1.0,-1.0);
    const vec3 v4 = vec3( 1.0, 1.0, 1.0);

    return normalize( v1*doModel( pos + v1*eps ) +
                     v2*doModel( pos + v2*eps ) +
                     v3*doModel( pos + v3*eps ) +
                     v4*doModel( pos + v4*eps ) );
}

float calcSoftshadow( in vec3 ro, in vec3 rd )
{
    float res = 1.0;
    float t = 0.0005;                 // selfintersection avoidance distance
    float h = 1.0;
    for( int i=0; i<5; i++ )         // 40 is the max numnber of raymarching steps
    {
        h = doModel(ro + rd*t);
        res = min( res, 64.0*h/t );   // 64 is the hardness of the shadows
        t += clamp( h, 0.02, 2.0 );   // limit the max and min stepping distances
    }
    return clamp(res,0.0,1.0);
}

mat3 calcLookAtMatrix( in vec3 ro, in vec3 ta, in float roll )
{
    vec3 ww = normalize( ta - ro );
    vec3 uu = normalize( cross(ww,vec3(sin(roll),cos(roll),0.0) ) );
    vec3 vv = normalize( cross(uu,ww));
    return mat3( uu, vv, ww );
}

void main( void )
{
    vec2 p = vertTexCoord.xy - vec2(.5,.5);
    p.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
    vec2 m = vec2(sin(time/1500.0), sin(time/2000.0));

    //-----------------------------------------------------
    // camera
    //-----------------------------------------------------

    // camera movement
    vec3 ro, ta;
    doCamera( ro, ta, time, m.xy );

    // camera matrix
    mat3 camMat = calcLookAtMatrix( ro, ta, 0.0 );  // 0.0 is the camera roll

    // create view ray
    vec3 rd = normalize( camMat * vec3(p.xy,2.0) ); // 2.0 is the lens length

    //-----------------------------------------------------
    // render
    //-----------------------------------------------------

    vec3 col = doBackground();

    // raymarch
    float t = calcIntersection( ro, rd );
    if( t>-0.5 )
    {
        // geometry
        vec3 pos = ro + t*rd;
        vec3 nor = calcNormal(pos);

        // materials
        vec3 mal = doMaterial( pos, nor );

        col = mix(doLighting( pos, nor, rd, t, mal ), col, fogFactorExp2(t, 0.15));
    }

    //-----------------------------------------------------
    // postprocessing
    //-----------------------------------------------------
    col = pow( clamp(col,0.0,1.0), vec3(0.4545) );
    col.r = mix(pow(col.r, 1.2), pow(col.r, 0.2), col.r);
    col.b = mix(pow(col.b + 0.75, 0.5) - 0.75, pow(col.b, 1.15), col.b);
    col = mix(col, col * 3.0 * vec3(1.5, 1, 1.1), dot(p, p * 0.1));

    gl_FragColor = vec4( col, 1.0 );
}
