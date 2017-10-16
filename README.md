# Haxademic

Haxademic is my personal Processing-based creative coding toolkit, built to run in Eclipse with Java and [Processing](http://processing.org/). It's a starting point for interactive visuals, rendering and desktop/installation apps. It requires several essential Java/Processing libraries and wraps them up to play nicely with each other. It also solves a number of problems faced by (potentially) thread-unsafe hardware inputs like audio, Kinect, MIDI and OSC.

## State of affairs
While the code has been open-sourced, I don't plan on making it easy/viable for others to use. This repository is more about sharing the interesting code within, and I fully endorse borrowing it however you see fit. I've outlined some useful code below.

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
