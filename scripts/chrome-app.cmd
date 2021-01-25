echo Starting the browser app
echo "URL: %1"
echo "Width: %2, %3"
"%SYSTEMDRIVE%%HOMEPATH%\AppData\Local\Chromium\Application\chrome.exe" --app=%1 --chrome-frame --incognito --disable-pinch --overscroll-history-navigation=0 --disable-session-crashed-bubble --disable-infobars --allow-file-access-from-files --allow-running-insecure-content --use-fake-ui-for-media-stream --autoplay-policy=no-user-gesture-required --unsafely-treat-insecure-origin-as-secure="http://localhost" --window-position=50,50 --window-size=%2,%3
