#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

varying vec4 vVertColor;
varying vec4 vVertTexCoord;
varying vec3 vVertNormal;
varying vec3 vVertex;
varying vec3 vNormal;

uniform sampler2D texture;
uniform int textureMode = 0;

uniform vec3 lightDir = vec3(0., 0.5, 1.);
uniform vec3 lightCol = vec3(0., 1., 0.);
uniform vec3 lightAmbient = vec3(0.2, 0., 0.4);
// uniform vec3 materialColor = vec3(0.6, 0.2, 0.8);
uniform float materialShininess = 2.;

void main() {
  //////////////////////////////////////////////
  // INFO & more research:
  // http://www.opengl-tutorial.org/beginners-tutorials/tutorial-8-basic-shading/#vertex-normals
  // let's look at this next: https://github.com/stackgl/glsl-lighting-walkthrough

  //////////////////////////////////////////////
  // get/calculate lighting attributes
  vec3 direction = normalize(lightDir);
  vec3 normal = normalize(vNormal);
  float intensity = max(0.0, dot(direction, vVertNormal));
  float cosTheta = clamp( dot( normal, direction ), 0, 1 );
  float specularCoefficient = pow(cosTheta, materialShininess);

  //////////////////////////////////////////////
  // Apply diffuse lighting
  // float dist = vVertex.z;
  // vec3 col = lightAmbient * lightCol * vVertColor.rgb * cosTheta + specularCoefficient;// / (dist*dist);
  vec3 col = vVertColor.rgb + lightAmbient * lightCol * cosTheta * specularCoefficient * intensity + specularCoefficient/5.;
  gl_FragColor = vec4(col, 1.);

  //////////////////////////////////////////////
  // if there was a texture defined on the PShape, use it! 
  if(textureMode == 1.) {
    gl_FragColor = texture2D(texture, vVertTexCoord.xy) * vVertColor;
    gl_FragColor.rgb += lightCol * cosTheta * specularCoefficient * intensity + specularCoefficient/5.; // add lighting to texture
    // gl_FragColor.a = 1.; // keep alpha for particle textures. maybe discard for 3d meshes
  }
  
  //////////////////////////////////////////////
  // If we want to just use the original color of the vertex, uncomment this:
  // gl_FragColor = vVertColor;
}
