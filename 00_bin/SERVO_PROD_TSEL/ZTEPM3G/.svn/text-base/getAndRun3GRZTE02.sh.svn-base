#!/bin/bash
cd /data02/Cynapse/ZTEPM3G/00_bin

if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+%d%b%Y'`
else
        DATE_PARAM=$1
fi

#separate 02 dan 03...
./getFile3GRZTE02.sh $DATE_PARAM
./run3GRZTE02.sh $DATE_PARAM
./run3GRZTE03.sh $DATE_PARAM
