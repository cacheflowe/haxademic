REM @echo off
echo "Building video %1"
PUSHD %1
del _sequence.mp4
"C:\Program Files\ffmpeg\bin\ffmpeg.exe" -r 30 -f image2 -i "floaty_blob_%%05d.png" -c:v libx264 -crf 1 -pix_fmt yuv420p -f mp4 _sequence.mp4
POPD
echo "Finished image sequence to movie"
