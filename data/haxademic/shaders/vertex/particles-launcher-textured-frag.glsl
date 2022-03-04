#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

varying vec4 vVertColor;
varying vec4 vVertTexCoord;
varying vec3 vVertNormal;
varying vec3 vVertex;
varying vec3 vNormal;
varying vec2 vSimulationUV;

uniform sampler2D texture;
uniform sampler2D textureParticle;
uniform sampler2D randomMap;
uniform sampler2D colorMap;
uniform int colorMapTints = 1;
uniform float baseAlpha = 1.;

float rand(vec2 co){
  return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main() {
  // tinted textured
  vec2 textureUV = vVertTexCoord.xy;
  textureUV.y = 1. - textureUV.y; // flip y
  vec4 textureColor = texture2D(textureParticle, textureUV);
  // vec4 tintColor = texture2D(colorMap, vSimulationUV);
  vec4 tintColor = texture2D(colorMap, vec2(vSimulationUV.x, rand(vSimulationUV))); // choose a random v coord because of court's 2 gradients
  vec4 finalColor = (colorMapTints == 1) ? textureColor * tintColor : textureColor;
  finalColor.a = textureColor.a * baseAlpha; 
  gl_FragColor = finalColor;

  // if there was a texture defined on the PShape, use it! 
  // vec4 randomColor = texture2D(randomMap, vSimulationUV);
  // if(randomColor.r < 0.33) {
  //   gl_FragColor = texture2D(texture1, vVertTexCoord.xy); // * vVertColor;
  // } else if(randomColor.r < 0.66) {
  //   gl_FragColor = texture2D(texture2, vVertTexCoord.xy); // * vVertColor;
  // } else {
  //   gl_FragColor = texture2D(texture3, vVertTexCoord.xy); // * vVertColor;
  // }

  //////////////////////////////////////////////
  // debug - make sure random color is correct
  // gl_FragColor = vec4(randomColor.r, randomColor.g, randomColor.b, 1.0);

  // alternate color look
  // float distFromCenter = distance(vec2(0.5), vVertTexCoord.xy) * 2.5;
  // distFromCenter = smoothstep(0.4, 0.9, distFromCenter);
  // gl_FragColor = vec4(randomColor.r, randomColor.g, randomColor.b, 1.0 - distFromCenter);
  
  // If we want to just use the original color of the vertex, uncomment this:
  // gl_FragColor = vVertColor;
}
