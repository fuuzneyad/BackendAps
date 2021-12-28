#!/bin/bash
cd /data02/Cynapse/HUAWEIPM2G/00_bin
if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi

./getPM2GHUA06.sh $DATE_PARAM
./runPM2GHUA06.sh $DATE_PARAM
