// from Poersch @ https://forum.processing.org/two/discussion/2153/how-to-render-z-buffer-depth-pass-image-of-a-3d-scene

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_COLOR_SHADER

// depth map
uniform vec4 farColor = vec4(1.0, 1.0, 1.0, 1.0);
uniform vec4 nearColor = vec4(0.0, 0.0, 0.0, 1.0);
uniform float near = 0.0;
uniform float far = 1000.0;

varying vec4 vertColor;

void main() {
    gl_FragColor = mix(nearColor, farColor, smoothstep(near, far, gl_FragCoord.z / gl_FragCoord.w));
}
