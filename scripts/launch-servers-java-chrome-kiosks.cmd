@REM =================================================================================================
@REM = Kill any existing apps
@REM =================================================================================================

start "" kill-chrome.cmd
start "" kill-node.cmd
start "" kill-java.cmd
timeout /T 5 > NUL

@REM =================================================================================================
@REM = Stop & restart server
@REM = Need to put these into their own scripts so they properly go away when complete, 
@REM = and allow this script to keep running
@REM =================================================================================================

@REM start "" server-stop.cmd
@REM timeout /T 4 > NUL
start "" launch-server-dev.cmd
timeout /T 4 > NUL
start /min launch-server-ws.cmd ^& exit
timeout /T 4 > NUL
start /min launch-java.cmd ^& exit
timeout /T 10 > NUL

@REM =================================================================================================
@REM = Launch Chromium kiosk windows on separate monitors
@REM = Prefix command with `start "" ` to launch w/o blocking
@REM =================================================================================================

@REM start "" %chromiumPath% %chromeFlags%1
start /min launch-chrome-kiosk-1.cmd ^& exit
timeout /T 4 > NUL
start /min launch-chrome-kiosk-2.cmd ^& exit
timeout /T 4 > NUL


@REM =================================================================================================
@REM = Done!
@REM =================================================================================================

pause
