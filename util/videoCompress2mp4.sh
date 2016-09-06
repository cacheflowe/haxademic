#!/bin/bash
# usage: ./videoCompress2mp4.sh /Absolute/movie/file.mov 10000
# scale down: -vf "scale=960:-1"
# frame rate: -r 30
# h264: -c:v libx264
filename=$1
extension="${filename##*.}"
ffmpeg -y -i $1 -vcodec mpeg4 -b:v $2k -f mp4 -r 30 "$1.rate-$2.mp4"
