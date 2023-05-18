@REM =================================================================================================
@REM = Set paths & vars
@REM =================================================================================================

set startServer=npm run start
set stopServer=npm run stop
set url=http://localhost:3000
set chromiumPath="%SYSTEMDRIVE%%HOMEPATH%\AppData\Local\Chromium\Application\chrome.exe"
set killChromeCmd=taskkill /F /T /IM chrome.exe
set chromeFlags=--app=%url% ^
-kiosk ^
--incognito ^
--disable-application-cache ^
--disable-pinch ^
--overscroll-history-navigation=0 ^
--disable-session-crashed-bubble ^
--disable-infobars ^
--allow-file-access-from-files ^
--allow-running-insecure-content ^
--use-fake-ui-for-media-stream ^
--no-user-gesture-required ^
--disable-gesture-requirement-for-presentation ^
--enable-experimental-accessibility-autoclick ^
--autoplay-policy=no-user-gesture-required ^
--unsafely-treat-insecure-origin-as-secure=http://192.168.0.61 ^
--user-data-dir=c:/chrome-kiosk/amfam

@REM =================================================================================================
@REM = Restart loop point
@REM =================================================================================================

:restart
echo Running app at %TIME%

@REM =================================================================================================
@REM = Kill any existing Chrome/Chromium instances
@REM =================================================================================================

%killChromeCmd%
timeout /T 5 > NUL

@REM =================================================================================================
@REM = Stop & restart server
@REM = Need to put these into their own scripts so they properly go away when complete, 
@REM = and allow this script to keep running
@REM =================================================================================================

start "" server-stop.cmd
timeout /T 4 > NUL
start /min server-start.cmd ^& exit
timeout /T 30 > NUL

@REM =================================================================================================
@REM = Launch Chromium kiosk windows on separate monitors
@REM = Prefix command with `start "" ` to launch w/o blocking
@REM =================================================================================================

start "" %chromiumPath% %chromeFlags%

@REM =================================================================================================
@REM = Wait until certain time of day to restart everything
@REM = Check time every 30 seconds, keep looping if not in target restart range 
@REM =================================================================================================

:loop
timeout /T 30 > NUL
if %TIME% LSS 8:00:00.00 goto loop
if %TIME% GTR 8:01:00.00 goto loop

@REM =================================================================================================
@REM = If between 1 minute range, wait 65 seconds and restart the whole thing
@REM =================================================================================================

echo Restarting at %TIME%
timeout /T 65 > NUL
goto restart

@REM =================================================================================================
@REM = We shouldn't ever make it here
@REM =================================================================================================

pause
