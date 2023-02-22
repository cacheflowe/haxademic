@REM =================================================================================================
@REM = Set paths & vars
@REM =================================================================================================

set chromiumPath="%SYSTEMDRIVE%%HOMEPATH%\AppData\Local\Chromium\Application\chrome.exe"
set killChromeCmd=taskkill /F /T /IM chrome.exe
set chromeFlags=--chrome ^
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
--autoplay-policy=no-user-gesture-required 
set url1=https://cacheflowe.com/
set url2=https://cacheflowe.com/
set screen2X=3840

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
@REM = Launch (2) Chromium kiosk windows on separate monitors
@REM = Prefix command with `start "" ` to launch w/o blocking
@REM = --window-position and --user-data-dir are used to launch kiosk on separate monitors
@REM =================================================================================================

start "" %chromiumPath% --app=%url1% %chromeFlags% --window-position=0,0 --user-data-dir=c:/chrome-kiosk/monitor-1
start "" %chromiumPath% --app=%url2% %chromeFlags% --window-position=%screen2X%,0 --user-data-dir=c:/chrome-kiosk/monitor-2

@REM =================================================================================================
@REM = Wait until certain time of day to restart everything
@REM = Check time every 30 seconds, keep looping if not in target restart range 
@REM =================================================================================================

:loop
timeout /T 30 > NUL
if %TIME% LSS 3:00:00.00 goto loop
if %TIME% GTR 3:01:00.00 goto loop

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