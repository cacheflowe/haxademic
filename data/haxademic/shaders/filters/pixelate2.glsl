#ifdef GL_ES
precision highp float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float time = 0.;
uniform float divider = 0.2;

void main() {
    vec2 uv = vertTexCoord.xy;
    vec2 resolution = vec2(1./texOffset.x, 1./texOffset.y);
    vec3 tex = texture2D(texture, uv).rgg;
    float granularity = floor(divider * 10. + 5.);
    if (mod(granularity, 2.) > 0.) {
        granularity += 1.;
    }
    if (granularity > 0.0) {
        float dx = granularity / resolution.x;
        float dy = granularity / resolution.y;
        uv = vec2(dx*(floor(uv.x/dx) + 0.5),
                  dy*(floor(uv.y/dy) + 0.5));
		    gl_FragColor = vec4(texture2D(texture, uv).rgg, 1.);
    } else {
    	  gl_FragColor = vec4(tex,1.);
    }
}
