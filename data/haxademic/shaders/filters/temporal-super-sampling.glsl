#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D tex1;
uniform sampler2D tex2;
uniform sampler2D tex3;
uniform sampler2D tex4;
uniform sampler2D tex5;
uniform sampler2D tex6;
uniform sampler2D tex7;
uniform sampler2D tex8;
uniform sampler2D tex9;
uniform sampler2D tex10;
uniform sampler2D tex11;
uniform sampler2D tex12;

const float numSamplers = 12.;

void main() {
  vec2 uv = vertTexCoord.xy;

  gl_FragColor = (
    texture2D(tex1, vertTexCoord.xy) +
    texture2D(tex2, vertTexCoord.xy) +
    texture2D(tex3, vertTexCoord.xy) +
    texture2D(tex4, vertTexCoord.xy) +
    texture2D(tex5, vertTexCoord.xy) +
    texture2D(tex6, vertTexCoord.xy) +
    texture2D(tex7, vertTexCoord.xy) +
    texture2D(tex8, vertTexCoord.xy) +
    texture2D(tex9, vertTexCoord.xy) +
    texture2D(tex10, vertTexCoord.xy) +
    texture2D(tex11, vertTexCoord.xy) +
    texture2D(tex12, vertTexCoord.xy)
  ) / numSamplers;
}
