# Processing built-in PShader uniforms

Fragment shaders:

* `uniform sampler2D texture` - The texture (PGraphics) that the shader is being applied to
* `varying vec4 vertTexCoord` - Normalized UV coordinates (0-1 from bottom-left corner)
* `uniform vec2 texOffset` - The size of a pixel, mapped to normalized `vertTexCoord` coordinates. If original texture is 1000px wide, `texOffset.x` is 1/1000.
* `varying vec4 vertColor` - Original vertex color, supplied by Processing draw calls (or PShape)

# GLSL Conversion notes

## Resolution correction

* Replicate Shadertoy's iResolution with: `vec2 resolution = vec2(1./texOffset.x, 1./texOffset.y);`
* Correct aspect ratio with:
```
  vec2 uv = vertTexCoord.xy - vec2(.5,.5);
  uv *= texOffset.y/texOffset.x;
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
