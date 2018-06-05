# Haxademic

Haxademic is my personal Processing-based creative coding toolkit, built to run in Eclipse with Java and the latest version of [Processing](http://processing.org/). It's a starting point for interactive visuals, rendering and desktop/installation apps. It requires several essential Java/Processing libraries and wraps them up to play nicely with each other.

## State of affairs
While the code has been open-sourced, I don't plan on making it easy/viable for others to use. This repository is more about sharing the interesting code within, and I fully endorse borrowing it however you see fit. I've outlined some useful code below.

## Notable code within:

`src.com.haxademic.core.app`

* __[PAppletHax](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/app/PAppletHax.java)__ - This is the base class for every Processing app that I build. It initializes the app based on `AppSettings` properties that are read in via `data/properties/run.proerties`, and override .properties file, or finally in the `overridePropsFile()` method.
* __[P](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/app/P.java)__ - This class holds static references to the current `PAppletHax` instance, so I don't have to pass the reference to the app around everywhere.

`src.com.haxademic.core.audio`

* __[AudioInputWrapper](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/audio/AudioInputWrapper.java)__ - This is intended to be a common interface between several possible Java libraries that do FFT analysis on an incoming audio signal.

`src.com.haxademic.core.data`

* `AppStore`, `IAppStoreUpdatable` - A singleton data store and emitter. As values are updated, subscribers are notified.

* `ConvertUtil` - A collection of basic Java type conversion methods.

* `FloatBuffer` - An object that keep a FIFO buffer of incoming data for smoothing purposes.

`src.com.haxademic.core.debug`

* `DebugUtil` - A collection of extra logging methods.

* `DebugView` - Instantiated with every `PAppletHax` app, allows us to toggle and add properties to the `DebugView` HashMap to show realtime values on-screen, rather than trying to watch Java console values fly by. Press `/` to toggle

* `JavaInfo` - Tons of methods to print out Java & system properties.

`src.com.haxademic.core.draw.color`

* `ColorHaxEasing` - An object that represents a single color with interpolation and helpful getter/setter methods.

* `ColorUtil` - Handy color conversion methods

* `EasedRGBColor`

* `ImageGradient` - Loads an image and scrubs across it horizontally. This is a gradient object that can be sampled from 0-1.

`src.com.haxademic.core.draw.context`

* `DrawUtil`

* `OpenGLUtil`

* `OrientationUtil`

`src.com.haxademic.core.draw.filters.shaders`

* `BadTVGlitchFilter`

* `BadTVLinesFilter`

* `BaseFilter`

* `BlurBasicFilter`

* `BlurHFilter`

* `BlurProcessingFilter`

* `BlurVFilter`

* `BrightnessFilter`

* `ChromaColorFilter`

* `ChromaKeyFilter`

* `ColorCorrectionFilter`

* `ColorDistortionFilter`

* `ColorizeFilter`

* `ColorizeTwoColorsFilter`

* `ContrastFilter`

* `CubicLensDistortionFilter`

* `DeformBloomFilter`

* `DeformTunnelFanFilter`

* `DilateFilter`

* `EdgeColorDarkenFilter`

* `EdgeColorFadeFilter`

* `EdgesFilter`

* `EmbossFilter`

* `ErosionFilter`

* `FlipHFilter`

* `FXAAFilter`

* `GlowFilter`

* `HalftoneFilter`

* `HalftoneLinesFilter`

* `HueFilter`

* `InvertFilter`

* `KaleidoFilter`

* `LeaveBlackFilter`

* `LiquidWarpFilter`

* `MirrorFilter`

* `PixelateFilter`

* `RadialBlurFilter`

* `RadialRipplesFilter`

* `RotateFilter`

* `SaturationFilter`

* `SharpenFilter`

* `SphereDistortionFilter`

* `ThresholdFilter`

* `VignetteAltFilter`

* `VignetteFilter`

* `WarperFilter`

* `WobbleFilter`


`src.com.haxademic.core.draw.image`

* `AnimatedGifEncoder`

* `Base64Image`

* `BrightnessBumper`

* `FractalBrownianMotion`

* `ImageCyclerBuffer`

* `ImageSequenceMovieClip`

* `ImageUtil`

* `MotionBlurPGraphics`

* `PerlinTexture`

* `ScreenUtil`

* `TickerScroller`

* `TiledTexture`


`src.com.haxademic.core.draw.mapping`

* `SavedRectangle` - A text-file-backed GUI-draggable rectangle for mapping and
 screen subdividing purposes.

* `PGraphicsKeystone`


`src.com.haxademic.core.draw.particle`

* `ForceDirectedLayout`

* `VectorFlyer`


`src.com.haxademic.core.draw.shapes`

* `BoxBetween`

* `CacheFloweLogo`

* `Extrude2dPoints`

* `Gradients`

* `Icosahedron`

* `MarchingCubes`

* `MeshShapes`

* `PShapeSolid`

* `PShapeUtil`

* `Shapes`

* `Superformula`

* `TextToPShape`


`src.com.haxademic.core.file`

* `ConfigTextFile`

* `DirImageLoader`

* `FileUtil`


`src.com.haxademic.core.math.easing`

* `EasingFloat`

* `EasingFloat3d`

* `EasingPowInterp`

* `ElasticFloat`

* `ElasticFloat3D`

* `FloatBuffer`

* `IEasingValue`

* `LinearFloat`

* `Penner`


`src.com.haxademic.core.math`

* `MagicNumbers`

* `MathUtil`


`src.com.haxademic.core.net`

* `JSONUtil`

* `WebServer`

* `WebServerRequestHandler`

* `WebSocketRelay`

* `WebSocketServer`


`src.com.haxademic.core.render`

* `GifRenderer`

* `JoonsWrapper`

* `MIDISequenceRenderer`

* `Renderer`


`src.com.haxademic.core.system`

* `AppRestart`

* `JavaInfo`

* `P5Properties`

* `SecondScreenViewer`

* `SystemUtil`

* `WindowsSystemUtil`


`src.com.haxademic.core.text`

* `FontUtil`

* `RandomStringUtil`

* `StringFormatter`

* `ValidateUtil`


`src.com.haxademic.core.ui`

* `Button`

* `CursorToggle`

* `IMouseable`

* `TextButton`

* `TextInput`



## General Eclipse/Processing Tips

Use the following VM Arguments when running the Java Application to increase memory allocated to your app

* `-Xmx2048M`
* `-Xms1024M`

or

* `-Xmx4G`
* `-Xms2G`

* If you want to wipe your `bin/` directory, you'll have to do a **Project -> Clean…** in Eclipse afterwards.

## Publish a .jar library

In Eclipse:
  * Go to `File` -> `Export...`
  * Select `Java` -> `Jar File`
  * Select the src files that you want to package up. In this case, everything in `com.haxademic.core`
  * Select the save location & jar name
  * Click `Next`
  * Check `Save the description file...` as a `.jardesc` config file
  * Click `Finish`
  * Next time, if package files haven't been added or removed, you can just double-click the new `.jardesc` file in Eclipse, and it will republish the .jar

This used to work, but not anymore (for me):

```
$ cd haxademic/bin
$ jar cfv ../../ohheckyeah/ohheckyeah-games-java/lib/haxademic/haxademic.jar ./com/haxademic/core/*
```

## Licensing

The Haxademic codebase and apps are [MIT licensed](https://raw.github.com/cacheflowe/haxademic/master/LICENSE), so do what you want with these files. Feel free to let me know that you're using it for something cool. I've added 3rd-party .jar files and compiled Java libraries that I'm probably not actually allowed to redistribute here, so if you're the owner of one of those libraries and would like the files removed, let me know. I've included them to aid those who would like a quick start and not have to search for the many dependencies of this project. Some of these libraries have disappeared from the web entirely, so searching for them may be futile anyway. I just want people to make cool things with this library, and hope you understand.
