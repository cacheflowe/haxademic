// by cacheflowe
// leaves opaque pixels but forces them to a specific color. otherwise, pixels go transparent

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform vec3 color;

void main() {
    vec4 pixelCol = texture2D(texture, vertTexCoord.xy);
    if(pixelCol.a < 0.99) {
      gl_FragColor = vec4(vec3(1.), 0.);
    } else {
      gl_FragColor = vec4(color, 1.);
    }
}
