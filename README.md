# Haxademic
Haxademic is a multimedia platform, built in Java and [Processing](http://processing.org/). It's a starting point for interactive visuals, giving you a unified environment for both realtime and rendering modes. It loads several Java libraries and wraps them up to play nicely with each other. It solves a number of problems faced by (potentially) thread-unsafe hardware inputs like audio, Kinect, MIDI and OSC. To view some projects created with the library, check out the [Haxademic Tumblr](http://haxademic.com/).

## State of affairs
While the code has been open-sourced, I haven't had time to write much (any) documentation, but I'm trying to get it there. You can see the example apps and sketches to get an idea of how to use various features. Even without fully installing everything, there's plenty of interesting code within, and I fully endorse borrowing it however you see fit in the meantime. If you're interested in collaborating, please contact me via my [GitHub account](http://github.com/cacheflowe), or my [web site](http://cacheflowe.com/contact).

## General Use / Tips

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
