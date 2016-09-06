#!/bin/bash
# usage: ./resizeImageDir.sh /Absolute/image/path.jpg 640
filename=$1
extension="${filename##*.}"
echo "Resizing image: $filename ..."
convert $1 -resize $2 "$1.$2.$extension"
