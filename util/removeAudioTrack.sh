#!/bin/bash
for file in $1/*mp4
do
  echo "Removing audio from: $file ..."
  extension="${file##*.}"
  ffmpeg -i $file -c copy -an "$file.noaudio.$extension"
done
