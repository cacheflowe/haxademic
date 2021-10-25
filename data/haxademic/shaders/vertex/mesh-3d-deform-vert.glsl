uniform mat4 transform;
uniform mat4 texMatrix;
uniform mat3 normalMatrix;
uniform mat4 modelviewMatrix;

uniform mat4 projection;
uniform mat4 modelview;

attribute vec4 vertex;
attribute vec4 color;
attribute vec2 texCoord;
attribute vec3 normal;

uniform sampler2D texture;
uniform int textureMode = 0;

uniform sampler2D displacementMap;
uniform float displaceAmp = 1.;
uniform float globalScale = 1.;
uniform float spreadScale = 1.;
uniform float individualMeshScale = 1.;
uniform int sheet = 0;
uniform int yAxisOnly = 0;
uniform int time = 0;
uniform mat4 modelviewInv;

attribute float x;
attribute float y;
attribute float shapeCenterX;
attribute float shapeCenterY;
attribute float shapeCenterZ;

varying vec4 vVertColor;
varying vec4 vVertTexCoord;
varying vec3 vVertNormal;
varying vec3 vVertLightDir;

varying vec3 vVertex;
varying vec3 vNormal;

#define PI     3.14159265358
#define TWO_PI 6.28318530718

#define PROCESSING_POLYGON_SHADER

float rgbToGray(vec4 rgba) {
  const vec3 W = vec3(0.2125, 0.7154, 0.0721);
  return dot(rgba.xyz, W);
}

mat4 translate( float x, float y, float z ) {
  return mat4(	1.0,		0,			0,		x,
                 0, 			1.0,		0,		y,
                0, 			0,	 		1.0,	z,
                0, 			0,			0, 		1);
}

mat4 rotationX( in float angle ) {
  return mat4(	1.0,		0,			0,			0,
           0, 	cos(angle),	-sin(angle),		0,
          0, 	sin(angle),	 cos(angle),		0,
          0, 			0,			  0, 		1);
}

mat4 rotationY( in float angle ) {
  return mat4(	cos(angle),		0,		sin(angle),	0,
               0,		1.0,			 0,	0,
          -sin(angle),	0,		cos(angle),	0,
              0, 		0,				0,	1);
}

mat4 rotationZ( in float angle ) {
  return mat4(	cos(angle),		-sin(angle),	0,	0,
           sin(angle),		cos(angle),		0,	0,
              0,				0,		1,	0,
              0,				0,		0,	1);
}

mat4 rotationMatrix(vec3 axis, float angle)
{
    axis = normalize(axis);
    float s = sin(angle);
    float c = cos(angle);
    float oc = 1.0 - c;

    return mat4(oc * axis.x * axis.x + c,           oc * axis.x * axis.y - axis.z * s,  oc * axis.z * axis.x + axis.y * s,  1.,
                oc * axis.x * axis.y + axis.z * s,  oc * axis.y * axis.y + c,           oc * axis.y * axis.z - axis.x * s,  1.,
                oc * axis.z * axis.x - axis.y * s,  oc * axis.y * axis.z + axis.x * s,  oc * axis.z * axis.z + c,           1.,
                0.0,                                0.0,                                0.0,                                1.0);
} 

////////////////////////////
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
  vec4 v = vertex;
  vec3 shapeCenter = vec3(shapeCenterX, shapeCenterY, shapeCenterZ);  // center is passed in via attributes

  // Calculating texture coordinates, with r and q set both to one
  // And pass values along to fragment shader
  vVertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
  vVertColor = color; 
  vVertNormal = normalize(normalMatrix * normal);
  vVertex = vertex.xyz;
  vNormal = normal;
  ///////////////////////////

  ////////////////////////////////////////////////////////
  // UV coords calculation
  // get displacement map color and map to displace x/y coords
  // use x/y attributes, (which are pixel coordinates) as normalized uv coords
  ivec2 texSize = textureSize(displacementMap, 0); 
  vec2 simulationUV = vec2(
    x / float(texSize.x),
    y / float(texSize.y)
  );
  vec4 displaceVal = texture2D(displacementMap, simulationUV);
  float luma = rgbToGray(displaceVal);
  ///////////////////////////

  ////////////////////////////////////////////////////////
  // GENERATE colors
  // Overwriting the `vVertColor` attribute entirely will break things. instead, overwrite individual color components
  vVertColor.r = cos(x/100. + sin(luma * TWO_PI * 2.));
  vVertColor.g = sin(y/100. + cos(luma * TWO_PI * 2.));
  vVertColor.b = sin(luma * 10. + sin(luma * TWO_PI * 2.));
  ///////////////////////////

  ////////////////////////////////////////////////////////
  // ROTATE individual shapes
  vec3 meshLocalVertInv = shapeCenter - v.xyz;	// get local vertex from center of the shape
  float rotationAmp = time/300. + luma * 4.;// displaceLuma * TWO_PI * 30.;
  vec3 rotatedPos = rotate_vertex_position(meshLocalVertInv, vec3(0., 1., 0.), rotationAmp); 
  vec3 newPos = rotatedPos;
  newPos += shapeCenter;
  ///////////////////////////
  
  ////////////////////////////////////////////////////////
  // DISPLACE postition
  float positionOffset = vec3(
    cos(luma * TWO_PI * 10.),
    sin(luma * TWO_PI * 10.),
    luma * -2.
  );
  newPos += displaceAmp * positionOffset; 
  ///////////////////////////

  ////////////////////////////////////////////////////////
  // SCALE individual meshes by checking center of shape vs. vertices
  vec3 meshLocalVertex = newPos.xyz - shapeCenter; 										// get vertex local to individual mesh center
  float scaleAdjust = luma * individualMeshScale;
  newPos.xyz += meshLocalVertex.xyz * scaleAdjust;
  ///////////////////////////

  ////////////////////////////////////////////////////////
  // SPREAD individual meshes with a multiplier
  newPos += shapeCenter * spreadScale;
  ///////////////////////////

  ////////////////////////////////////////////////////////
  // SPREAD individual meshes with a multiplier
  newPos *= globalScale;
  ///////////////////////////

  ////////////////////////////////////////////////////////
  // SET FINAL VERTEX POSITION
  vec4 finalPosition = projection * modelview * vec4(newPos, 1.);
  gl_Position = finalPosition;
  ///////////////////////////

  ////////////////////////////////////////////////////////
  // NOTES
  // working/default displacement technique: multiply with original transform matrix
  // vec4 finalPosition = transform * vec4(rotatedPos, 1.0);
  // alternate way of setting finalPosition
  // gl_Position = projection * modelview * vec4(newPos, 1);
}
