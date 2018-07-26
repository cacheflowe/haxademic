#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform sampler2D textureMap;
uniform vec2 textureOffset = vec2(0.);

varying vec4 vertColor;
varying vec4 vertTexCoord;
varying vec3 vert;
varying vec3 norm;

vec3 lightDir = vec3(1., 0., 0.);
vec3 lightCol = vec3(1., 1., 1.);
vec3 lightAmbient = vec3(0., 0., 0.);
vec3 materialColor = vec3(1., 1., 1.);

// mapping with attempt at lights
// void main() {
//   // http://www.opengl-tutorial.org/beginners-tutorials/tutorial-8-basic-shading/#vertex-normals
//   // float cosTheta = dot( norm, lightDir );
//   // diffuse lighting
//   // let's look at this next: https://github.com/stackgl/glsl-lighting-walkthrough
//   float cosTheta = clamp( dot( norm, lightDir ), 0, 1 );
//   vec3 materialColor = texture2D(textureMap, fract(vertTexCoord.st + textureOffset)).rgb;
//   vec3 col = lightAmbient + lightCol * materialColor * cosTheta;// / (dist*dist);

//   gl_FragColor = vec4(col, 1.0);
// }

// BASIC MAPPING:
// void main() {
//   gl_FragColor = texture2D(textureMap, fract(vertTexCoord.st + textureOffset)); // fract to repeat with offset
// }

// MULTIPLY WITH DISPLACEMENT TEXTURE:
void main() {
  vec3 materialColor = texture2D(textureMap, fract(vertTexCoord.xy + textureOffset)).rgb;
  vec3 displaceColor = texture2D(texture, fract(vertTexCoord.xy)).rgb;
  gl_FragColor = vec4(materialColor * displaceColor, 1.0);
}
