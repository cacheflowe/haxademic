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

* `DebugView` - Instantiated with every `PAppletHax` app, allows us to toggle and add properties to the `DebugView` HashMap to show realtime values on-screen, rather than trying to watch Java console values fly by.

* `JavaInfo` - Tons of methods to print out Java-accessible system properties.

`src.com.haxademic.core.draw`

* More coming soon...


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
