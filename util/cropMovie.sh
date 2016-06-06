#!/bin/bash
# usage: ./cropMovie.sh /Absolute/movie/file.mov x y width height bitrate
filename=$1
extension="${filename##*.}"
ffmpeg -i $filename -filter:v "crop=$4:$5:$2:$3" -b:v $6k -c:a copy "$filename.crop.$extension"
