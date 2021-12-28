#!/bin/bash
cd /data02/Cynapse/HUAWEIPMPSC/00_bin 
if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-4 date '+%Y%m%d'`
else
        DATE_PARAM=$1
fi

./getPSCHUA01.sh $DATE_PARAM
./runPSCHUA01.sh $DATE_PARAM
