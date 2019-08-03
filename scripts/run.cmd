@echo off
REM ======== Don't display this script's commands

REM ======== Restart point if app disappears
:restart

REM ======== Move to project root to launch
cd ..

REM ======== Give time for computer to start up (you can always press a button to launch)
timeout 15

REM ======== Use `java` instead of `javaw` if you want logging
"C:\Program Files\Java\jdk1.8.0_202\bin\javaw.exe" -Djava.library.path=lib\jinput\native;lib\KinectPV2\library;lib\LeapMotionForProcessing\library\macosx;lib\processing-3\libraries\serial\library\windows64;lib\processing-3\libraries\sound\library\macosx;lib\RealSenseProcessing\library\native\windows-x64;lib\jasiohost -javaagent:C:\Users\cacheflowe\eclipse\java-2019-06\eclipse\configuration\org.eclipse.osgi\226\0\.cp\lib\javaagent-shaded.jar -Dfile.encoding=Cp1252 -classpath bin;lib\beads\library\beads.jar;lib\beads\library\jarjar-1.0.jar;lib\beads\library\jl1.0.1.jar;lib\beads\library\org-jaudiolibs-audioservers-jack.jar;lib\beads\library\org-jaudiolibs-audioservers-javasound.jar;lib\beads\library\org-jaudiolibs-audioservers.jar;lib\beads\library\org-jaudiolibs-jnajack.jar;lib\beads\library\tools.jar;lib\beads\library\mp3spi1.9.4.jar;lib\beads\library\tritonus_aos-0.3.6.jar;lib\beads\library\tritonus_share.jar;lib\blobDetection\library\blobDetection.jar;lib\dmxP512\library\dmxP512.jar;lib\Ess\library\Ess.jar;lib\geomerative\library\batikfont.jar;lib\geomerative\library\geomerative.jar;lib\GIFAnimation\library\GifAnimation.jar;lib\hemesh\library\trove-3.1a1.jar;lib\hemesh\library\exp4j.jar;lib\hemesh\library\hemesh-data-2_2_0.jar;lib\hemesh\library\hemesh-external-2_2_0.jar;lib\hemesh\library\javolution-6.1.0.jar;lib\hemesh\library\jts.jar;lib\hemesh\library\objparser.jar;lib\hemesh\library\hemesh.jar;lib\java_websocket\Java-WebSocket-1.3.9.jar;lib\jetty\jetty-all-9.4.15.v20190215-uber.jar;lib\jinput\jinput-2.0.9.jar;lib\jnajack\jnajack-1.3.0.jar;lib\joons\library\janino.jar;lib\joons\library\sunflow73.jar;lib\joons\library\joons.jar;lib\KinectPV2\library\KinectPV2.jar;lib\LeapMotionForProcessing\library\LeapJava.jar;lib\LeapMotionForProcessing\library\LeapMotionForProcessing.jar;lib\minim\library\jsminim.jar;lib\minim\library\minim.jar;lib\newhull\library\newhull.jar;lib\openkinect_processing\library\openkinect_processing.jar;lib\oscP5\library\oscP5.jar;lib\PixelFlow\library\PixelFlow.jar;lib\poly2tri\poly2tri-core-0.1.2.jar;lib\poly2tri\slf4j-api-1.6.3.jar;lib\processing-3\core\library\core.jar;lib\processing-3\core\library\gluegen-rt.jar;lib\processing-3\core\library\jogl-all.jar;lib\processing-3\libraries\pdf\library\itext.jar;lib\processing-3\libraries\pdf\library\pdf.jar;lib\processing-3\libraries\serial\library\jssc.jar;lib\processing-3\libraries\serial\library\serial.jar;lib\processing-3\libraries\sound\library\sound.jar;lib\processing-3\libraries\video\library\gstreamer-java.jar;lib\processing-3\libraries\video\library\jna.jar;lib\processing-3\libraries\video\library\video.jar;lib\RealSenseProcessing\library\librealsense.jar;lib\RealSenseProcessing\library\RealSenseProcessing.jar;lib\SimpleOpenNI\library\SimpleOpenNI.jar;lib\sphinx4\sphinx4-core-5prealpha.jar;lib\sphinx4\sphinx4-data-1.0.0.jar;lib\super-csv\super-csv-2.1.0.jar;lib\themidibus\library\themidibus.jar;lib\toxiclibs-complete-0020\toxiclibs_p5\library\toxiclibs_p5.jar;lib\toxiclibs-complete-0020\toxiclibscore\library\toxiclibscore.jar;lib\toxiclibs-complete-0020\volumeutils\library\volumeutils.jar;lib\toxiclibs-complete-0020\colorutils\library\colorutils.jar;lib\udp\library\udp.jar;lib\UMovieMaker\library\monte-cc.jar;lib\UMovieMaker\library\UMovieMaker.jar;lib\IPCapture\library\IPCapture.jar;lib\jasiohost\JAsioHost.jar;lib\openimaj\openimaj-test-project-1.0-SNAPSHOT-jar-with-dependencies.jar;lib\twilio\twilio-7.38.0-jar-with-dependencies.jar;lib\twilio\commons-codec-1.12.jar;lib\usb4java\lib\libusb4java-1.3.0-linux-arm.jar;lib\usb4java\lib\libusb4java-1.3.0-linux-x86.jar;lib\usb4java\lib\libusb4java-1.3.0-linux-x86-64.jar;lib\usb4java\lib\libusb4java-1.3.0-win32-x86.jar;lib\usb4java\lib\libusb4java-1.3.0-win32-x86-64.jar;lib\usb4java\lib\usb4java-1.3.0.jar;lib\usb4java\lib\commons-lang3-3.8.1.jar;lib\usb4java\lib\libusb4java-1.3.0-darwin-x86-64.jar;lib\usb4java\lib\libusb4java-1.3.0-linux-aarch64.jar;lib\usb4java\lib\usb4java-javax-1.3.0.jar;lib\usb4java\lib\usb-api-1.0.2.jar com.haxademic.demo.system.Demo_CrashMonitor

REM ======== Move back to ./scripts dir
cd scripts

REM ======== Pause command to keep script window open if java program quits [REMOVED]
REM pause

REM ======== Try restarting if the program quits
taskkill /f /im javaw.exe
timeout 15
echo Restarting at %DATE%,%TIME%
goto :restart
