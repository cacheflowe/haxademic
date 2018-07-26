// from: http://mrdoob.github.io/three.js/examples/js/shaders/HorizontalBlurShader.js
// @author zz85 / http://www.lab4games.net/zz85/blog
//
// Two pass Gaussian blur filter (horizontal and vertical blur shaders)
// - described in http://www.gamerendering.com/2008/10/11/gaussian-blur-filter-shader/
//   and used in http://www.cake23.de/traveling-wavefronts-lit-up.html
//
// - 9 samples per pass
// - standard deviation 2.7
// - "h" and "v" parameters should be set to "1 / width" and "1 / height"

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float h;

void main() {
    vec4 sum = vec4( 0.0 );
    sum += texture2D( texture, vec2( vertTexCoord.x - 4.0 * h, vertTexCoord.y ) ) * 0.051;
    sum += texture2D( texture, vec2( vertTexCoord.x - 3.0 * h, vertTexCoord.y ) ) * 0.0918;
    sum += texture2D( texture, vec2( vertTexCoord.x - 2.0 * h, vertTexCoord.y ) ) * 0.12245;
    sum += texture2D( texture, vec2( vertTexCoord.x - 1.0 * h, vertTexCoord.y ) ) * 0.1531;
    sum += texture2D( texture, vec2( vertTexCoord.x, vertTexCoord.y ) ) * 0.1633;
    sum += texture2D( texture, vec2( vertTexCoord.x + 1.0 * h, vertTexCoord.y ) ) * 0.1531;
    sum += texture2D( texture, vec2( vertTexCoord.x + 2.0 * h, vertTexCoord.y ) ) * 0.12245;
    sum += texture2D( texture, vec2( vertTexCoord.x + 3.0 * h, vertTexCoord.y ) ) * 0.0918;
    sum += texture2D( texture, vec2( vertTexCoord.x + 4.0 * h, vertTexCoord.y ) ) * 0.051;
    gl_FragColor = sum;
}