#!/bin/sh
# http://blog.pkh.me/p/21-high-quality-gif-with-ffmpeg.html
# usage: $ ./movie2gif.sh input.mov output.gif 600
# be sure to create ./tmp/ directory to make it work

palette="./tmp/palette.png"

filters="fps=30,scale=$3:-1:flags=lanczos"

ffmpeg -v warning -i $1 -vf "$filters,palettegen" -y $palette
ffmpeg -v warning -i $1 -i $palette -lavfi "$filters [x]; [x][1:v] paletteuse" -y $2
