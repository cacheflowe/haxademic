# Haxademic
Haxademic is a multimedia platform, built in Java and [Processing](http://processing.org/). It's a starting point for interactive visuals, giving you a unified environment for both realtime and rendering modes. It loads several Java libraries and wraps them up to play nicely with each other. It solves a number of problems faced by (potentially) thread-unsafe hardware inputs like audio, Kinect, MIDI and OSC. To view some projects created with the library, check out the [Haxademic Tumblr](http://haxademic.com/).

## State of affairs
While the code has been open-sourced, I haven't had time to write much (any) documentation, but I'm trying to get it there. You can see the example apps and sketches to get an idea of how to use various features. Even without fully installing everything, there's plenty of interesting code within, and I fully endorse borrowing it however you see fit in the meantime. If you're interested in collaborating, please contact me via my [GitHub account](http://github.com/cacheflowe), or my [web site](http://cacheflowe.com/?page=contact). If you'd like help getting the project running, follow the instructions below.

## Features / Capabilities
* Inputs
	* Audio input and analysis (both realtime and step-through for rendering)
		* FFT analysis
		* Waveform/oscilloscope data
		* Audio beat detection
	* Step-through rendering with multiple audio files concurrently
	* MIDI input (both realtime and step-through for rendering)
		* Cached MIDI input to avoid thread-unsafe operations
	* OSC input
	* Kinect input, with skeleton data via OpenNI
	* Simple webcam wrapper
* 3D tools (using Toxiclibs WETriangleMesh objects as the common format)
	* Convert SVG files to 2D meshes
	* Simple 3D extrusion of 2D meshes
	* Load and convert .obj, .gif & .svg files to WETriangleMesh
	* Mesh pool object to load and hold instance of any number of meshes
	* Base Camera type, with simple camera subclasses (needs work) 
	* Draw a mesh with incoming Audio data spread across the faces
	* Shatter a box or sphere with randomized Voronoi3D calculations (soon to work on any mesh)
	* Some basic shape-drawing code
	* Mesh smoothing
	* Mesh deform from audio input
	* Apply off-screen audio-reactive textures to a 3D mesh
* Text tools
	* Create a 2D or extruded 3D mesh from text with a custom .ttf font
	* Draw 2D text with a custom .ttf font
* Input controls
	* Button
	* TextInput
* Image processing
	* PImage reversal & other image utilities/helpers
	* PImage <-> BufferedImage conversion for native image processing
	* Multiple screenshot methods
	* Photoshop-like effects processing (via Java Image Filters)
	* Custom image filters
* Math objects
	* Math utility methods
	* Easing 3d floats
	* Elastic 3d floats
* Drawing utilities
	* Utilities to (re)set Applet properties like lighting, current matrix, drawing colors
	* OpenGL utility to set GL-specific properties
	* Eased color interpolation
* Output
	* Render to Quicktime or image sequence with minimal effort
	* High-quality rendering with the Sunflow renderer, for beautiful globally-illuminated, antialiased scenes 
	* Audio playback with cached audio clip pool
* General Environment / System utilities
	* .properties file loader with overridable defaults
	* Directory searching for specific filetypes
	* Automatic system screensaver disabling while running
	* ~~True full-screen mode on OS X~~
	* Toggles the window chrome on a non-fullscreen applet
	* Debug utilities to report current actual frame rate, memory usage 
	* Timestamp generators
	* CSV loading & saving
* Apps
	* HaxVisual - A modular VJ system ([video](http://www.youtube.com/watch?v=6G1jLZrN1Ig))
	* KacheOut - A 2-player Kinect-based video game ([video](https://vimeo.com/43230920))
	* AirDrums - A Kinect-based air drum machine
	* TimeLapse - Renders a .mov video from an image sequence in a directory ([video](https://vimeo.com/42046179))
	* MusicVideos - A collection of Applets that render music videos, audio-reactive and otherwise

## Installing / Compiling
* Download the standard Eclipse IDE for Java development, and the Java Development Kit itself:
	* [Eclipse](http://www.eclipse.org/)
	* [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Download](http://code.google.com/p/simple-openni/downloads/list) and install the latest SimpleOpenNI (Kinect) drivers with the [instructions](http://code.google.com/p/simple-openni/wiki/Installation) for your particular platform
* Clone or [download](https://github.com/cacheflowe/haxademic/archive/master.zip) the Haxademic project
* Open Eclipse and: **File -> Import -> General / Existing Projects into Workspace**
	* Choose the `haxademic` directory that you cloned/downloaded, press **Finish**, and the project should be ready to use
* Make sure you're compiling with Java 1.6 instead of the new default of 1.7:
	* Right-click the `haxademic` project in the **Package Explorer** or **Navigator** window and click **Properties**
	* Click the **Java Compiler** section and check the **Enable project specific settings** box on the right
	* Select **1.6** as your **Compiler compliance level**, if possible
	* If "Configure the **Installed JREs**" is shown at the bottom of this window, click that, make sure the **1.6** item is checked, then click OK.
* Right-click on a PApplet or PAppletHax subclass within `src` and choose **Run As -> Java Applet** from the menu. Hopefully you're seeing something awesome at this point.

Haxademic uses the following Java & Processing libraries, which I've included in this repository so you don't have to find them yourself (more on that below):

* [Processing](http://processing.org/) (view the [Processing for Eclipse instructions](http://processing.org/learning/eclipse/))
* [ESS](http://www.tree-axis.com/Ess/)
* [simple-openni](http://code.google.com/p/simple-openni/)
* [toxiclibs](http://toxiclibs.org/)
* [p5sunflow](https://github.com/hryk/p5sunflow) original site is down :-/
* [OBJLoader](http://code.google.com/p/saitoobjloader/)
* [themidibus](https://github.com/sparks/themidibus)
* [oscP5](http://www.sojamo.de/libraries/oscP5/)
* [fullscreen](http://www.superduper.org/processing/fullscreen_api/)
* [He_Mesh](http://hemesh.wblut.com/)
* [minim](http://code.compartmental.net/tools/minim/)
* [Geomerative](http://www.ricardmarxer.com/geomerative/)
* [blobDetection](http://www.v3ga.net/processing/BlobDetection/)
* [Java Image Filters](http://www.jhlabs.com/ip/filters/index.html)
* [Super CSV](http://supercsv.sourceforge.net/)

Use the following VM Arguments when running the Java Application

* `-d32` (when rendering to Quicktime movie or using a web cam)
* `-Xmx1024M`
* `-Xms1024M`

General Use / Tips

* If you get a Quicktime error, try adding `-d32` to your VM arguments when compiling. 
* If you want to wipe your `bin/` directory, you'll have to do a **Project -> Clean…** in Eclipse afterwards.

## Todo
* Comment the codebase and generate docs
* Create a unified keyboard/MIDI/OSC input system with improved midi/osc data handling
* Clean up legacy code that's no longer used
* Lots more noted in the PAppletHax class comments

## Licensing

The Haxademic codebase and apps are [MIT licensed](https://raw.github.com/cacheflowe/haxademic/master/LICENSE), so do what you want with these files. Feel free to let me know that you're using it for something cool. I've added 3rd-party .jar files and compiled Java libraries that I'm probably not actually allowed to redistribute here, so if you're the owner of one of those libraries and would like the files removed, let me know. I've included them to aid those who would like a quick start and not have to search for the many dependencies of this project. Some of these libraries have disappeared from the web entirely, so searching for them may be futile anyway. I just want people to make cool things with this library, and hope you understand.