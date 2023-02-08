echo Starting the capture web app
REM indx=3
SET indx=%1
SET appURL="http://localhost:8080/capture/#stationIndex=%indx%&serverIp=192.168.1.110"
if %indx%==0 (
  SET appURL="http://localhost:8080/capture-text/#stationIndex=%indx%&serverIp=192.168.1.110"
)

echo Killing prior browser task
Taskkill /F /T /IM chrome.exe

echo Starting the browser app
REM --chrome-frame
"%SYSTEMDRIVE%%HOMEPATH%\AppData\Local\Chromium\Application\chrome.exe" --app=%appURL% --chrome -kiosk --incognito --disable-application-cache --disable-pinch --overscroll-history-navigation=0 --disable-session-crashed-bubble --disable-infobars --allow-file-access-from-files --allow-running-insecure-content --use-fake-ui-for-media-stream --no-user-gesture-required --disable-gesture-requirement-for-presentation --enable-experimental-accessibility-autoclick --autoplay-policy=no-user-gesture-required --window-size=1400,900 --window-position=0,0
