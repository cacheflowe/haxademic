# Processing notes

## Notes on Advanced OpenGL in Processing:

* [https://github.com/processing/processing/wiki/Advanced-OpenGL](https://github.com/processing/processing/wiki/Advanced-OpenGL)

## Use newer versions of the Processing Video library

v2.0 beta1 will play 4k videos at least 100% faster/better than the current video library release.

* https://github.com/gohai/processing-video/releases/tag/v1.0.2
* https://github.com/processing/processing-video/releases/tag/r3-v2.0-beta1

## Run an app with an undecorated window

```java
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

## Run a thread inline

This is helpful for any non-drawing code that you need to run that might slow down the UI thread. One example is calling `play()` on a Movie object, which can freeze the UI thread, as seen below.

```java
new Thread(new Runnable() { public void run() {
	myMovie.play();
}}).start();
```

It's also a good idea to communicate with hardware on a Thread, as this can also slow down your app's framerate. You can pick up results in your `draw()` loop from the Thread by setting a "isBusy" boolean, and setting it to true when done. This ensures that your threaded operation takes as much time as it needs, while your graphics continue running fast.

```java
public boolean working = false;
public int result = 0;

public void draw() {
	// start a new Thread if the last is complete
	if(working == false) {
		startLongProcess();
	} else {
		// do nothing, wait for the results!
	}
	// draw the threaded result
	text(result, 20, 20);
}

public void startLongProcess() {
	working = true;
	new Thread(new Runnable() { public void run() {
	  result = serialDevice.update();
	  working = false;
	}}).start();
}
```

## Sort an array

```java
Arrays.sort(myArray, compare);

public static Comparator<CustomObject> compare = new Comparator<CustomObject>() {
  @Override
  public int compare(CustomObject a, CustomObject b){
  return (a.z() < b.z()) ? -1 : (a.z() > b.z()) ? 1 : 0;
  }
};
```

## Create a HashMap literal (almost)

```java
protected static HashMap<String, String> colors;
static
  {
  colors = new HashMap<String, String>();
  colors.put("blue", "#0000ff");
  colors.put("red", "#ff0000");
  colors.put("yellow", "#ffff00");
  }
```

## Iterate over a HashMap

```java
HashMap<Rectangle, PGraphics> screenBuffers = new HashMap<Rectangle, PGraphics>();
// ...

for (HashMap.Entry<Rectangle, PGraphics> entry : screenBuffers.entrySet()) {
	Rectangle rect = entry.getKey();
	PGraphics buffer = entry.getValue();
	// do something with the key/value
}
```

or

```java
for (Iterator<Entry<Rectangle, PGraphics>> iterator = screenSources.entrySet().iterator(); iterator.hasNext();) {
	HashMap.Entry<Rectangle, PGraphics> entry = iterator.next();
}
```

Switch from `HashMap` to a `LinkedHashMap` if you want the keys to come out in the order they were created!

If you run into a `ConcurrentModificationException` when looping over a HashMap, switch from this style of loop:

```java
for (IUIControl control : controls.values()) {
    control.update();
}
```

To the following safe option. More info [here](https://stackoverflow.com/questions/16180568/concurrentmodificationexception-with-linkedhashmap).

```java
for (HashMap.Entry<String, IUIControl> entry : controls.entrySet()) {
    String key = entry.getKey();
    IUIControl control = entry.getValue();
    control.update();
}
```

## Build an array of HashMap keys

```java
Set<String> keys = hashMap.keySet();
keysArray = new String[keys.size()];
int index = 0;
for(String element : keys) keysArray[index++] = element;
```

## Loop through the keys of a JSONObject

```java
Iterator<String> itr = curNumberData.keys().iterator();
while(itr.hasNext()){
    String key = itr.next();
    P.out(curNumberData.getInt(key));
}
```

## Pick a random enum

```java
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

## Extend a class inline

```java
PVector p = new PVector(0, 0, 0) {
  public String toString() {
    return "x: " + x + ", y: " + y + ", z: " + z;
  }
};
```

or 
  
```java
Movie vid = new Movie(this, "transit.mov") {
  @Override public void eosEvent() {
    super.eosEvent();
    myEoS();
  }
};
```

## Add a site's SSL cert for secure http/ws connections

If you see an error that looks like the following, try [this solution](https://stackoverflow.com/a/36427118).

```
javax.net.ssl.SSLHandshakeException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
```

* Step 1: Download the .crt file from the website
  * Visit the CMS in Chrome
  * Click the padlock to the left of the URL
  * Click "Connection is Secure"
  * Click "Certificate is valid"
  * Click the "Details" tab
  * Click "Export" and save the file
* Step 2: Add the certificate to the Java jre
  * Run the following command in an elevated command promt with administrative access. Make sure to use the correct paths to the Java keytool, the Java keystore, and the cert that you downloaded!
  * "C:\Program Files\Java\jdk17.xxx\bin\keytool.exe" -import -alias example -keystore "C:\Program Files\Java\jdk17.xxx\lib\security\cacerts" -file "website.com.crt"
  * Enter password: "changeit"
  * You should see a message confirming that the keystore was updated
  * Restart the entire app


## Play a 4k video (or just play videos faster) - THIS IS NOT VALID ANYMORE!!

* Use ffmpeg to compress your videos.
* Encoding at 24fps vs 30fps gains some performance headroom.
	* 60fps video doesn't seem likely to play back very well (I noticed dropped frames & chunky visual playback)
* I tested a bunch of different codecs, and by far the most performant was `mjpeg`
	* `ffmpeg -i input.mov -vcodec mjpeg -pix_fmt yuvj420p -q:v 2 -huffman optimal -vtag MJPG -an output.mov`
	* `-pix_fmt yuv420p`
	* See mjpeg options: `ffmpeg -h encoder=mjpeg`
		* https://stackoverflow.com/a/32151594
	* h264 was 2nd in performance and smooth playback, but there was a pretty big gain by using mjpeg, which results in a much larger filesize (and might not be able to play audio)
	* BUT! mjpeg has bad compression, so I'm recommending h264
	* https://blog.angelcam.com/what-is-the-difference-between-mjpeg-and-h-264/
* Upgrade the Processing video library to this beta version for far better performance and more supported codecs
	* https://github.com/processing/processing-video/releases/tag/r3-v2.0-beta1
	* It seems like using `jna.jar` from the current-release video library is helpful for stability...
* I also heard that uncompressed video could perform better because compressed codec decoding is slow. However, ProRes videos performed terribly, even with several different encoding methods.

## Add multiple identical webcams on Windows

* https://github.com/processing/processing-video/issues/68
* In Device Manager, be sure to "show hidden devices"

1. Press Windows Key + R and type devmgmt.msc in Run menu and press enter to open device manager.
2. Look for the device you want to rename and right click on it and click on Properties.
3. Now go to Details Tab and click on Property dropdown menu and select driver key and copy the key that appears.
4. Press Windows Key + R and write regedit in Run menu to open Registry Editor.
5. Now go to this directory HKEY_LOCAL_MACHINE -> SYSTEM -> ControlSet001 -> Enum and get permission on that folder, if you don’t know how to to get you can read in [Take Ownership of a Registry Key](https://www.maketecheasier.com/full-ownership-windows-registry-keys/)
6. When you’re done taking the ownership of that registry folder click CTRL+F to search and paste the driver key that we have copied at step 3 and it will find the driver you want to rename and you select friendly name below that one.
7. Double click on it and rename the second line with the name you want to put on it and click on ok.
8. Now you can close registry and go to device manager and check if your name is changed
NOTE:You may need to restart your PC to see the changes or Scan for hardware changes

## Errors

```java
java.lang.ArrayIndexOutOfBoundsException:
	at processing.opengl.PShapeOpenGL.scaleTextureUV(PShapeOpenGL.java:838)
```

* This is likely a result of swapping textures of different sizes. Use a PGraphics instance, and update that instead of swapping image objects for a PShape texture.

## General Eclipse/Processing Tips

Code will hot-reload when running an app in Debug mode. If it's not, make sure `Project -. Build Automatically` is active

Use the following VM Arguments when running the Java Application to increase memory allocated to your app

* `-Xmx4096M`
* `-Xms2048M`

or

* `-Xmx4G`
* `-Xms2G`

* Joshua Davis swears by these extra args as well:

```
-XX:+UseConcMarkSweepGC
-XX:+UseParNewGC
-XX:+CMSIncrementalPacing
-XX:+AggressiveOpts
-Djava.net.preferIPv4Stack=true
```

This will print Java environment properties, which can help while debugging:

```
 -XshowSettings:properties
```

* If you want to wipe your `bin/` directory, you'll have to do a **Project -> Clean** in Eclipse afterwards.

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

## Other Eclipse debugging tools

These tools allow for deeper inspection of your Java app when building in Eclipse

* [mat](https://www.eclipse.org/mat/)
* [VisualVM](https://visualvm.github.io/)


## Add another project's `src` 

This lets you use a source folder from another project, for your core library, for example

* Right click your project and "Build Path -> Link Source..."
* For "Linked folder location", browse to your other project's `src` directory
  * Give it a name like "haxademic-src"


## Deal with Java ssl cert issues

###  the CMS cert for Java apps

If the Java apps stop checking in to the CMS dashboard, an SSL cert could be the problem. Java might need you to add it to the Java keystore.

For background, in late 2022 when the CMS SSL certificate was updated, the Java apps were blocked from posting JSON to the dashboard. This solved the issue:

* Step 1: Download the .crt file from the CMS website (<a href="https://stackoverflow.com/a/36427118" target="_blank">more info here</a>)
  * Visit the CMS in Chrome
  * Click the padlock to the left of the URL
  * Click "Connection is Secure"
  * Click "Certificate is valid"
  * Click the "Details" tab
  * Click "Export" and save the file
* Step 2: Add the certificate to the Java jre
  * Run the following command in an elevated command promt with administrative access. Make sure to use the correct paths to the Java keytool, the Java keystore, and the cert that you downloaded!
  * `"C:\Program Files\Java\jdk1.8.0_231\bin\keytool.exe" -import -alias example -keystore "C:\Program Files\Java\jdk1.8.0_231\jre\lib\security\cacerts" -file "C:\Users\cacheflowe\Downloads\_.website.com.crt"`
  * Enter password: `changeit`
  * You should see a message confirming that the keystore was updated
  * Restart the entire app
  

## Create an Uber jar with Maven

* Add the following xml to the .pom file, inside the closing </project> tag
```
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
        <version>3.1.0</version>
        <executions>
          <execution>
            <goals>
              <goal>shade</goal>
            </goals>
            <phase>package</phase>
            <configuration>
              <artifactSet>
                <!-- <excludes>
                  <exclude>classworlds:classworlds</exclude>
                  <exclude>junit:junit</exclude>
                  <exclude>jmock:*</exclude>
                  <exclude>*:xml-apis</exclude>
                  <exclude>org.apache.maven:lib:tests</exclude>
                  <exclude>log4j:log4j:jar:</exclude>
                </excludes> -->
              </artifactSet>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```
Then run:
* `mvn package`

If tests fail, you can ignore them when building:
* `mvn package -DskipTests`
