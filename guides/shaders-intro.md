# <span>Learning GLSL</span>

### Why learn shaders?

Shaders, (most-commonly) written with GLSL, are an increasingly universal layer of interactive graphics toolkits. Built into the OpenGL spec, shaders are little programs that run on your computer's GPU for a variety of purposes. As the language is built for the GPU (most of the code you normally write executes on the CPU), they're very fast at manipulating grids of _pixel_ and _3d mesh_ data (amongst other use-cases). However, this performance comes at a cost of weirdness and complexity for the uninitiated. I consider GLSL to be a bit like Functional Programming: it's very powerful, but very terse, making it less approachable than more [imperative](https://www.youtube.com/watch?v=E7Fbf7R3x6I) types of programming. The good thing is that once you gain your footing, shaders can become an indispensible means of creating high-performance graphics operations that can travel with you across frameworks, languages and operating systems.

### Use cases

Shader programs and the GPUs that run them are traditionally associated with drawing imagery to your screen. This technology emerged from the video game and graphics worlds, with fragmented ancestry going back to PPUs (https://ultimatepopculture.fandom.com/wiki/Picture_Processing_Unit) in Nintendo systems, all the way back to the [first examples of 3d graphics](https://vimeo.com/16292363) in the 1970s. Their original use was to assist in the *shading* of 3d meshes, providing lighting and materials calculations for the triangulated surfaces of volumetric models. This technology has proven to be very powerful and efficient for other purposes in graphical contexts, like:

* [Generative graphics](https://www.shadertoy.com) in [2d](https://www.shadertoy.com/view/XdGGzw) and [3d](https://www.shadertoy.com/view/XdGGzw) (via raymarching)
* Post-processing effects (think real-time Photoshop filters)
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

* The language spec itself is fairly small, and is more-or-less a smiplified c++
* The native coordinate system is normalized, meaning 0-1 on both axis. A coordinate of `0, 0` is the **bottom-left**, unlike most drawing contexts, where that would be top-left or world center. This is actually really nice, because the size of your canvas doesn't matter, however you need to think of things in a relative or responsive way
* The two most common types of shaders are **fragment** and **vertex** shaders. You need both to draw to the screen, but many environments like Shadertoy remove the need to think about the vertices, and allow you to use the fragement shader as a 2d canvas.
  * When getting into drawing 3d meshes, vertex shaders become useful for:
    * Shading/texturing your mesh dynamically
    * Displacing/deforming the mesh vertices
  * There are other types of shaders, but they're not as universally supported, and are more advanced:
    * Geometry shaders
    * Tellesation shaders
    * Compute shaders
* TODO: More to come!!!

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

### Raymarching dark arts for the brave

* [[SH17C] Raymarching tutorial](https://www.shadertoy.com/view/4dSfRc)
* [Raymarching in Raymarching](https://www.shadertoy.com/view/wlSGWy)
* [ray marching (with THREE.js)](http://barradeau.com/blog/?p=575)
* [Ray Marching and Signed Distance Functions](http://jamie-wong.com/2016/07/15/ray-marching-signed-distance-functions/)
* [Ray Marching and Signed Distance Functions](https://michaelwalczyk.com/blog-ray-marching.html)
* [Simple Raymarching Example Scene](https://www.shadertoy.com/view/wd2SR3)
* [SDF Tutorial 1 : box & balloon](https://www.shadertoy.com/view/Xl2XWt)
* [Getting started with shaders: signed distance functions!](https://jvns.ca/blog/2020/03/15/writing-shaders-with-signed-distance-functions/)