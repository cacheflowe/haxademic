@REM =================================================================================================
@REM = Start or restart!
@REM =================================================================================================

:restart
echo Running app at %TIME%

@REM =================================================================================================
@REM = Check time every 30 seconds, if between 1 minute range, wait 65 seconds and restart the whole thing
@REM =================================================================================================

:loop
timeout /T 20 > NUL

@REM Get the current time
SET "hours=%time:~0,2%"
IF "%hours:~0,1%"==" " SET "hours=0%hours:~1,1%"
SET "minutes=%time:~3,2%"
SET "seconds=%time:~6,2%"
SET "hundredths=%time:~9,2%"
SET "formattedTime=%hours%:%minutes%:%seconds%.%hundredths%"

@REM Check if we're within the time range
echo Checking time at %formattedTime% ...
if %formattedTime% LSS 01:00:00.00 goto loop
if %formattedTime% GTR 01:10:00.00 goto loop


@REM =================================================================================================
@REM = If between 1 minute range, wait 65 seconds and restart the whole thing
@REM =================================================================================================

echo Continuing at %TIME%
timeout /T 65 > NUL
goto restart

@REM =================================================================================================
@REM = We shouldn't ever make it here
@REM =================================================================================================

pause