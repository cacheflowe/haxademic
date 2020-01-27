REM @echo off
REM ======== Don't display this script's commands

REM ======== Restart point if app disappears
:restart

REM ======== Move to project root to launch
cd ..

REM ======== Give time for computer to start up (you can always press a button to launch)
timeout 15

REM ======== Pass along app-specific args from launch script
REM ======== Allow different local java paths to be passed in as arg #4
REM ======== Use `java` instead of `javaw` if you want logging
SET args=%*
SET arg1=%1
SET arg2=%2
SET arg3=%3
SET javaPath=%4
IF [%javaPath%] == [] (
	SET javaPath="C:\Program Files\Java\jdk1.8.0_231\bin\java.exe"
)

REM ======== String replacement to find javac next to java.exe
SET javaCPath=%javaPath%
call SET javaCPath=%%javaCPath:java.exe=javac.exe%%
echo %javaCPath%

REM ======== Remove old compiled .class files recursively
del /S src\*.class

REM ======== Compile & run
SET natives=-Djava.library.path=lib\processing-3\libraries\serial\library\windows64
SET classPath=src;bin;lib\Ess\library\Ess.jar;lib\haxademic\haxademic.jar;lib\java_websocket\Java-WebSocket-1.3.9.jar;lib\jetty\jetty-all-9.4.22.v20191022-uber.jar;lib\processing-3\core\library\core.jar;lib\processing-3\core\library\gluegen-rt.jar;lib\processing-3\core\library\jogl-all.jar;lib\processing-3\libraries\video\library\gstreamer-java.jar;lib\processing-3\libraries\video\library\jna.jar;lib\processing-3\libraries\video\library\video.jar;lib\themidibus\library\themidibus.jar;lib\dmxP512\library\dmxP512.jar;lib\processing-3\libraries\serial\library\jssc.jar;lib\processing-3\libraries\serial\library\serial.jar

%javaCPath% -classpath %classPath% src\com\dbg\server\DBGServer.java
%javaPath% -Xmx256m -Xms64m %natives% -classpath %classPath% com.dbg.server.DBGServer %arg1% %arg2% %arg3%

REM ======== Move back to ./scripts dir
cd scripts

REM ======== Pause command to keep script window open if java program quits [REMOVED]
REM pause

REM ======== Try restarting if the program quits
REM taskkill /f /im javaw.exe
timeout 15
echo Restarting at %DATE%,%TIME%
goto :restart
