#!/bin/bash
# usage: $ ./thumbnailsForMovies /path/to/videos
for file in $1/*mp4
do
  echo "Saving thumbnail for movie: $file ..."
  extension="${file##*.}"
  ffmpeg -ss 0.5 -i $file -t 1 -qscale 0 -f image2 "$file.jpg"
done
