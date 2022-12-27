REM  @echo off
REM
REM  This script uses ffmpeg to convert a series of TGA files into an MP4 movie.
REM  The TGA files should be in sequential order with no gaps. 
REM  The TGA files should be named with a 5 digit number padded with zeros
REM
REM  The script expects ffmpeg to be installed at C:\Program Files\ffmpeg\bin\ffmpeg.exe
REM
REM  Usage: image-sequence-to-video-tga.cmd <movie directory>
REM
REM  Example: image-sequence-to-video-tga.cmd dirName 30
REM

@echo off
set FFMPEG=C:\Program Files\ffmpeg\bin
set IMAGES_DIR=%1
set FPS=%2
IF "%FPS%"=="" (set FPS=30)
echo "Building video from %IMAGES_DIR%"
PUSHD %IMAGES_DIR%
set PATH=%PATH%;%FFMPEG%
del _sequence.mp4
ffmpeg.exe -r %FPS% -f image2 -i "%%05d.tga" -c:v libx264 -crf 1 -pix_fmt yuv420p -f mp4 _sequence.mp4
POPD
echo "Finished image sequence to movie"
