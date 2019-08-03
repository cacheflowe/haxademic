# Processing built-in PShader uniforms

Fragment shaders:

* `uniform sampler2D texture` - The texture (PGraphics) that the shader is being applied to
* `varying vec4 vertTexCoord` - Normalized UV coordinates (0-1 from bottom-left corner)
* `uniform vec2 texOffset` - The size of a pixel, mapped to normalized `vertTexCoord` coordinates. If original texture is 1000px wide, `texOffset.x` is 1/1000.
* `varying vec4 vertColor` - Original vertex color, supplied by Processing draw calls (or PShape)

# Snippets

Shader header, built-in varyings & uniforms
```
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform sampler2D texture;
uniform vec2 texOffset;
```

Pi
```
#define PI     3.14159265358
#define TWO_PI 6.28318530718
// #define TWO_PI (PI * 2.)
```

Centered UV w/square aspect ratio
```
vec2 uv = vertTexCoord.xy - 0.5;
uv.x *= texOffset.y / texOffset.x;		// Correct for aspect ratio
```

Angle to center
```
float rads = atan(uv.x, uv.y);
```

Rotate a UV coord
```
vec2 rotateCoord(vec2 uv, float rads) {
    uv *= mat2(cos(rads), sin(rads), -sin(rads), cos(rads));
    return uv;
}
```

Remap a number
```
float map(float value, float low1, float high1, float low2, float high2) {
   return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
}
```

Luminance
```
float luma(vec4 rgba) {
	const vec3 W = vec3(0.2125, 0.7154, 0.0721);
  return dot(rgba.xyz, W);
}
```

Saw, synced up with sin()
```
float saw(float rads) {
    rads += PI * 0.5; // sync oscillation up with sin()
    float percent = mod(rads, PI) / PI;
    float dir = sign(sin(rads));
    return dir * (2. * percent  - 1.);
}
```

Noise & random
```
// 2D Random
float random (in vec2 st) {
    return fract(sin(dot(st.xy,vec2(12.9898,78.233))) * 43758.5453123);
}

// 2D Noise based on Morgan McGuire @morgan3d
// https://www.shadertoy.com/view/4dS3Wd
float noise (in vec2 st) {
    vec2 i = floor(st);
    vec2 f = fract(st);
    float a = random(i);
    float b = random(i + vec2(1.0, 0.0));
    float c = random(i + vec2(0.0, 1.0));
    float d = random(i + vec2(1.0, 1.0));
    vec2 u = f*f*(3.0-2.0*f);
    return mix(a, b, u.x) +
            (c - a)* u.y * (1.0 - u.x) +
            (d - b) * u.x * u.y;
}
```

```
// hash based 3d value noise
float hash( float n )
{
    return fract(sin(n)*43758.5453);
}
// 3d noise from @iq
float noise( in vec3 x )
{
    vec3 p = floor(x);
    vec3 f = fract(x);

    f = f*f*(3.0-2.0*f);
    float n = p.x + p.y*57.0 + 113.0*p.z;
    return mix(mix(mix( hash(n+  0.0), hash(n+  1.0),f.x),
                   mix( hash(n+ 57.0), hash(n+ 58.0),f.x),f.y),
               mix(mix( hash(n+113.0), hash(n+114.0),f.x),
                   mix( hash(n+170.0), hash(n+171.0),f.x),f.y),f.z);
}
```

```
float snoise(vec3 uv, float res) {
	const vec3 s = vec3(1e0, 1e2, 1e3);
	uv *= res;
	vec3 uv0 = floor(mod(uv, res))*s;
	vec3 uv1 = floor(mod(uv+vec3(1.), res))*s;
	vec3 f = fract(uv); f = f*f*(3.0-2.0*f);
	vec4 v = vec4(uv0.x+uv0.y+uv0.z, uv1.x+uv0.y+uv0.z,
		      	  uv0.x+uv1.y+uv0.z, uv1.x+uv1.y+uv0.z);
	vec4 r = fract(sin(v*1e-1)*1e3);
	float r0 = mix(mix(r.x, r.y, f.x), mix(r.z, r.w, f.x), f.y);
	r = fract(sin((v + uv1.z - uv0.z)*1e-1)*1e3);
	float r1 = mix(mix(r.x, r.y, f.x), mix(r.z, r.w, f.x), f.y);
	return mix(r0, r1, f.z)*2.-1.;
}
```

Shapes
```
float triangle(vec2 p, float size) {
    vec2 q = abs(p);
    return max(q.x * 0.866025 + p.y * 0.5, -p.y * 0.5) - size * 0.5;
}

float hexagon(vec2 p, float radius) {
    vec2 q = abs(p);
    return max(abs(q.y), q.x * 0.866025 + q.y * 0.5) - radius;
}

float polygon(vec2 p, int vertices, float size) {
    float a = atan(p.x, p.y) + 0.2;
    float b = 6.28319 / float(vertices);
    return cos(floor(0.5 + a / b) * b - a) * length(p) - size;
}
```


# GLSL Conversion notes

## Resolution correction

* Replicate Shadertoy's iResolution with: `vec2 resolution = vec2(1./texOffset.x, 1./texOffset.y);`
* Correct aspect ratio with:
```
  vec2 uv = vertTexCoord.xy - vec2(.5,.5);
  uv *= texOffset.y / texOffset.x;
```
or
```
  vec2 uv = vertTexCoord.xy - 0.5;
  uv.x *= texOffset.y / texOffset.x;
```

## pixel position

----------------------

##### glsl.heroku

	vec2 p = -1.0 + 2.0 * gl_FragCoord.xy / iResolution.xy;

becomes:

    vec2 p = vertTexCoord.xy;


##### shadertoy

	vec2 uv = gl_FragCoord.xy/iResolution.xy;

becomes:

	vec2 uv = vertTexCoord.xy;

or:

    vec2 p = (-iResolution.xy + 2.0*fragCoord.xy)/iResolution.y;

becomes:

	vec2 p = -vertTexCoord.xy + 0.5;


## center

--------------------

	vec2 uv = gl_FragCoord.xy;

becomes:

	vec2 uv = vertTexCoord.xy - vec2(.5,.5);

## resolution

--------------------

##### shadertoy + glsl.heroku

	iResolution

becomes:

  vec2 resolution = vec2(1./texOffset.x, 1./texOffset.y);



### Shaders to convert:

* https://www.shadertoy.com/view/4sVSRd
* https://www.shadertoy.com/view/MlsXDr
* https://www.shadertoy.com/view/ltBXDd
* https://www.shadertoy.com/view/4scXWB
* https://www.shadertoy.com/view/XdGSW3
* https://www.shadertoy.com/view/4tlfRB
* https://www.shadertoy.com/view/XljBRh
* https://www.shadertoy.com/view/XtjfDy

* http://glslsandbox.com/e#38684.0
* http://glslsandbox.com/e#1419.0
* http://glslsandbox.com/e#1329.0
* http://glslsandbox.com/e#1200.0
* http://glslsandbox.com/e#1591.3
* http://glslsandbox.com/e#1692.0
* http://glslsandbox.com/e#2165.0
* http://glslsandbox.com/e#2234.1
* http://glslsandbox.com/e#3005.1
* http://glslsandbox.com/e#3849.0
* http://glslsandbox.com/e#4153.0
* http://glslsandbox.com/e#4154.0
* http://glslsandbox.com/e#4201.0
* http://glslsandbox.com/e#4196.0
* http://glslsandbox.com/e#4243.0
* http://glslsandbox.com/e#4242.1
* http://glslsandbox.com/e#4294.0
* http://glslsandbox.com/e#4346.0
* http://glslsandbox.com/e#4460.0
* http://glslsandbox.com/e#4646.0
* http://glslsandbox.com/e#4722.2
* http://glslsandbox.com/e#5254.1
* http://glslsandbox.com/e#5267.0
* http://glslsandbox.com/e#5246.0
* http://glslsandbox.com/e#5321.0
* http://glslsandbox.com/e#5398.8
* http://glslsandbox.com/e#5514.0
* http://glslsandbox.com/e#5359.8
* http://glslsandbox.com/e#5485.0
* http://glslsandbox.com/e#5611.0
* http://glslsandbox.com/e#5670.0
* http://glslsandbox.com/e#5664.1
* http://glslsandbox.com/e#5688.0
* http://glslsandbox.com/e#5654.17
* http://glslsandbox.com/e#5785.8
* http://glslsandbox.com/e#5985.0
* http://glslsandbox.com/e#6029.0
* http://glslsandbox.com/e#6042.5
* http://glslsandbox.com/e#6098.23
* http://glslsandbox.com/e#6304.0
* http://glslsandbox.com/e#6428.0
* http://glslsandbox.com/e#6416.0
* http://glslsandbox.com/e#6450.4
* http://glslsandbox.com/e#6550.0
* http://glslsandbox.com/e#6726.3
* http://glslsandbox.com/e#6808.0
* http://glslsandbox.com/e#6810.0
* http://glslsandbox.com/e#6786.0
* http://glslsandbox.com/e#6939.0
* http://glslsandbox.com/e#6972.1
* http://glslsandbox.com/e#7016.4
* http://glslsandbox.com/e#7022.0
* http://glslsandbox.com/e#7055.0
* http://glslsandbox.com/e#7083.10
* http://glslsandbox.com/e#7315.1
* http://glslsandbox.com/e#7070.6
* http://glslsandbox.com/e#7403.0
* http://glslsandbox.com/e#7405.0
* http://glslsandbox.com/e#7453.12
* http://glslsandbox.com/e#7520.0
* http://glslsandbox.com/e#7597.0
* http://glslsandbox.com/e#7600.1
* http://glslsandbox.com/e#7716.0
* http://glslsandbox.com/e#7734.0
* http://glslsandbox.com/e#7757.5
* http://glslsandbox.com/e#8034.0
* http://glslsandbox.com/e#7988.0
* http://glslsandbox.com/e#8010.0
*
