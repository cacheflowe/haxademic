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
uniform sampler2D randomMap;
uniform sampler2D positionMap;
uniform sampler2D flowMap;
uniform float width = 1000.;
uniform float height = 1000.;
uniform float rotateAmp = 1.;
uniform float globalScale = 1.;
uniform float individualMeshScale = 1.;
uniform int time = 0;
uniform int speedSimMode = 0;
uniform float pointSize = 1.;
uniform float scaleCenterShrinkAmp = 0.;
uniform float scaleCenterShrinkRadius = 0.5;

uniform float curlZoom = 400.;
uniform float curlAmpBase = 80.;
uniform float curlCohesion = 10.;  // larger numbers bring particles cohesion closer

// Custom attributes
attribute float x;
attribute float y;
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
varying vec2 vSimulationUV;

// Constants
#define PI     3.14159265358
#define TWO_PI 6.28318530718


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

// CURL START ///////////////////////////////////////////////////////////////
vec3 mod289(vec3 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec4 mod289(vec4 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec4 permute(vec4 x) { return mod289(((x*34.0)+1.0)*x); }
vec4 taylorInvSqrt(vec4 r) { return 1.79284291400159 - 0.85373472095314 * r; }
float snoise(vec3 v) { const vec2 C = vec2(1.0/6.0, 1.0/3.0) ; const vec4 D = vec4(0.0, 0.5, 1.0, 2.0); vec3 i = floor(v + dot(v, C.yyy) ); vec3 x0 = v - i + dot(i, C.xxx) ; vec3 g = step(x0.yzx, x0.xyz); vec3 l = 1.0 - g; vec3 i1 = min( g.xyz, l.zxy ); vec3 i2 = max( g.xyz, l.zxy ); vec3 x1 = x0 - i1 + C.xxx; vec3 x2 = x0 - i2 + C.yyy; vec3 x3 = x0 - D.yyy; i = mod289(i); vec4 p = permute( permute( permute( i.z + vec4(0.0, i1.z, i2.z, 1.0 )) + i.y + vec4(0.0, i1.y, i2.y, 1.0 )) + i.x + vec4(0.0, i1.x, i2.x, 1.0 )); float n_ = 0.142857142857; vec3 ns = n_ * D.wyz - D.xzx;vec4 j = p - 49.0 * floor(p * ns.z * ns.z); vec4 x_ = floor(j * ns.z); vec4 y_ = floor(j - 7.0 * x_ ); vec4 x = x_ *ns.x + ns.yyyy; vec4 y = y_ *ns.x + ns.yyyy; vec4 h = 1.0 - abs(x) - abs(y);vec4 b0 = vec4( x.xy, y.xy ); vec4 b1 = vec4( x.zw, y.zw );vec4 s0 = floor(b0)*2.0 + 1.0; vec4 s1 = floor(b1)*2.0 + 1.0; vec4 sh = -step(h, vec4(0.0));vec4 a0 = b0.xzyw + s0.xzyw*sh.xxyy ; vec4 a1 = b1.xzyw + s1.xzyw*sh.zzww ; vec3 p0 = vec3(a0.xy,h.x); vec3 p1 = vec3(a0.zw,h.y); vec3 p2 = vec3(a1.xy,h.z); vec3 p3 = vec3(a1.zw,h.w);  vec4 norm = taylorInvSqrt(vec4(dot(p0,p0), dot(p1,p1), dot(p2, p2), dot(p3,p3))); p0 *= norm.x; p1 *= norm.y; p2 *= norm.z; p3 *= norm.w; vec4 m = max(0.6 - vec4(dot(x0,x0), dot(x1,x1), dot(x2,x2), dot(x3,x3)), 0.0); m = m * m; return 42.0 * dot( m*m, vec4( dot(p0,x0), dot(p1,x1), dot(p2,x2), dot(p3,x3) ) ); }
vec3 snoiseVec3( vec3 x ){float s = snoise(vec3( x )); float s1 = snoise(vec3( x.y - 19.1 , x.z + 33.4 , x.x + 47.2 )); float s2 = snoise(vec3( x.z + 74.2 , x.x - 124.5 , x.y + 99.4 )); vec3 c = vec3( s , s1 , s2 ); return c;}
vec3 curlNoise( vec3 p ) { const float e = .1; vec3 dx = vec3( e , 0.0 , 0.0 ); vec3 dy = vec3( 0.0 , e , 0.0 ); vec3 dz = vec3( 0.0 , 0.0 , e ); vec3 p_x0 = snoiseVec3( p - dx ); vec3 p_x1 = snoiseVec3( p + dx ); vec3 p_y0 = snoiseVec3( p - dy ); vec3 p_y1 = snoiseVec3( p + dy ); vec3 p_z0 = snoiseVec3( p - dz ); vec3 p_z1 = snoiseVec3( p + dz ); float x = p_y1.z - p_y0.z - p_z1.y + p_z0.y; float y = p_z1.x - p_z0.x - p_x1.z + p_x0.z; float z = p_x1.y - p_x0.y - p_y1.x + p_y0.x; const float divisor = 1.0 / ( 2.0 * e ); return normalize( vec3( x , y , z ) * divisor ); }
// CURL END /////////////////////////////////////////////////////////////////


void main() {
  ////////////////////////////////////////////////////////
  // UV coords calculation
  // Get original position * model center
  vec4 uvImageCoords = vec4(x, y, 0., 1.);
  vec3 shapeCenter = vec3(shapeCenterX, shapeCenterY, shapeCenterZ);  // center is passed in via attributes
  vec3 meshLocalVert = shapeCenter - vertex.xyz;  // get local vertex from center of the shape


  ////////////////////////////////////////////////////////
  // UV coords calculation
  // get displacement map color and map to displace x/y coords
  // use x/y attributes, (which are pixel coordinates) as normalized uv coords
  // ivec2 texSize = textureSize(displacementMap, 0); 
  // vec2 displaceUV = vec2(
  //   x / float(texSize.x),
  //   y / float(texSize.y)
  // );
  // vec4 displaceVal = texture2D(displacementMap, displaceUV);
  float luma = 1.; // lumaFromRGB(displaceVal);
  ///////////////////////////

  ////////////////////////////////////////////////////////
  // Position from simulation
  ivec2 texSizeSim = textureSize(positionMap, 0); 
  vec2 simulationUV = vec2(
    x / float(texSizeSim.x),
    y / float(texSizeSim.y)
  );
  vec4 posTex = texture2D( positionMap, simulationUV ); // rgba color of displacement map
  // use vertex color for positioning use - here we're putting points in a cube
  float w = width; // * scale;
  float h = height; // * scale;
  float x = -w / 2. + posTex.x * w;
  float y = -h / 2. + posTex.y * h;
  float z = 0.; // luma * 100.; // -h / 2. + posTex.z * h;
  vec4 simulationPos = vec4(x, y, z, 1.);
  vSimulationUV = simulationUV;

  ////////////////////////////////////////////////////////
  // Use flow map to scale up when flow it really active
  ////////////////////////////////////////////////////////
  vec2 opFlowDisplace = texture2D(flowMap, simulationPos.xy + vec2(0.25, 0.)).xy;
  float scaleAdd = 3. * distance(opFlowDisplace, vec2(-0.5 - 2./255.)); // how far from middle gray?

  ////////////////////////////////////////////////////////
  // Progress
  float progress = posTex.a;
  if(speedSimMode == 1) progress = distance(posTex.a, 0.5) * 100.;
  ////////////////////////////////////////////////////////
  // Random
  vec3 randomVec3 = texture2D( randomMap, simulationUV ).rgb; // rgba color of displacement map

  ////////////////////////////////////////////////////////
  // offset position with curl noise
  // move this after rotation for bendy effects
  float curlAmp = curlAmpBase * (1. - progress);  // multiply by progress to spread furtherover time
  vec3 curlInputOffset = randomVec3.rgb / curlCohesion;  // use random color to slightly offset its input into the curl to give a little randomness
  vec3 curlVertInput = simulationPos.xyz;
  curlVertInput.z = 0.;  // things get too curly if we're moving z and also using that for the curl input
  curlVertInput /= curlZoom;
  vec3 curlInput = curlVertInput + curlInputOffset;
  vec3 curlResult = curlNoise(curlInput);
  simulationPos.xy += curlResult.xy * curlAmp; // only curl .xy for now. z fades off into the distance

  ////////////////////////////////////////////////////////
  // SCALE (applied below)
  float scaleAmp = (pointSize + scaleAdd) * progress; // scale by luma map

  ////////////////////////////////////////////////////////
  // ROTATE individual shapes
  float rotationAmp = luma * rotateAmp * randomVec3.x * TWO_PI * 3.;
  vec3 rotateAxis = vec3(0., 0., 1.);
  vec3 rotatedPos = rotate_vertex_position(meshLocalVert, rotateAxis, rotationAmp); 
  vec3 vertPos = rotatedPos;    // set position to local/rotated position
  vertPos *= scaleAmp;          // apply scale
  vertPos += simulationPos.xyz; // move back to actual position by adding the original *mesh* position from the simulation buffer

  ////////////////////////////////////////////////////////
  // GLOBAL scale multiplier - scales the entire particle system
  vertPos *= globalScale;
  ///////////////////////////

  ////////////////////////////////////////////////////////
  // SET FINAL VERTEX POSITION
  vec4 finalPosition = projection * modelview * vec4(vertPos, 1.);
  gl_Position = finalPosition;
  ///////////////////////////

  ////////////////////////////////////////////////////////
  // Calculating texture coordinates, with r and q set both to one
  // Pass values along to fragment shader
  vVertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
  vVertColor = color; 
  vVertNormal = normalize(normalMatrix * normal);
  vVertex = finalPosition.xyz;
  vNormal = normal;

  // GENERATE colors - passed to fragment shader
  // Overwriting the `vVertColor` attribute entirely will break things. instead, overwrite individual color components
  // vVertColor.r = cos(x/100. + sin(luma * TWO_PI * 2.));
  // vVertColor.g = sin(y/100. + cos(luma * TWO_PI * 2.));
  // vVertColor.b = sin(luma * 10. + sin(luma * TWO_PI * 2.));
  // if we don't want to colorize the texture, reset lights with:
  // vVertColor.r = 1.;
  // vVertColor.g = 1.;
  // vVertColor.b = 1.;
}
