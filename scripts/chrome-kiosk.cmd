REM from: https://stackoverflow.com/questions/27649264/run-chrome-in-fullscreen-mode-on-windows
REM also: https://stackoverflow.com/questions/13436855/launch-google-chrome-from-the-command-line-with-specific-window-coordinates/19789383
REM note: get the top version w/codecs: http://chromium.woolyss.com/

@echo off

echo Step 1 of 5: Waiting a few seconds before starting the Kiosk...
"C:\windows\system32\ping" -n 3 -w 1000 127.0.0.1 >NUL

REM echo Step 2 of 5: Starting browser as a pre-start to delete error messages...
REM "C:\google_homepage.url"

echo Step 3 of 5: Waiting a few seconds before killing the browser task...
"C:\windows\system32\ping" -n 11 -w 1000 127.0.0.1 >NUL

echo Step 4 of 5: Killing the browser task gracefully to avoid session restore...
Taskkill /IM chrome.exe

echo Step 5 of 5: Waiting a few seconds before restarting the browser...
"C:\windows\system32\ping" -n 11 -w 1000 127.0.0.1 >NUL

echo Final 'invisible' step: Starting the browser, Finally...
"C:\Users\cacheflowe\AppData\Local\Chromium\Application\chrome.exe" --app=%1 --chrome -kiosk --incognito --disable-pinch --overscroll-history-navigation=0 --disable-session-crashed-bubble

exit
