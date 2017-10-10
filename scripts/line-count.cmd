@echo off

:CountLines
setlocal
cd ../src
set /a totalNumLines = 0
for /r %1 %%F in (*.java *.html *.hpp *.cs *.c) do (
  echo "%%F"
  for /f %%N in ('find /v /c "" ^<"%%F"') do set /a totalNumLines+=%%N
)

echo Total LOC (Java) = %totalNumLines%
timeout 15 > NUL
