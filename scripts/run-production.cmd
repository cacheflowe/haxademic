@echo off
REM ======== Don't display this script's commands

REM ======== Move to project root to launch
cd ..

REM ======== Give time for computer to start up (you can always press a button to launch)
timeout 15

REM ======== Use `java` instead of `javaw` if you want logging
"C:\Program Files\Java\jre1.8.0_231\bin\javaw.exe" -Xmx4G -Xms2G -Djava.library.path=lib\processing-3\libraries\serial\library\windows64;lib\jcef\win64\java-cef-build-bin\bin\lib\win64 -Dfile.encoding=Cp1252 -classpath bin;lib\beads\library\beads.jar;lib\beads\library\jarjar-1.0.jar;lib\beads\library\jl1.0.1.jar;lib\beads\library\org-jaudiolibs-audioservers-jack.jar;lib\beads\library\org-jaudiolibs-audioservers-javasound.jar;lib\beads\library\org-jaudiolibs-audioservers.jar;lib\beads\library\org-jaudiolibs-jnajack.jar;lib\beads\library\tools.jar;lib\beads\library\tritonus_aos-0.3.6.jar;lib\beads\library\tritonus_share.jar;lib\dmxP512\library\dmxP512.jar;lib\Ess\library\Ess.jar;lib\haxademic\haxademic.jar;lib\java_websocket\Java-WebSocket-1.3.9.jar;lib\jetty\jetty-all-9.4.22.v20191022-uber.jar;lib\minim\library\jsminim.jar;lib\minim\library\minim.jar;lib\minim\library\mp3spi1.9.5.jar;lib\processing-3\core\library\core.jar;lib\processing-3\core\library\gluegen-rt.jar;lib\processing-3\core\library\jogl-all.jar;lib\processing-3\libraries\pdf\library\itext.jar;lib\processing-3\libraries\pdf\library\pdf.jar;lib\processing-3\libraries\serial\library\jssc.jar;lib\processing-3\libraries\serial\library\serial.jar;lib\processing-3\libraries\sound\library\javamp3-1.0.4.jar;lib\processing-3\libraries\sound\library\jsyn-20171016.jar;lib\processing-3\libraries\sound\library\sound.jar;lib\processing-3\libraries\video\library\gstreamer-java.jar;lib\processing-3\libraries\video\library\jna.jar;lib\processing-3\libraries\video\library\video.jar;lib\themidibus\library\themidibus.jar;lib\UMovieMaker\library\monte-cc.jar;lib\UMovieMaker\library\UMovieMaker.jar;lib\jcef\win64\java-cef-build-bin\bin\gluegen-rt-natives-windows-amd64.jar;lib\jcef\win64\java-cef-build-bin\bin\gluegen-rt.jar;lib\jcef\win64\java-cef-build-bin\bin\jcef-tests.jar;lib\jcef\win64\java-cef-build-bin\bin\jcef.jar;lib\jcef\win64\java-cef-build-bin\bin\jogl-all-natives-windows-amd64.jar;lib\jcef\win64\java-cef-build-bin\bin\jogl-all.jar com.cobra.speedzone.CobraPgaShow mode=production

REM ======== Move back to ./scripts dir
cd scripts

REM ======== Pause command to keep script window open if java program quits [REMOVED]
REM pause

