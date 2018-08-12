REM @echo off

REM "============== GET PARAMS"

SET sox="C:\Program Files (x86)\sox-14-4-2\sox.exe"
SET filePath=%1
SET fileNormPath=%filePath%.norm.wav
SET gain=%2

echo "============== Normalizing audio: %fileNormPath%"
%sox%  %filePath% %fileNormPath% norm %gain%

echo "============== Deleting original: %filePath%"
del %filePath%

echo "============== Renaming normalized file back to source: %filePath%"
For %%A in ("%filePath%") do (
    Set fileLocation=%%~dpA
    Set fileName=%%~nxA
)
ren %fileNormPath% %fileName%

echo "============== Finished normalize"
