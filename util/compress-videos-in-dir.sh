#!/bin/bash
# usage: $ ./compress-videos-in-dir /path/to/videos 4000
for file in $1/*mp4
do
  echo "Compressing file: $file ..."
  extension="${file##*.}"
  # echo "$file.rate-$2.$extension"
  ffmpeg -y -i $file -b:v $2k "$file.rate-$2.$extension"
done
