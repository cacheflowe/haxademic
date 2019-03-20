// from @flockaroo: https://www.shadertoy.com/view/MsGSRd
// and: https://www.shadertoy.com/view/tdlSDl

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 texOffset;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float ambient = 4.0;   // 0.3 - 5.0
uniform float gradAmp = 1.0;   // 0.3 - 5.0
uniform float gradBlur = 1.0;  // 0.5 - 3.0
uniform float specAmp = 1.5;   // 0.5 - 1.5
uniform float diffDark = 0.5;  // 0.1 - 1.0

float getVal(vec2 uv) {
    // return length(texture(texture,uv).xyz);
    return dot(texture(texture,uv).xyz, vec3(0.299, 0.587, 0.114));
}

vec2 getGrad(vec2 uv,float delta) {
    vec2 d=vec2(delta,0);
    return vec2(
        getVal(uv+d.xy)-getVal(uv-d.xy),
        getVal(uv+d.yx)-getVal(uv-d.yx)
    )/delta;
}

void main() {
    vec2 uv = vertTexCoord.xy;
    vec4 texColor = texture2D(texture, uv);
    vec2 resolution = vec2(1./texOffset.x, 1./texOffset.y);

    // get original color
    vec3 n = vec3(getGrad(uv, 1.0/resolution.y * gradBlur), resolution.y * gradAmp);
    // n *= n;
    n = normalize(n);
    // gl_FragColor = vec4(n,1);	// show debug
    vec3 light = normalize(vec3(1,1,ambient));
    float diff = clamp(dot(n,light), diffDark, 1.0);
    float spec = clamp(dot(reflect(light,n),vec3(0,0,-1)),0.0,1.0);
    spec = pow(spec,36.0) * specAmp;
	  gl_FragColor = vec4((texColor * vec4(diff) + vec4(spec)).rgb, texColor.a);  // alpha was affected, so use original
}
