## Processing notes

Run an app with an undecorated window. More under-the-hood code [here](https://github.com/praxis-live/praxis/blob/master/praxis.video.pgl/src/net/neilcsmith/praxis/video/pgl/PGLPlayer.java#L251)

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

Run a thread inline

```
new Thread(new Runnable() { public void run() {
  doSomething();
}}).start();	

```

Sort an array

```
Arrays.sort(myArray, compare);
	
public static Comparator<CustomObject> compare = new Comparator<CustomObject>() {
  @Override
  public int compare(CustomObject a, CustomObject b){
  return (a.z() < b.z()) ? -1 : (a.z() > b.z()) ? 1 : 0;
  }
};
```

Errors

```
java.lang.ArrayIndexOutOfBoundsException:
	at processing.opengl.PShapeOpenGL.scaleTextureUV(PShapeOpenGL.java:838)
```

* This is likely a result of swapping textures of different sizes. Use a PGraphics instance, and update that instead of swapping image objects for a PShape texture.