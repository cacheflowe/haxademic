#!/bin/bash
# usage: $ ./tgaSequence2mp4.sh /absolute/path
cd $1
ffmpeg -r 30 -pattern_type glob -i "*.tga" -preset slow -b:v 15000k -maxrate 20000k -bufsize 1000k -vf "transpose=0" _output.mpeg
ffmpeg -i _output.mpeg _output.mp4
