REM ======== don't display this script's commands
@echo off

REM ======== move to project root to launch
cd ..

REM ======== give time for computer to start up (you can always press a button to launch)
timeout 30

REM ======== use java instead of javaw if you want logging
"C:\Program Files\Java\jre1.8.0_144\bin\javaw.exe" -Xmx24G -Xms4G -Djava.library.path=lib\processing-3\libraries\sound\library\windows64 -Dfile.encoding=Cp1252 -classpath bin;lib\Ess\library\Ess.jar;lib\UMovieMaker\library\monte-cc.jar;lib\UMovieMaker\library\UMovieMaker.jar;lib\java_websocket\dist\java_websocket.jar;lib\processing-3\core\library\core.jar;lib\processing-3\core\library\gluegen-rt.jar;lib\processing-3\core\library\jogl-all.jar;lib\processing-3\libraries\video\library\gstreamer-java.jar;lib\processing-3\libraries\video\library\jna.jar;lib\processing-3\libraries\video\library\video.jar;lib\controlP5\library\controlP5.jar;lib\processing-3\libraries\sound\library\sound.jar;lib\haxademic\haxademic.jar;lib\minim\library\jl1.0.1.jar;lib\minim\library\jsminim.jar;lib\minim\library\minim.jar;lib\minim\library\mp3spi1.9.5.jar;lib\minim\library\tritonus_aos.jar;lib\minim\library\tritonus_share.jar;lib\themidibus\library\themidibus.jar;lib\processing-3\libraries\pdf\library\itext.jar;lib\processing-3\libraries\pdf\library\pdf.jar;lib\processing-3\libraries\net\library\net.jar;lib\jetty\jetty-all-9.4.6.v20170531-uber.jar;lib\geomerative\library\batikfont.jar;lib\geomerative\library\geomerative.jar;lib\socket-io\socket.io-client-1.0.0.jar;lib\socket-io\engine.io-client-1.0.0.jar;lib\socket-io\hamcrest-core-1.3.jar;lib\socket-io\hamcrest-library-1.3.jar;lib\socket-io\json-20090211.jar;lib\socket-io\junit-4.12.jar;lib\socket-io\okhttp-3.8.1.jar;lib\socket-io\okio-1.13.0.jar com.app.name.MainClass

REM ======== move back to ./scripts dir
cd scripts

REM ======== pause command to keep script window open if java program quits
pause
