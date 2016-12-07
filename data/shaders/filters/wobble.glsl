// Radial sin /cos displacement
// Ported from felixturner (http://airtight.cc/), which was ported from http://uglyhack.appspot.com/videofx/

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time;
uniform float speed;
uniform float strength;
uniform float size;

void main() {
  // vec2 uv = vertTexCoord.xy; // - vec2(.5,.5);
  vec2 p = vertTexCoord.xy;
  vec2 uv = vertTexCoord.xy - vec2(.5,.5);
  uv.x *= texOffset.y/texOffset.x;
  gl_FragColor = texture2D(
    texture,
    p + strength * vec2( cos(time*speed+length(uv*size)), sin(time*speed+length(uv*size) )
    )
  );
}
