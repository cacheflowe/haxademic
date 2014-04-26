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
	* Kinect input, with skeleton tracking and helper methods
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
	* ~~OpenGL utility to set GL-specific properties~~
	* Eased color interpolation
* Output
	* Render to Quicktime or image sequence with minimal effort
	* High-quality rendering with the Sunflow renderer, for beautiful globally-illuminated, antialiased scenes 
	* Audio playback with cached audio clip pool
* General Environment / System utilities
	* .properties file loader with overridable defaults
	* Directory searching for specific filetypes
	* Automatic system screensaver disabling while running
	* True full-screen mode on OS X
	* Toggles the window chrome on a non-fullscreen Java application
	* Debug utilities to report current actual frame rate, memory usage and print red text to the console
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
* [Download](http://code.google.com/p/simple-openni/downloads/list) and install the latest SimpleOpenNI (Kinect) drivers with the [instructions](http://code.google.com/p/simple-openni/wiki/Installation) for your particular platform. This is most likely to work with the 1st-gen Kinect model 1414
	* If you have a model 1473 Kinect camera, you might try [this build](http://intermedia.itu.dk/1473/) of the SimpleOpenNI library	
* Clone or [download](https://github.com/cacheflowe/haxademic-2/archive/master.zip) the Haxademic-2 project
* Open Eclipse and: **File -> Import -> General / Existing Projects into Workspace**
	* Choose the `haxademic` directory that you cloned/downloaded, press **Finish**, and the project should be ready to use
* Make sure you're compiling with Java 1.6 instead of the new default of 1.7:
	* Right-click the `haxademic` project in the **Package Explorer** or **Navigator** window and click **Properties**
	* Click the **Java Compiler** section and check the **Enable project specific settings** box on the right
	* Select **1.6** as your **Compiler compliance level**, if possible
	* If "Configure the **Installed JREs**" is shown at the bottom of this window, click that, make sure the **1.6** item is checked, then click OK.
* [Download](http://processing.org) and install the Processing 2.0 core libraries (they're too big to include in this project). Add the jars to your build path, as well as the libraries that come with Processing (video, minim, etc.):
	* Download Processing and right-click the application. Select **Show Package Contents**
	* Within the package, navigate to `Contents/Resources/Java/core/library`
		* Copy the contents of this directory to `haxademic-2/lib/processing-2.0/core` (create this directory if it doesn't exist)
	* Within the application package again, navigate to `Contents/Resources/Java/modes/java/libraries`, and again copy the contents. Paste them into `haxademic-2/lib/processing-2.0/libraries`
	* In the **Package Explorer** in Eclipse, right-click the `lib` directory and select **Refresh**
	* In the `lib/processing-2.0/core` directory, right click the following .jar files and select **Build path -> Add to build path**:
		* core.jar
		* gluegen-rt.jar
		* jogl-all.jar
	* In the `lib/processing-2.0/libraries` directory, right click the following required .jar files and select **Build path -> Add to build path**. You can add others that you might need for your project: 
		* minim/library/jl1.0.jar
		* minim/library/jsminim.jar
		* minim/library/minim.jar
		* minim/library/mp3spi1.9.4.jar
		* minim/library/tritonus_aos.jar
		* minim/library/tritonus_share.jar
		* video/library/video.jar
		* video/library/gstreamer-java.jar
		* video/library/jna.jar
* Right-click on a PApplet or PAppletHax subclass within `src` and choose **Run As -> Java Applet** from the menu. Hopefully you're seeing something awesome at this point.

Haxademic uses the following Java & Processing libraries, which I've included in this repository so you don't have to find them yourself (more on that below):

* [Processing](http://processing.org/) (view the [Processing for Eclipse instructions](http://processing.org/learning/eclipse/))
* [ESS](http://www.tree-axis.com/Ess/)
* [simple-openni](http://code.google.com/p/simple-openni/)
* [toxiclibs](http://toxiclibs.org/)
* [Joons renderer](https://github.com/joonhyublee/joons-renderer/wiki)
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
* [UDP Processing Library](http://ubaa.net/shared/processing/udp/)
* [ControlP5](http://www.sojamo.de/libraries/controlP5/)
* [Leap Motion for Processing](https://github.com/voidplus/leap-motion-processing/)

Use the following VM Arguments when running the Java Application to increase memory allocated to your app

* `-Xmx1024M`
* `-Xms1024M`

General Use / Tips

* If you want to wipe your `bin/` directory, you'll have to do a **Project -> Clean…** in Eclipse afterwards.

## Copying Haxademic to a new project 
* Copy the entire **haxademic-2** project directory to a new one in your filesystem
* Make sure hidden/system files are showing
* Delete the **.git** folder
* Open the **.classpath** file with a text editor and replace the instance of **haxademic-2** with the name of your new project directory
* Open the **.project** file with a text editor and repeat the previous step
* Open Eclipse, and in the Package Explorer panel, right click in the empty space and select "Import Project"
	* Select "Import existing projects into workspace…"
	* Open Eclipse and: **File -> Import -> General / Existing Projects into Workspace**
	* Choose your new project directory and press **Finish** - the project should be ready to use in its new sandbox
	
## Converting .mov output
```
$ ffmpeg -y -i input.mov -vcodec mpeg4 -b:v 10000k -f mp4 output.mp4
$ ffmpeg -y -i output.mp4 -vcodec libx264 -b:v 10000k output-final.mp4
```

## Publish a .jar of the /core
```	
$ cd haxademic-2/bin
$ jar cfv ../../ohheckyeah/games-java/lib/haxademic/haxademic.jar ./com/haxademic/core/*
```

## Todo
* Comment the codebase and generate docs
* Create a unified keyboard/MIDI/OSC input system with improved midi/osc data handling
* Clean up legacy code that's no longer used
* Lots more noted in the PAppletHax class comments

## Licensing

The Haxademic codebase and apps are [MIT licensed](https://raw.github.com/cacheflowe/haxademic/master/LICENSE), so do what you want with these files. Feel free to let me know that you're using it for something cool. I've added 3rd-party .jar files and compiled Java libraries that I'm probably not actually allowed to redistribute here, so if you're the owner of one of those libraries and would like the files removed, let me know. I've included them to aid those who would like a quick start and not have to search for the many dependencies of this project. Some of these libraries have disappeared from the web entirely, so searching for them may be futile anyway. I just want people to make cool things with this library, and hope you understand.