#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float spread;
uniform float darkness;

void main() {

    // Eskil's vignette
//    vec4 color = texture2D(texture, vertTexCoord.xy);
//    vec2 uv = ( vertTexCoord.xy - vec2( 0.5 ) ) * vec2( spread );
//    gl_FragColor = vec4( mix( color.rgb, vec3( 1.0 - darkness ), dot( uv, uv ) ), color.a );

     // alternative version from glfx.js
     // this one makes more dusty look (as opposed to burned)
     vec4 color = texture2D( texture, vertTexCoord.xy );
     float dist = distance( vertTexCoord.xy, vec2( 0.5 ) );
     color.rgb *= smoothstep( 0.8, spread * 0.799, dist * ( darkness + spread ) );
     gl_FragColor = color;

}
