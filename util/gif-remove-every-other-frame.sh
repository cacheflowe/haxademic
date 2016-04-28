#!/bin/bash
# http://graphicdesign.stackexchange.com/questions/20908/how-to-remove-every-second-frame-from-an-animated-gif
# usage: $ ./gif-remove-every-other-frame.sh input.gif
input="$1"
# [cacheflowe] util [system] [master *+] gifsicle -U /Users/cacheflowe/Downloads/c4f64a4a-688e-468c-2c76-e35423a33db5.gif  `seq -f "#%g" 0 2 250` -O2 -o /Users/cacheflowe/Downloads/c4f64a4a-688e-468c-2c76-e35423a33db5-quick.gif

gifsicle -U $1 `seq -f "#%g" 0 2 $(identify $1 | tail -1 | cut -d "[" -f2 - | cut -d "]" -f1 -)` -O2 -o "$1half.gif"
