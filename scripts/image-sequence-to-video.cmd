@echo off
echo "Building video from %1"
echo "Width audio: %2"
PUSHD %1
del _output.mp4
del _output.final.mp4
"C:\Program Files\ffmpeg\bin\ffmpeg.exe" -r 30 -f image2 -i %%04d.png -i "%2" -c:v libx264 -crf 1 -pix_fmt yuv420p -f mp4 _output.mp4
"C:\Program Files\ffmpeg\bin\ffmpeg.exe" -f concat -i _concat.txt -vf scale=800:600  -c:v libx264 -crf 12 _output.final.mp4
REM "C:\Program Files\ffmpeg\bin\ffmpeg.exe" -i %3 -i _output.mp4 -i %4 -c copy -an _output.final.mp4
POPD
echo "Finished image sequence to movie"
