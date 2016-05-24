#!/bin/sh
# usage: $ ./webm2mp4.sh input.webm
ffmpeg -fflags +genpts -i $1 -r 30 "$1.mp4"
