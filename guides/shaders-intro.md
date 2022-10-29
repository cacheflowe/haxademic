# <span>Learning GLSL</span>

### Why learn shaders?

Shaders, (most-commonly) written with GLSL (OpenGL Shader Language), are an increasingly ubiquitous part of interactive graphics toolkits. Built into the OpenGL spec, shaders are (usually small) programs that run on your computer's GPU for a variety of (usually graphical) purposes. The GLSL language is built for the GPU, while most of the code you normally write (JavaScript, Python, c++) executes on the CPU. Shader programs are very fast at manipulating grids of _pixel_ and _3d mesh_ data (amongst other use-cases), because they run at large scale in parallel. However, this performance comes at a cost of weirdness and complexity for people new to the language and tools. I consider GLSL to be a bit like Functional Programming: it's very powerful, but very terse, making it less approachable than more [imperative](https://www.youtube.com/watch?v=E7Fbf7R3x6I) types of programming. The good thing is that once you gain your footing, shaders can become an indispensible means of creating high-performance graphics operations that can travel with you across frameworks, languages and operating systems.

### Use cases

Shader programs and the GPUs that run them are traditionally associated with drawing imagery to your screen. This technology rapidly evolved in the video game industry, with ancestors of the modern GPU like the [PPUs](https://ultimatepopculture.fandom.com/wiki/Picture_Processing_Unit) in Nintendo systems, all the way back to the [first examples of 3d graphics](https://vimeo.com/16292363) in the 1970s. Their original use was to assist in the *shading* of 3d meshes, providing lighting and materials calculations for the triangulated surfaces of volumetric models. This technology has proven to be very powerful and efficient for other purposes in graphical contexts, like:

* [Generative graphics](https://www.shadertoy.com) in [2d](https://www.shadertoy.com/view/lsjBRD) and [3d](https://www.shadertoy.com/view/XdGGzw) (via raymarching)
* Post-processing effects (like real-time Photoshop filters)
  * [Shadertoy example](https://www.shadertoy.com/view/MdffDS)
  * [PIXI.js example](https://filters.pixijs.download/main/docs/index.html)
  * [THREE.js example](https://threejs.org/docs/#examples/en/postprocessing/EffectComposer) via EffectComposer
  * [Cacheflowe's shaders for Processing](https://github.com/cacheflowe/haxademic/tree/master/data/haxademic/shaders/filters)
* Double buffer, or Ping-pong effects for:
  * [Feedback effects](https://www.shadertoy.com/view/4syyDK)
  * [Frame-differencing](https://www.instagram.com/p/By-jx8ZlDVo/) for motion-detection (simple computer vision steps)
* Simulations:
  * [GPGPU particles](https://cacheflowe.github.io/haxademic.js/demo/#three-scene-gpu-particles-noise), where each particle's position & other properties are stored in pixel data and updated via a shader
* Fast parallel processing of large data sets (though getting data in & out of the GPU is a major bottleneck)
  * Machine Learning

### How to get started?

Check out the following interactive tutorials, and bear in mind some essential facts about GLSL:

* The language spec itself is fairly small, and is more-or-less a simplified [c++](https://www.khronos.org/opengl/wiki/Core_Language_(GLSL))
* The native coordinate system is *normalized*, meaning 0-1 on both axis. A coordinate of `0, 0` is the **bottom-left**, unlike most drawing contexts, where that would be top-left or world center. Normalized coordinates are really nice most of the time, because the size of your canvas might not be known to you, so you need to think about coordinates relative to your canvas's aspect ratio.
  * This normalized coordinate system is also referred to in shader world as your **UV coordinates**
  * Get used to working with normalized numbers (0-1) for both coordinates and color components. In GLSL, black is  and white is `vec3(1., 1., 1.)`
* The two most common types of shaders are **fragment** and **vertex** shaders. You need both to draw to the screen, but many environments like Shadertoy remove the need to think about vertices, and allow you to just use the fragment shader as a 2d canvas, and obscure the vertex shader.
  * When getting into drawing 3d meshes, vertex shaders become useful for:
    * Shading/texturing your mesh dynamically
    * Displacing/deforming the mesh vertices
  * There are other types of shaders, but they're not as universally supported, and are more advanced:
    * Geometry shaders
    * Tellesation shaders
    * Compute shaders
* Your program runs on all pixels at the same time. 800x450 = 360,000 executions of your program!
  * [NVIDIA: Adam and Jamie Paint the Mona Lisa in 80 Milliseconds](https://www.youtube.com/watch?v=WmW6SD-EHVY)
* `for()` loops are expensive
* Remapping the coordinate space ([domain warping](https://iquilezles.org/articles/warp/)) is *very* common - with generative shaders you do this all the time. "What if this pixel was actually over here...?"
* `float` values **ALWAYS NEED A DECIMAL POINT**, otherwise it's an `int`, which doesn't work if your function parameter, variable, or mathematical operation expects a `float`. This will bite you many times until you (almost) always to remember to put a decimal point after your number.
* Most common data types: `int`, `float`, `vec2`, `vec3`, `vec4`
* There aren't traditional drawing tools that you find in p5js like `rect(x, y, w, h)` - almost all calculations are distance-based, and require a very different approach to drawing. This is where **distance functions** come into play. These are difficult to approach, but become very powerful.
* `Uniforms` - common input variables from the CPU, that are available for all pixels (and vertices), while local variables are specific to just this pixel.
* Different versions of OpenGL and GLSL will provide a slightly different language API
* Noise can be generated with code, but also might be faster sampled from a texture!
  * [GLSL Noise Functions](https://gist.github.com/patriciogonzalezvivo/670c22f3966e662d2f83)

Essential interactive tutorials

* [Fragment Foundry by @hughsk](https://hughsk.io/fragment-foundry/)
* [The Book of Shaders](https://thebookofshaders.com/)

Some more great interactive tutorials

* [28 GLSL tutorials @ Shadertoy](https://www.shadertoy.com/view/Md23DV)
* [StackGL Shader School](https://github.com/stackgl/shader-school)

### See how shaders work in... 

p5js
* https://itp-xstory.github.io/p5js-shaders/
* https://github.com/aferriss/p5jsShaderExamples
* https://twitter.com/adamferriss/status/1482140602573606912

PIXI.js
* https://cacheflowe.github.io/haxademic.js/demo/#pixi-stage

THREE.js
* https://cacheflowe.github.io/haxademic.js/demo/#three-scene-fbo-background

Shadertoy
* https://www.shadertoy.com/user/cacheflowe


### "Beginner" articles

* [Post-Processing Distortion Effects](https://blog.en.uwa4d.com/2022/10/11/post-processing-in-depth-camera-distortion-effects/)
* [Basic fragment shader distortions](https://www.youtube.com/watch?v=DH1KqXQvICQ)
* [A Beginner's Guide to Coding Graphics Shaders](https://gamedevelopment.tutsplus.com/tutorials/a-beginners-guide-to-coding-graphics-shaders--cms-23313)
* [3D Game Shaders For Beginners](https://github.com/lettier/3d-game-shaders-for-beginners)
* [Intro to Compute Shaders (workshop for Unity)](https://paprika.studio/workshops/compute/index.html)

### Raymarching (dark arts for the brave)

* [[SH17C] Raymarching tutorial](https://www.shadertoy.com/view/4dSfRc)
* [Raymarching in Raymarching](https://www.shadertoy.com/view/wlSGWy)
* [ray marching (with THREE.js)](http://barradeau.com/blog/?p=575)
* [Ray Marching and Signed Distance Functions](http://jamie-wong.com/2016/07/15/ray-marching-signed-distance-functions/)
* [Ray Marching and Signed Distance Functions](https://michaelwalczyk.com/blog-ray-marching.html)
* [Simple Raymarching Example Scene](https://www.shadertoy.com/view/wd2SR3)
* [SDF Tutorial 1 : box & balloon](https://www.shadertoy.com/view/Xl2XWt)
* [Getting started with shaders: signed distance functions!](https://jvns.ca/blog/2020/03/15/writing-shaders-with-signed-distance-functions/)

### Addendum

* [Comparison of shader languages](https://alain.xyz/blog/a-review-of-shader-languages)




