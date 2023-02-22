set flags=--incognito ^
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

echo %flags%
pause