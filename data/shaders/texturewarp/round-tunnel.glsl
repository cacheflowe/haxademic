// Created by inigo quilez - iq/2015
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
// https://www.shadertoy.com/view/ltXGW4

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

uniform sampler2D textureInput;


void main()
{
	vec2 p = -vertTexCoord.xy + 0.5;
    gl_FragColor.w = length(p);
    gl_FragColor = texture2D( textureInput, vec2(atan(p.y,p.x), 0.2/gl_FragColor.w)+time )*gl_FragColor.w;
}
