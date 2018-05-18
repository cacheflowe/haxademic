#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float angle;
uniform float scale;
uniform vec2 center;
uniform vec2 tSize;


float pattern() {
    float s = sin( angle );
    float c = cos( angle );
    vec2 tex = vertTexCoord.xy * tSize - center;
    vec2 point = vec2( c * tex.x - s * tex.y, s * tex.x + c * tex.y ) * scale;
    return ( sin( point.x ) * sin( point.y ) ) * 4.0;
}
void main() {
    vec4 color = texture2D( texture, vertTexCoord.xy );
    float average = ( color.r + color.g + color.b ) / 3.0;
    gl_FragColor = vec4( vec3( average * 10.0 - 5.0 + pattern() ), color.a );
}