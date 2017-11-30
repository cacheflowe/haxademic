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
