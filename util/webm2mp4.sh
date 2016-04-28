#!/bin/sh
# usage: $ ./webm2mp4.sh input.webm
ffmpeg -fflags +genpts -i $1 -r 24 "$1.mp4"
