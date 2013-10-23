#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float aperture;

const float PI = 3.1415926535;

void main() {
    float apertureHalf = 0.5 * aperture * (PI / 180.0);
    
    // This factor ajusts the coordinates in the case that
    // the aperture angle is less than 180 degrees, in which
    // case the area displayed is not the entire half-sphere.
    float maxFactor = sin(apertureHalf);
    
    vec2 pos = 2.0 * vertTexCoord.st - 1.0;
    
    float l = length(pos);
    if (l > 1.0) {
        gl_FragColor = vec4(0, 0, 0, 1);
    } else {
        float x = maxFactor * pos.x;
        float y = maxFactor * pos.y;
        
        float n = length(vec2(x, y));
        
        float z = sqrt(1.0 - n * n);
        
        float r = atan(n, z) / PI;
        
        float phi = atan(y, x);
        
        float u = r * cos(phi) + 0.5;
        float v = r * sin(phi) + 0.5;
        
        gl_FragColor = texture2D(texture, vec2(u, v)) * vertColor;
    }
}