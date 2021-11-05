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

void main() {
  // if there was a texture defined on the PShape, use it! 
  gl_FragColor = texture2D(texture, vVertTexCoord.xy); // * vVertColor;
  
  //////////////////////////////////////////////
  // If we want to just use the original color of the vertex, uncomment this:
  // gl_FragColor = vVertColor;
}
