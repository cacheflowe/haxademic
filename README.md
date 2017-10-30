# Haxademic

Haxademic is my personal Processing-based creative coding toolkit, built to run in Eclipse with Java and [Processing](http://processing.org/). It's a starting point for interactive visuals, rendering and desktop/installation apps. It requires several essential Java/Processing libraries and wraps them up to play nicely with each other. It also solves a number of problems faced by (potentially) thread-unsafe hardware inputs like audio, Kinect, MIDI and OSC.

## State of affairs
While the code has been open-sourced, I don't plan on making it easy/viable for others to use. This repository is more about sharing the interesting code within, and I fully endorse borrowing it however you see fit. I've outlined some useful code below.

## Interesting code

`src.com.haxademic.core.app`

* `PAppletHax` - This is my base class for every Processing app I build. It initializes the app based on `AppSettings` properties that are read in via `data/properties/run.proerties`, and override .properties file, or finally in the `overridePropsFile()` method.
* `P` - This class holds static references to the current `PAppletHax` instance, so I don't have to pass this reference around everywhere.

`src.com.haxademic.core.audio`

* `AudioInputWrapper` - This is intended to be a common interface between several possible Java libraries that do FFT analysis on an incoming audio signal.

`src.com.haxademic.core.data`

* `AppStore`, `IAppStoreUpdatable` - A singleton data store and emitter. As values are updated, subscribers are notified.

* `ConvertUtil` - A collection of basic Java type conversion methods.

* `FloatBuffer` - An object that keep a FIFO buffer of incoming data for smoothing purposes.

* `SavedRectangle` - A text-file-backed GUI-draggable rectangle for mapping and screen subdividing purposes. *Should be moved to projection mapping tools package*

`src.com.haxademic.core.debug`

* `DebugUtil` - A collection of extra logging methods.

* `DebugView` - Instantiated with every `PAppletHax` app, allows us to toggle and add properties to the `DebugView` HashMap to show realtime values on-screen, rather than trying to watch Java console values fly by. Press `/` to toggle

* `JavaInfo` - Tons of methods to print out Java & system properties.

`src.com.haxademic.core.draw.color`

ColorHaxEasing.java
ColorUtil.java
EasedRGBColor.java
ImageGradient.java

`src.com.haxademic.core.draw.context`

DrawUtil.java
OpenGLUtil.java
OrientationUtil.java

`src.com.haxademic.core.draw.filters.shaders`

BadTVGlitchFilter.java
BadTVLinesFilter.java
BaseFilter.java
BlurBasicFilter.java
BlurHFilter.java
BlurProcessingFilter.java
BlurVFilter.java
BrightnessFilter.java
ChromaColorFilter.java
ChromaKeyFilter.java
ColorCorrectionFilter.java
ColorDistortionFilter.java
ColorizeFilter.java
ColorizeTwoColorsFilter.java
ContrastFilter.java
CubicLensDistortionFilter.java
DeformBloomFilter.java
DeformTunnelFanFilter.java
DilateFilter.java
EdgeColorDarkenFilter.java
EdgeColorFadeFilter.java
EdgesFilter.java
EmbossFilter.java
ErosionFilter.java
FlipHFilter.java
FXAAFilter.java
GlowFilter.java
HalftoneFilter.java
HalftoneLinesFilter.java
HueFilter.java
InvertFilter.java
KaleidoFilter.java
LeaveBlackFilter.java
LiquidWarpFilter.java
MirrorFilter.java
PixelateFilter.java
RadialBlurFilter.java
RadialRipplesFilter.java
RotateFilter.java
SaturationFilter.java
SharpenFilter.java
SphereDistortionFilter.java
ThresholdFilter.java
VignetteAltFilter.java
VignetteFilter.java
WarperFilter.java
WobbleFilter.java

`src.com.haxademic.core.draw.image`

AnimatedGifEncoder.java
Base64Image.java
BrightnessBumper.java
FractalBrownianMotion.java
ImageCyclerBuffer.java
ImageSequenceMovieClip.java
ImageUtil.java
MotionBlurPGraphics.java
PerlinTexture.java
ScreenUtil.java
TickerScroller.java
TiledTexture.java

`src.com.haxademic.core.draw.mapping`

PGraphicsKeystone.java

`src.com.haxademic.core.draw.particle`

ForceDirectedLayout.java
VectorFlyer.java

`src.com.haxademic.core.draw.shapes`

BoxBetween.java
CacheFloweLogo.java
Extrude2dPoints.java
Gradients.java
Icosahedron.java
MarchingCubes.java
MeshShapes.java
PShapeSolid.java
PShapeUtil.java
Shapes.java
Superformula.java
TextToPShape.java

`src.com.haxademic.core.file`

ConfigTextFile.java
DirImageLoader.java
FileUtil.java

`src.com.haxademic.core.math.easing`

EasingFloat.java
EasingFloat3d.java
EasingPowInterp.java
ElasticFloat.java
ElasticFloat3D.java
FloatBuffer.java
IEasingValue.java
LinearFloat.java
Penner.java

`src.com.haxademic.core.math`

MagicNumbers.java
MathUtil.java

`src.com.haxademic.core.net`

JSONUtil.java
WebServer.java
WebServerRequestHandler.java
WebSocketRelay.java
WebSocketServer.java

`src.com.haxademic.core.render`

GifRenderer.java
JoonsWrapper.java
MIDISequenceRenderer.java
Renderer.java

`src.com.haxademic.core.system`

AppRestart.java
JavaInfo.java
P5Properties.java
SecondScreenViewer.java
SystemUtil.java
WindowsSystemUtil.java

`src.com.haxademic.core.text`

FontUtil.java
RandomStringUtil.java
StringFormatter.java
ValidateUtil.java

`src.com.haxademic.core.ui`

Button.java
CursorToggle.java
IMouseable.java
TextButton.java
TextInput.java


## General Eclipse/Processing Tips

Use the following VM Arguments when running the Java Application to increase memory allocated to your app

* `-Xmx2048M`
* `-Xms1024M`

or

* `-Xmx4G`
* `-Xms2G`

* If you want to wipe your `bin/` directory, you'll have to do a **Project -> Clean…** in Eclipse afterwards.

## Publish a .jar of the /core
```
$ cd haxademic/bin
$ jar cfv ../../ohheckyeah/ohheckyeah-games-java/lib/haxademic/haxademic.jar ./com/haxademic/core/*
```

## Licensing

The Haxademic codebase and apps are [MIT licensed](https://raw.github.com/cacheflowe/haxademic/master/LICENSE), so do what you want with these files. Feel free to let me know that you're using it for something cool. I've added 3rd-party .jar files and compiled Java libraries that I'm probably not actually allowed to redistribute here, so if you're the owner of one of those libraries and would like the files removed, let me know. I've included them to aid those who would like a quick start and not have to search for the many dependencies of this project. Some of these libraries have disappeared from the web entirely, so searching for them may be futile anyway. I just want people to make cool things with this library, and hope you understand.
