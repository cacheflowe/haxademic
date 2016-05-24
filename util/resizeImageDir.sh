#!/bin/bash
# usage: ./resizeImageDir.sh /Absolute/image/dir/ 640
for file in $1/*jpg
do
  echo "Resizing image: $file ..."
  convert $file -resize $2 $file
done
