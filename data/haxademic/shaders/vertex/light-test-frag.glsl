#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_LIGHT_SHADER

varying vec4 vertColor;
varying vec3 ecNormal;
varying vec3 vert;

uniform vec3 lightDir = vec3(0., 0.5, 1.);
uniform vec3 lightCol = vec3(0., 1., 0.);
uniform vec3 lightAmbient = vec3(0.2, 0., 0.4);
uniform vec3 materialColor = vec3(0.6, 0.2, 0.8);
uniform float materialShininess = 180.;


void main() {
  vec3 direction = normalize(lightDir);
  vec3 normal = normalize(ecNormal);
  float intensity = max(0.0, dot(direction, normal));

  // http://www.opengl-tutorial.org/beginners-tutorials/tutorial-8-basic-shading/#vertex-normals
  // float cosTheta = dot( norm, lightDir );
  // diffuse lighting
  // let's look at this next: https://github.com/stackgl/glsl-lighting-walkthrough
  float cosTheta = clamp( dot( normal, lightDir ), 0, 1 );
  float specularCoefficient = pow(cosTheta, materialShininess);
  // float dist = vert.z;
  vec3 col = lightAmbient + lightCol * materialColor * cosTheta + specularCoefficient;// / (dist*dist);

  gl_FragColor = vec4(col, 1.0);

  // gl_FragColor = vec4(intensity, intensity, intensity, 1) * vertColor;
}