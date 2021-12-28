#!/bin/bash
cd /data03/Cynapse/NOKIAPM2G/00_bin
if [ -z $1 ]; then
        DATE_PARAM=`TZ=GMT-5 date '+%Y%m%d%H'`
else
        DATE_PARAM=$1
fi

./getDataPM2GRNOK04.sh $DATE_PARAM
./runPM2GRNOK04.sh $DATE_PARAM
