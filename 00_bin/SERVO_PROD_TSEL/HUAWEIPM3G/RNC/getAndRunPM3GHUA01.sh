#!/bin/bash
cd /data02/Cynapse/HUAWEIPM3G/RNC/00_bin 
if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-6 date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi

./getPM3GHUA01.sh $DATE_PARAM
./runPM3GHUA01.sh $DATE_PARAM
