@echo off

:CountLines
setlocal

pushd 
cd ../src
set /a totalNumLines = 0
for /r %1 %%F in (*.java *.html *.hpp *.cs *.c) do (
  echo "%%F"
  for /f %%N in ('find /v /c "" ^<"%%F"') do set /a totalNumLines+=%%N
)
popd

pushd 
cd ../data
set /a shaderNumLines = 0
for /r %1 %%F in (*.glsl) do (
  echo "%%F"
  for /f %%N in ('find /v /c "" ^<"%%F"') do set /a shaderNumLines+=%%N
)
popd

echo Total LOC (Java) = %totalNumLines%
echo Total LOC (GLSL) = %shaderNumLines%
timeout 15 > NUL
