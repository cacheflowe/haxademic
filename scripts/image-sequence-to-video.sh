#!/bin/bash
echo "Building video: $1"
rm "$1/_sequence.mp4"
/usr/local/Cellar/ffmpeg/4.0.2/bin/ffmpeg -r 30 -f image2 -pattern_type glob -i "$1/*.png" -vcodec libx264 -q:v 0 -pix_fmt yuv420p -f mp4 "$1/_sequence.mp4"
echo "Finished image sequence to movie"
