# TODO

## New ideas, TODOs

* Processing 4 update
  * Fix JavaFX WebView & demos. Needs updated JavaFX jars?
  * Upgrade notes:
  	* Now we can use AdoptOpenJDK's version of Java, which is more open and doesn't require a login for Oracle
  	* JavaSound changes required an update to Beads AudioContext initialization
  		* A huge bonus though - with Java 11, system audio input (across all audio libraries) no longer starts to lag after a few hours of running an app! This was a really annoying bug that I couldn't seem to fix
	* Any references to `frame` need to go away
		* DropFile library no longer works
	* Updated JavaFX components exist in the external JavaFX library
		* Still need to figure out the right way to import WebView, which used to come in Java 8, but was very out-of-date
	* Webcam interface via `Capture` object is very different. No longer gives you a list of resolutions, but has other improvements based on the updates to the underlying gstreamer library
	* Video library is far more performant based on these same changes
	* MIDI output meant for a hardware device started triggering system MIDI piano sounds
	* [To watch] Does Robot's screenshot utility fix the old problem of screenshots getting stuck and not updating?
* More GPGPU/curl effects
  * Splash shapes or letters onto screen
  * Start with an actual grid of pixels & displace from there
  * Build array of launch positions to colorize a color map by pixel in a 2nd draw call
* Optical flow shader displace filter
* Grab a row of pixels from any image to populate audio buffer AND Interphase sequencer
* Fill a PShape with GPU particles. (check barradeu article)
* Glitch mirror w/slit scan history but blocks on buffer difference active areas
  * Bring VLC wrapper into Haxademic (& add to this demo)
* Text rasterizer/cacher/cropper w/border per letter

## Bugs

* Add javafx web
* Fix CEF Spout browser
* [WIP, needs styling] UI via web interface (json config)  should support:
  * New textfield component
  * Title component

## Modularize PAppletHax

* Next:
  * WebCam menu should look more structured like UI/DebugView - can they all live together?
  * Clean up Renderer situation
    * It always inits a video renderer, which is shouldn't. Test different modes.
    * [Done, but clean up] Add ability to target specific offscreen buffers
    * Test audioreactive rendering
  	* Joons leaves the context in a weird place, and DebugView looks busted & scaled up. Do we need to reset the camera?
  * Revisit AppWindow & update demos. Try to keep on top on an interval, in case of windows popups

  * Update README with updated references
  * Should OscState (and maybe GamepadState) be separated to OscConnection and OscState, so multiple OscConnections can send data to it? Multiple objects could communicate on different ports.

* Converted:
  * Webcam
  * DMXUniverse
  * AudioIn
  * KeyboardState
  * Mouse
  * MidiState / MidiDevice
  * UI
  * DebugView
  * Config
  * GamepadState
  * HttpInputState
  * OscState
  * ScreenSaverBlocker
  * FrameLoop
  * DepthCamera
  * Renderer

## Bring into Haxademic:

Damping
x += (newX-x * 0.1)
Easing
t = t * t * (3.-2.) * t

Infinity:

* Custom UITextField example (kill ESC and other keys)
* Debug tools in main app
* New Slack wrapper
  * See if we can remove the 2 extra slack-client jars

PGA + Bespoke:

* Chrome launch script? And java side?

Other: 

* New 3d particle system from WashHands render
* this: https://skeleton-tracing.netlify.app/
* https://github.com/Bonjour-Interactive-Lab/Processing-GPUImage/
* Evenly-spaced random distribution on a sphere: https://blog.davidlochhead.xyz/posts/2017-10-12-random-position-on-the-surface-of-a-sphere.html#spherical-coordinates

## InputTrigger revamp

* Key press triggers with KeyboardState don't always register - especially over TeamViewer of if the FPS is low
  * Test the rest of this - we should be in good shape now
* Threaded safety of InputTrigger isn't good - especially at a low FPS?
  * WebServer requests can fall through the cracks (button clicks, etc)
  * Web requests that route to p.ui aren't predictable, especially at 30fps? Look at p.ui button handling in Re-Creation
  * InputTrigger should merge MIDI buttons and CC controls?

## WebCam

* WebCam updates for Video 2.0 / Processing 4
  * WebCam UI picker should draw flat on top of everything like DebugView
  	* It should also layout like UI, so it builds across the screen with title headers
  * Add webcam inspector to provide a list of native webcam options
    * Should return these config options: https://webcamtests.com/resolution
    * libusb or something like that to query weather a webcam is still plugged in. Java library?
      * http://usb4java.org/quickstart/libusb.html
      * http://usb4java.org/configuration.html
* Android high-framerate capture or UVC use?
  * https://github.com/android/media-samples/blob/master/MediaRecorder/Application/src/main/java/com/example/android/mediarecorder/MainActivity.java

## DebugView / UI
  * Update Web UI CSS to work on more devices. Use Plus Six slider styles?
    * Update in general & make it more attractive. Titles & textfields too
    * https://materializecss.com ?
  * Show full text on hover (move to top of z-stack)
  * Click to copy a value

## Big Mother / DashboardPoster / CrashMonitor

* Add a delegate callback or event(!) that's called when a screenshot is taking place, in case we want to do other stuff. Yotel should take an rgb capture
* Update standalone DashboardPoster app w/new dashboard code
* Should Screenshot in DashboardPoster be it's own app/process, like the CrashMonitor? or *in* CrashMonitor? As an UptimeSuite??
* CrashMonitor is very slow to communicate & turns yellow if a heavy app is starting up
* Screenshots still get "stuck" and don't update
	* Maybe fixed with Java 11 updates?
* [DONE?] On web side, uptime is modded every 24 hours - needs a day count up front!
* Can CrashMonitor launch the main app instead of the other way around?
* Can CrashMonitor init DashboardPoster and other tools like monitor size change and auto-mouse clicking, rather than those being in the main app
* Add config CMS to BigMother for any app to receive commands and config
* BigMother should be implemented in a Simplesite instance

## DMX / LED

* Add multiple universes to DMXEditor
* ArtNet:
  * Advatek controller
    * Use a dirty flag and have ArtNetDataSender subscribe to `post` and only send data once per frame
    * Sketch: Use Kinect to detect angle of hands, and flow a pattern left or righton LED strip depending on tint
    * Sketch: Projection map onto new light fixture that samples from the same pixels underneath
    * Sketch: Bounce a light strip between 2 par lights  
    * Wrap up sAcn & ArtNet objects & demos
    * Map using UV coordinates to a texture? (like webcam reverse-projection mapping)
    * Merge ArtNet & sAcn objects with DMX lighting objects
  * Add mappable strips like DMXEditor
  * Send webcam to 16x16 matrix :) 
* sACN: http://www.bennette.co.uk/codeViewer.html?rootPath=downloads/src_files/java/sACN4J&projId=sacnlight&srcObj=0
  * Can we turn this into a Processing library if given permission?

## Processing problems

* Make some vanilla Processing examples to help with post 3.3.7 render issues: https://github.com/processing/processing/issues/5806
* Make a GitHub issue around this:
	* https://forum.processing.org/two/discussion/1723/unwanted-artifacts-in-a-simple-pixelation-shader
	* Fixed here: https://github.com/cacheflowe/haxademic/blob/master/data/haxademic/shaders/filters/pixelate.glsl
	* Old, broken version: https://github.com/cacheflowe/haxademic/blob/3b520a7e850e0da7063f18075bf36e249601e052/data/haxademic/shaders/filters/pixelate.glsl
* PShaderCompiler - PShader shouldn't absolutely kill the app on a failed GLSL compile
* Processing bugs:
  * OBJ models
    * MTL files need to strip the relative path "./" to work
      * mtllib ./materials.mtl
        In my experience, Processing doesn't like this. If your .obj file has this, try removing the dot slash like this:
        mtllib materials.mtl
    * Alpha (d or Tr) in obj files isn't working
    * Drawing obj files out of the box is odd per PApplet or PGraphics
  * GLSL UV multiplication on original texture sampling (rotate & zoom repeat shaders)
  * float32 textures:
    * https://stackoverflow.com/questions/56310425/upload-gl-unsigned-byte-to-a-gl-float-texture
    * https://www.khronos.org/opengl/wiki/Pixel_Transfer
    * https://stackoverflow.com/questions/34497195/difference-between-format-and-internalformat
    * https://www.khronos.org/opengl/wiki/Image_Format
    * gradient banding
    * low-precision for particle systems (POINTS mesh displacement)
    * blank spots (dead pixels) in some shaders
    * feedback can only be so slow if using a texture map for displacement? maybe just need to divide


## Art projects
  * Blow up Arduino via camera feed & mircophones. Re-pitch? Use a motor that spins something that a sensor picks up (for mechanical oscillation)?
  * Simulations & complex systems
  * Dark room w/Light/sound combo
  * Tiny projection mapping w/custom objects
  * Video filter that does a bunch of feedback operations every frame, leading to GAN-like looks?
  * 8-channel surround sound & light
  * C.A.C.H.E.
    * CACHE performance: launchpads, visuals, 8-bar LED array, kinect, drum pads, gqom beats
  * Gradient circle spinning, leaving a trail
  * Variable Reaction-Diffusion using amplitude map & custom blur/sharpen shaders
  * Kinect-triggered music - krump to trigger sounds & lights
  * Kinect silhouette studies (uv coord offset, glitch overlay, feedback)
  * Convert Fliud sim: 
    * https://github.com/PavelDoGreat/WebGL-Fluid-Simulation/blob/master/script.js
    * https://storage.googleapis.com/movenet/creative/index.html
    * https://github.com/malik-tillman/Fluid-JS
  * Shatter model & triangles fall down (Sunflow final render)
  * Sound in space. Raspberry Pis via wifi, attached to speakers. Split channels for more outputs
  * New video loops should have a soundtrack. use my tunes? look in sketch/render/avloops
  * Kinect history textures in 3d
  	* Kinect history point mesh history
  * Convert all webcam VFX apps (and old video filters) to BaseVideoFilter subclasses
  * Moire sphere/shader for MW prototying
    * Of interest: https://www.shadertoy.com/view/MllSDr
    * Variable Reaction-Diffusion as noted above
  * Voice-activated color room: What Say Hue?
  * Grass cutout - laser cut
  * Make a dmx/launchpad gif loop
  * Motion detection point-of-interest motion capture by small rectangles - figure out how to zoom out and create multiple zones
  * Turn client snake mirror into its own thing
  * Make a version of partycles with GPU particles from VFX code and ability to swap webcam instead of Kinect
	* Also, blob tracking VFX but sweet patterns inside the blobs
  * Simulate stop frame overlay via Jesse

## HaxVisualTwo
  * Less kaleidoscope
    * Fix quad mirror lines
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
    * R/D
    * Scrolling text from Reset animation
    * Background sheet from Reset animation
  	* Any other cool loop/render?!

## Interphase
  * Better randomize functions to help the creative process
  * Store/recall JSON (add more props)
  * Add more samples
  * Fix up Audio buffers per input
    * MAKE AN ADDRESSABLE LED DEMO WITH FFT/WAVEFORM!
    * Build a demo with an FFT history texture per channel
  * Sequencer properties to add to JSON & UI. 
  * ...Pull some of these from SequencerConfig & make setters on Sequencer?
    * Base Pitch (new feature)
    * Mute
    * Evolve
    * Attack / Release (active or not, also number!)
    * Swing
    * Sample index
    * Plays Notes (currently in SequencerConfig)
    * Plays chords (currently in SequencerConfig)
    * Note offset
    * Note randomization
  * Add button to restart sequence
  * Can a track be a loop instead of a one-shot? how does this work? add to SequencerConfig
  * Save current config to JSON
    * Use filenames instead of index position? And/or?
    * Load JSON back in
    	* Drag & drop?
    	* JSON config slider for collection
  * Add getters for audio data + waveform per-channel
  * Work within Demo_Interphase_AV_Example & make a solid framework for AV loops
  * Switch to playing audio with WavPlayer? 
  * [Demo exists!] perfectly-looped audio clips, mapped to main loop length
  	* [Kinda works! would be better w/Interphase Metronome] Scrub to random parts of samples to chop breaks
    * Add to Interphase sequencer
  * C.A.C.H.E. - Creative Adversarial Computer-Human Exchange
  * Integrate Communichords looping tones (for Moire Room, specifically)
  * Space button to start/stop
  * Store/recall audio & visual combos
    * Serialize sequencer config
    * [Done?] Need to turn off totally random notes (rely on position-based notes)
    * Morph between stored configs
    * Store premade patterns for different beats/songs. Json?
    * Make alternate z-space-scrolling sequencer grid
  * Musical interaction
    * allow doubletime sequencer
    * Add breakbeat & synth loop Sequencer types? How to work these concepts in?
  * Sound style
    * More/new samples! Get rid of abrasive samples
    * Make interphase more bangy & think about converting to be more spatial w/lighting
  * Effects
    * delay / offset
    * add a compressor to main output? audio needs to be squished
  * Use interphase beat timing rather than beat detection to make the next HaxVisual change
  * Clean up basic music code (even more)
  * Integrations
    * MIDI output for Ableton sync and other app integration. (OSC?)
    * build a more custom touchscreen 8x16 interface via `p.ui`
      * Map more functions to hardware controls
      * Use a button to play sample without changing pattern
  * Visual style
    * Better haxvisual patterns configurations

## Audio
  * Auto-pitch script
    * `aubio notes keys-013.wav --release-drop 90`
    * `sox keys-012.wav keys-012.pitched.wav pitch -100`
  * Last convo
    * Build a sequencer texture
    * Sequencer offset control (1-16). show playhead in sequencer display
      * Do we have an evolve that pushes the entire sequence forward/back?
    * Random generator w/thumbs up to keep
    * How to export a wav file on the fly
    * Swing on a lfo, different for on & off beats
    * Scale pattern up or down(with repeating)
    * oscillated quantization
    * Sample note offset when loaded - add a base pitch adjust
    * Chords - stick with 4th & 5th
    * Load MIDI file for quantization purposes
    * MIDI sync with ableton - slave to ableton tempo
    * Drag & drop wav file to load
      * https://stackoverflow.com/a/39415436
    * [DONE?] Figure out delay for swing
    * Check this out: https://www.elf-audio.com/synths/bauble/#
    * Create separate demos for each input object?
  * Demo_DroneSampler upgrades: check TODO list
  * WavPlayer panning and FFT analysis need love:
    * FFT only works for the left channel if it's been panned
    * Build a demo that tests:
      * ADSR - add sustain
      * Reverse??
      * Filter?!
      * Auto-panning?
  * Text to speech In Processing. Webview in Processing? Or web sockets to external browser? Vanilla Java?
  * Sphinx4 speech recognition
  * Beads synthesis examples/inspiration
    * https://github.com/orsjb/beads/blob/master/src/beads_main/java/net/beadsproject/beads/data/buffers/SineBuffer.java
    * http://aum.dartmouth.edu/~mcasey/cs2/notes/sound3.php
    * https://www.google.com/search?client=firefox-b-1-d&q=granular+synthesis+buffer
    * https://www.google.com/search?client=firefox-b-1-d&q=wavetable+synthesis
    * https://evanxmerz.com/soundsynthjava/Sound_Synth_Java.html#digital_sound
    * https://www.jabaco.org/board/1189-beads-audio-library-question.html
  * Split audio stepthrough rendering from Renderer.java, which should just save movies. MIDIStepthrough renderer is a good example of splitting
  * Make demos for rendering at a specific bpm
  * Spatial audio:
    * Ableton w/M4L: https://cycling74.com/articles/audio-routings-a-new-system-for-multi-channel-routing-in-ableton-live
    * Javasound
      * https://stackoverflow.com/questions/2416935/how-to-play-wav-files-with-java
	    * https://forum.processing.org/two/discussion/3109/how-to-get-multiple-audio-outputs-with-minim
	    * https://www.developerfusion.com/article/84314/wired-for-sound/
      * Java Sound Resources
        * Loop an audio file: http://jsresources.sourceforge.net/examples/AudioLoop.html
        * FAQ: Audio Programming: http://jsresources.sourceforge.net/faq_audio.html
      * Javasound tutorials: https://docs.oracle.com/javase/tutorial/sound/accessing.html
      * Playing audio: https://docs.oracle.com/javase/8/docs/technotes/guides/sound/programmer_guide/chapter4.html
      * Mixer: https://docs.oracle.com/javase/7/docs/api/javax/sound/sampled/Mixer.html
        * https://www.codota.com/code/java/methods/javax.sound.sampled.Mixer/getLine
      * AudioSystem: https://docs.oracle.com/javase/7/docs/api/javax/sound/sampled/AudioSystem.html
      * AudioFormat: https://docs.oracle.com/javase/8/docs/api/javax/sound/sampled/class-use/AudioFormat.html
      * Clip example: https://www.codejava.net/coding/how-to-play-back-audio-in-java-with-examples
    * JAsioHost
      * https://www.programcreek.com/java-api-examples/?code=Sinius15/Virtual-Audio-Mixer/Virtual-Audio-Mixer-master/src/vam/core/AudioManager.java
      * https://www.programcreek.com/java-api-examples/?code=exch-bms2/beatoraja/beatoraja-master/src/bms/player/beatoraja/audio/ASIODriver.java#
      * https://github.com/mhroth/jasiohost/blob/master/src/com/synthbot/jasiohost/AsioChannel.java
      * https://stackoverflow.com/questions/14174968/how-do-i-determine-which-channel-is-left-right-etc
      * https://www.alastairbarber.com/?post=using-jasiohost
    * Beads
      * https://groups.google.com/g/beadsproject
      * AudioContext: http://www.beadsproject.net/doc/index.html?net/beadsproject/beads/core/AudioContext.html
      * IOAudioFormat: http://www.beadsproject.net/doc/net/beadsproject/beads/core/IOAudioFormat.html
      * AudioIO: https://github.com/orsjb/beads/blob/master/src/beads_main/net/beadsproject/beads/core/AudioIO.java
      * https://forum.processing.org/two/discussion/16611/multichannel-audio#latest
      * https://discourse.processing.org/t/using-beads-or-other-libraries-for-multihannel-audio-output/5648
      * https://groups.google.com/forum/#!topic/beadsproject/dSvxUM1l9S0  
      * https://groups.google.com/forum/#!searchin/beadsproject/jack%7Csort:date/beadsproject/B_4UFyFSomk/X2npvnL2CwAJ
    * PortAudio
      * https://app.assembla.com/wiki/show/portaudio/V19ReleasePlan
      * https://github.com/EddieRingle/portaudio/blob/master/bindings/java/jportaudio/src/com/portaudio/PortAudio.java
    * JSYN
      * http://www.softsynth.com/jsyn/
    * JACK
      * http://jackaudio.org/downloads/
	  * Openframeworks
      * https://github.com/borg/ofxMultiDeviceSoundPlayer
    * C++
      * https://github.com/WeAreROLI/JUCE
    * Hardware:
      * Behringer U-phoria UMC1820
      * Tascam US-16x08
      * MOTU UltraLite-mk3 Hybrid
      * M-Audio M-Track Eight
      * TASCAM Series 208i

## Unity/Spout

  * 3d scene to RenderTexture to Spout
    * https://www.youtube.com/watch?v=iIwcqgAPVWI
  * Scripted startup/shutdown
  * Recompile Chromium w/Spout: https://github.com/fg-uulm/cef-spout

## MIDI:
  * Check MIDI rendering now that MIDI code has been revamped

## GLSL
  * Refraction shader: 
    * https://tympanus.net/codrops/2019/10/29/real-time-multiside-refraction-in-three-steps/
    * https://www.youtube.com/watch?v=NCpaaLkmXI8&feature=youtu.be
  * Fresnel shader:
    * https://github.com/poikilos/KivyGlops/blob/master/shaders/fresnel.glsl
  * Bitangent noise
    * https://github.com/atyuwen/bitangent_noise/
  * Get a handle on modelViewInv uniform. make a basic example to test
  * Get more into custom lighting. Good start with this demo:
    * com.haxademic.demo.draw.shapes.shader.Demo_VertexShader_MoveSpheres
    * Also figure out vert shader rotation of sub shapes
  * Cheap depth buffer: https://github.com/kosowski/Processing_DepthBuffer/blob/master/DepthBuuferRead/CustomFrameBuffer.pde
    * Also: com.haxademic.sketch.shader.ShaderSSAO
    * Also: com.haxademic.sketch.shader.DepthVertexTest
  * Compute Shaders: https://github.com/perses-games/jogl-compute-shaders-fireworks
  * Big particle sim like: https://www.instagram.com/p/B-h3tp7oUWN/
  * Processing float 32 support: https://github.com/processing/processing/issues/3321
  * Do the physarum: https://sagejenson.com/physarum
  * Do this with GPU particles: https://twitter.com/mamboleoo/status/1224738602200064000?s=12
  * Shader builder w/snippets - look through Hepp's code
    * Add uniforms automatically?
  	* Default shader chunks
  	  * Assemble w/replacement strings that match filenames?
  * Wrap up GLSL transitions collection and make a common interface
    * Port these too: https://tympanus.net/Development/webGLImageTransitions/index8.html
  * Bump-mapping
    * https://github.com/codeanticode/pshader-experiments/tree/master/BlueMarble
  * Convert Orbit noise: https://www.shadertoy.com/view/4t3yDn
  * Convert some postprocessing effects:
    * https://github.com/libretro/glsl-shaders
    * https://github.com/Vidvox/ISF-Files/blob/master/ISF/Optical%20Flow%20Distort.fs
    * https://github.com/v002/v002-Optical-Flow/blob/master/v002.GPUHSFlow.frag
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
    * Use the 'discard' keyword to *not* update a fragment, anywhere in a fragment shader.
  * GPU Particles
    * Fix up GPU particle launcher to store colors per-particle
    * https://github.com/armdz/ParticlesGPU/blob/master/bin/data/shader/render.vert
  	* http://barradeau.com/blog/?p=621
	   * Look into Processing shader types - is there a point shader? yes - https://processing.org/tutorials/pshader/
	* https://codeanticode.wordpress.com/2014/05/08/shader_api_in_processing_2/
	* http://atduskgreg.github.io/Processing-Shader-Examples/
	* http://www.beautifulseams.com/2013/04/30/shaders/
	* https://github.com/codeanticode/pshader-tutorials
  * GPU fluid:
  	* https://github.com/PavelDoGreat/WebGL-Fluid-Simulation
  * Geometry shaders
  	* http://www.nachocossio.com/geometry-shaders-in-processing/
  	* https://github.com/kosowski/Geometry-shaders-Processing/blob/master/geometry_shader_tessellation/sphere.pde
  * 32-bit packing (not needed now with P32 renderer?)
  	* https://stackoverflow.com/questions/18453302/how-do-you-pack-one-32bit-int-into-4-8bit-ints-in-glsl-webgl
  	* http://www.ozone3d.net/blogs/lab/20080604/glsl-float-to-rgba8-encoder/
  	* https://community.khronos.org/t/pack-more-than-4-components-into-rgba-32-texture-in-vertex-shader/72945/2
  	* https://forum.processing.org/two/discussion/17629/how-to-get-round-using-16-bit-image-buffers-shadertoy-question

## net
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
  * openImaj / FaceTracker: https://www.programcreek.com/java-api-examples/?code=openimaj/openimaj/openimaj-master/image/faces/src/main/java/org/openimaj/image/processing/face/alignment/CLMAligner.java#

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
  * Check out fast voronoi/delaunay library: http://leebyron.com/mesh/
  * Video
    * Add VLCJ for 4k/HAP/webcam playback: https://github.com/linux-man/VLCJVideo
    * IP camera (get one that does an mjpeg stream)
      * Or does this one work with VLCJ??
    * Rtp video in Java?
      * https://www.oracle.com/technetwork/java/javase/documentation/toolstx-178270.html
      * https://forum.processing.org/two/discussion/17166/livestreaming-ipcapture-sdp-file
    * NDI video: https://github.com/WalkerKnapp/devolay
  * AppGenerator:
    * CLASSPATH_ATTR_LIBRARY_PATH_ENTRY - this needs text replace on `haxademic` to new project name
  * Clean up /lib - we should have src for all libraries if possible, pointed to for Eclipse click-through, but nothing else. Except maybe examples?
  * Merge Windows & normal SystemUtil - make sure Java-killing code works on both OS X & Windows
  * Test importing a Java class into a Processing IDE project
  * How can we optimize for Raspberry Pi? It wants Java 1.7 for the old version of Eclipse :(
  * Look into JarSplice or other compiling tools for application deployment
  * BufferActivityMonitor should use FrameDifferenceBuffer object
  * Replace ColorUtil with EasingColor
  * ImageSequence -> ffmpeg rendering from a class. Would make rendering easier on both platforms
  * JNA: https://www.techbeamers.com/write-a-simple-jna-program-in-java/
    * https://github.com/bytedeco/javacpp

## To investigate

* Look into these CV demos
	* https://github.com/chungbwc/Magicandlove
	* http://www.magicandlove.com/blog/2018/08/06/openpose-in-processing-and-opencv-dnn/
	* https://github.com/chungbwc/Magicandlove/tree/master/ml20180806b
	* http://www.magicandlove.com/blog/2018/08/08/darknet-yolo-v3-testing-in-processing-with-the-opencv-dnn-module/
	* http://www.magicandlove.com/blog/2018/08/06/deep-neural-network-dnn-module-with-processing/
* Investigate Task Scheduler
  * https://docs.microsoft.com/en-us/windows/win32/taskschd/task-scheduler-start-page


## Topics



What is your app doing when you're not home?
  * Haxademic tools
  * DashboardPoster / BigMother
  * CrashMonitor
  * TextEventLog
  * DebugView
  * UIControlPanel
    * Hide debug panels if left open
  * FrozenImageMonitor
  * Click app to stay on top & move mouse offscreen
  * Fullscreen, force on top
  * WebCamPicker
  * JSONPoller + CMS
  * Restart app after uptime, within hour range
  * Restart computer, launch scripts
  * TeamViewer

  * Can certain parts of this be moved to shell scripts?
    * https://github.com/laserpilot/Installation_Up_4evr

Advanced Processing
  * Processing v4 updates:
    * Video player is *far* faster
    * Capture (webcam) doesn't provide sizes, and that's tricky, because you don't know what you're actually getting
  * Hot reloading code in Eclipse/IntelliJ/Processing - make instructions!
    * Hot reloading PShader code w/Haxademic objects
  * How to track people
  * DMX & Addressable LEDs
  * Shader basics
  * PShape geometry caching vs creating it all every frame
  * Shaders & chaining
    * Kinect noise smoothing
    * GPU particles
  * Embedded jetty web server
  * WebSocket server
  * PGraphics compositing
    * Handling large graphics in realtime (Joran clouds example)
    * Text buffer & alpha channel
  * Kinect topics
    * Room scan
    * Center-of-mass technique, vs. skeleton detection
  * Run a Jetty web server
  * Run a websocket server
  * Scan & cache a kinect camera view via texture
  * PGraphics compositing
  * GPU particles
  * Set an app icon
  * AppStore concept
  * Run in Eclipse
  	* How Eclipse works vs the command line code it generates
  	* Java compiling w/javac, etc
  	* Extra tools:
  	  * VisualVM
  	  * mat
  * ShaderCompiler
  * Shader techniques
    * Post-processing
    * PShape deformation
  * PShapeUtil
  * Text to 3d shape
  * DMX spatial lighting
  * Spatial audio (multiple soundcards or Jack/Beads)
