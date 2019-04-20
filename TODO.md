# TODO

## Art projects
  * Shatter model & triangles fall down (Sunflow final render)
  * Zaibiti
  	* Pi webcam? Is it possible?
  * AdaptBB poly lerp/feedback loop displacing a sheet
  * Sound in space. Raspberry Pis via wifi, attached to speakers. Split channels for more outputs
  * New video loops should have a soundtrack. use my tunes? look in sketch/render/avloops
  * Kinect history textures in 3d
  	* Kinect history point mesh history
  * Convert all webcam VFX apps (and old video filters) to BaseVideoFilter subclasses
  * Moire sphere/shader for MW prototying
  * Voice-activated color room: What Say Hue?
  * Grass cutout - laser cut w/Seied
  * Make a dmx/launchpad gif loop
  * Motion detection point-of-interest motion capture by small rectangles - figure out how to zoom out and create multiple zones
  * Turn client snake mirror into its own thing
  * Make a version of partycles with GPU particles from VFX code and ability to swap webcam instead of Kinect
	* Also, blob tracking VFX but sweet patterns inside the blobs
  
## HaxVisualTwo
  * Less kaleidoscope
  * Add new concepts for layout, rather than just relying on displacement & mask effects 
  * Categorize textures: 
    * Complex pattern
    * basic B&W
    * central shape
    * audioreactive?
  * BaseVideoFilter should have an optional need to create sourceBuffer - most don't need this extra buffer created
  * Extract HaxVisualTwo post-processing into its own reusable component?
  * Preset texture combos
  * Why don't any audio inputs besides ESS work anymore? (Beads, Minim)
  * Make PGraphics pool so every visual doesn't have it's own - this is way too heavy
  * Start moving all apps towards objects that can receive a PGraphics instance. decoupling from PApplet will help move visuals into HaxVisualTwo
  	* Houndstooth animation
  	* Circle Sphere
  	* Brim liski sphere
  	* ParticleBranchers
  	* SpaceCube
  	* Any other cool loop/render?!
  	
## Interphase
  * C.A.C.H.E. - Creative Adversarial Computer-Human Exchange
  * Make alternate z-space-scrolling sequencer grid 
  * Make all keyboard commands capital letters, so Caps lock toggles key commands between HaxVisual and Interphase 
  * Come up with a way to film. 2 phones? dual webcam? what about audio? does it need to run on the mac laptop for a/v capture?
  	* Capture Launchpads from above, A/V from video feed
  * When sequencers trigger, send an event in AppStore. this could trigger lights, HaxVisual events
  * Clean up basic music code
  * Map more functions to hardware controls
  * MIDI output for Ableton sync
  * Build a representation of the sequencer for on-screen display with HaxVisual
  * Use interphase beat timing rather than beat detection to make the next HaxVisual change
  * Store premade patterns for different beats/songs. Json?
  * build a touchscreen 8x16 interface
  * add a compressor to main output? audio needs to be squished
  * Make interphase more bangy & think about converting to be more spatial w/lighting

## Audio
  * Text to speech In Processing. Webview in Processing? Or web sockets to external browser? Vanilla Java?
  * Sphinx4 speech recognition
	* Copy / paste + Robot for tired hands
  * Test basic audio input. why is audio getting delayed after hours of running?
    * Why isn't Minim/Beads working on audio in anymore?
  * Turn off Beads audio input object output - this should not pass through
  * Split audio stepthrough rendering from Renderer.java, which should just save movies. MIDIStepthrough renderer is a good example of splitting
  * Make demos for rendering at a specific bpm

## MIDI:
  * Add midi input to prefsSliders
  * Move midibus instance to MidiState (now MidiDevice)
  * InputTrigger should merge MIDI buttons and CC controls, just like OSC commands
  * Check MIDI rendering now that MIDI code has been revamped

## GLSL
  * 32-bit packing
  	* https://stackoverflow.com/questions/18453302/how-do-you-pack-one-32bit-int-into-4-8bit-ints-in-glsl-webgl
  	* http://www.ozone3d.net/blogs/lab/20080604/glsl-float-to-rgba8-encoder/
  	* https://community.khronos.org/t/pack-more-than-4-components-into-rgba-32-texture-in-vertex-shader/72945/2
  	* https://forum.processing.org/two/discussion/17629/how-to-get-round-using-16-bit-image-buffers-shadertoy-question
  * Wrap up GLSL transitions collection and make a common interface
  * Fix up GPU particle launcher to store colors per-particle
  * Convert Orbit noise: https://www.shadertoy.com/view/4t3yDn
  * Convert some postprocessing effects: https://github.com/libretro/glsl-shaders
  * Figure out `particle-displace-curl.glsl`
  * Add `feedback-map.glsl` & `feedback-radial.glsl` shader wrapper classes
  * Build a post-processing library: https://github.com/processing/processing/wiki/Library-Basics
  * Demo_VertexShader_NoiseTest_WIP
  	* make a trexture that does audioreactive stripes emitting from the top down
  * Delete old displacement shaders since we have a new wrapper object
  * Optical flow glsl port - ported glsl file (with 2nd reference) is ready to fix up
  * Notes from book
	* Shader uniform updates should check if dirty before sending to shader
    * Look into structs and output from a fragment shader
    * Look at vertex attributes - Is that an array of values?
    	* Example here: https://github.com/gohai/processing-glvideo/blob/master/examples/VideoMappingWithShader/VideoMappingWithShader.pde
    * Data exits vertex processing by user-defined varying variables
    * gl_Position can be null and not be rendered?
    * gl_PointSize can be written to in vertex shader
    * gl_fragCoord.z has depth data for the fragment?!
    * Build a basic demo that uses vertex depth to fade to a color- probably already have something similar
    * Does textureSize(Sampler2D) give us the texture size???
    * Doing calculations in the vertex shader should always be faster than the fragment shader, since there are fewer vertices than fragments.
    * Use the ‘discard’ keyword to *not* update a fragment, anywhere in a fragment shader.
  * GPU Particles
	* http://barradeau.com/blog/?p=621
	* Look into Processing shader types - is there a point shader? yes - https://processing.org/tutorials/pshader/
	* https://codeanticode.wordpress.com/2014/05/08/shader_api_in_processing_2/
	* http://atduskgreg.github.io/Processing-Shader-Examples/
	* http://www.beautifulseams.com/2013/04/30/shaders/
	* https://github.com/codeanticode/pshader-tutorials

## net
  * Should Screenshot in DashboardPoster be it's own app/process, like the CrashMonitor?
  * PrefsSliders should also serve up a web server that has just those sliders. .json config maybe?
  * WebServer and SocketServer should be more stylistically similar. See PORT in WebServer, and DEBUG static boolean - should be passed in?
  * Replace JavaWebsocket with Jetty WebSocket server??

## Demos:
  * Can lines be rendered in Joons?
  * Render a video with effects, using BrimVFX as example
  * Replicate independent 40k shape demo from Processing examples - update with GPU
  * Make a little planet generator with icosahedron deformation and colorized texture map of depth
    * https://github.com/ashima/webgl-noise/wiki
  * Distill more demos for `core` code
  * Make a texture map by drawing optical flow to ellipses and blurring

* PShape & PShapeUtil:
  * sine-distorted 3d models
  * Scrolling feedback texture mapped to a model with lighting
  * Move around a sphere (advice from EdanKwan: Generate the vector field on a sphere. Cross the 3d noise with the surface normal. Make the items move around on the surface of the sphere, set the initial direction of the items, move them with the noise field, normalize its position (assuming the sphere origin is center with 1 radius).
  * Make a vertex shader that does this to a sheet: https://www.google.com/search?ei=z9e3Wo6iOdG45gKIyZrgAQ&q=graph+z%3Dsin%28y*0.1%29*sin%28x%29&oq=graph+z%3Dsin%28y*0.1%29*sin%28x%29&gs_l=psy-ab.3...11324.12507.0.13684.4.4.0.0.0.0.72.277.4.4.0....0...1c.1.64.psy-ab..0.1.69...0i8i30k1.0.tqpD6rWz4Hk
  * PShapeUtil: Build a demo for changing the x/z registration/center point so models that sit on the ground can spin from the right origin (controlP5 ?)
  * Extrude2dPoints should be able to return a PShape
    * Shapes.java should have a filled or open extruded polygon generator method
	* PShapeSolid should loop properly through children like PShapeUtil does now
  * GIVE A MODEL FRONT & BACK TEXTURES! do wrapping the same way as now, but a different texture if negative z (or normal?)
  * 3d lighting w/glsl
    * Demo_VertexShader_NoiseTest_WIP
    * Demo_VertexShader_Fattener

## General / tools
  * Merge Windows & normal SystemUtil - make sure Java-killing code works on both OS X & Windows
  * Add .txt event log to PAppletHax
  * Test importing a Java class into a Processing IDE project
  * How can we optimize for Raspberry Pi? It wants Java 1.7 for the old version of Eclipse :(
  * Look into JarSplice or other compiling tools for application deployment
  * Web interface to control PrefsSliders: Add JSON interface for PrefsSliders & WebServer/WebSockets?
  * Fix overhead view of KinectRegionGrid - with larger grids it's off-screen
  * BufferActivityMonitor should use FrameDifferenceBuffer object
  * Replace ColorUtil with EasingColor
  * Clean up /data directory with assets that can be used across demos, and move sketch assets into their own location
  * ImageSequence -> ffmpeg rendering from a class. Would make rendering easier on both platforms
  * Clean up old stuff - get rid of non-useful demos
  * Document important files/concepts/tools for README
