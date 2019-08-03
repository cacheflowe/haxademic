<img src="https://raw.githubusercontent.com/cacheflowe/haxademic/master/data/haxademic/images/haxademic-logo.png" alt="Haxademic" width="160"/>

# Haxademic

Haxademic is my personal Processing-based creative coding toolkit, built to run in Eclipse with Java and the latest version of [Processing](http://processing.org/). It's a starting point for interactive visuals, installations, rendering and writing Processing "sketches" in a more robust environment. It loads a bunch of useful Java/Processing libraries and contains a ton of other useful tools that I've written.

## State of affairs

While the code has been open-sourced, I don't plan on making it easy/viable for others to use - I don't have the time or desire to support it. This repository is more about sharing the interesting code within, and I fully endorse borrowing it however you see fit.

## Notable code

Below you'll find a long list of classes and utilities that I've built to make my life easier. I've tried to make as many basic [demos](https://github.com/cacheflowe/haxademic/tree/master/src/com/haxademic/demo) as possible for all of these features. The `demo` package mostly mirrors the `core` directory and should give you an idea of how to implement these object on your own.

* [Data](#data)
* [Debug](#debug)
* [Draw](#draw)
* [File](#file)
* [Hardware](#hardware)
* [Math](#math)
* [Media](#media)
* [Net](#net)
* [Render](#render)
* [System](#system)
* [Text](#text)
* [UI](#ui)

### App

`com.haxademic.core.app`

* __[PAppletHax](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/app/PAppletHax.java)__ - This is the base class for every Processing app that I build. It initializes tools for app-level concerns, rendering, multiple input devices, audio FFT, and debugging tools. Tons of additional tools and utilities can be found in the library.

* __[P](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/app/P.java)__ - This class holds static references and methods - primarily to the app instance so I don't have to pass it around everywhere.

`com.haxademic.core.app.config`

* __[AppSettings](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/app/config/AppSettings.java)__ - Static constants to help set app properties and initialize special objects in `PAppletHax`. Used in tandem with `P5Properties`.

* __[P5Properties](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/app/config/P5Properties.java)__ - Loads properties from `data/properties/run.properties` (or an alternate specified .properties file), using the same string constants in `AppSettings`. All of these properties can be overridden in PAppletHax in the automatically-called `overridePropsFile()` function on app initialization.

### Data

`com.haxademic.core.data`

* __[ConvertUtil](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/data/ConvertUtil.java)__ - A collection of basic Java type-conversion methods.

* __[Patterns](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/data/patterns)__ - A collection of pattern generators for step sequencer.

`com.haxademic.core.data.constants`

* __[GLBlendModes](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/data/constants/GLBlendModes.java)__ - Fancy OpenGL blend modes with helper methods to set them on a given PGraphics context.

* __[PBlendModes](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/data/constants/PBlendModes.java)__ - Static constants list of Processing [blend modes](https://processing.org/reference/blendMode_.html).

* __[PRenderers](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/data/constants/PRenderers.java)__ - Static constants list of Processing [renderers](https://processing.org/reference/size_.html).

* __[PShapeTypes](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/data/constants/PShapeTypes.java)__ - Static constants list of Processing [shape types](https://processing.org/reference/beginShape_.html).

* __[PStrokeCaps](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/data/constants/PStrokeCaps.java)__ - Static constants list of Processing [stoke cap types](https://processing.org/reference/strokeCap_.html).

* __[PTextAlign](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/data/constants/PTextAlign.java)__ - Static constants list of Processing [text alignment options](https://processing.org/reference/textAlign_.html).

`com.haxademic.core.data.store`

* __[AppStore](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/data/store/AppStore.java)__ - A singleton data store and emitter. As values are updated, subscribers are notified. Uses __[IAppStoreListener](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/data/store/IAppStoreListener.java)__ as the callback interface for `AppStore` updates.

* __[AppStoreDistributed](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/data/store/AppStoreDistributed.java)__ - A WebSockets-enabled extension of `AppStore` to keep multiple machines in sync with a shared data structure.

### Debug

`com.haxademic.core.debug`

* __[DebugView](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/debug/DebugView.java)__ - Instantiated with every `PAppletHax` app, allows us to toggle and add properties to the `DebugView` HashMap to show realtime values on-screen, rather than trying to watch Java console values fly by. Press `/` to toggle. Also includes a panel for key commands and extra developer-defined info.

### Draw

`com.haxademic.core.draw.camera`

* __[CameraUtil](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/camera/CameraUtil.java)__ - Primarily just a helper method to increase the camera distance on a PGraphics context.

`com.haxademic.core.draw.color`

* __[ColorUtil](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/color/ColorUtil.java)__ - Handy color conversion methods.

* __[EasingColor](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/color/EasingColor.java)__ - An object that represents a single color with interpolation and helpful getter/setter methods. Has some overlap with `ColorUtil` - the two classes should possibly be merged.

* __[ImageGradient](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/color/ImageGradient.java)__ - Loads an image and samples its colors horizontally. Works well for gradients and comes with a library of presets.

`com.haxademic.core.draw.context`

* __[OpenGL32Util](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/context/OpenGL32Util.java)__ - Some helper methods to move graphics between 8-bit Processing graphics for display and PixelFlow 32-bit graphics contexts for higher-resolution drawing and shaders.

* __[OpenGLUtil](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/context/OpenGLUtil.java)__ - Lower-level helper methods to set OpenGL flags that aren't obviously available in Processing.

* __[OrientationUtil](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/context/OrientationUtil.java)__ - Helper methods to orient the graphics context rotation towards a specific 3d location. These utilities help point 3d objects toward each other.

* __[PG](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/context/PG.java)__ - Lots of static helper methods to set properties on the specified PGraphics context. Used constantly throughout the core and demos.

`com.haxademic.core.draw.filters.pgraphics`

* __[A collection](https://github.com/cacheflowe/haxademic/tree/master/src/com/haxademic/core/draw/filters/pgraphics)__ of PGraphics-based image/video filters. These effects redraw an image in traditional Processing style on a PGraphics buffer.

`com.haxademic.core.draw.filters.pshader`

* __[A collection](https://github.com/cacheflowe/haxademic/tree/master/src/com/haxademic/core/draw/filters/pshader)__ of (over 100) PShader filter effects. These Java classes are convenience wrappers for GLSL programs/files and provide a common interface and (as minimal as) one-line implementations for shader filters. Shader uniforms are exposed through public setter methods.

`com.haxademic.core.draw.image`

* __[AnimatedGifEncoder](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/image/AnimatedGifEncoder.java)__ - Renders a gif file directly out of a Processing app. I've mostly abandoned this in favor of rendering a video, then using conversion tools like ffmpeg to more precisely convert to gif.

* __[Base64Image](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/image/Base64Image.java)__ - Encodes and decodes between a PImage and a base64-encoded string.

* __[BufferActivityMonitor](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/image/BufferActivityMonitor.java)__ - Gives us an "activity" rating of a moving image, like a webcam, video, or other PGraphics instance that animates. Useful for motion detection.

* __[BufferColorObjectDetection](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/image/BufferColorObjectDetection.java)__ - Finds the center of mass of a specific color (within a threshold) on an image. Useful for object tracking in controlled situations.

* __[BufferMotionDetectionMap](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/image/BufferMotionDetectionMap.java)__ - Uses shaders for fast frame-diffing to detect motion between video/camera frames. Includes helpers to find random motion points in the motion map result.

* __[BufferThresholdMonitor](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/image/BufferThresholdMonitor.java)__ - Copies an image to a low-res buffer, runs a threshold filter, and counts white vs black pixels. Useful as a step in certain CV processes.

* __[FrozenImageMonitor](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/image/FrozenImageMonitor.java)__ - Helps detect a frozen video stream (from a webcam, most likely). Camera feeds have some natural noise that will change over time, even if the subject doesn't. If the feed is frozen, this can be detected.

* __[ImageCyclerBuffer](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/image/ImageCyclerBuffer.java)__ - Uses GLSL transitions to create a slideshow from an array of PImages.

* __[ImageSequenceMovieClip](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/image/ImageSequenceMovieClip.java)__ - Loads and plays back an image sequence like a Movie object does for video files.

* __[ImageSequenceRecorder](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/image/ImageSequenceRecorder.java)__ - Builds an array of PGraphics objects to record recent video frames and play them back for time distortion effects or short video compiling.

* __[ImageUtil](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/image/ImageUtil.java)__ - Lots of tools for dealing with images and drawing them to screen.

* __[TickerScroller](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/image/TickerScroller.java)__ - Repeats a texture across a PGraphics and scrolls.

* __[TiledTexture](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/image/TiledTexture.java)__ - A drawing helper that takes advantage of OpenGL's texture repeat function and lets us draw a rectangle with a texture fill. Includes zoom & rotation controls for fancy texture & pattern tiling.

`com.haxademic.core.draw.mapping`

* __[BaseSavedQuadUI](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/mapping/BaseSavedQuadUI.java)__ - Base class for `CaptureKeystoneToRectBuffer` and `PGraphicsKeystone`. Stores/recalls a quad to text file and provides a GUI and keyboard controls to move the vertices.

* __[CaptureKeystoneToRectBuffer](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/mapping/CaptureKeystoneToRectBuffer.java)__ - Reverse-projection-mapping tool. Pulls from an image with a configurable quad and draws to a PGraphics instance.

* __[PGraphicsKeystone](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/mapping/PGraphicsKeystone.java)__ - Projection-mapping tool. Draws a PGraphics buffer to screen, with custom keystoning backed by a text file.

* __[SavedRectangle](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/mapping/SavedRectangle.java)__ - Basic mapping tool. Provides rectangle data controlled by a mouse-based UI, backed by a text file.

`com.haxademic.core.draw.particle`

* __[ForceDirectedLayout](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/particle/ForceDirectedLayout.java)__ - Force-directed layout implementation.

* __[ParticleLauncher](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/particle/ParticleLauncher.java)__ - GPU particle implementation.

* __[VectorFlyer](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/particle/VectorFlyer.java)__ - 3D particle that flies toward a destination point.

`com.haxademic.core.draw.shapes`

* __[Extrude2dPoints](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/shapes/Extrude2dPoints.java)__ - Turns a set of 2d points into a 3d extrusion.

* __[Icosahedron](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/shapes/Icosahedron.java)__ - Icosahedron generator, with optional texture application.

* __[MarchingCubes](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/shapes/MarchingCubes.java)__ - Marching cubes implementation.

* __[MeshShapes](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/shapes/MeshShapes.java)__ - A collection of interesting generative mesh shape drawing functions.

* __[LinesDeformAndTextureFilter](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/shapes/pshader/LinesDeformAndTextureFilter.java)__ - GPU displacement shader for a textured LINES PShape.

* __[MeshDeformAndTextureFilter](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/shapes/pshader/MeshDeformAndTextureFilter.java)__ - GPU displacement shader for a standard PShape.

* __[PointsDeformAndTextureFilter](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/shapes/pshader/PointsDeformAndTextureFilter.java)__ - GPU displacement shader for a textured POINTS PShape.

* __[PShapeSolid](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/shapes/PShapeSolid.java)__ - Accepts a SPhape and tracks shared vertices, providing multiple deformation strategies without letting connected triangles diverge.

* __[PShapeUtil](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/shapes/PShapeUtil.java)__ - A large collection of normalization, texturing and other manipulation methods to apply to PShape objects.

* __[Shapes](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/shapes/Shapes.java)__ - Some custom mesh-drawing tools.

* __[Superformula](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/shapes/Superformula.java)__ - A superformula implementation.

* __[TextToPShape](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/shapes/TextToPShape.java)__ - Text with a custom font turned into a 2d or 3d mesh.

`com.haxademic.core.draw.textures`

* __[SimplexNoiseTexture](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/textures/SimplexNoiseTexture.java)__ - Fast simplex noise.

`com.haxademic.core.draw.textures.pgraphics`

* __[A collection](https://github.com/cacheflowe/haxademic/tree/master/src/com/haxademic/core/draw/textures/pgraphics)__ of PGraphics-based realtime textures, originally built as "VJ" clips/layers.

`com.haxademic.core.draw.textures.pshader`

* __[A collection](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/draw/textures/pshader/TextureShader.java)__ of (over 100) realtime shader textures, both original creations (prefixed with 'cacheflowe') and shaders ported from other artists on the web. Check the GLSL source for credits.

### File

* __[FileUtil](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/file/FileUtil.java)__ - File & directory methods to help with file creation, deletion & listing.

* __[PrefToText](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/file/PrefToText.java)__ - Saves & retries a float/int/String value from a text file.

* __[WatchDir](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/file/WatchDir.java)__ - Watches a directory & provides a delegate callback when files have changed.

### Hardware

* __[DMXWrapper](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/hardware/dmx/DMXWrapper.java)__ - Helper to "easily" connect and send messages to an ENTTEC DMX USB Pro.

* __[GamepadListener](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/hardware/gamepad/GamepadListener.java)__ - Uses JInput to receive messages from a gamepad controller.

* __[MidiText](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/hardware/midi/MidiText.java)__ - Parses a MIDI file.

* __[InputTrigger](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/hardware/shared/InputTrigger.java)__ - Accepts multiple inputs to trigger the same action. Keystrokes, MIDI, OSC, HTTP and more.

* __[IDepthCamera](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/hardware/depthcamera/cameras/IDepthCamera.java)__ - Interface for `KinectWrapperV1` and `KinectWrapperV2` and `RealSenseWrapper`.

* __[KinectAmbientActivityMonitor](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/hardware/depthcamera/KinectAmbientActivityMonitor.java)__ - Provides a general ambient activity value for a room.

* __[KinectRoomScanDiff](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/hardware/depthcamera/KinectRoomScanDiff.java)__ - Scans a room with the Kinect, then watches for depth differences from scan.

* __[KinectDepthSilhouetteSmoothed](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/hardware/depthcamera/KinectDepthSilhouetteSmoothed.java)__ - A hardware-accelerated smoothed silhouette image from raw depth data.

* __[KinectRegionGrid](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/hardware/depthcamera/KinectRegionGrid.java)__ - Breaks up kinect raw data into an x/z grid, with center-of-mass (joystick) and `active` readings for each quadrant.

* __[MouseUtil](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/hardware/mouse/MouseUtil.java)__ - Automates, moves & clicks the system mouse with a Java Robot.

* __[OscWrapper](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/hardware/osc/OscWrapper.java)__ - Wraps up OSC i/o functionality.

* __[PrintPageDirect](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/hardware/printer/PrintPageDirect.java)__ - Print an image to a physical printer directly from Processing.

* __[SerialDevice](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/hardware/serial/SerialDevice.java)__ - Basic input/output wrapping for Arduino and similar USB serial devices.

* __[WebCam](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/hardware/webcam/WebCam.java)__ - Inits a webcam via singleton or normal instances, and receives new frames by implementing `IWebCamCallback`. It stores & recalls your `Capture` configuration for the next app launch.

### Math

* __[DisplacementPoint](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/math/easing/DisplacementPoint.java)__ - Elastic displacement from a static point, based on an influencing external point.

* __[EasingBoolean](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/math/easing/EasingBoolean.java)__ - Lerps towards true or false over time. Switches when it reaches the end. Useful for dealing with noisy data.

* __[EasingFloat](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/math/easing/EasingFloat.java)__ - Lerps towards a target. Includes optional frame delay and acceleration.

* __[ElasticFloat](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/math/easing/ElasticFloat.java)__ - Lerps towards a target with Hooke's law springiness. Configurable friction and acceleration.

* __[FloatBuffer](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/math/easing/FloatBuffer.java)__ - A FIFO buffer of incoming float values for smoothing purposes.

* __[LinearFloat](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/math/easing/LinearFloat.java)__ - Linearly lerps towards a target. Includes optional frame delay. Works great in tandem with `Penner` equations.

* __[Penner](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/math/easing/Penner.java)__ - Robert Penner's easing equations.

* __[MathUtil](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/math/MathUtil.java)__ - Lots of useful math utility functions.

* __[SphericalCoord](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/math/SphericalCoord.java)__ - Spherical coordinate helper functions.

### Media

`com.haxademic.core.media`

* __[DemoAssets](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/media/DemoAssets.java)__ - A collection of media files (svg, obj, png, ttf, mp4) to help quickly load an asset for demo purposes. Files are pulled from `data/haxademic/`.

* __[MediaTimecodeTrigger](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/media/MediaTimecodeTrigger.java)__ - A helper object to run callbacks as audio or video playback reaches specified timecodes. Could be used for any time-tracked event triggering.

`com.haxademic.core.media.audio.playback`

* __[WavPlayer](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/media/audio/playback/WavPlayer.java)__ - Play a .wav file with Beads and cache for future plays.

`com.haxademic.core.media.audio.input`

* __[NormalizeMonoWav](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/media/audio/input/NormalizeMonoWav.java)__ - Normalizes a mono .wav file without any external libraries.

* __[WavRecorder](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/media/audio/input/WavRecorder.java)__ - Record a .wav file with Minim.

`com.haxademic.core.media.audio.analysis`

* __[IAudioInput](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/media/audio/analysis/IAudioInput.java)__ - A common interface between several Java libraries that run FFT analysis and beat detection on an incoming audio signal. Choose between [Beads](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/media/audio/analysis/AudioInputBeads.java), [Minim](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/media/audio/analysis/AudioInputMinim.java), [ESS](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/media/audio/analysis/AudioInputESS.java) or [Processing Sound](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/media/audio/analysis/AudioInputProcessingSound.java), via [AppSettings](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/demo/media/audio/analysis/Demo_IAudioInput.java).

* __[AudioStreamData](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/media/audio/analysis/AudioStreamData.java)__ - The common data storage object for audio analysis results.

`com.haxademic.core.media.video`

* __[MovieFinishedListener](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/media/video/MovieFinishedListener.java)__ - Adds a native listener for video playback completion.

* __[MovieToImageSequence](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/media/video/MovieToImageSequence.java)__ - Loads frames from a video into an array of PImages in realtime. Useful for fancier & faster playback/scrubbing through video frames without relying on Movie decoding once the frames are extracted.


### Net

* __[DashboardPoster](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/net/DashboardPoster.java)__ - Post debug info & screenshots to a web-based dashboard.

* __[FileDownloader](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/net/FileDownloader.java)__ - Download a file from the web to a local directory.

* __[IPAddress](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/net/IPAddress.java)__ - Get the local machine's IP address.

* __[JsonRequest](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/net/JsonRequest.java)__ - Make a JSON request to a web server.

* __[JsonUtil](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/net/JsonUtil.java)__ - JSON formatting utility.

* __[SocketServer](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/net/SocketServer.java)__ - Run a websocket server with Java-WebSocket.

* __[WebServer](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/net/WebServer.java)__ - Run a web server with Jetty.

### Render

* __[AnimationLoop](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/render/AnimationLoop.java)__ - Frame-counting helper to build looping animations.

* __[GifRenderer](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/render/GifRenderer.java)__ - Auto-render gifs from the app.

* __[ImageSequenceRenderer](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/render/ImageSequenceRenderer.java)__ - Auto-render an image sequence from the app.

* __[JoonsWrapper](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/render/JoonsWrapper.java)__ - Render high-quality raytraced geometry with Joons.

* __[VideoRenderer](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/render/VideoRenderer.java)__ - Render a video directly from the app.

### System

* __[AppRestart](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/system/AppRestart.java)__ - Kills & restarts the app!

* __[AppUtil](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/system/AppUtil.java)__ - Set app window properties and generate the script that was used to launch the app.

* __[CrashMonitor](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/system/CrashMonitor.java)__ - Launches a 2nd app window to monitor the first, in case of a crash.

* __[DateUtil](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/system/DateUtil.java)__ - Date & time helper functions.

* __[JavaInfo](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/system/JavaInfo.java)__ - Tons of methods to print out Java & system properties.

* __[SecondScreenViewer](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/system/SecondScreenViewer.java)__ - Launches a 2nd, scaled-down window to monitor the main window from a 2nd monitor.

* __[ScriptRunner](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/system/shell/ScriptRunner.java)__ - Cross-platform script runner - makes up for the difference between .cmd/bat and .sh scripts. Works with `IScriptCallback` delegate to callback when the script finishes.

* __[SystemUtil](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/system/SystemUtil.java)__ - Get a timestamp, take a screenshot, copy text to clipboard, open a web browser, check/kill system processes, run a Timer.

### Text

* __[FitTextSourceBuffer](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/text/FitTextSourceBuffer.java)__ - Generates a tightly-cropped texture from a String with a custom PFont.

* __[RandomStringUtil](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/text/RandomStringUtil.java)__ - Generates random Strings.

* __[StringBufferLog](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/text/StringBufferLog.java)__ - Generates a buffer of text for an on-screen log tail.

* __[StringFormatter](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/text/StringFormatter.java)__ - Format numbers to strings.

### UI

* __[UISlider](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/ui/PrefSlider.java)__ - A text file-backed slider UI.

* __[UIButton](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/ui/UIButton.java)__ - A Button object with optional toggle mode.

* __[UIControlPanel](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/ui/UIControlPanel.java)__ - A collection of `IUIControl` objects, auto-initialized with PAppletHax. Press `\` to toggle paneL, and add new sliders/buttons by accessing `p.ui`.


## Dependencies

Haxademic uses the following Java & Processing libraries, which I've included in this repository so you don't have to find them yourself (more on that below):

* [Processing Core](http://processing.org/) (view the [Processing for Eclipse instructions](https://processing.org/tutorials/eclipse/))
* [Beads](http://www.beadsproject.net/)
* [blobDetection](http://www.v3ga.net/processing/BlobDetection/)
* [DMXP512](https://github.com/hdavid/dmxP512)
* [ESS](https://web.archive.org/web/20171209153252/http://www.tree-axis.com/Ess/)
* [Geomerative](http://www.ricardmarxer.com/geomerative/)
* [He_Mesh](http://hemesh.wblut.com/)
* [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket)
* [Java Image Filters](http://www.jhlabs.com/ip/filters/index.html)
* [jetty](https://www.eclipse.org/jetty/)
* [JInput](https://github.com/jinput/jinput)
* [Joons renderer](https://github.com/joonhyublee/joons-renderer/wiki)
* [KinectPV2](https://github.com/ThomasLengeling/KinectPV2)
* [Leap Motion for Processing](https://github.com/voidplus/leap-motion-processing/)
* [minim](http://code.compartmental.net/tools/minim/)
* [OpenKinect for Processing](https://github.com/shiffman/OpenKinect-for-Processing)
* [oscP5](http://www.sojamo.de/libraries/oscP5/)
* [PixelFlow](https://github.com/diwi/PixelFlow)
* [Poly2Tri](https://github.com/orbisgis/poly2tri.java)
* [RealSenseProcessing](https://github.com/cansik/realsense-processing)
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
	* [Java 8 (OpenJDK HotSpot)](https://adoptopenjdk.net/) - After installing JDK 8, open Eclipse, go to **Preferences** then **Java -> Installed JREs**, and click **Search...** to have Eclipse find the newly-installed library.
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
