<img src="https://raw.githubusercontent.com/cacheflowe/haxademic/master/data/haxademic/images/haxademic-logo.png" alt="Haxademic" width="200"/>

# Haxademic

Haxademic is my personal Processing-based creative coding toolkit, built to run in Eclipse with Java and the latest version of [Processing](http://processing.org/). It's a starting point for interactive visuals, rendering and desktop/installation apps. It requires several essential Java/Processing libraries and wraps them up to play nicely with each other.

## State of affairs

While the code has been open-sourced, I don't plan on making it easy/viable for others to use. This repository is more about sharing the interesting code within, and I fully endorse borrowing it however you see fit.

## Notable code

`com.haxademic.core.app`

* __[PAppletHax](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/app/PAppletHax.java)__ - This is the base class for every Processing app that I build. It initializes tools for app-level concerns, rendering, multiple input devices, audio FFT, and debugging tools. Tons of additional tools and utilities can be found in the library.

* __[P](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/app/P.java)__ - This class holds static references and methods - primarily to the app instance so I don't have to pass it around everywhere.

`com.haxademic.core.audio.analysis.input`

* __[IAudioInput](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/audio/analysis/input/IAudioInput.java)__ - A common interface between several Java libraries that run FFT analysis and beat detection on an incoming audio signal. Choose between [Beads](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/audio/analysis/input/AudioInputBeads.java), [Minim](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/audio/analysis/input/AudioInputMinim.java), [ESS](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/audio/analysis/input/AudioInputESS.java) or [Processing Sound](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/audio/analysis/input/AudioInputProcessingSound.java), via [AppSettings](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/demo/audio/analysis/Demo_IAudioInput.java).

* __[AudioStreamData](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/audio/analysis/input/AudioStreamData.java)__ - The common data storage object for audio analysis results.

`com.haxademic.core.audio`

* __[NormalizeMonoWav](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/audio/NormalizeMonoWav.java)__ - Normalizes a mono .wav file without any external libraries.

* __[WavPlayer](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/audio/WavPlayer.java)__ - Play a .wav file.

* __[WavRecorder](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/audio/WavRecorder.java)__ - Record a .wav file.

... more to come

## Dependencies

Haxademic uses the following Java & Processing libraries, which I've included in this repository so you don't have to find them yourself (more on that below):

* [Processing Core](http://processing.org/) (view the [Processing for Eclipse instructions](http://processing.org/learning/eclipse/))
* [Beads](http://www.beadsproject.net/)
* [blobDetection](http://www.v3ga.net/processing/BlobDetection/)
* [ControlP5](http://www.sojamo.de/libraries/controlP5/)
* [DMXP512](https://github.com/hdavid/dmxP512)
* [ESS](https://web.archive.org/web/20171209153252/http://www.tree-axis.com/Ess/)
* [Geomerative](http://www.ricardmarxer.com/geomerative/)
* [He_Mesh](http://hemesh.wblut.com/)
* [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket)
* [Java Image Filters](http://www.jhlabs.com/ip/filters/index.html)
* [jetty](https://www.eclipse.org/jetty/)
* [Joons renderer](https://github.com/joonhyublee/joons-renderer/wiki)
* [KinectPV2](https://github.com/ThomasLengeling/KinectPV2)
* [Leap Motion for Processing](https://github.com/voidplus/leap-motion-processing/)
* [minim](http://code.compartmental.net/tools/minim/)
* [OpenKinect for Processing](https://github.com/shiffman/OpenKinect-for-Processing)
* [oscP5](http://www.sojamo.de/libraries/oscP5/)
* [PixelFlow](https://github.com/diwi/PixelFlow)
* [Poly2Tri](https://github.com/orbisgis/poly2tri.java)
* [Super CSV](http://supercsv.sourceforge.net/)
* [simple-openni](https://github.com/totovr/SimpleOpenni)
* [themidibus](https://github.com/sparks/themidibus)
* [toxiclibs](http://toxiclibs.org/)
* [UDP Processing Library](http://ubaa.net/shared/processing/udp/)
* [UMovieMaker](https://github.com/mariuswatz/modelbuilder)


## Installing / Compiling

* If you're on OS X, it's helpful to see hidden files. Run this command in Terminal:
	* `defaults write com.apple.finder AppleShowAllFiles YES`
* Download the standard Eclipse IDE for Java development, and the Java Development Kit itself:
	* [Eclipse](http://www.eclipse.org/)
	* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) - After installing JDK 1.8, open Eclipse, go to **Preferences** then **Java -> Installed JREs**, and click **Search...** to have Eclipse find the newly-installed library.
* Clone or [download](https://github.com/cacheflowe/haxademic/archive/master.zip) the `haxademic` project
* Open Eclipse and: **File -> Import -> General / Existing Projects into Workspace**
	* Choose the `haxademic` directory that you cloned/downloaded, press **Finish**, and the project should be ready to use.
* In the **Package Explorer** in Eclipse, right-click the `lib` directory and select **Refresh**. This will let Eclipse know that you've added the appropriate libraries on your file system.
* Make sure you're compiling with Java 1.8:
	* Right-click the `haxademic` project in the **Package Explorer** or **Navigator** window and click **Properties**
	* Click the **Java Compiler** section and check the **Enable project specific settings** box on the right
	* Select **1.8** as your **Compiler compliance level**, if possible
	* If "Configure the **Installed JREs**" is shown at the bottom of this window, click that, make sure the **1.8** item is checked, then click OK.
* Right-click on any of the demo apps within `src/com/demo/` and choose **Run As -> Java Application** from the menu. This will create a run configuration for the app.
* If it's necessary to add more RAM to the app, go to **Run -> Run Configurations**, select your app and add the following **VM Arguments** when running the Java Application to increase memory allocated to your app. This is a minimum of 1gb and a maximum of 4gb of RAM:
	* `-Xms1G`
	* `-Xmx4G`

## Licensing

The Haxademic codebase and apps are [MIT licensed](https://raw.github.com/cacheflowe/haxademic/master/LICENSE), so do what you want with these files. Feel free to let me know that you're using it for something cool. I've added 3rd-party .jar files and compiled Java libraries that I'm probably not actually allowed to redistribute here, so if you're the owner of one of those libraries and would like the files removed, let me know. I've included them to aid those who would like a quick start and not have to search for the many dependencies of this project. Some of these libraries have disappeared from the web entirely, so searching for them may be futile anyway. I just want people to make cool things with this library, and hope you understand.
