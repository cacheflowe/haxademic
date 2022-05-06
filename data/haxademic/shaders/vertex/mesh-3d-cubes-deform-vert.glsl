#define PROCESSING_POLYGON_SHADER

// Processing uniforms
uniform mat4 transform;
uniform mat4 texMatrix;
uniform mat3 normalMatrix;
uniform mat4 modelviewMatrix;
uniform mat4 projection;
uniform mat4 modelview;

// Processing PShape vertex attributes
attribute vec4 vertex;
attribute vec4 color;
attribute vec2 texCoord;
attribute vec3 normal;

// Custom uniforms
uniform sampler2D texture;
uniform int textureMode = 0;
uniform sampler2D displacementMap;
uniform float displaceAmp = 200.;
uniform float rotateAmp = 0.5;
uniform float globalScale = 1.0;
uniform float spreadScale = 0.0;
uniform float curlZoom = 0.;
uniform float osc = 0.;
uniform float individualMeshScale = 1.;
uniform int time = 0;

// Custom attributes
attribute float x;
attribute float y;
attribute float z;
attribute float shapeCenterX;
attribute float shapeCenterY;
attribute float shapeCenterZ;

// Data for fragment shaders
varying vec4 vVertColor;
varying vec4 vVertTexCoord;
varying vec3 vVertNormal;
varying vec3 vVertLightDir;
varying vec3 vVertex;
varying vec3 vNormal;

// Constants
#define PI     3.14159265358
#define TWO_PI 6.28318530718
#define HALF_PI 1.57079632679489661923 


////////////////////////////////////////////////////////
// Displacement helpers
////////////////////////////////////////////////////////

float lumaFromRGB(vec4 rgba) {
  const vec3 W = vec3(0.2125, 0.7154, 0.0721);
  return dot(rgba.xyz, W);
}

////////////////////////////////////////////////////////
// Translation helpers. Currenltly unused
////////////////////////////////////////////////////////

mat4 translate( float x, float y, float z ) {
  return mat4(  1.0,    0,      0,    x,
                0,      1.0,    0,    y,
                0,      0,      1.0,  z,
                0,      0,      0,    1);
}

mat4 rotationX( in float angle ) {
  return mat4(  1.0,    0,          0,             0,
                0,      cos(angle), -sin(angle),   0,
                0,      sin(angle), cos(angle),    0,
                0,      0,          0,             1);
}

mat4 rotationY( in float angle ) {
  return mat4(  cos(angle),   0,    sin(angle),  0,
                0,            1.0,  0,            0,
                -sin(angle),  0,    cos(angle),   0,
                0,            0,    0,           1);
}

mat4 rotationZ( in float angle ) {
  return mat4(  cos(angle),   -sin(angle),  0,  0,
                sin(angle),   cos(angle),   0,  0,
                0,            0,            1,  0,
                0,            0,            0,  1);
}

mat4 rotationMatrix(vec3 axis, float angle) {
    axis = normalize(axis);
    float s = sin(angle);
    float c = cos(angle);
    float oc = 1.0 - c;

    return mat4(oc * axis.x * axis.x + c,           oc * axis.x * axis.y - axis.z * s,  oc * axis.z * axis.x + axis.y * s,  1.,
                oc * axis.x * axis.y + axis.z * s,  oc * axis.y * axis.y + c,           oc * axis.y * axis.z - axis.x * s,  1.,
                oc * axis.z * axis.x - axis.y * s,  oc * axis.y * axis.z + axis.x * s,  oc * axis.z * axis.z + c,           1.,
                0.0,                                0.0,                                0.0,                                1.0);
} 

////////////////////////////////////////////////////////
// Rotation functions from: 
// https://www.geeks3d.com/20141201/how-to-rotate-a-vertex-by-a-quaternion-in-glsl/
// More to look at here:
// https://stackoverflow.com/questions/27215854/rotate-a-sphere-in-an-opengl-shader
////////////////////////////

vec4 quat_from_axis_radians(vec3 axis, float rads)
{ 
  vec4 qr;
  float half_rads = (rads * 0.5) * TWO_PI;
  qr.x = axis.x * sin(half_rads);
  qr.y = axis.y * sin(half_rads);
  qr.z = axis.z * sin(half_rads);
  qr.w = cos(half_rads);
  return qr;
}

vec3 rotate_vertex_position(vec3 position, vec3 axis, float rads)
{ 
  vec4 q = quat_from_axis_radians(axis, rads);
  vec3 v = position.xyz;
  return v + 2.0 * cross(q.xyz, cross(q.xyz, v) + q.w * v);
}

////////////////////////////
// End Rotation functions
////////////////////////////

// CURL START
vec3 mod289(vec3 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec4 mod289(vec4 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec4 permute(vec4 x) { return mod289(((x*34.0)+1.0)*x); }
vec4 taylorInvSqrt(vec4 r) { return 1.79284291400159 - 0.85373472095314 * r; }
float snoise(vec3 v) { const vec2 C = vec2(1.0/6.0, 1.0/3.0) ; const vec4 D = vec4(0.0, 0.5, 1.0, 2.0); vec3 i = floor(v + dot(v, C.yyy) ); vec3 x0 = v - i + dot(i, C.xxx) ; vec3 g = step(x0.yzx, x0.xyz); vec3 l = 1.0 - g; vec3 i1 = min( g.xyz, l.zxy ); vec3 i2 = max( g.xyz, l.zxy ); vec3 x1 = x0 - i1 + C.xxx; vec3 x2 = x0 - i2 + C.yyy; vec3 x3 = x0 - D.yyy; i = mod289(i); vec4 p = permute( permute( permute( i.z + vec4(0.0, i1.z, i2.z, 1.0 )) + i.y + vec4(0.0, i1.y, i2.y, 1.0 )) + i.x + vec4(0.0, i1.x, i2.x, 1.0 )); float n_ = 0.142857142857; vec3 ns = n_ * D.wyz - D.xzx;vec4 j = p - 49.0 * floor(p * ns.z * ns.z); vec4 x_ = floor(j * ns.z); vec4 y_ = floor(j - 7.0 * x_ ); vec4 x = x_ *ns.x + ns.yyyy; vec4 y = y_ *ns.x + ns.yyyy; vec4 h = 1.0 - abs(x) - abs(y);vec4 b0 = vec4( x.xy, y.xy ); vec4 b1 = vec4( x.zw, y.zw );vec4 s0 = floor(b0)*2.0 + 1.0; vec4 s1 = floor(b1)*2.0 + 1.0; vec4 sh = -step(h, vec4(0.0));vec4 a0 = b0.xzyw + s0.xzyw*sh.xxyy ; vec4 a1 = b1.xzyw + s1.xzyw*sh.zzww ; vec3 p0 = vec3(a0.xy,h.x); vec3 p1 = vec3(a0.zw,h.y); vec3 p2 = vec3(a1.xy,h.z); vec3 p3 = vec3(a1.zw,h.w);  vec4 norm = taylorInvSqrt(vec4(dot(p0,p0), dot(p1,p1), dot(p2, p2), dot(p3,p3))); p0 *= norm.x; p1 *= norm.y; p2 *= norm.z; p3 *= norm.w; vec4 m = max(0.6 - vec4(dot(x0,x0), dot(x1,x1), dot(x2,x2), dot(x3,x3)), 0.0); m = m * m; return 42.0 * dot( m*m, vec4( dot(p0,x0), dot(p1,x1), dot(p2,x2), dot(p3,x3) ) ); }
vec3 snoiseVec3( vec3 x ){float s = snoise(vec3( x )); float s1 = snoise(vec3( x.y - 19.1 , x.z + 33.4 , x.x + 47.2 )); float s2 = snoise(vec3( x.z + 74.2 , x.x - 124.5 , x.y + 99.4 )); vec3 c = vec3( s , s1 , s2 ); return c;}
vec3 curlNoise( vec3 p ) { const float e = .1; vec3 dx = vec3( e , 0.0 , 0.0 ); vec3 dy = vec3( 0.0 , e , 0.0 ); vec3 dz = vec3( 0.0 , 0.0 , e ); vec3 p_x0 = snoiseVec3( p - dx ); vec3 p_x1 = snoiseVec3( p + dx ); vec3 p_y0 = snoiseVec3( p - dy ); vec3 p_y1 = snoiseVec3( p + dy ); vec3 p_z0 = snoiseVec3( p - dz ); vec3 p_z1 = snoiseVec3( p + dz ); float x = p_y1.z - p_y0.z - p_z1.y + p_z0.y; float y = p_z1.x - p_z0.x - p_x1.z + p_x0.z; float z = p_x1.y - p_x0.y - p_y1.x + p_y0.x; const float divisor = 1.0 / ( 2.0 * e ); return normalize( vec3( x , y , z ) * divisor ); }
// CURL END


void main() {
  ////////////////////////////////////////////////////////
  // UV coords calculation
  // Get original position * model center
  vec3 shapeCenter = vec3(shapeCenterX, shapeCenterY, shapeCenterZ);  // center is passed in via attributes

  ////////////////////////////////////////////////////////
  // UV coords calculation
  // get displacement map color and map to displace x/y coords
  // use x/y attributes, (which are pixel coordinates) as normalized uv coords
  // ivec2 texSize = textureSize(displacementMap, 0); 
  // vec2 simulationUV = vec2(
  //   x / float(texSize.x),
  //   y / float(texSize.y)
  // );
  // vec4 displaceVal = texture2D(displacementMap, simulationUV);
  // float luma = lumaFromRGB(displaceVal);
  ///////////////////////////

  ////////////////////////////////////////////////////////
  // Curl calculation
	// float osc = sin(time * 0.005);
	// float curlZoom = 0.002 + 0.002 * osc;
	vec3 curlResult = curlNoise(shapeCenter * curlZoom) * osc;
	float luma = curlResult.z + curlResult.y + curlResult.z; // length(curlResult) * 1.;
  ////////////////////////////////////////////////////////

  ////////////////////////////////////////////////////////
  // ROTATE individual shapes
  vec3 meshLocalVertInv = shapeCenter - vertex.xyz;  // get local vertex from center of the shape
  float rotationAmp = curlResult.z * rotateAmp;   //  time/300. +   // displaceLuma * TWO_PI * 30.;
  vec3 rotateAxis = vec3(0., 1., 0.);
  vec3 rotatedPos = rotate_vertex_position(meshLocalVertInv, rotateAxis, rotationAmp); 
  vec3 newPos = rotatedPos; // meshLocalVertInv
  newPos += shapeCenter;

  // and normal too so the light keeps hitting on the same side.
  // not sure if this is correct, but it looks about right!
  vec3 rotatedNorm = rotate_vertex_position(normal, rotateAxis, rotationAmp);
  normal.x = rotatedNorm.x;
  normal.y = rotatedNorm.y;
  normal.z = rotatedNorm.z;
  ///////////////////////////
  
  ////////////////////////////////////////////////////////
  // SCALE individual meshes by checking center of shape vs. vertices
  vec3 meshLocalVertex = newPos.xyz - shapeCenter;                     // get vertex local to individual mesh center
  float scaleAdjust = luma * individualMeshScale;
	scaleAdjust = scaleAdjust - scaleAdjust * 1.5 * osc;
  newPos.xyz += meshLocalVertex.xyz * scaleAdjust;
  ///////////////////////////

  ////////////////////////////////////////////////////////
  // DISPLACE postition
  vec3 positionOffset = vec3(
    cos(luma * TWO_PI * 10.),
    sin(luma * TWO_PI * 10.),
    luma * -2.
  );
  newPos += displaceAmp * curlResult;// positionOffset; 
  ///////////////////////////

  ////////////////////////////////////////////////////////
  // SPREAD individual meshes with a multiplier
  newPos += shapeCenter * spreadScale;
  newPos *= (1. + 0.3 * (-.5 + curlResult.y));
  ///////////////////////////

  ////////////////////////////////////////////////////////
  // GLOBAL scale multiplier
  newPos *= globalScale;
  ///////////////////////////

  ////////////////////////////////////////////////////////
  // SET FINAL VERTEX POSITION
  vec4 finalPosition = projection * modelview * vec4(newPos, 1.);
  gl_Position = finalPosition;
  ///////////////////////////

  ////////////////////////////////////////////////////////
  // Calculating texture coordinates, with r and q set both to one
  // Pass values along to fragment shader
  vVertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
  vVertColor = color; 
  vVertNormal = normalize(normalMatrix * normal);
  vVertex = vertex.xyz;
  vNormal = normal;

  // GENERATE colors - passed to fragment shader
  // Overwriting the `vVertColor` attribute entirely will break things. instead, overwrite individual color components
  vVertColor.r = 0.65 + 0.5 * cos(HALF_PI + x/24. + sin(0.6 + luma * TWO_PI * 2.));
  vVertColor.g = 0.65 + 0.5 * sin(-2. + y/29. + cos(1.7 + luma * PI * 1.));
  vVertColor.b = 0.65 + 0.5 * sin(y/15. + sin(luma * TWO_PI * 2.));
  // if we don't want to colorize the texture, reset lights with:
  // vVertColor.r = 1.;
  // vVertColor.g = 1.;
  // vVertColor.b = 1.;
  ///////////////////////////

  ////////////////////////////////////////////////////////
  // NOTES
  // working/default displacement technique: multiply with original transform matrix
  // vec4 finalPosition = transform * vec4(rotatedPos, 1.0);
  // alternate way of setting finalPosition
  // gl_Position = projection * modelview * vec4(newPos, 1);
}
