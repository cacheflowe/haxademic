#!/bin/bash
# usage: ./extractGifFrame.sh /Absolute/image/path.gif 10
function string_replace {
  echo "${1/\*/$2}"
}
filename=$1
frameNum=$2
tempGifFile="$filename.$frameNum.gif"
echo "Extracting frame: $frameNum ..."
gifsicle $filename '#'"$2"'' > $tempGifFile # funny escaping of '#10' for frame number
convert $tempGifFile "${filename/.gif/-poster.jpg}" # string replace
rm $tempGifFile
