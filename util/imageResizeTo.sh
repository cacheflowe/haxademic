#!/bin/bash
# usage: ./imageResizeTo.sh /Absolute/image/file.jpg 640
filename=$1
extension="${filename##*.}"
convert $1 -resize $2 "$1.$2.$extension"
