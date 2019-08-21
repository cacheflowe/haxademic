// started with basic LINES shader: https://processing.org/tutorials/pshader/

uniform mat4 transform;
uniform vec4 viewport;
uniform vec4 texMatrix;
uniform mat3 normalMatrix;

attribute vec4 vertex;
attribute vec4 position;
attribute vec4 color;
attribute vec4 direction;
attribute vec2 texCoord;

varying vec2 center;
varying vec2 normal;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float weight = 2.;
uniform sampler2D displacementMap;
uniform float displaceAmp = 1.;
uniform int sheet = 0;
uniform int yAxisOnly = 0;
uniform int time = 0;
uniform mat4 modelviewInv;

#define PROCESSING_LINE_SHADER

#define PI     3.14159265358
#define TWO_PI 6.28318530718

float rgbToGray(vec4 rgba) {
	const vec3 W = vec3(0.2125, 0.7154, 0.0721);
  return dot(rgba.xyz, W);
}

vec3 clipToWindow(vec4 clip, vec4 viewport) {
  vec3 dclip = clip.xyz / clip.w;
  vec2 xypos = (dclip.xy + vec2(1.0, 1.0)) * 0.5 * viewport.zw;
  return vec3(xypos, dclip.z * 0.5 + 0.5);
}

void main() {
  // copy incoming vertex position and alter/use the copy
  vec4 posUpdated = vertex;

  // get/pass colors
  vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
  vec4 texDisplace = texture2D( displacementMap, vertex.xy*300. ); // rgba color
  // texColor = vec4(0., 1., 0., 1.);
  vertColor = color;// texDisplace;

	// get displacement map color and map to displace x/y coords
  float luma = rgbToGray(texDisplace);
	float offsetX = cos(luma * TWO_PI);
	float offsetY = sin(luma * TWO_PI);
	posUpdated.x += vertex.x + displaceAmp * offsetX;
	posUpdated.y += vertex.y + displaceAmp * offsetY;
	vertColor = vec4(luma);

  // default line rendering shader code ------------------
  vec4 clip0 = transform * posUpdated;
  vec4 clip1 = clip0 + transform * vec4(direction.xyz, 0);
  float thickness = direction.w * weight;  // weight added by @cacheflowe

  // clip0.z = 10.;
  // clip1.z = 500.;
  vec3 win0 = clipToWindow(clip0, viewport);
  vec3 win1 = clipToWindow(clip1, viewport);
  vec2 tangent = win1.xy - win0.xy;

  normal = normalize(vec2(-tangent.y, tangent.x));
  vec2 offset = normal * thickness;
	// gl_Position = transform * vertex;
  gl_Position.xy = clip0.xy + offset.xy;
  gl_Position.zw = clip0.zw;

  // pass along vertex vertex
  center = (win0.xy + win1.xy) / 2.0;
  // end default line rendering shader code ------------------
}
