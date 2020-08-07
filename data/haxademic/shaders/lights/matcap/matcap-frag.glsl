precision mediump float;

uniform sampler2D matcap;
varying vec3 eyeNormal;

uniform float range=1.1;
void main() {
 vec2 uv = range*0.5*vec2(normalize(eyeNormal).xy)+vec2(0.5);
 gl_FragColor = texture2D(matcap,uv);   
}
