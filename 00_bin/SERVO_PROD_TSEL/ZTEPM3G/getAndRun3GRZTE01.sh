#!/bin/bash
cd /data02/Cynapse/ZTEPM3G/00_bin

if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+%d%b%Y'`
else
        DATE_PARAM=$1
fi


./getFile3GRZTE01.sh $DATE_PARAM
./run3GRZTE01.sh $DATE_PARAM
