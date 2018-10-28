# TODO

* Art projects
  * Convert all webcam VFX apps (and old video filters) to BaseVideoFilter subclasses
  * Moire sphere/shader for MW prototying
  * Voice-activated color room: What Say Hue?
  * Interphase
    * Integrate HaxVisualTwo
    * Integrate Launchpad direct interface
  * Grass cutout - laser cut w/Seied
  * Make a dmx gif loop
  * Motion detection point-of-interest motion capture by small rectangles - figure out how to zoom out and create multiple zones
  * Turn client snake mirror into its own thing
  * Turn client TiledTexture mirror into a motion-reactive mirror that changes when you move
* Audio
  * Sphinx4 speech recognition
    * Copy / paste + Robot for tired hands
  * Test basic audio input. why is audio getting delayed after hours of running?
  * Turn off Beads audio input object output - this should not pass through
  * Split audio stepthrough rendering from Renderer.java, which should just save movies. MIDIStepthrough renderer is a good example of splitting
  * Make demos for rendering at a specific bpm
* MIDI:
  * Move midibus instance to MidiState (now MidiDevice)
  * InputTrigger should merge MIDI buttons and CC controls, just like OSC commands
  * Check MIDI rendering now that MIDI code has been revamped
* DMX
  * Bring timeclock app into Haxademic as a new, more robust video-to-dmx demo/app
* GLSL
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
* SystemUtil:
  * Merge Windows & normal SystemUtil - make sure Java-killing code works on both OS X & Windows
* net
  * PrefsSliders should also serve up a web server that has just those sliders. .json config maybe?
  * WebServer and SocketServer should be more stylistically similar. See PORT in WebServer, and DEBUG static boolean - should be passed in?
  * Replace JavaWebsocket with Jetty WebSocket server??
* Demos:
  * Replicate indpendent 40k shape demo from Processing examples - update with GPU
  * Make a little planet generator with icosahedron deformation and colorized texture map of depth
    * https://github.com/ashima/webgl-noise/wiki
  * Finish icosa hair w/triangle tentacles!
  * Distill more demos for `core` code
  * Make a Demo based on Eutopia that runs via command line & passes args in
* PShape & PShapeUtil:
  * Scrolling feedback texture mapped to a model with lighting
  * Make a vertex shader that does this to a sheet: https://www.google.com/search?ei=z9e3Wo6iOdG45gKIyZrgAQ&q=graph+z%3Dsin%28y*0.1%29*sin%28x%29&oq=graph+z%3Dsin%28y*0.1%29*sin%28x%29&gs_l=psy-ab.3...11324.12507.0.13684.4.4.0.0.0.0.72.277.4.4.0....0...1c.1.64.psy-ab..0.1.69...0i8i30k1.0.tqpD6rWz4Hk
  * PShapeUtil: Build a demo for changing the x/z registration/center point so models that sit on the ground can spin from the right origin (controlP5 ?)
  * Extrude2dPoints should be able to return a PShape
    * Shapes.java should have a filled or open extruded polygon generator method
	* PShapeSolid should loop properly through children like PShapeUtil does now
  * GIVE A MODEL FRONT & BACK TEXTURES! do wrapping the same way as now, but a different texture if negative z (or normal?)
  * 3d lighting w/glsl
    * Demo_VertexShader_NoiseTest_WIP
    * Demo_VertexShader_Fattener
* General / tools
  * Add print page object for Kalishr project
  * Update JoonsRenderer - it looks like it's a regular Processing library now
  * Web interface to control PrefsSliders: Add JSON interface for PrefsSliders & WebServer/WebSockets?
  * Fix overhead view of KinectRegionGrid - with larger grids it's off-screen
  * BufferActivityMonitor should use FrameDifferenceBuffer object
  * Replace ColorHax with EasingColor
  * Clean up /data directory with assets that can be used across demos, and move sketch assets into their own location
  * ImageSequence -> ffmpeg rendering from a class. Would make rendering easier on both platforms
  * Start moving all apps towards objects that can receive a PGraphics instance. decoupling from PApplet will help move visuals into HaxVisualTwo
  * Clean up old stuff - get rid of non-useful demos
  * Document important files/concepts/tools for README
