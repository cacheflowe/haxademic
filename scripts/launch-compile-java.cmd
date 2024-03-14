REM @echo off
REM ======== Don't display this script's commands

REM ======== Restart point if app disappears
:restart

REM ======== Move to project root to launch
cd ..\java

REM ======== Kill previous java app
Taskkill /F /T /IM java.exe
Taskkill /F /T /IM javaw.exe
Taskkill /F /T /IM java.exe
Taskkill /F /T /IM javaw.exe

REM ======== Give time for computer to start up (you can always press a button to launch)
timeout 5

REM ======== Pass along app-specific args from launch script
REM ======== Allow different local java paths to be passed in as arg #4
REM ======== Use `java` instead of `javaw` if you want logging
SET args=%*
SET arg1=%1
SET arg2=%2
SET arg3=%3
SET javaPath=%4
IF [%javaPath%] == [] (
	SET javaPath="C:\Program Files\Eclipse Adoptium\jdk-17.0.10.7-hotspot\bin\java.exe"
	@REM SET javaPath="C:\Program Files\Eclipse 4.30.0\eclipse\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_17.0.9.v20231028-0858\jre\bin\javaw.exe" 
)

REM ======== String replacement to find javac next to java.exe
SET javaCPath=%javaPath%
call SET javaCPath=%%javaCPath:java.exe=javac.exe%%
echo %javaCPath%

REM ======== Remove old compiled .class files recursively
del /S src\*.class

REM ======== Compile & run
SET natives=lib\processing-4\core\library\windows-amd64;lib\processing-4\libraries\video\library\windows-amd64
SET classPath=src;bin;lib\artnet4j\library\artnet4j.jar;lib\dmxP512\library\dmxP512.jar;lib\Ess\library\Ess.jar;lib\haxademic\haxademic.jar;lib\java_websocket\Java-WebSocket-1.3.9.jar;lib\jetty\jetty-all-9.4.30.v20200611-uber.jar;lib\processing-4\core\library\core.jar;lib\processing-4\core\library\gluegen-rt.jar;lib\processing-4\core\library\jogl-all.jar;lib\processing-4\libraries\serial\library\jssc.jar;lib\processing-4\libraries\serial\library\serial.jar;lib\processing-4\libraries\video\library\gst1-java-core-1.4.0.jar;lib\processing-4\libraries\video\library\jna.jar;lib\processing-4\libraries\video\library\video.jar;lib\zxing4p3\zxingjavase_3.3.2.jar;lib\zxing4p3\zxing4p3.jar;lib\zxing4p3\zxingcore_3.3.2.jar

%javaCPath% -classpath %classPath% src\com\bounty\BountyToss.java
%javaPath% -Xmx8G -Xms8G -Djava.library.path=%natives% -classpath %classPath% com.bounty.BountyToss

REM ======== Move back to ./scripts dir
cd ..\scripts

REM ======== Pause command to keep script window open if java program quits [REMOVED]
REM pause

REM ======== Try restarting if the program quits
REM taskkill /f /t /im javaw.exe
timeout 15
echo Restarting at %DATE%,%TIME%
goto :restart
