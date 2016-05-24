#!/bin/bash
# usage: ./change_movie_rate.sh /Absolute/movie/file.mp4 1.5
filename=$1
extension="${filename##*.}"
ffmpeg -i $1 -filter:v "setpts=$2*PTS" "$1.rate-$2.$extension"
# ffmpeg -r 60 -i 001.mov -filter:v "setpts=0.25*PTS,transpose=0" -filter:a "atempo=2.0,atempo=2.0" -r 30 001.portrait.mp4
