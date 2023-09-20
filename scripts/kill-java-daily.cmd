@REM =================================================================================================
@REM = Kills Java processes every day at 8am
@REM = Launch in a new CMD window with:
@REM = start "" kill-java-nightly.cmd
@REM = Or run it on its own
@REM =================================================================================================

@REM =================================================================================================
@REM = Restart loop point
@REM =================================================================================================

:restart
echo Restarted at %TIME%

@REM =================================================================================================
@REM = Wait until certain time of day to restart Java
@REM = Check time every 30 seconds, keep looping if not in target restart range 
@REM =================================================================================================

:loop
timeout /T 30 > NUL
if %TIME% LSS 8:00:00.00 goto loop
if %TIME% GTR 8:01:00.00 goto loop

@REM =================================================================================================
@REM = If between 1 minute range, kill all Java processes, twice for good measure
@REM =================================================================================================

echo Restarting at %TIME%

taskkill /f /t /im javaw.exe
taskkill /f /t /im java.exe
wmic process where "name='javaw.exe'" delete
wmic process where "name='java.exe'" delete

timeout /T 5 > NUL

taskkill /f /t /im javaw.exe
taskkill /f /t /im java.exe
wmic process where "name='javaw.exe'" delete
wmic process where "name='java.exe'" delete

@REM =================================================================================================
@REM = Wait 65 seconds and start the loop again, now we're good until tomorrow, 
@REM = and out of the time window
@REM =================================================================================================

timeout /T 65 > NUL
goto restart

@REM =================================================================================================
@REM = We shouldn't ever make it here
@REM =================================================================================================

pause
