#!/bin/bash
# usage: ./videoCompress2mp4.sh /Absolute/movie/file.mov 10000
filename=$1
extension="${filename##*.}"
ffmpeg -y -i $1 -vcodec mpeg4 -b:v $2k -f mp4 "$1.rate-$2.mp4"
