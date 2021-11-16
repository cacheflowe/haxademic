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
uniform sampler2D displacementMap;
uniform sampler2D positionMap;
uniform float width = 1000.;
uniform float height = 1000.;
uniform float rotateAmp = 1.;
uniform float globalScale = 1.;
uniform float individualMeshScale = 1.;
uniform int time = 0;
uniform float pointScale = 1.;
uniform float scaleCenterShrinkAmp = 0.;
uniform float scaleCenterShrinkRadius = 0.5;

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
  ivec2 texSize = textureSize(displacementMap, 0); 
  vec2 displaceUV = vec2(
    x / float(texSize.x),
    y / float(texSize.y)
  );
  vec4 displaceVal = texture2D(displacementMap, displaceUV);
  float luma = lumaFromRGB(displaceVal);
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
  // SCALE (applied below)
  float scaleAmp = pointScale * luma; // scale by luma map

  // additional scaling - shrink towards center
  // this is project-specific, but disabled via uniforms `scaleCenterShrinkAmp` & `scaleCenterShrinkRadius`
  float distFromCenter = length(simulationPos);
  float scaleDownRadius = width * scaleCenterShrinkRadius;
  // if(scaleCenterShrinkAmp > 0. && distFromCenter < scaleDownRadius) {
    float scaleCenterShrink = (1.-(distFromCenter/scaleDownRadius));
    scaleCenterShrink = smoothstep(0.5, 0.9, scaleCenterShrink);
    scaleCenterShrink *= scaleCenterShrinkAmp;
    scaleAmp *= 1. - scaleCenterShrink;
  // }

  ////////////////////////////////////////////////////////
  // ROTATE individual shapes
  float rotationAmp = luma * rotateAmp;
  vec3 rotateAxis = vec3(0., 0., 1.);
  vec3 rotatedPos = rotate_vertex_position(meshLocalVert, rotateAxis, rotationAmp); 
  vec3 vertPos = rotatedPos;    // set position to local/rotated position
  vertPos *= scaleAmp;          // apply scale
  vertPos += simulationPos.xyz; // move back to actual position by adding the original *mesh* position from the simulation buffer

  ////////////////////////////////////////////////////////
  // MOVE individual meshes by checking center of shape vs. vertices
  // vec3 meshLocalVertex = vertPos.xyz - shapeCenter;                     // get vertex local to individual mesh center
  // float scaleAdjust = luma * individualMeshScale;
  // vertPos.xyz += meshLocalVertex.xyz; // * scaleAdjust; // * 0.01;
  ///////////////////////////

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
  vVertColor.r = cos(x/100. + sin(luma * TWO_PI * 2.));
  vVertColor.g = sin(y/100. + cos(luma * TWO_PI * 2.));
  vVertColor.b = sin(luma * 10. + sin(luma * TWO_PI * 2.));
  // if we don't want to colorize the texture, reset lights with:
  // vVertColor.r = 1.;
  // vVertColor.g = 1.;
  // vVertColor.b = 1.;
}
