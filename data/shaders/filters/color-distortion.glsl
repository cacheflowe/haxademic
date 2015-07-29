// from: https://www.shadertoy.com/view/XtlXzj


#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform float time;
uniform float amplitude;
varying vec4 vertColor;
varying vec4 vertTexCoord;

float terrain(float x) {
    //Used Shadershop
    return amplitude * ((sin( (sin( (x - -1.33) / 0.76 / 1.23 ) * 0.8 - 0.69) / 0.58 )) * (((((sin( (((x - -1.33) / 0.76 - -3.0) / 2.61 - -0.38) / 1.52 ) * 2.25) * (sin( (((x - -1.33) / 0.76 - -3.0) / 2.61 - -0.47) / 1.61 ) * 1.03))) * (sin( ((x - -1.33) / 0.76 - -3.0) / 2.61 / 0.44 ) * 1.48)) * 1.08)) * 0.78;
}

void main() {
    vec2 uv = vertTexCoord.xy;
    vec3 dist_texture = vec3(texture2D(texture, uv+(terrain((uv.y*20.)+(time*30.))/200.)).r,texture2D(texture, uv+(terrain((uv.y*22.)+(time*30.))/201.)).g,texture2D(texture, uv+(terrain((uv.y*14.)+(time*30.))/202.)).b);
    gl_FragColor = vec4(dist_texture,1.0);
}
