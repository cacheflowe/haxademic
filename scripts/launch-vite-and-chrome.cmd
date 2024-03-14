@REM =================================================================================================
@REM = Kill any existing apps
@REM =================================================================================================

start "" kill-previous.cmd
timeout /T 2 > NUL

@REM =================================================================================================
@REM = Stop & restart server
@REM = Need to put these into their own scripts so they properly go away when complete, 
@REM = and allow this script to keep running
@REM =================================================================================================

@REM start "" server-stop.cmd
@REM timeout /T 4 > NUL
start /min launch-server-dev.cmd
timeout /T 4 > NUL

@REM =================================================================================================
@REM = Launch Chromium kiosk windows on separate monitors
@REM = Prefix command with `start "" ` to launch w/o blocking
@REM =================================================================================================

@REM start "" %chromiumPath% %chromeFlags%1
start /min launch-chrome-kiosk.cmd ^& exit
timeout /T 2 > NUL


@REM =================================================================================================
@REM = Done!
@REM =================================================================================================

pause
