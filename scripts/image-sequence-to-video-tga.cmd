REM @echo off
echo "Building video from %1"
PUSHD %1
del _sequence.mp4
"C:\Program Files\ffmpeg\bin\ffmpeg.exe" -r 60 -f image2 -i "%%05d.tga" -c:v libx264 -crf 1 -pix_fmt yuv420p -f mp4 _sequence.mp4
POPD
echo "Finished image sequence to movie"
