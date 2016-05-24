#!/bin/bash
# usage: $ ./posterFromStillFrame /path/to/images
for file in $1/*jpg
do
  echo "Saving poster for: $file ..."
  convert $file -gravity Center -crop 512x512+0+0 +repage $file.poster.jpg
done
