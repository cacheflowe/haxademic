#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

// uniform sampler2D texture;
uniform float time;

varying vec4 vertColor;
varying vec4 vertTexCoord;
varying vec3 vert;
varying vec3 norm;

uniform vec3 lightDir = vec3(0., 0., 1.);
uniform vec3 lightCol = vec3(1., 1., 1.);
uniform vec3 lightAmbient = vec3(0., 0., 0.);

void main() {
  // generative material color
  float vertY = vert.y + 10. * sin(vert.x * 0.03 + time);
  float stripes = 0.5 + 0.5 * sin(vertY);
  vec3 materialColor = vec3(stripes);

  materialColor = vertColor.rgb;

  // http://www.opengl-tutorial.org/beginners-tutorials/tutorial-8-basic-shading/#vertex-normals
  // float cosTheta = dot( norm, lightDir );
  // diffuse lighting
  // let's look at this next: https://github.com/stackgl/glsl-lighting-walkthrough
  float cosTheta = clamp( dot( norm, lightDir ), 0, 1 );
  float dist = vert.z;
  vec3 col = lightAmbient + lightCol * materialColor * cosTheta;// / (dist*dist);

  gl_FragColor = vec4(vertColor.rgb, 1.0);
  // gl_FragColor = texture2D(texture, vertTexCoord.st) * vertColor;
}
