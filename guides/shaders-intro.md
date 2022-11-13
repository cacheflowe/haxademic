# Learning GLSL

## What are Shaders

Shaders are relatively small programs, most-commonly written with the GLSL language. These programs run on your computer's GPU, rather than its CPU, which is where most other code executes (JavaScript, Python, c++). 

While shaders were built to draw real-time graphics, they're now used for many modern programming purposes. 

Shader programs are fast and efficient at manipulating collections of _pixel_ and _3d mesh_ data, because they run in _parallel_ at large scale. However, this performance comes at a cost of complexity for people new to the language and tools. I consider GLSL to be a bit like Functional Programming: it's very powerful, but very terse, making it less approachable than more [imperative](https://www.youtube.com/watch?v=E7Fbf7R3x6I) programming. 

## Why learn shaders?

Once you gain your footing, shaders can become a powerful and indispensible tool for creating high-performance and portable graphics operations that can travel with you across frameworks, programming languages and operating systems.



## Use cases

* [Generative graphics](https://www.shadertoy.com) in [2d](https://www.shadertoy.com/view/lsjBRD) and [3d](https://www.shadertoy.com/view/XdGGzw) (via raymarching)
* Post-processing effects and graphical operations (like real-time Photoshop filters)
  * [Shadertoy example](https://www.shadertoy.com/view/MdffDS)
  * [PIXI.js example](https://filters.pixijs.download/main/docs/index.html)
  * [THREE.js example](https://threejs.org/docs/#examples/en/postprocessing/EffectComposer) via EffectComposer
  * [Cacheflowe's shaders for Processing](https://github.com/cacheflowe/haxademic/tree/master/data/haxademic/shaders/filters)
* Double buffer, or Ping-pong effects for:
  * [Feedback effects](https://www.shadertoy.com/view/4syyDK)
  * [Frame-differencing](https://www.instagram.com/p/By-jx8ZlDVo/) for motion-detection
* Simulations:
  * [GPGPU particles](https://cacheflowe.github.io/haxademic.js/demo/#three-scene-gpu-particles-noise), where each particle's position & other properties are stored in pixel data and updated via a shader
  * [Physarum](https://www.shadertoy.com/view/tlKGDh)
* Fast parallel processing of large data sets (though getting data in & out of the GPU can be a major bottleneck)
  * Machine Learning

## Some essential facts about GLSL

* The language spec itself is fairly small, and is more-or-less a very simplified [c++](https://www.khronos.org/opengl/wiki/Core_Language_(GLSL))
* The native coordinate system is *normalized*, meaning 0-1 on both axis. A coordinate of `0, 0` is at the **bottom-left**, unlike most drawing contexts, where `0, 0` would be top-left or world center. Working with normalized coordinates is really nice most of the time, because the size of your canvas might not be known to you. But you still need to think about these coordinates relative to your canvas's aspect ratio. A coordinate of `1, 1` might actually mean `1024, 512`.
  * This normalized coordinate system is also referred to in shader world as **UV coordinates**
  * Get used to working with normalized numbers (0-1) for both coordinates and color components. In GLSL, black is  `vec3(0., 0., 0.)` and white is `vec3(1., 1., 1.)`
* The two most common types of shaders are **fragment** and **vertex** shaders. You need one of each to draw to the screen, but many environments like Shadertoy abstract away the vertex shader and allow you to just use the fragment shader as a 2d canvas.
  * When getting into drawing 3d meshes, vertex shaders become useful for:
    * Shading/texturing your mesh dynamically
    * Displacing/deforming the mesh vertices
  * There are other types of shaders, but they're not as universally supported, and are more advanced:
    * Geometry shaders
    * Tellesation shaders
    * Compute shaders
* Your program runs on all pixels at the same time. 800x450 = 360,000 executions of your program in parallel!
  * [NVIDIA: Adam and Jamie Paint the Mona Lisa in 80 Milliseconds](https://www.youtube.com/watch?v=WmW6SD-EHVY)
* The most common data types are: `int`, `float`, `vec2`, `vec3`, `vec4`, and `sampler2D`
* `float` values **ALWAYS NEED A DECIMAL POINT** when defined, otherwise it's an `int`, which doesn't work if your function parameter, variable, or mathematical operation expects a `float`. This will bite you many times until you (almost) always to remember to put a decimal point after your number.
* `for()` loops are expensive
* Remapping the coordinate space ([domain warping](https://iquilezles.org/articles/warp/)) is *very* common - with generative shaders you do this all the time. "What if this pixel was actually over here...?"
* There aren't traditional drawing tools that you find in p5js like `rect(x, y, w, h)` - almost all calculations are distance-based, and require a very different approach to drawing. This is where **distance functions** come into play. These are difficult to approach, but become very powerful.
* `Uniforms` - common input variables from the CPU, that are available for all pixels (and vertices), while local variables are specific to just this pixel.
* Different versions of OpenGL and GLSL will provide a slightly different language API.
* Noise can be generated with code, but also might be faster when sampled from a texture, which is a common technique.
  * [GLSL Noise Functions](https://gist.github.com/patriciogonzalezvivo/670c22f3966e662d2f83)

### How to get started?

Step 1: Check out the following interactive tutorials:

* [Fragment Foundry by @hughsk](https://hughsk.io/fragment-foundry/)
* [The Book of Shaders](https://thebookofshaders.com/)

Step 2: If you get past the first two, here are some more great interactive tutorials

* [28 GLSL tutorials @ Shadertoy](https://www.shadertoy.com/view/Md23DV)
* [StackGL Shader School](https://github.com/stackgl/shader-school)

Step 3: Start reading other developers' shader code on ShaderToy!

### Learn how shaders work in:

p5js
* https://itp-xstory.github.io/p5js-shaders/
* https://github.com/aferriss/p5jsShaderExamples
* https://twitter.com/adamferriss/status/1482140602573606912

THREE.js
* [An introdcution to shaders, part II](https://aerotwist.com/tutorials/an-introduction-to-shaders-part-2/) by Aerotwist
* [Three.js and Shadertoy](https://threejs.org/manual/#en/shadertoy)
* [Cacheflowe's shader background](https://cacheflowe.github.io/haxademic.js/demo/#three-scene-fbo-background)

Shadertoy
* https://www.shadertoy.com/user/cacheflowe

PIXI.js
* https://cacheflowe.github.io/haxademic.js/demo/#pixi-stage


### "Beginner" articles

* [Post-Processing Distortion Effects](https://blog.en.uwa4d.com/2022/10/11/post-processing-in-depth-camera-distortion-effects/)
* [Basic fragment shader distortions](https://www.youtube.com/watch?v=DH1KqXQvICQ)
* [A Beginner's Guide to Coding Graphics Shaders](https://gamedevelopment.tutsplus.com/tutorials/a-beginners-guide-to-coding-graphics-shaders--cms-23313)
* [3D Game Shaders For Beginners](https://github.com/lettier/3d-game-shaders-for-beginners)
* [Intro to Compute Shaders (workshop for Unity)](https://paprika.studio/workshops/compute/index.html)

### Raymarching (advanced technique for the brave)

* [[SH17C] Raymarching tutorial](https://www.shadertoy.com/view/4dSfRc)
* [Raymarching in Raymarching](https://www.shadertoy.com/view/wlSGWy)
* [ray marching (with THREE.js)](http://barradeau.com/blog/?p=575)
* [Ray Marching and Signed Distance Functions](http://jamie-wong.com/2016/07/15/ray-marching-signed-distance-functions/)
* [Ray Marching and Signed Distance Functions](https://michaelwalczyk.com/blog-ray-marching.html)
* [Simple Raymarching Example Scene](https://www.shadertoy.com/view/wd2SR3)
* [SDF Tutorial 1 : box & balloon](https://www.shadertoy.com/view/Xl2XWt)
* [Getting started with shaders: signed distance functions!](https://jvns.ca/blog/2020/03/15/writing-shaders-with-signed-distance-functions/)

### Addendum

* [Shader Tutorials By XorDev](https://www.getrevue.co/profile/xordev)
* [Comparison of shader languages](https://alain.xyz/blog/a-review-of-shader-languages)
* [NES PPU](https://ultimatepopculture.fandom.com/wiki/Picture_Processing_Unit)
* [first examples of 3d graphics](https://vimeo.com/16292363)
