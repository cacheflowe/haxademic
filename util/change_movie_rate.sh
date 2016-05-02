#!/bin/bash
# usage: ./change_movie_rate.sh /Absolute/movie/file.mp4 1.5
filename=$1
extension="${filename##*.}"
ffmpeg -i $1 -filter:v "setpts=$2*PTS" "$1.rate-$2.$extension"
