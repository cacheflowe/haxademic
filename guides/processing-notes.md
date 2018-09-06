# Processing notes

#### Notes on Advanced OpenGL in Processing:

* [https://github.com/processing/processing/wiki/Advanced-OpenGL](https://github.com/processing/processing/wiki/Advanced-OpenGL)

#### Run an app with an undecorated window

More under-the-hood code [here](https://github.com/praxis-live/praxis/blob/master/praxis.video.pgl/src/net/neilcsmith/praxis/video/pgl/PGLPlayer.java#L251)

```
public void settings() {
  fullScreen();
}

public void setup() {
  int appW = 640;
  int appH = 480;
  surface.setSize(appW, appH);
  surface.setLocation(displayWidth/2 - appW/2, displayHeight/2 - appH/2);
}

public void draw() {
  background(127 + 127 * sin(frameCount * 0.01));
}
```

#### Run a thread inline

```
new Thread(new Runnable() { public void run() {
  doSomething();
}}).start();

```

#### Sort an array

```
Arrays.sort(myArray, compare);

public static Comparator<CustomObject> compare = new Comparator<CustomObject>() {
  @Override
  public int compare(CustomObject a, CustomObject b){
  return (a.z() < b.z()) ? -1 : (a.z() > b.z()) ? 1 : 0;
  }
};
```

#### Create a HashMap literal (almost)

```
protected static HashMap<String, String> colors;
static
  {
  colors = new HashMap<String, String>();
  colors.put("blue", "#0000ff");
  colors.put("red", "#ff0000");
  colors.put("yellow", "#ffff00");
  }
```

#### Pick a random enum

```
enum DrawMode {
  Color,
  Textured,
  Points,
}
protected DrawMode drawMode;

private static final List<DrawMode> VALUES = Collections.unmodifiableList(Arrays.asList(DrawMode.values()));
private static final int SIZE = VALUES.size();
private static final Random RANDOM = new Random();
public static DrawMode randomDrawMode()  {
  return VALUES.get(RANDOM.nextInt(SIZE));
}

// choose a random mode
drawMode = randomDrawMode();
```

#### Errors

```
java.lang.ArrayIndexOutOfBoundsException:
	at processing.opengl.PShapeOpenGL.scaleTextureUV(PShapeOpenGL.java:838)
```

* This is likely a result of swapping textures of different sizes. Use a PGraphics instance, and update that instead of swapping image objects for a PShape texture.

#### General Eclipse/Processing Tips

Use the following VM Arguments when running the Java Application to increase memory allocated to your app

* `-Xmx2048M`
* `-Xms1024M`

or

* `-Xmx4G`
* `-Xms2G`

* If you want to wipe your `bin/` directory, you'll have to do a **Project -> Clean…** in Eclipse afterwards.

#### Publish a .jar library

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
